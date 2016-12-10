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
	//private static final String TAA = "YYY";
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
	private int lastFocuPos=-1,lastLastPos=-1,lastFirstPos=-1; // channelist中失去焦点的位置
	
    private static final int DEF_PAGESIZE = 7;  //每页最大显示条数    
    private int totalCount=0;    //内容总条数   
    private int pageSize=0;    //一页显示的行数   
    private int pageIndex=0;    //当前页码,取值0~x,仅用于翻页键有效
    private int pageCount=0;    //总页数   
    private int opMode = 0;		//默认上下键单行滚动,==1 翻页操作
	private int curType = 0;
	private int curListIndex = 0;

	// all type channel
	List<Channel> favTvList = new ArrayList<Channel>();
	List<Channel> otherTvList = new ArrayList<Channel>();

	List<Channel> allTvList = new ArrayList<Channel>();
	List<Channel> CCTVList = new ArrayList<Channel>();
	List<Channel> starTvList = new ArrayList<Channel>();
	List<Channel> audList = new ArrayList<Channel>();
	List<Channel> localTvList = new ArrayList<Channel>();
	List<Channel> HDTvList = new ArrayList<Channel>();
	List<Channel> testTvList = new ArrayList<Channel>();
	List<Channel> bTvList = new ArrayList<Channel>();
	List<Channel> shopTvList = new ArrayList<Channel>();
	
	private static final int tvTypeNoMax = 8; //0~8
	/* Integer: 取值 0 全部频道all, 1:央视频道CCTV;2:北京频道BTV;3:卫视频道STV;4:高清频道HDTV;5:区县频道Area;
	6:购物频道Shopping;7:体验频道Experience;8:音频广播Radio
*/	
	boolean []bChanListAdded = new boolean[tvTypeNoMax+1];
	boolean bChanListLoad = false;

	//private static HashMap<Integer, List<Channel>> tvCategoryList = new HashMap<Integer, List<Channel>>();
	
	// animation

	// application
	SysApplication objApplication;

	private static final int HANDLE_MSG_CLOSE_CHANNELLIST = 1002;
	Class_Global obj_Global = new Class_Global();
	//private PF_Handler UI_Handler = new PF_Handler(this);

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
			/*Intent mIntent = new Intent(ChannelList.this, DialogNotice.class);
			mIntent.putExtra(Class_Constant.DIALOG_TITLE, "无节目");
			mIntent.putExtra(Class_Constant.DIALOG_DETAILS, "没有节目！");
			mIntent.putExtra(Class_Constant.DIALOG_BUTTON_NUM, 1);
			startActivity(mIntent);
			finish();*/
		}
		
		Channel channel = objApplication.dvbDatabase.getChannel(objApplication
				.getLastProgram());
		channelId = channel.chanId;
		
		//curType = getIntent().getIntExtra("curType", 0);
		curType = 0 /*channel.favorite*/;
		getAllTVtype(curType);
		registerBroadReceiver();

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
		
		IntentFilter filter2 = new IntentFilter();
		filter2.addAction("com.changhong.action.syncbanner");
		registerReceiver(showBannerForEver, filter2);
		
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
	BroadcastReceiver showBannerForEver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			int chanid = -1;
			chanid = arg1.getIntExtra("chanid", -1);
			if(chanid!=-1){
				banner.show(chanid,999999999);	
			}
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

	private int getSortIndex2(int curType, int chanId) {
		int index = 0;
		List<Channel> locList;
		switch (curType) {
		case 1: {
			locList = CCTVList;
			break;
		}
		case 2: {
			locList = bTvList;
			break;
		}
		case 3: {
			locList = starTvList;
			break;
		}
		case 4: {
			locList = HDTvList;
			break;
		}
		case 5: {
			locList = localTvList;
			break;
		}
		case 6: {
			locList = shopTvList;
			break;
		}
		case 7: {
			locList = testTvList;
			break;
		}
		case 8: {
			locList = audList;
			break;
		}
		default: {
			locList = allTvList;
			break;
		}
		}

		for (int i = 0; i < locList.size(); i++) {
			if (locList.get(i).chanId == chanId) {
				index = i;
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
			
		if(bChanListLoad)
			return ;
		
		bChanListLoad = true;
		
		Channel[] Channels = objApplication.dvbDatabase.getChannelsAllSC();
		
		allTvList.clear();
		CCTVList.clear();
		bTvList.clear();
		starTvList.clear();
		HDTvList.clear();
		localTvList.clear();
		shopTvList.clear();
		testTvList.clear();
		audList.clear();
		
		for (Channel chan : Channels) {
			/*
			allTvList.add(chan);				// all tv type;
			switch(chan.favorite){
				case 1: {CCTVList.add(chan);break;}
				case 2: {bTvList.add(chan);break;}
				case 3: {starTvList.add(chan);break;}
				case 4: {HDTvList.add(chan);break;}
				case 5: {localTvList.add(chan);break;}
				case 6: {shopTvList.add(chan);break;}
				case 7: {testTvList.add(chan);break;}
				case 8: {audList.add(chan);break;}
				default:break;		
			}
			Log.i("mmmm","dtv: channel>> id="+chan.chanId+",logicNo="+chan.logicNo+",categoryId="+chan.favorite+",name= "+chan.name);
			*/
			if(checkValidofAVChannel(chan.sortId)){
				allTvList.add(chan);				// all tv type;
			if((chan.favorite&(1<<1))!=0){
				CCTVList.add(chan);
			}
			if((chan.favorite&(1<<2))!=0){
				bTvList.add(chan);
			}
			if((chan.favorite&(1<<3))!=0){
				starTvList.add(chan);
			}
			if((chan.favorite&(1<<4))!=0){
				HDTvList.add(chan);
			}
			if((chan.favorite&(1<<5))!=0){
				localTvList.add(chan);
			}
			if((chan.favorite&(1<<6))!=0){
				shopTvList.add(chan);
			}
			if((chan.favorite&(1<<7))!=0){ 
				testTvList.add(chan);
			}
			if((chan.favorite&(1<<8))!=0){
				audList.add(chan);
			}	
			}	
		}
		Log.i(TAG,"getAllTVtype>>>>");
		
	}
	
	private boolean checkValidofAVChannel(int sortId) {
		if(sortId==0x1||sortId==0x2||sortId>=0xe3&&sortId<=0xe9){
			return true;
		}else {
			return false;
		}
	}

	private void getAllTVtype_NoUsed(int index) {
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
		case 1:
			curChannels = CCTVList;
			break;
		case 2:
			curChannels = bTvList;
			break;
		case 3:
			curChannels = starTvList;
			break;
		case 4:
			curChannels = HDTvList;
			break;
		case 5:
			curChannels = localTvList;
			break;
		case 6:
			curChannels = shopTvList;
			break;
		case 7:
			curChannels = testTvList;
			break;
		case 8:
			curChannels = audList;
			break;	
		default:
			curChannels = allTvList;
			break;			
		}
		mCurChannels = curChannels;
		if (mAdapter == null) {
			mAdapter = new ChannelAdapter();
			channelListView.setAdapter(mAdapter);
		} else {
			if(channelListView.getSelectedItemPosition()!=0) //不作如此处理，在切换频道类型时,listview有可能认为当前Selection没变化从而不会去执行onItemSelected
			{
				channelListView.setSelection(0);	
			}else {
				channelListView.setSelection(1);
			}
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
		channelListView.setSelection(getSortIndex2(curType,channelId));
		request_update_banner(channelId);
		channelListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO show the select channel
				curListIndex = position+pageIndex*pageSize;
				//Log.i(TAA, "onItemSelected>>>view="+view+",pos="+position+",id="+id);
				// int[] pos = { -1, -1 };
				if (view != null) {
					int itemCnt = channelListView.getLastVisiblePosition()-channelListView.getFirstVisiblePosition();
					// view.getLocationOnScreen(pos);
					dislistfocus((FrameLayout) view);  
					
					TextView channelIndex = (TextView) view.findViewById(R.id.chanIndex);
					channelIndex.setTextColor(0xffffffff);					
					TextView channelName = (TextView) view.findViewById(R.id.chanName);
					channelName.setTextColor(0xffffffff);
					/*
					Channel chan = mCurChannels.get(curListIndex);
					if(chan!=null){
						request_update_banner(chan.chanId);
					}*/
					
					//updateBannerinfo(curListIndex);
					
					/*Log.i(TAG,"bNewScreen="+bNewScreen+",curPos:"+position+",lastFocuPos:"+lastFocuPos
							+",last:"+lastLastPos+" vs "+channelListView.getLastVisiblePosition()+
							",first:"+lastFirstPos +"vs "+channelListView.getFirstVisiblePosition());*/
					
					if(!bNewScreen){		
						if(channelListView.getFirstVisiblePosition()>lastFirstPos){
							lastFocuPos -=1;
						}else if(channelListView.getFirstVisiblePosition()<lastFirstPos){
							lastFocuPos +=1;
						}
						View pre_vi=null;
						try {
							pre_vi = channelListView.getChildAt(lastFocuPos); 
						} catch (Exception e) {
							Log.i(TAG,"can't find pre_item");
							pre_vi = null;
						}
						if(pre_vi!=null/* && ((curListIndex%DEF_PAGESIZE)!=(lastFirstPos%DEF_PAGESIZE))*/){
							//Log.i(TAG, "onItemSelected>>>>"+curListIndex%DEF_PAGESIZE + " vs "+lastFirstPos%DEF_PAGESIZE);
							TextView channelIndex2 = (TextView) pre_vi.findViewById(R.id.chanIndex);
							channelIndex2.setTextColor(0xffa0a2a4);					
							TextView channelName2 = (TextView) pre_vi.findViewById(R.id.chanName);
							channelName2.setTextColor(0xffa0a2a4);		
						}						
					}
					bNewScreen = false;
					lastFocuPos = position - channelListView.getFirstVisiblePosition();
					lastLastPos = channelListView.getLastVisiblePosition();
					lastFirstPos = channelListView.getFirstVisiblePosition();
					/*					
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
				//Log.i(TAA, "onNothingSelected >>>view=");
			}
		});
		channelListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//Log.i(TAA, "onItemClick>>>view="+view+",pos="+position+",id="+id);
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
				request_update_banner(index);
				finish();//退出列表
			}
		});
	}
/*
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
*/
	private Channel toNextChannel_NoUsed(int curType, Channel preChannel) {
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

	@SuppressWarnings("deprecation")
	private void showPopWindow() {

		//YBDEL skipView = getLayoutInflater().inflate(R.layout.skipchannels_pop, null);
		//YBDEL skipListView = (ListView) skipView.findViewById(R.id.pop_listview);

		adapter = new SimpleAdapter(this, getSkipData(), R.layout.channelitem_gh,
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
					R.layout.channelitem_gh, new String[] { "id", "name",
							"serviceid" }, new int[] { R.id.chanId,
							R.id.chanName, R.id.chanIndex });
			skipListView.setAdapter(adapter);
			// adapter.notifyDataSetChanged();
			getAllTVtype(curType);
			showChannelList();

		}
	};
	public boolean bNewScreen=true;

	public int dip2px(float dipValue) {

		float scale = getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean bKeyUsed = false;
		// TODO Auto-generated method stub
			
		Log.i(TAG, "key>>"+keyCode); 
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

			if (curType == tvTypeNoMax) {
				curType = 0;
			} else {
				curType++;
			}
			getAllTVtype(curType);
			resetPageData();
			showChannelList();
			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:

			if (curType == 0) {
				curType = tvTypeNoMax;
			} else {
				curType--;
			}
			getAllTVtype(curType);
			resetPageData();
			showChannelList();
			break;
		case Class_Constant.KEYCODE_UP_ARROW_KEY:

			/*
			 * 歌华不允许焦点循环 if (curListIndex == 0) { getAllTVtype(curType);
			 * showChannelList();
			 * channelListView.setSelection(channelListView.getCount() - 1); }
			 */
			ToOneLineOfPrePage();
			break;
		case Class_Constant.KEYCODE_DOWN_ARROW_KEY:
			/*
			 * 歌华不允许焦点循环 if (curListIndex == (channelListView.getCount() - 1)) {
			 * getAllTVtype(curType); showChannelList();
			 * channelListView.setSelection(0); }
			 */
			ToOneLineOfNextPage();
			break;

		case Class_Constant.KEYCODE_CHANNEL_UP:
			/*
			 * getAllTVtype(curType); showChannelList(); if (curListIndex == 0)
			 * { channelListView.setSelection(channelListView.getCount() - 1); }
			 * else { channelListView.setSelection(curListIndex - 1); }
			 */
			nextPage();
			break;
		case Class_Constant.KEYCODE_CHANNEL_DOWN:
			/*
			 * getAllTVtype(curType); showChannelList(); if (curListIndex ==
			 * (channelListView.getCount() - 1)) {
			 * channelListView.setSelection(0); } else {
			 * channelListView.setSelection(curListIndex + 1); }
			 */
			prePage();
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
		break;
		case Class_Constant.KEYCODE_OK_KEY:
		case KeyEvent.KEYCODE_ENTER: {
			P.i("OK key>>iKeyNum="+iKeyNum+"status:"+tvRootDigitalkey.isShown());
			if (iKeyNum!=0 && tvRootDigitalkey.isShown())
			{//如果数字键存在，则响应为快速切换到数字指定的频道
				mUiHandler.sendEmptyMessageDelayed(
						MESSAGE_HANDLER_DIGITALKEY, 10);	
				bKeyUsed = true;
			}		
		}
		break;
		case Class_Constant.KEYCODE_BACK_KEY:
			banner.cancel(); 
			break;
		default:
			break;
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
		unregisterReceiver(showBannerForEver);
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
		public void notifyDataSetChanged(){
			bNewScreen = true;
			super.notifyDataSetChanged();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			//Log.i(TAA, "getView>>>view="+convertView+",pos="+position);
			int progIndex=position+pageIndex*pageSize;
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
			
			Log.i(TAG, "getView>>>>>pos:"+ progIndex+","+position+"/"+pageIndex+"/"+pageSize);
			
			if(progIndex>=mCurChannels.size()){
				Log.i(TAG, "getView>>>>>invail pos:"+ progIndex+","+position+"/"+pageIndex+"/"+pageSize);
				progIndex = mCurChannels.size()-1;
				return null;
			}else if(progIndex<0){
				Log.i(TAG, "getView>>>>>invail pos:"+ progIndex+","+position+"/"+pageIndex+"/"+pageSize);				
				progIndex = 0;				
				return null;
			}
			
			Channel Channel = mCurChannels.get(progIndex); 

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
			holder.channelIndex.setTextColor(0xffa0a2a4);

			if (Channel.chanId < 10) {
				holder.channelId.setText("00" + Channel.chanId);
			} else if (Channel.chanId < 100) {
				holder.channelId.setText("0" + Channel.chanId);
			} else {
				holder.channelId.setText("" + Channel.chanId);
			}
			
			holder.channelName.setTextColor(0xffa0a2a4);
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
			return position;
		}

		   /** 
	     * ListView通过此方法获知要显示多少行内容 
	     * 我们即在此方法下手，每次设置一页需要显示的行数 
	     * 返回值：ListView 要显示的行数 
	     */  
		@Override
	    public int getCount()  
	    {  
			int retValue=0,type=0;
			if(pageSize==0){
				type = 0;
				retValue= mCurChannels.size();
			}
			else if(totalCount < pageSize)
	        {//如果总行数小于一页显示的行数，返回总行数    
				type=1;
				retValue= totalCount;  
	        }   
	        else if(totalCount < (pageIndex+1)*pageSize)  
	        {//即最后一页不足5行（页面行数）  
	        	type=2;
	        	retValue= (totalCount-pageIndex*pageSize);  
	        }else    //其他情况返回页面尺寸   
	        {  
	        	type=3;
	        	retValue= pageSize;  
	        }  
			Log.i(TAG, "getCount>>>type:"+type+",num:"+retValue);
			return retValue;
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

	private static Banner banner;
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
	private static final int MESSAGE_REQ_UPBANNER = 801;
	
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
		P.d(" main->onVkey: " + ri_KeyCode);

		mUiHandler.removeMessages(MESSAGE_SHOW_DIGITALKEY);
		mUiHandler.removeMessages(MESSAGE_DISAPPEAR_DIGITAL);

		iKeyNum++;
		P.i("onVkey-key<" + iKey + ">");

		if (iKeyNum > 0 && iKeyNum <= 3) {
			iKey = (ri_KeyCode - Class_Constant.KEYCODE_KEY_DIGIT0) + iKey * 10;
		}

		P.i("get digital key   =  " + ri_KeyCode);
		P.i("iKey value   =  " + iKey);

		// tvRootDigitalKeyInvalid.setVisibility(View.GONE);
		tvRootDigitalkey.setVisibility(View.VISIBLE);

		Display_Program_Num(iKey);
		
		mUiHandler
		.sendEmptyMessageDelayed(MESSAGE_HANDLER_DIGITALKEY, 1500);//要求1.5秒无操作则执行		
		/*
		if (iKey >= 100) {
			mUiHandler
					.sendEmptyMessageDelayed(MESSAGE_HANDLER_DIGITALKEY, 2000);
		} else {
			mUiHandler
					.sendEmptyMessageDelayed(MESSAGE_HANDLER_DIGITALKEY, 2000);
		}*/

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
	private void request_update_banner(int postion){
		mUiHandler.removeMessages(MESSAGE_REQ_UPBANNER);
		Message msg = new Message();
		msg.what = MESSAGE_REQ_UPBANNER;
		msg.arg1 = postion;
		mUiHandler.sendMessage(msg);		
	}
	/**
	 * @param position
	 */
	private static void updateBannerinfo2(int chanid) {
		banner.show(chanid,999999999);	
	}	
	private void updateBannerinfo3(int position) {
		try{
			/*String sProgId = channelIndex.getText().toString();
			int iProgId = Integer.parseInt(sProgId);
			Channel chan = objApplication.dvbDatabase.getChannel(iProgId);*/
			Channel chan = mCurChannels.get(position);
			if(chan!=null){
				banner.show(chan.chanId,999999999);
			}
		}catch(Exception e){
			
		}
	}	
	private int getChanId(int position) {
		try{
			/*String sProgId = channelIndex.getText().toString();
			int iProgId = Integer.parseInt(sProgId);
			Channel chan = objApplication.dvbDatabase.getChannel(iProgId);*/
			Channel chan = mCurChannels.get(position);
			if(chan!=null){
				//banner.show(chan.chanId,999999999);
				return chan.chanId;
			}
		}catch(Exception e){
			
		}
		return -1;
	}
	private void updateBannerinfo(int position) {
		int chid = getChanId(position);
		if (chid != -1) {
			Intent intent = new Intent();
			intent.setAction("com.changhong.action.syncbanner");
			intent.putExtra("chanid", chid);
			sendBroadcast(intent);
		}
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
										
					/* 按数字键仅选择某频道(若存在)但不换台
					 int succ = theActivity.objApplication.playChannelByLogicNo(
							theActivity.iKey, true);

					if (succ < 0) {
						theActivity.tvRootDigitalkey
								.setVisibility(View.INVISIBLE);
						theActivity.tvRootDigitalKeyInvalid
								.setVisibility(View.VISIBLE);
					} else */{

						
						//if(	theActivity.volume_layout.getVisibility()==View.VISIBLE)
						//	theActivity.volume_layout.setVisibility(View.INVISIBLE);

						//theActivity.banner.show(SysApplication.iCurChannelId);
						updateChanListInfo(theActivity.iKey);
	
						/*
						Message msg2 = new Message();
						msg2.what = MESSAGE_SHOW_DIGITALKEY;
						msg2.arg1 = theActivity.iKey;
						sendMessage(msg2);
						*/
						
						
						
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

			case MESSAGE_REQ_UPBANNER:
				updateBannerinfo2(msg.arg1);
				break;
			}
		}

		private void updateChanListInfo(int iKey) {
			final ChannelList theActivity = mActivity.get();
			
			//首先从当前类型分类查找输入频道  --->按歌华规范需要从全部分类频道里查找
			/*
			for (int i=0;i<theActivity.mCurChannels.size();i++) {
				if(theActivity.mCurChannels.get(i).logicNo == iKey)
				{
					theActivity.channelListView.setSelection(i);
					theActivity.mAdapter.notifyDataSetChanged();
					Log.i(TAG,"Found/cur>> input:"+iKey+", in curType:"+theActivity.curType+",pos:"+i);
					return ;
				}
				
			}
			*/	
			
			//从全部频道分类中查找输入频道
			/*if(theActivity.curType!=0)*/{
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
			t_left = tvTypeNoMax;
		if(t_right>tvTypeNoMax) 
			t_right = 0;		
		epgListTitleViewLeft.setText(TVtype[t_left]);
		epgListTitleViewCenter.setText(TVtype[t_center]);
		epgListTitleViewRight.setText(TVtype[t_right]);
	}	
	
	//上一页
	private void prePage() {
		Log.i(TAG,"curPage:" + pageIndex);
		if(opMode==0){
			opMode=1;
			calPageData(true);	
			//calCurPosInPageMode();
		}
		else if(pageIndex>0)
			pageIndex--;
		else if(channelListView.getSelectedItemPosition()>0)
			channelListView.setSelection(0);		
		else
			return;
		Log.i(TAG,"prePage:" + pageIndex);		
		mAdapter.notifyDataSetChanged();
	}
	//��һҳ�ķ���
	private void nextPage() {
		Log.i(TAG,"curPage:" + pageIndex);
		if(opMode==0){
			opMode=1;
			calPageData(false);	
			//calCurPosInPageMode();
		}
		else if(pageIndex<(pageCount-1))
			pageIndex++;
		else if(channelListView.getSelectedItemPosition()<(mAdapter.getCount()-1))
			channelListView.setSelection(mAdapter.getCount()-1);
		else
			return;		
		Log.i(TAG,"nextPage:" + pageIndex);
		mAdapter.notifyDataSetChanged();
	}	
	//需要在切换成翻页键时调用
	private void calPageData(boolean bPageUp){
		totalCount = mCurChannels.size();  
        pageSize = DEF_PAGESIZE;  
        //pageIndex = 0;  
        pageCount = totalCount/pageSize;    //   
        if(totalCount%pageSize > 0)    //最后一页不足pagesize个   
            pageCount++;  

        pageIndex = channelListView.getSelectedItemPosition()/pageSize;
        if(bPageUp)
        	channelListView.setSelection(0);
        else 
        	channelListView.setSelection(mAdapter.getCount()-1);
        
		Log.i(TAG, "calPageData>>>bPageUp:"+bPageUp+",pageIndex:"+pageIndex +",lastPos:"+(mAdapter.getCount()-1)+ ",oldPos:"+channelListView.getSelectedItemPosition());        
	}	
	//需要在切换成非翻页键时调用
	private void resetPageData(){
        pageSize = 0;
        opMode = 0;
	}	
	//从翻页模式按下键滚动到下一页
	private void ToOneLineOfNextPage()
	{
		if(opMode!=0){
			opMode = 0;
			//if(channelListView.getSelectedItemPosition() < (mCurChannels.size()-1))
			if(pageIndex<(pageCount-1))
			{
				int pos = calCurPosInLineMode();
				pageSize = 0;
				channelListView.setSelection(pos);
				Log.i(TAG, "set new pos:"+pos+","+pageIndex+"/"+pageCount);
				mAdapter.notifyDataSetChanged();
			}else{
				Log.i(TAG, "this is realy the last page>>> "+pageIndex);
				pageSize = 0;
			}			
		}		
	}
	
	//从翻页模式按上键滚动到上一页
	private void ToOneLineOfPrePage()
	{
		if(opMode!=0){
			opMode = 0;
			//if(channelListView.getSelectedItemPosition() < (mCurChannels.size()-1))
			if(pageIndex>0)
			{
				int pos = calCurPosInLineMode();
				pageSize = 0;
				channelListView.setSelection(pos);
				Log.i(TAG, "set new pos:"+pos+","+pageIndex+"/"+pageCount);
				mAdapter.notifyDataSetChanged();
			}else{
				Log.i(TAG, "this is realy the first page>>> "+pageIndex);
				pageSize = 0;
			}			
		}		
	}
	//计算从单行模式切换到翻页模式的位置
	private int calCurPosInPageMode(){
		int curpos=0;
		if(channelListView.getSelectedItemPosition()>DEF_PAGESIZE){			
			curpos = channelListView.getSelectedItemPosition()% DEF_PAGESIZE;		
			pageIndex = channelListView.getSelectedItemPosition()/DEF_PAGESIZE;			
		}
		else{
			pageIndex = 0;
			curpos = channelListView.getSelectedItemPosition()% DEF_PAGESIZE;	
		}
		channelListView.setSelection(curpos);
		Log.i(TAG, "calCurPosInPageMode>>>curpose:"+curpos+",pageIndex:"+pageIndex + "oldPos:"+channelListView.getSelectedItemPosition());
		return curpos;
	}
	//计算从翻页模式切换到单行模式的位置
	private int calCurPosInLineMode(){
		int pos=0;
		if(pageIndex>0)
			pos = (pageIndex-1)*pageSize + channelListView.getSelectedItemPosition();
		else {
			pos = channelListView.getSelectedItemPosition();
		}
		Log.i(TAG, "calCurPos>>>pos:"+pos+" <<<pageIndex:"+pageIndex+"pageSize:"+pageSize+"pos:"+channelListView.getSelectedItemPosition());
		return pos;
	}
}
