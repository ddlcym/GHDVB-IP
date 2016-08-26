package com.changhong.app.dtv;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.app.constant.Class_Constant;
import com.changhong.app.constant.Class_Global;
import com.changhong.app.dtv.DialogUtil.DialogBtnOnClickListener;
import com.changhong.app.dtv.DialogUtil.DialogMessage;
import com.changhong.app.dtv.Main.UI_Handler;
import com.changhong.app.utils.Utils;
import com.changhong.dvb.Channel;
import com.changhong.dvb.ProtoMessage.DVB_RectSize;
import com.iflytek.xiri.Feedback;
import com.iflytek.xiri.scene.ISceneListener;
import com.iflytek.xiri.scene.Scene;

public class ChannelList extends Activity implements ISceneListener {
	private static final String TAG = "ChannelList";
	private Context context;
	private String sceneJson;

	private Scene scene;
	private Feedback feedback;
	private TextView epgListTitleViewLeft,epgListTitleViewCenter,epgListTitleViewRight;// chanellist title
	private ListView channelListView; // channel list
	private ImageView focusView; // foucus image
	private LinearLayout channelListLinear;// channellist layout

	private static final int SKIP = 1;
	private static final int UNSKIP = 0;
	private int channelCount;
	private Channel curChannel;
	private View curView;
	private int channelId;
	private String[] TVtype;// all tv type
	private int curChannelIndex=-1;// selected channelindex
	private int lastViewPos=-1,firstViewPos=-1;// channelist中失去焦点前所在屏幕的第一个和最后一个可见view位置
	private int lastFocuPos=-1; // channelist中失去焦点的位置

	private int curType = 0;
	private int curListIndex = 0;

	// all type channel
	List<Channel> allTvList = new ArrayList<Channel>();
	List<Channel> CCTVList = new ArrayList<Channel>();
	List<Channel> starTvList = new ArrayList<Channel>();
	List<Channel> favTvList = new ArrayList<Channel>();
	List<Channel> localTvList = new ArrayList<Channel>();
	List<Channel> HDTvList = new ArrayList<Channel>();
	List<Channel> otherTvList = new ArrayList<Channel>();
	boolean []bChanListAdded = {false,false,false,false,false,false,false};

	// animation

	// application
	SysApplication objApplication;

	private static final int HANDLE_MSG_CLOSE_CHANNELLIST = 1002;
	Class_Global obj_Global = new Class_Global();
	private PF_Handler UI_Handler = new PF_Handler(this);

	private boolean lockSwap = false;
	private int old_chanId;
	private int new_ChanId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channellist_gh);
		objApplication = SysApplication.getInstance();
		banner = Banner.getInstance(this);
		scene = new Scene(this);
		feedback = new Feedback(this);
		channelCount = objApplication.dvbDatabase.getChannelCount();

		if (channelCount <= 0) {
			Intent mIntent = new Intent(ChannelList.this, DialogNotice.class);
			mIntent.putExtra(Class_Constant.DIALOG_TITLE, "无节目");
			mIntent.putExtra(Class_Constant.DIALOG_DETAILS, "没有节目！");
			mIntent.putExtra(Class_Constant.DIALOG_BUTTON_NUM, 1);
			startActivity(mIntent);
			finish();
		}
		curType = getIntent().getIntExtra("curType", 0);
		getAllTVtype(curType);
		registerBroadReceiver();
		Channel channel = objApplication.dvbDatabase.getChannel(objApplication
				.getLastProgram());
		channelId = channel.chanId;
		// setfullscreen
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		DVB_RectSize.Builder builder = DVB_RectSize.newBuilder().setX(0)
				.setY(0).setW(size.x).setH(size.y);
		objApplication.dvbPlayer.setSize(builder.build());
		initView();
	}

	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		scene.init(this);
	}

	private void registerBroadReceiver() {
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.changhong.action.stoptvlive");
		registerReceiver(stopReceiver, filter);
	}

	BroadcastReceiver stopReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// 停掉main

			if (objApplication.dvbPlayer != null) {
				objApplication.dvbPlayer.stop();
				objApplication.dvbPlayer.blank();
			}
			finish();
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		scene.release();
	}

	private int getSortIndex(int chanId) {
		int index = 0;
		Channel[] channels = objApplication.dvbDatabase.getChannelsAllSC();// Only
																			// get
																			// channels
																			// type=1(TV)
		if (channels == null) {
			return index;
		}
		for (Channel channel : channels) {
			// Skip the radio and invalid channels.
			if (channel.sortId != 0x1 && channel.sortId != 0x11
					&& channel.sortId != 0x16 && channel.sortId != 0x19
					&& (channel.videoPid == 0x0 || channel.videoPid == 0x1fff)) {
				continue;
			}
			if (channel.chanId != chanId) {
				if (channel.skip == 0) {
					index++;
				}
			} else {
				break;
			}
		}
		return index;
	}

	private boolean isThisChannelWants(Channel thisChannel) {
		// Skip the radio and invalid channels.排除下面sevicetype的节目
		if (thisChannel.sortId != 0x1
				&& thisChannel.sortId != 0x11
				&& thisChannel.sortId != 0x16
				&& thisChannel.sortId != 0x19
				&& (thisChannel.videoPid == 0x0 || thisChannel.videoPid == 0x1fff)) {
			return false;
		}
		return true;
	}

	private void getAllTVtype(int index) {
		// fill all type tv
		
		if(index<0||index>6)
			return;
		
		if(bChanListAdded[index])
			return ;
		
		bChanListAdded[index] = true;
		
		Channel[] Channels = objApplication.dvbDatabase.getChannelsAllSC();// Only
																			// get
																			// channels
																			// type=1(TV)
		// clear all tv type;
		switch (index) {
		case 0:
			allTvList.clear();
			for (Channel Channel : Channels) {
				allTvList.add(Channel);
			}
			break;

		case 1:
			CCTVList.clear();
			String regExCCTV;
			regExCCTV = getResources().getString(R.string.zhongyang);
			java.util.regex.Pattern pattern = java.util.regex.Pattern
					.compile("CCTV|" + regExCCTV);
			for (Channel Channel : Channels) {
				java.util.regex.Matcher matcher = pattern.matcher(Channel.name);
				boolean classBytype = matcher.find();
				if (classBytype) {
					CCTVList.add(Channel);
				}
			}
			break;
		case 2:
			starTvList.clear();
			String regExStar;
			regExStar = getResources().getString(R.string.weishi);
			java.util.regex.Pattern patternStar = java.util.regex.Pattern
					.compile(".*" + regExStar + "$");
			for (Channel Channel : Channels) {
				java.util.regex.Matcher matcherStar = patternStar
						.matcher(Channel.name);
				boolean classBytypeStar = matcherStar.matches();
				if (classBytypeStar) {
					starTvList.add(Channel);
				}
			}
			break;
		case 3:
			localTvList.clear();
			String regExLocal = "CDTV|SCTV|"
					+ getResources().getString(R.string.rongcheng) + "|"
					+ getResources().getString(R.string.jingniu) + "|"
					+ getResources().getString(R.string.qingyang) + "|"
					+ getResources().getString(R.string.wuhou) + "|"
					+ getResources().getString(R.string.chenghua) + "|"
					+ getResources().getString(R.string.jinjiang) + "|"
					+ getResources().getString(R.string.chengdu) + "|"
					+ getResources().getString(R.string.sichuan);
			java.util.regex.Pattern patternLocal = java.util.regex.Pattern
					.compile(regExLocal);
			for (Channel Channel : Channels) {
				java.util.regex.Matcher matcherLocal = patternLocal
						.matcher(Channel.name);
				boolean classBytypeLocal = matcherLocal.find();
				if (classBytypeLocal) {
					localTvList.add(Channel);
				}
			}
			break;
		case 4:
			HDTvList.clear();
			String regExHD = getResources().getString(R.string.hd_dtv) + "|"
					+ getResources().getString(R.string.xinyuan_hdtv1) + "|"
					+ getResources().getString(R.string.xinyuan_hdtv2) + "|"
					+ getResources().getString(R.string.xinyuan_hdtv3) + "|"
					+ getResources().getString(R.string.xinyuan_hdtv4);
			java.util.regex.Pattern patternHD = java.util.regex.Pattern
					.compile("3D|" + regExHD + "|.*HD$");
			for (Channel Channel : Channels) {
				java.util.regex.Matcher matcherHD = patternHD
						.matcher(Channel.name);
				boolean classBytypeHD = matcherHD.find();
				if (classBytypeHD) {
					HDTvList.add(Channel);
				}
			}
			break;
		case 5:
			favTvList.clear();
			for (Channel Channel : Channels) {
				if (Channel.favorite == 1) {
					favTvList.add(Channel);
				}

			}
			break;
		case 6:
			otherTvList.clear();
			String regExOther = "CDTV|SCTV|CCTV|"
					+ getResources().getString(R.string.weishi) + "|"
					+ getResources().getString(R.string.rongcheng) + "|"
					+ getResources().getString(R.string.jingniu) + "|"
					+ getResources().getString(R.string.qingyang) + "|"
					+ getResources().getString(R.string.wuhou) + "|"
					+ getResources().getString(R.string.chenghua) + "|"
					+ getResources().getString(R.string.jinjiang) + "|"
					+ getResources().getString(R.string.chengdu) + "|"
					+ getResources().getString(R.string.sichuan);
			java.util.regex.Pattern patternOther = java.util.regex.Pattern
					.compile(regExOther);
			for (Channel Channel : Channels) {
				java.util.regex.Matcher matcherOther = patternOther
						.matcher(Channel.name);
				boolean classBytypeOther = matcherOther.find();
				if (!classBytypeOther) {
					otherTvList.add(Channel);
				}
			}
			break;
		}

	}

	private void showChannelList() {
		// TODO show channellist
		List<Channel> curChannels = null;
		
		showEpgTitle(curType);
		
		switch (curType) {
		case 0:
			curChannels = allTvList;
			break;
		case 1:
			curChannels = CCTVList;
			break;
		case 2:
			curChannels = starTvList;
			break;
		case 3:
			curChannels = localTvList;
			break;
		case 4:
			curChannels = HDTvList;
			break;
		case 5:
			curChannels = favTvList;
			break;
		case 6:
			curChannels = otherTvList;
			break;
		}
		mCurChannels = curChannels;
		if (mAdapter == null) {
			mAdapter = new ChannelAdapter();
			channelListView.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
		if (mCurChannels.size() <= 0) {
			focusView.setVisibility(View.INVISIBLE);
		}
	}

	public List<Channel> mCurChannels;
	public ChannelAdapter mAdapter;

	private void dislistfocus(FrameLayout selected) {
		Rect imgRect = new Rect();
		FrameLayout.LayoutParams focusItemParams = new FrameLayout.LayoutParams(
				10, 10);
		selected.getGlobalVisibleRect(imgRect);
		focusItemParams.leftMargin = imgRect.left - 8;
		focusItemParams.topMargin = imgRect.top - 7;
		focusItemParams.width = imgRect.width() + 16;
		focusItemParams.height = imgRect.height() + 14;

		focusView.setLayoutParams(focusItemParams);
		focusView.setVisibility(View.VISIBLE);
		focusView.bringToFront();
	}

	TextView channelIndexView;

	private void initView() {
		context = ChannelList.this;
		
		dtv_digital_1 = (ImageView) findViewById(R.id.dtv_digital_1);
		dtv_digital_2 = (ImageView) findViewById(R.id.dtv_digital_2);
		dtv_digital_3 = (ImageView) findViewById(R.id.dtv_digital_3);
		tvRootDigitalkey = (LinearLayout) findViewById(R.id.id_dtv_digital_root);
		
		TVtype = getResources().getStringArray(R.array.tvtype);
		channelListLinear = (LinearLayout) findViewById(R.id.chlist_back);
		channelListView = (ListView) findViewById(R.id.id_epg_chlist);
		focusView = (ImageView) findViewById(R.id.set_focus_id);
		
		epgListTitleViewLeft = (TextView) findViewById(R.id.id_epglist_title_left);
		epgListTitleViewCenter = (TextView) findViewById(R.id.id_epglist_title_center);
		epgListTitleViewRight = (TextView) findViewById(R.id.id_epglist_title_right);
		
		showChannelList();
		channelListView.setSelection(getSortIndex(channelId));
		channelListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO show the select channel
				curListIndex = position;
				// int[] pos = { -1, -1 };
				if (view != null) {
					int itemCnt = channelListView.getLastVisiblePosition()-channelListView.getFirstVisiblePosition();
					// view.getLocationOnScreen(pos);
					dislistfocus((FrameLayout) view);
/*
					TextView channelIndex = (TextView) view.findViewById(R.id.chanId);
					channelIndex.setTextColor(Color.rgb(255,0,0));					
					TextView channelName = (TextView) view.findViewById(R.id.chanName);
					channelName.setTextColor(Color.rgb(255,0,0));
					
					Log.i("YYY","curPos:"+position+",lastFocuPos:"+lastFocuPos+",last:"+channelListView.getLastVisiblePosition()+
							",first:"+channelListView.getFirstVisiblePosition());
					
					
					//恢复上一条为本色
					if(lastViewPos == position)
						lastFocuPos = itemCnt-2;
					
					if(firstViewPos==position)
						lastFocuPos = 1;
					
					if(lastFocuPos>=0 && lastFocuPos<(itemCnt-1)){
						View pre_vi=null;
						try {
							pre_vi = channelListView.getChildAt(lastFocuPos); 
						} catch (Exception e) {
							Log.i(TAG,"can't find pre_item");
							pre_vi = null;
							// TODO: handle exception
						}
						if(pre_vi!=null){
							TextView channelIndex2 = (TextView) pre_vi.findViewById(R.id.chanId);
							channelIndex2.setTextColor(Color.rgb(160,162,164));					
							TextView channelName2 = (TextView) pre_vi.findViewById(R.id.chanName);
							channelName2.setTextColor(Color.rgb(160,162,164));		
						}			
					}
					
					lastFocuPos = position - channelListView.getFirstVisiblePosition();
					
					lastViewPos = channelListView.getLastVisiblePosition();
					firstViewPos = channelListView.getFirstVisiblePosition();
					*/
					/*int index = Integer.parseInt(channelIndex.getText()
							.toString());
					curChannelIndex = index;
					*/
					curView = view;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		channelListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView channelIndex = (TextView) view
						.findViewById(R.id.chanId);
				if (lockSwap) {
					new_ChanId = Integer.parseInt(channelIndex.getText()
							.toString());

					if (new_ChanId == old_chanId) {
						Toast.makeText(ChannelList.this, "不能与本身位置交换",
								Toast.LENGTH_SHORT).show();
						lockSwap = false;
						return;
					}
					objApplication.dvbDatabase.swapChannels(old_chanId,
							new_ChanId);
					getAllTVtype(curType);
					showChannelList();
					lockSwap = false;
					Toast.makeText(ChannelList.this, "交换成功", Toast.LENGTH_SHORT)
							.show();

					return;

				}
				int index = Integer.parseInt(channelIndex.getText().toString());
				Log.i("xbtest", String.valueOf(index));
				objApplication.playChannel(index, true);
			}
		});
	}

	static class PF_Handler extends Handler {
		WeakReference<ChannelList> mActivity;

		PF_Handler(ChannelList activity) {
			mActivity = new WeakReference<ChannelList>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			final ChannelList theActivity = mActivity.get();
			switch (msg.what) {
			case HANDLE_MSG_CLOSE_CHANNELLIST:
				theActivity.finish();
				break;
			}
		}

	}

	private Channel toNextChannel(int curType, Channel preChannel) {
		List<Channel> channels = null;
		switch (curType) {
		case 0:
			channels = allTvList;
			break;

		case 1:
			channels = CCTVList;
			break;
		case 2:
			channels = starTvList;
			break;
		case 3:
			channels = localTvList;
			break;
		case 4:
			channels = HDTvList;
			break;
		case 5:
			channels = favTvList;
			break;
		case 6:
			channels = otherTvList;
			break;
		default:
			break;
		}
		Channel curChannel = null;
		if (channels != null && channels.size() > 0) {
			if (channels.size() == 1) {
				return curChannel;
			}
			for (int i = 0; i < channels.size(); i++) {

				if (channels.get(i).chanId == preChannel.chanId) {

					if (i == channels.size() - 1) {
						curChannel = channels.get(i - 1);
					} else {
						curChannel = channels.get(i + 1);
					}
					break;

				}
			}
		}
		return curChannel;
	}

	private PopupWindow skipWindow;
	private View skipView;

	private ListView skipListView;
	SimpleAdapter adapter = null;
	List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	private List<HashMap<String, String>> getSkipData() {
		list.clear();
		Channel[] channels = objApplication.dvbDatabase.getChannelsAllSC();
		for (Channel channel : channels) {
			if (channel.skip == 1) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("id", "" + channel.chanId);
				map.put("name", channel.name);
				map.put("serviceid", Utils.formatServiceId(channel.serviceId));
				list.add(map);
			}
		}
		return list;
	}

	private void showPopWindow() {

		skipView = getLayoutInflater().inflate(R.layout.skipchannels_pop, null);
		skipListView = (ListView) skipView.findViewById(R.id.pop_listview);

		adapter = new SimpleAdapter(this, getSkipData(), R.layout.channelitem,
				new String[] { "id", "name", "serviceid" }, new int[] {
						R.id.chanId, R.id.chanName, R.id.chanIndex });
		skipListView.setAdapter(adapter);
		skipListView.setOnItemClickListener(skipItemClickListener);
		skipWindow = new PopupWindow(skipView, dip2px(280), 720);
		skipWindow.setFocusable(true);
		skipWindow.setOutsideTouchable(true);
		skipWindow.setBackgroundDrawable(new BitmapDrawable());

		skipWindow.setAnimationStyle(R.style.pop_anim);

		skipWindow.showAtLocation(getWindow().getDecorView(), Gravity.LEFT,
				dip2px(280), 0);
		setAlpha(0.7f);
		skipWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				setAlpha(1.0f);
			}
		});
	}

	private void setAlpha(float f) {

		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.alpha = f;

		getWindow().setAttributes(params);
	}

	OnItemClickListener skipItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			Toast.makeText(ChannelList.this,
					"添加" + list.get(position).get("name") + "到频道列表中",
					Toast.LENGTH_SHORT).show();
			String channelid = list.get(position).get("id");
			Log.i("xm", channelid);
			objApplication.dvbDatabase
					.updateChannel(Integer.parseInt(channelid), "skip",
							String.valueOf(UNSKIP));
			adapter = new SimpleAdapter(ChannelList.this, getSkipData(),
					R.layout.channelitem, new String[] { "id", "name",
							"serviceid" }, new int[] { R.id.chanId,
							R.id.chanName, R.id.chanIndex });
			skipListView.setAdapter(adapter);
			// adapter.notifyDataSetChanged();
			getAllTVtype(curType);
			showChannelList();

		}
	};

	public int dip2px(float dipValue) {

		float scale = getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean bKeyUsed = false;
		// TODO Auto-generated method stub
		Channel channel;
		TextView chanView;
		String dialogButtonTextOk = ChannelList.this
				.getString(R.string.str_zhn_yes);
		String dialogButtonTextCancel = ChannelList.this
				.getString(R.string.str_zhn_no);
		String dialogSkipTitle = ChannelList.this
				.getString(R.string.str_zhn_skiptitle);
		String dialogSkipMess = ChannelList.this
				.getString(R.string.str_zhn_skipmessage);
		switch (keyCode) {
		/*
		 * case KeyEvent.KEYCODE_F4: { // 交换节目排序 chanView = (TextView)
		 * curView.findViewById(R.id.chanId); Toast.makeText(ChannelList.this,
		 * ChannelList.this.getString(R.string.str_swap),
		 * Toast.LENGTH_LONG).show(); lockSwap = true; old_chanId =
		 * Integer.parseInt(chanView.getText().toString()); break; } case
		 * KeyEvent.KEYCODE_F3: { // f3键将显示出跳过的节目，可以在列表中取消节目跳过功能
		 * 
		 * showPopWindow(); break; } case KeyEvent.KEYCODE_F2: { //
		 * F2键跳过显示节目,在这里每次取chanview为了避免发生偶尔取不到值的情况 chanView = (TextView)
		 * curView.findViewById(R.id.chanId); final int skipId =
		 * Integer.parseInt(chanView.getText().toString()); AlertDialog.Builder
		 * builder = new AlertDialog.Builder( ChannelList.this);
		 * builder.setTitle(dialogSkipTitle);
		 * builder.setMessage(dialogSkipMess);
		 * builder.setPositiveButton(dialogButtonTextOk, new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) { //
		 * TODO Auto-generated method stub
		 * 
		 * objApplication.dvbDatabase.updateChannel(skipId, "skip",
		 * String.valueOf(SKIP)); // 刷新界面，notifydatasetchanged Channel channel =
		 * objApplication.dvbDatabase .getChannel(skipId);
		 * 
		 * Channel nextChannel = toNextChannel(curType, channel);
		 * getAllTVtype(curType); showChannelList(); if (nextChannel == null) {
		 * 
		 * Intent mIntent = new Intent(ChannelList.this, DialogNotice.class);
		 * mIntent.putExtra(Class_Constant.DIALOG_TITLE, "无节目");
		 * mIntent.putExtra(Class_Constant.DIALOG_DETAILS, "没有节目！");
		 * mIntent.putExtra( Class_Constant.DIALOG_BUTTON_NUM, 1);
		 * startActivity(mIntent); } else {
		 * objApplication.playChannel(nextChannel, false); } } });
		 * builder.setNegativeButton(dialogButtonTextCancel, new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) { //
		 * TODO Auto-generated method stub dialog.dismiss(); } });
		 * builder.create().show(); break; } case KeyEvent.KEYCODE_F1: { //
		 * 绿色的按键删除节目 TextView chanIdView = (TextView)
		 * curView.findViewById(R.id.chanId); Channel curChannel =
		 * objApplication.dvbDatabase.getChannel(Integer
		 * .parseInt(chanIdView.getText().toString())); String dialogTitle =
		 * ChannelList.this .getString(R.string.str_remove_title); String
		 * dialogContent = ChannelList.this
		 * .getString(R.string.str_remove_content1); dialogContent += "\"" +
		 * curChannel.name + "\"\n"; dialogContent += ChannelList.this
		 * .getString(R.string.str_remove_content2); String
		 * dialogButtonTextRemove = ChannelList.this
		 * .getString(R.string.str_edit_del); String dialogButtonTextRmain =
		 * ChannelList.this .getString(R.string.str_zhn_no);
		 * 
		 * AlertDialog.Builder removeDialog = new AlertDialog.Builder(
		 * ChannelList.this); removeDialog.setTitle(dialogTitle);
		 * removeDialog.setMessage(dialogContent);
		 * 
		 * removeDialog .setPositiveButton(dialogButtonTextRemove, new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) { //
		 * TODO Auto-generated method stub lockSwap = false; TextView chanView =
		 * (TextView) curView .findViewById(R.id.chanId); int channelIdToDelete
		 * = Integer .parseInt(chanView.getText() .toString()); Channel channel
		 * = objApplication.dvbDatabase .getChannel(channelIdToDelete); Channel
		 * nextChannel = toNextChannel( curType, channel); channelIdToDelete =
		 * channel.chanId; objApplication.dvbDatabase
		 * .removeChannel(channel.chanId); getAllTVtype(curType);
		 * showChannelList();
		 * 
		 * if (nextChannel == null) {
		 * 
		 * Intent mIntent = new Intent( ChannelList.this, DialogNotice.class);
		 * mIntent.putExtra( Class_Constant.DIALOG_TITLE, "无节目");
		 * mIntent.putExtra( Class_Constant.DIALOG_DETAILS, "没有节目！");
		 * mIntent.putExtra( Class_Constant.DIALOG_BUTTON_NUM, 1);
		 * startActivity(mIntent); } else {
		 * objApplication.playChannel(nextChannel, false); } } })
		 * .setNegativeButton(dialogButtonTextRmain, new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) { //
		 * TODO Auto-generated method stub dialog.dismiss(); }
		 * }).create().show();
		 * 
		 * break; } case Class_Constant.KEYCODE_MENU_KEY: { // add favourite
		 * channel chanView = (TextView) curView.findViewById(R.id.chanId);
		 * Common.LOGI("the key is menu key"); channel =
		 * objApplication.dvbDatabase.getChannel(Integer
		 * .parseInt(chanView.getText().toString())); // if (curType == 6) { if
		 * (curType == 5) {
		 * 
		 * channel = objApplication.dvbDatabase.getChannel(Integer
		 * .parseInt(chanView.getText().toString())); // favorite channel,click
		 * menu key is remove the channel
		 * objApplication.dvbDatabase.updateChannel(channel.chanId, "favorite",
		 * "0"); getAllTVtype(curType); showChannelList(); } else { // other
		 * type channel,click menu key is add to fav channel
		 * 
		 * ImageView favView = (ImageView) curView
		 * .findViewById(R.id.chan_image); if (channel.favorite == 0) { //
		 * channel.mi_Favorite=1;
		 * favView.setBackgroundResource(R.drawable.listfav);
		 * favView.setVisibility(View.VISIBLE);
		 * objApplication.dvbDatabase.updateChannel(channel.chanId, "favorite",
		 * "1"); } else { // channel.mi_Favorite=0;
		 * favView.setVisibility(View.INVISIBLE);
		 * objApplication.dvbDatabase.updateChannel(channel.chanId, "favorite",
		 * "0"); }
		 * 
		 * }
		 * 
		 * // showChannelList(); break; }
		 */
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:

			if (curType == 6) {
				curType = 0;
			} else {
				curType++;
			}
			getAllTVtype(curType);
			showChannelList();
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:

			if (curType == 0) {
				curType = 6;
			} else {
				curType--;
			}
			getAllTVtype(curType);
			showChannelList();
			break;
		case Class_Constant.KEYCODE_UP_ARROW_KEY:
		case Class_Constant.KEYCODE_PAGE_UP:
			/*
			 * 歌华不允许焦点循环 if (curListIndex == 0) { getAllTVtype(curType);
			 * showChannelList();
			 * channelListView.setSelection(channelListView.getCount() - 1); }
			 */
			break;
		case Class_Constant.KEYCODE_DOWN_ARROW_KEY:
		case Class_Constant.KEYCODE_PAGE_DOWN:
			/*
			 * 歌华不允许焦点循环 if (curListIndex == (channelListView.getCount() - 1)) {
			 * getAllTVtype(curType); showChannelList();
			 * channelListView.setSelection(0); }
			 */
			break;
		case Class_Constant.KEYCODE_CHANNEL_UP:
			/*
			 * getAllTVtype(curType); showChannelList(); if (curListIndex == 0)
			 * { channelListView.setSelection(channelListView.getCount() - 1); }
			 * else { channelListView.setSelection(curListIndex - 1); }
			 */
			break;
		case Class_Constant.KEYCODE_CHANNEL_DOWN:
			/*
			 * getAllTVtype(curType); showChannelList(); if (curListIndex ==
			 * (channelListView.getCount() - 1)) {
			 * channelListView.setSelection(0); } else {
			 * channelListView.setSelection(curListIndex + 1); }
			 */
			break;
		case Class_Constant.KEYCODE_KEY_DIGIT0:
		case Class_Constant.KEYCODE_KEY_DIGIT1:
		case Class_Constant.KEYCODE_KEY_DIGIT2:
		case Class_Constant.KEYCODE_KEY_DIGIT3:
		case Class_Constant.KEYCODE_KEY_DIGIT4:
		case Class_Constant.KEYCODE_KEY_DIGIT5:
		case Class_Constant.KEYCODE_KEY_DIGIT6:
		case Class_Constant.KEYCODE_KEY_DIGIT7:
		case Class_Constant.KEYCODE_KEY_DIGIT8:
		case Class_Constant.KEYCODE_KEY_DIGIT9: {
			onDigitalkey(keyCode);
			bKeyUsed = true;
		}			
		}

		if (bKeyUsed) {
			return bKeyUsed;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(stopReceiver);
	}

	class ChannelAdapter extends BaseAdapter {

		LayoutInflater inflater = LayoutInflater.from(context);

		class ViewHolder {
			TextView channelId;
			TextView channelIndex;
			TextView channelName;
			// ImageView favView;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.channelitem_gh, null);
				holder = new ViewHolder();
				holder.channelId = (TextView) convertView
						.findViewById(R.id.chanId);
				holder.channelIndex = (TextView) convertView
						.findViewById(R.id.chanIndex);
				holder.channelName = (TextView) convertView
						.findViewById(R.id.chanName);
				/*
				 * holder.favView = (ImageView) convertView
				 * .findViewById(R.id.chan_image);
				 */
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Channel Channel = mCurChannels.get(position);

			/*
			 * if (Channel.favorite == 0 || curType == 5) {
			 * holder.favView.setVisibility(View.INVISIBLE); } else { // show
			 * imageview for favourite channel
			 * holder.favView.setBackgroundResource(R.drawable.listfav);
			 * holder.favView.setVisibility(View.VISIBLE); }
			 */

			if (Channel.logicNo < 10) {
				holder.channelIndex.setText("00" + Channel.logicNo);
			} else if (Channel.logicNo < 100) {
				holder.channelIndex.setText("0" + Channel.logicNo);
			} else {
				holder.channelIndex.setText("" + Channel.logicNo);
			}
			holder.channelIndex.setTextColor(0xffffff00);

			if (Channel.chanId < 10) {
				holder.channelId.setText("00" + Channel.chanId);
			} else if (Channel.chanId < 100) {
				holder.channelId.setText("0" + Channel.chanId);
			} else {
				holder.channelId.setText("" + Channel.chanId);
			}
			holder.channelId.setTextColor(0xffffff00);

			holder.channelName.setText("" + Channel.name);
			return convertView;
		}

		private void updateView() {
			notifyDataSetChanged();
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mCurChannels.size();
		}
	};

	@Override
	public void onExecute(Intent intent) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		feedback.begin(intent);
		if (intent.hasExtra("_scene")
				&& intent.getStringExtra("_scene").equals(
						"com.changhong.app.dtv:ChannelList")) {
			if (intent.hasExtra("_command")) {
				String command = intent.getStringExtra("_command");
				if ("key1".equals(command)) {
					feedback.feedback("下一个频道", Feedback.SILENCE);
					// 频道切换处理
					changenextchannel();
				}
				if ("key2".equals(command)) {
					feedback.feedback("上一个频道", Feedback.SILENCE);
					// 频道切换处理
					changeprechannel();
				}
			}
		}

	}

	@Override
	public String onQuery() {
		// TODO Auto-generated method stub
		sceneJson = "{" + "\"_scene\": \"com.changhong.app.dtv:ChannelList\","
				+ "\"_commands\": {" + "\"key1\": [ \"下一个频道\", \"频道加\" ],"
				+ "\"key2\": [ \"上一个频道\", \"频道减\" ]" + "}" + "}";
		return sceneJson;
	}

	private String INTENT_CHANNEL_NEXT = "channelnext";
	private String INTENT_CHANNEL_PRE = "channelpre";
	private String INTENT_CHANNEL_ONLYINFO = "channelonlyinfo";

	private void changenextchannel() {
		if ((SysApplication.iCurChannelId != -1)
				&& (objApplication.dvbDatabase.getChannelCount() >= 0)) {
			objApplication.playNextChannel(true);
			banner.show(SysApplication.iCurChannelId);
			finish();
		}
	}

	private Banner banner;
	private int iKeyNum;
	private int iKey;
	LinearLayout tvRootDigitalkey;
	private RelativeLayout tvRootDigitalKeyInvalid;
	private final UI_Handler mUiHandler = new UI_Handler(this);
	private static final int MESSAGE_SHOW_AUTOSEARCH = 201;
	private static final int MESSAGE_HANDLER_DIGITALKEY = 202;
	private static final int MESSAGE_DISAPPEAR_DIGITAL = 203;
	private static final int MESSAGE_CA_SHOWNOTICE = 204;
	private static final int MESSAGE_CA_HIDENOTICE = 205;
	private static final int MESSAGE_SHOW_DIGITALKEY = 206;
	private static final int MESSAGE_START_RECORD = 207;
	private static final int MESSAGE_STOP_RECORD = 208;
	private static final int MESSAGE_PLAY_PRE = 301;
	private static final int MESSAGE_PLAY_NEXT = 302;
	private static final int MESSAGE_VOLUME_DISAPPEAR = 902;
	private static final int MESSAGE_VOLUME_SHOW = 901;
	private static final int MESSAGE_SHOW_DIGITALKEY_FOR_PRE_OR_NEXT_KEY = 903;
	private static final int VOLUME_ID_YIHAN_AD = 904;
	private ImageView dtv_digital_1;
	private ImageView dtv_digital_2;
	private ImageView dtv_digital_3;
	
	private void changeprechannel() {
		if ((SysApplication.iCurChannelId != -1)
				&& (objApplication.dvbDatabase.getChannelCount() >= 0)) {
			objApplication.playPreChannel(true);
			banner.show(SysApplication.iCurChannelId);
			finish();
		}
	}

	private void onDigitalkey(int ri_KeyCode) {

		boolean b_Result = false;
		Common.LOGD(" main->onVkey: " + ri_KeyCode);

		mUiHandler.removeMessages(MESSAGE_SHOW_DIGITALKEY);
		mUiHandler.removeMessages(MESSAGE_DISAPPEAR_DIGITAL);

		iKeyNum++;
		Common.LOGI("onVkey-key<" + iKey + ">");

		if (iKeyNum > 0 && iKeyNum <= 3) {
			iKey = (ri_KeyCode - Class_Constant.KEYCODE_KEY_DIGIT0) + iKey * 10;
		}

		Common.LOGI("get digital key   =  " + ri_KeyCode);
		Common.LOGI("iKey value   =  " + iKey);

		// tvRootDigitalKeyInvalid.setVisibility(View.GONE);
		tvRootDigitalkey.setVisibility(View.VISIBLE);

		Display_Program_Num(iKey);

		if (iKey >= 100) {
			mUiHandler
					.sendEmptyMessageDelayed(MESSAGE_HANDLER_DIGITALKEY, 2000);
		} else {
			mUiHandler
					.sendEmptyMessageDelayed(MESSAGE_HANDLER_DIGITALKEY, 4000);
		}

	}

	private void Display_Program_Num(int channelId) {

		int num[] = {R.drawable.num_0,R.drawable.num_1,R.drawable.num_2,R.drawable.num_3,R.drawable.num_4,R.drawable.num_5,
				R.drawable.num_6,R.drawable.num_7,R.drawable.num_8,R.drawable.num_9};
		int pos1,pos2,pos3;
		
		pos1 = channelId/100;
		pos2 = (channelId-pos1*100)/10;
		pos3 = (channelId-pos1*100-pos2*10);
		
		dtv_digital_1.setBackgroundResource(num[pos1]);
		dtv_digital_2.setBackgroundResource(num[pos2]);
		dtv_digital_3.setBackgroundResource(num[pos3]);

	}
	public static class UI_Handler extends Handler {
		
		WeakReference<ChannelList> mActivity;

		UI_Handler(ChannelList activity) {
			mActivity = new WeakReference<ChannelList>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final ChannelList theActivity = mActivity.get();

			switch (msg.what) {
			
			case MESSAGE_HANDLER_DIGITALKEY: {				
				
				if (theActivity.iKey < 0) {
					theActivity.tvRootDigitalkey.setVisibility(View.INVISIBLE);
					
					
					theActivity.tvRootDigitalKeyInvalid
							.setVisibility(View.VISIBLE);

					theActivity.mUiHandler
							.removeMessages(MESSAGE_HANDLER_DIGITALKEY);
					theActivity.mUiHandler.sendEmptyMessageDelayed(
							MESSAGE_DISAPPEAR_DIGITAL, 1000);

					theActivity.iKeyNum = 0;
					theActivity.iKey = 0;
				} else {
										
					int succ = theActivity.objApplication.playChannelByLogicNo(
							theActivity.iKey, true);
					// int succ =
					// theActivity.objApplication.playChannelKeyInput(theActivity.iKey,true);
					if (succ < 0) {
						theActivity.tvRootDigitalkey
								.setVisibility(View.INVISIBLE);
						theActivity.tvRootDigitalKeyInvalid
								.setVisibility(View.VISIBLE);
						//theActivity.id_dtv_channel_name.setVisibility(View.INVISIBLE);
					} else {

						
						//if(	theActivity.volume_layout.getVisibility()==View.VISIBLE)
						//	theActivity.volume_layout.setVisibility(View.INVISIBLE);

						//theActivity.banner.show(SysApplication.iCurChannelId);
						updateChanListInfo(theActivity.iKey);
	
						Message msg2 = new Message();
						msg2.what = MESSAGE_SHOW_DIGITALKEY;
						msg2.arg1 = theActivity.iKey;
						sendMessage(msg2);
						
						
						
					}
					theActivity.iKeyNum = 0;
					theActivity.iKey = 0;
					theActivity.mUiHandler
							.removeMessages(MESSAGE_HANDLER_DIGITALKEY);
					theActivity.mUiHandler.sendEmptyMessageDelayed(
							MESSAGE_DISAPPEAR_DIGITAL, 2000);
				}

			}
				break;

			case MESSAGE_SHOW_DIGITALKEY:
				

				theActivity.tvRootDigitalkey.setVisibility(View.VISIBLE);
				
				int channelId = msg.arg1;

				Log.i("zhougang  main",
						"MESSAGE_SHOW_DIGITALKEY   -----channelId-------theActivity.UpOrDownIsPressed " + channelId+"   "+theActivity.tvRootDigitalkey.getVisibility());
				//theActivity.tvRootDigitalKeyInvalid.setVisibility(View.GONE);

				theActivity.Display_Program_Num(channelId);

				/*
				Channel tempChannel_z3;

				 tempChannel_z3 = theActivity.objApplication.dvbDatabase
						.getChannelSC(SysApplication.iCurChannelId);
				theActivity.id_dtv_channel_name.setText(tempChannel_z3.name);
				theActivity.id_dtv_channel_name.setVisibility(View.VISIBLE);
				*/
				
		
				theActivity.mUiHandler
						.removeMessages(MESSAGE_DISAPPEAR_DIGITAL);
				theActivity.mUiHandler.sendEmptyMessageDelayed(
						MESSAGE_DISAPPEAR_DIGITAL, 3500);
				break;
				
				
				

			case MESSAGE_SHOW_DIGITALKEY_FOR_PRE_OR_NEXT_KEY:
				int channelId2 = msg.arg1;

				Log.i("zhougang  main",
						"MESSAGE_SHOW_DIGITALKEY   -----channelId------- " + channelId2+"   ");
				theActivity.tvRootDigitalKeyInvalid.setVisibility(View.GONE);
				
				if(theActivity.tvRootDigitalkey.getVisibility()==View.INVISIBLE)
						theActivity.tvRootDigitalkey.setVisibility(View.VISIBLE);

				theActivity.Display_Program_Num(channelId2);


				/*
				Channel tempChannel_z4;
	
					 tempChannel_z4 = theActivity.objApplication.dvbDatabase
							.getChannelSC(theActivity.tempChannelID);
					theActivity.id_dtv_channel_name.setText(tempChannel_z4.name);
					theActivity.id_dtv_channel_name.setVisibility(View.VISIBLE);
					*/

				theActivity.mUiHandler
						.removeMessages(MESSAGE_DISAPPEAR_DIGITAL);
				theActivity.mUiHandler.sendEmptyMessageDelayed(
						MESSAGE_DISAPPEAR_DIGITAL, 3500);
				break;


			case MESSAGE_DISAPPEAR_DIGITAL: {
				theActivity.iKey = 0;
				if (theActivity.tvRootDigitalKeyInvalid != null) {
					theActivity.tvRootDigitalKeyInvalid
							.setVisibility(View.INVISIBLE);
				}
				if (theActivity.tvRootDigitalkey != null) {
					theActivity.tvRootDigitalkey.setVisibility(View.INVISIBLE);
				}

				//theActivity.id_dtv_channel_name.setVisibility(View.INVISIBLE);
			}
				break;


			}
		}

		private void updateChanListInfo(int iKey) {
			final ChannelList theActivity = mActivity.get();
			
			//首先从当前类型分类查找输入频道
			for (int i=0;i<theActivity.mCurChannels.size();i++) {
				if(theActivity.mCurChannels.get(i).logicNo == iKey)
				{
					theActivity.channelListView.setSelection(i);
					theActivity.mAdapter.notifyDataSetChanged();
					Log.i(TAG,"Found/cur>> input:"+iKey+", in curType:"+theActivity.curType+",pos:"+i);
					return ;
				}
				
			}	
			
			//从全部频道分类中查找输入频道
			if(theActivity.curType!=0){
				theActivity.getAllTVtype(0);
				for (int i=0;i<theActivity.allTvList.size();i++) {
					if(theActivity.allTvList.get(i).logicNo == iKey)
					{
						theActivity.curType = 0;
						theActivity.showChannelList();
						theActivity.channelListView.setSelection(i);
						theActivity.mAdapter.notifyDataSetChanged();
						Log.i(TAG,"Found/other>> input:"+iKey+", in curType:"+theActivity.curType+",pos:"+i);
						return ;
					}
				}
			}
		}
	}

	public void showEpgTitle(int curType2) {
		// TODO Auto-generated method stub
		int t_left=curType2-1,t_center=curType2,t_right=curType2+1;
		if(t_left<0)
			t_left = 6;
		if(t_right>6) 
			t_right = 0;
		
		epgListTitleViewLeft.setText(TVtype[t_left]);
		epgListTitleViewCenter.setText(TVtype[t_center]);
		epgListTitleViewRight.setText(TVtype[t_right]);
	}	
}
