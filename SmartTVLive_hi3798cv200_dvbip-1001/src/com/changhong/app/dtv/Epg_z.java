package com.changhong.app.dtv;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import com.changhong.app.book.BookInfo;
import com.changhong.app.constant.Advertise_Constant;
import com.changhong.app.constant.Class_Constant;
import com.changhong.app.constant.Class_Global;
import com.changhong.app.dtv.DialogUtil.DialogBtnOnClickListener;
import com.changhong.app.dtv.DialogUtil.DialogMessage;
import com.changhong.app.utils.Utils;
import com.changhong.dvb.Channel;
import com.changhong.dvb.DVB;
import com.changhong.dvb.ProtoMessage.DVB_EPG_Event;
import com.changhong.dvb.ProtoMessage.DVB_EPG_SCH;
import com.changhong.dvb.ProtoMessage.DVB_RectSize;
import com.iflytek.xiri.Feedback;
import com.iflytek.xiri.scene.ISceneListener;
import com.iflytek.xiri.scene.Scene;
import com.xormedia.adplayer.AdItem;
import com.xormedia.adplayer.AdPlayer;
import com.xormedia.adplayer.AdStrategy;
import com.xormedia.adplayer.IAdPlayerCallbackListener;
import com.xormedia.adplayer.IAdStrategyResponseListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Epg_z extends Activity implements ISceneListener {

	private Context context;
	String[] EpgWeek = null;
	private UI_Handler uiHandler = new UI_Handler(this);
	public static final int MSG_BOOK_NEW = 0x301;
	static Vector<BookInfo> mBookInfos = null;
	static EventListAdapter epgEventListAdapter = null;
	private String sceneJson;
	private static EpgListview epgEventListview;
	private Scene scene;
	private Feedback feedback;

	boolean IsRegisterStopReceiver = true;
	// private TextView epgListTitleView;// chanellist title
	private EpgListview channelListView; // channel list/
	private ImageView focusView; // foucus image
	private LinearLayout channelListLinear;// channellist layout

	RelativeLayout noprogram_prompt, epg_layout;
	int endHour = 0;
	int endMinute = 0;
	Dialog epgPromptDiaog = null;

	boolean EpgRefreshControl = false, ReturnEPGSearch = true;

	int current_week_select = 0;
	String mYear;
	String mMonth;
	String mDay;
	String mWeek;
	String mHour, mMinute;
	boolean endHourIsTomorrow = false;
	static List<Map<String, Object>> SimpleAdapterEventdata = null;

	private static final int SKIP = 1;
	private static final int UNSKIP = 0;
	private int channelCount = -1;
	private Channel curChannel;
	private View curView;

	String TAG = "ChannelList";
	private String[] TVtype;// all tv type
	private int curChannelIndex = 0;// selected channelindex
	private int curType = 0;
	private int curListIndex = 0;
	RelativeLayout epg_prompt1;

	private static final int MESSAGE_DISAPPEAR_PROMPT = 203;
	private static final int FINISH = 202;

	// private static GridView epgWeekInfoView;

	// all type channel
	List<Channel> allTvList = new ArrayList<Channel>();
	List<Channel> CCTVList = new ArrayList<Channel>();
	List<Channel> starTvList = new ArrayList<Channel>();
	List<Channel> favTvList = new ArrayList<Channel>();
	List<Channel> localTvList = new ArrayList<Channel>();
	List<Channel> HDTvList = new ArrayList<Channel>();
	List<Channel> otherTvList = new ArrayList<Channel>();

	Button[] weekday_z = new Button[7];
	// animation

	// application
	static SysApplication objApplication;

	private static final int HANDLE_MSG_CLOSE_CHANNELLIST = 1002;
	Class_Global obj_Global = new Class_Global();
	List<Map<String, Object>> SimpleAdapterWeekdata = null;

	private homeReceiver mHomeReceiver;

	private AdPlayer adPlayer;
	private ArrayList<AdItem> adList = new ArrayList<AdItem>();
	private AdStrategy ads;
	private int glbChanLstIndex = 1;

	private class homeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			String strAction = arg1.getAction();

			if (strAction.equals("HOME_PRESSED")) {
				finish();
			}

		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.channellist);

		mHomeReceiver = new homeReceiver();
		registerReceiver(mHomeReceiver, new IntentFilter("HOME_PRESSED"));

		objApplication = SysApplication.getInstance();
		banner = Banner.getInstance(this);
		scene = new Scene(this);
		feedback = new Feedback(this);

		if (objApplication.dvbDatabase != null)
			channelCount = objApplication.dvbDatabase.getChannelCount();

		noprogram_prompt = (RelativeLayout) findViewById(R.id.noprogram_prompt);
		epg_layout = (RelativeLayout) findViewById(R.id.epg_layout);

		if (channelCount <= 0) {

			IsRegisterStopReceiver = false;
			epg_layout.setVisibility(View.INVISIBLE);
			noprogram_prompt.setVisibility(View.VISIBLE);
			uiHandler.sendEmptyMessageDelayed(FINISH, 3000);

		} else {

			IsRegisterStopReceiver = true;
			curType = getIntent().getIntExtra("curType", 0);
			getAllTVtype(curType);
			registerBroadReceiver();
			Channel channel = objApplication.dvbDatabase.getChannel(objApplication.getLastProgram());
			curChannelIndex = channel.chanId;
			// setfullscreen
			Point size = new Point();
			getWindowManager().getDefaultDisplay().getSize(size);
			DVB_RectSize.Builder builder = DVB_RectSize.newBuilder().setX(0).setY(0).setW(size.x).setH(size.y);
			objApplication.dvbPlayer.setSize(builder.build());

			getDateTime();
			initView();
			channelListView.requestFocus();
		}

	}

	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		scene.init(this);
	}

	private void getDateTime() {

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
			if (channel.sortId != 0x1 && channel.sortId != 0x11 && channel.sortId != 0x16 && channel.sortId != 0x19
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
		if (thisChannel.sortId != 0x1 && thisChannel.sortId != 0x11 && thisChannel.sortId != 0x16
				&& thisChannel.sortId != 0x19 && (thisChannel.videoPid == 0x0 || thisChannel.videoPid == 0x1fff)) {
			return false;
		}
		return true;
	}

	private void getAllTVtype(int index) {
		// fill all type tv
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
			java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("CCTV|" + regExCCTV);
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
			java.util.regex.Pattern patternStar = java.util.regex.Pattern.compile(".*" + regExStar + "$");
			for (Channel Channel : Channels) {
				java.util.regex.Matcher matcherStar = patternStar.matcher(Channel.name);
				boolean classBytypeStar = matcherStar.matches();
				if (classBytypeStar) {
					starTvList.add(Channel);
				}
			}
			break;
		case 3:
			localTvList.clear();
			String regExLocal = "CDTV|SCTV|" + getResources().getString(R.string.rongcheng) + "|"
					+ getResources().getString(R.string.jingniu) + "|" + getResources().getString(R.string.qingyang)
					+ "|" + getResources().getString(R.string.wuhou) + "|" + getResources().getString(R.string.chenghua)
					+ "|" + getResources().getString(R.string.jinjiang) + "|"
					+ getResources().getString(R.string.chengdu) + "|" + getResources().getString(R.string.sichuan);
			java.util.regex.Pattern patternLocal = java.util.regex.Pattern.compile(regExLocal);
			for (Channel Channel : Channels) {
				java.util.regex.Matcher matcherLocal = patternLocal.matcher(Channel.name);
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
			java.util.regex.Pattern patternHD = java.util.regex.Pattern.compile("3D|" + regExHD + "|.*HD$");
			for (Channel Channel : Channels) {
				java.util.regex.Matcher matcherHD = patternHD.matcher(Channel.name);
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
			String regExOther = "CDTV|SCTV|CCTV|" + getResources().getString(R.string.weishi) + "|"
					+ getResources().getString(R.string.rongcheng) + "|" + getResources().getString(R.string.jingniu)
					+ "|" + getResources().getString(R.string.qingyang) + "|" + getResources().getString(R.string.wuhou)
					+ "|" + getResources().getString(R.string.chenghua) + "|"
					+ getResources().getString(R.string.jinjiang) + "|" + getResources().getString(R.string.chengdu)
					+ "|" + getResources().getString(R.string.sichuan);
			java.util.regex.Pattern patternOther = java.util.regex.Pattern.compile(regExOther);
			for (Channel Channel : Channels) {
				java.util.regex.Matcher matcherOther = patternOther.matcher(Channel.name);
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
		switch (curType) {
		case 0:
			// epgListTitleView.setText(TVtype[0]);
			curChannels = allTvList;
			break;
		case 1:
			// epgListTitleView.setText(TVtype[1]);
			curChannels = CCTVList;
			break;
		case 2:
			// epgListTitleView.setText(TVtype[2]);
			curChannels = starTvList;
			break;
		case 3:
			// epgListTitleView.setText(TVtype[3]);
			curChannels = localTvList;
			break;
		case 4:
			// epgListTitleView.setText(TVtype[4]);
			curChannels = HDTvList;
			break;
		case 5:
			// epgListTitleView.setText(TVtype[5]);
			curChannels = favTvList;
			break;
		case 6:
			// epgListTitleView.setText(TVtype[6]);
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

	TextView channelIndexView;

	private void initView() {

		current_week_select = 0;

		context = Epg_z.this;

		TVtype = getResources().getStringArray(R.array.tvtype);

		epgEventListview = (EpgListview) findViewById(R.id.EpgEventInfo);

		epg_prompt1 = (RelativeLayout) findViewById(R.id.epg_prompt1);

		epgEventListview.setOnItemClickListener(epgEventClickListener);
		weekday_z[0] = (Button) findViewById(R.id.week_1);
		weekday_z[1] = (Button) findViewById(R.id.week_2);
		weekday_z[2] = (Button) findViewById(R.id.week_3);
		weekday_z[3] = (Button) findViewById(R.id.week_4);
		weekday_z[4] = (Button) findViewById(R.id.week_5);
		weekday_z[5] = (Button) findViewById(R.id.week_6);
		weekday_z[6] = (Button) findViewById(R.id.week_7);

		weekday_z[0].setBackgroundDrawable(getResources().getDrawable(R.drawable.epgbkgz97));

		channelListView = (EpgListview) findViewById(R.id.id_epg_chlist);
		showChannelList();

		/* proguide advertisement */
		adPlayer = (AdPlayer) findViewById(R.id.adplayer_proguide);
		adPlayer.setDefaultAd(R.drawable.default_img, 1);
		for (int i = 0; i < 7; i++) {

			weekday_z[i].setOnFocusChangeListener(weekdayChangeListener);

		}

		EpgWeek = getResources().getStringArray(R.array.str_dtv_epg_week_name);
		SimpleAdapterWeekdata = new ArrayList<Map<String, Object>>();
		SimpleAdapterWeekdata = GetWeekDate();

		for (int j = 0; j < 7; j++) {
			weekday_z[j].setText((String) SimpleAdapterWeekdata.get(j).get("DayWeek"));
		}

		channelListView.setSelection(getSortIndex(curChannelIndex));

		channelListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO show the select channel
				curListIndex = position;

				if (view != null) {

					EpgRefreshControl = true;
					ReturnEPGSearch = false;

					if (current_week_select > 0)
						weekday_z[current_week_select]
								.setBackgroundDrawable(getResources().getDrawable(R.drawable.week_button));

					weekday_z[0].setBackgroundDrawable(getResources().getDrawable(R.drawable.epgbkgz97));

					TextView channelIndex = (TextView) view.findViewById(R.id.chanId);
					int index = Integer.parseInt(channelIndex.getText().toString());

					objApplication.playChannel(index, true);

					curChannelIndex = index;
					curView = view;

					// while(!ReturnEPGSearch&&EpgRefreshControl){

					getEpgEventData(index, 0);

					// }

					EpgEventListRefresh(SimpleAdapterEventdata);

					// mAdapter.setSelectedPosition(position);
					// mAdapter.notifyDataSetChanged();
					Log.i("zyt", "选中的位置  " + curListIndex + "  chanel id is " + index);
					if (proGuideRunnable != null) {
						uiHandler.removeCallbacks(proGuideRunnable);
					}
					// uiHandler.postDelayed(r, delayMillis)
					uiHandler.post(proGuideRunnable);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		channelListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

			}
		});

	}

	public void getYiHanProGuideAD(int channelId) {
		// ads = new AdStrategy(mContext, "yinhedtvpf", getParams());
		Channel curChannel = DVB.getManager().getChannelDBInstance().getChannel(channelId);

		ads = new AdStrategy(SysApplication.getInstance(), Advertise_Constant.LIVEPLAY_ID_GUIDE, Common.getParams(
				curChannel.serviceId + "", Advertise_Constant.LIVEPLAY_ID_GUIDE, Advertise_Constant.TEMP_IP_ADDRESS));
		// 请求广告决策
		ads.request();

		adPlayer.setIAdPlayerCallbackListener(new IAdPlayerCallbackListener() {

			@Override
			public void onPrepared(AdPlayer v) {
				// 广告内容准备就绪后播放
				Log.e("FSLog", "视频准备完成------------onPrepared");
				// adPlayer.play("");
			}

			@Override
			public void onError(AdPlayer v, int errorCode, String message) {
				// 广告控件处理中发生错误抛出后处理代码
				Log.e("FSLog", "播放异常------------onError");
			}

			@Override
			public void onCompleted(AdPlayer v) {
				// 广告控件中广告内容播放完毕后代码
				Log.e("FSLog", "广告播放结束-----------onCompleted");
				adPlayer.stop();
			}
		});

		ads.setOnResponseListener(new IAdStrategyResponseListener() {

			@Override
			public void onResponse(ArrayList<AdItem> items) {
				// 获得广告策略结果后处理代码
				Log.i("zyt", "AdStrategy-------------------------onResponse");
				Log.i("zyt", "AdStrategy-------------------------onResponse  之前广告列表的长度" + items.size());
				if (items != null && items.size() > 0) {
					AdItem temp = items.get(0);
					// Log.i("zyt", " " + items.size());
					if (temp.type == AdItem.TYPE_SELF_PLAY) {// 开机广告
					} else {// 非开机广告，获取插播广告的相对位置
						adList = items;
						if (temp.type == AdItem.TYPE_SELF_PLAY) {// 开机广告
						} else {// 非开机广告，获取插播广告的相对位置
							// 播放图片
							playAd(items.get(0).Id);
						}
					}
				} else {
					Log.i("zyt", "AdStrategy---------------onResponse:items is null or size is 0");
				}
			}

			@Override
			public void onError(int errorCode, String message) {
				// 获得广告策略结果出错
				Log.i("zyt", "AdStrategy---------------onError");
			}
		});

		// 将广告策略设置到广告控件中
		adPlayer.setAdStrategy(ads);
	}

	public void playAd(String id) {
		adPlayer.setVisibility(View.VISIBLE);
		adPlayer.play(id);
	}

	private OnItemClickListener epgEventClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			if (arg1 != null) {

				Log.i("ChannelList", " >>" + curChannelIndex);

				Channel mChannel = objApplication.dvbDatabase.getChannel(curChannelIndex);

				DVB_EPG_Event mEvent = (DVB_EPG_Event) arg1.getTag();

				String startHour = String.valueOf(mEvent.getStartTime().getHour());
				if (startHour.length() < 2)
					startHour = "0" + startHour;
				String startMinute = String.valueOf(mEvent.getStartTime().getMinute());
				if (startMinute.length() < 2)
					startMinute = "0" + startMinute;
				Calendar c = Calendar.getInstance();
				c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
				c.setTime(new Date());

				c.add(Calendar.DAY_OF_MONTH, current_week_select);

				SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
				final String tmpDateName = dateFormat.format(c.getTime());
				final String startTime = startHour + ":" + startMinute;

				ImageView bookView = (ImageView) arg1.findViewById(R.id.epg_event_Tview_timer);

				if (bookView.getVisibility() == View.VISIBLE) {
					objApplication.dvbBookDataBase.RemoveOneBookInfo(tmpDateName, startTime);

					mBookInfos = objApplication.dvbBookDataBase.GetBookInfo();
					epgEventListAdapter.notifyDataSetChanged();
					return;
				}
				if (current_week_select == 0) {
					int startH = mEvent.getStartTime().getHour();
					Date date = new Date();
					int nowH = c.get(Calendar.HOUR_OF_DAY);
					if (startH < nowH) {

						if (epg_prompt1.getVisibility() == View.INVISIBLE) {

							// epg_prompt1.setVisibility(View.VISIBLE);
							// uiHandler.sendEmptyMessageDelayed(
							// MESSAGE_DISAPPEAR_PROMPT, 2000);
						}

						return;
					}
					int startM = Integer.parseInt(startMinute);
					int nowM = c.get(Calendar.MINUTE);
					if (startH == nowH && startM <= nowM) {

						if (epg_prompt1.getVisibility() == View.INVISIBLE) {
							// epg_prompt1.setVisibility(View.VISIBLE);
							// uiHandler.sendEmptyMessageDelayed(
							// MESSAGE_DISAPPEAR_PROMPT, 2000);
						}

						return;
					}
				}

				final BookInfo mBookInfo = new BookInfo();
				mBookInfo.bookEnventName = mEvent.getName();
				mBookInfo.bookChannelName = mChannel.name;
				mBookInfo.bookTimeStart = startTime;
				mBookInfo.bookChannelIndex = mChannel.chanId;
				mBookInfo.bookDay = tmpDateName;

				Log.i(TAG,
						"mBookInfo.bookEnventName --mBookInfo.bookChannelName--mBookInfo.bookTimeStart--mBookInfo.bookChannelIndex--mBookInfo.bookDay--"

				+ mEvent.getName() + "   " + mBookInfo.bookChannelName + "  " + mBookInfo.bookTimeStart

				+ "   " + mBookInfo.bookDay + "   " + mBookInfo.bookDay);

				if (null != mBookInfos && mBookInfos.size() != 0) {

					for (int i = 0; i < mBookInfos.size(); i++) {

						if (mBookInfos.get(i).bookTimeStart.equals(startTime)
								&& mBookInfos.get(i).bookDay.equals(tmpDateName)) {

							epgPromptDiaog = DialogUtil.showPromptDialog(context, "当前节目与其他节目冲突", "是否预定当前节目？", null,
									null, new DialogBtnOnClickListener() {
								@Override
								public void onSubmit(DialogMessage dialogMessage) {
									Log.i(TAG, "epgPromptDiaog   Submit  ");
									objApplication.delBookChannel(tmpDateName, startTime);

									if (objApplication.dvbBookDataBase.BookInfoCommit(mBookInfo)) {

										SharedPreferences sharedPre = getSharedPreferences("id", MODE_PRIVATE);
										SharedPreferences.Editor editor = sharedPre.edit();
										int flag = sharedPre.getInt("id", 0);

										Intent myBookIntent = new Intent("android.intent.action.SmartTVBook");
										myBookIntent.putExtra("bookinfo", mBookInfo);
										myBookIntent.putExtra("SmartTV_BookFlag", flag);

										sendBroadcast(myBookIntent);

										editor.putInt("id", flag + 1);
										editor.commit();
										Log.i(TAG, "bookInfo添加成功");
										uiHandler.sendEmptyMessage(MSG_BOOK_NEW);
									} else {
										Log.i(TAG, "bookInfo添加失败");
									}

									if (dialogMessage.dialog != null && dialogMessage.dialog.isShowing()) {
										dialogMessage.dialog.cancel();
									}

									return;

								}

								@Override
								public void onCancel(DialogMessage dialogMessage) {
									if (dialogMessage.dialog != null && dialogMessage.dialog.isShowing()) {
										dialogMessage.dialog.cancel();

										return;

									}
								}
							});

							return;

						}
					}
				}

				if (objApplication.dvbBookDataBase.BookInfoCommit(mBookInfo)) {

					SharedPreferences sharedPre = getSharedPreferences("id", MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPre.edit();
					int flag = sharedPre.getInt("id", 0);

					Intent myBookIntent = new Intent("android.intent.action.SmartTVBook");
					myBookIntent.putExtra("bookinfo", mBookInfo);
					myBookIntent.putExtra("SmartTV_BookFlag", flag);

					sendBroadcast(myBookIntent);

					editor.putInt("id", flag + 1);
					editor.commit();
					Log.i(TAG, "bookInfo添加成功");
					uiHandler.sendEmptyMessage(MSG_BOOK_NEW);
				} else {
					Log.i(TAG, "bookInfo添加失败");
				}

			}
		}

	};

	private OnFocusChangeListener weekdayChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {

			int WeeklistItemindex = -1;

			if (hasFocus) {

				int whichDay = v.getId();

				switch (whichDay) {
				case R.id.week_1:
					WeeklistItemindex = 0;
					break;
				case R.id.week_2:
					WeeklistItemindex = 1;
					break;
				case R.id.week_3:
					WeeklistItemindex = 2;
					break;
				case R.id.week_4:
					WeeklistItemindex = 3;
					break;
				case R.id.week_5:
					WeeklistItemindex = 4;
					break;
				case R.id.week_6:
					WeeklistItemindex = 5;
					break;
				case R.id.week_7:
					WeeklistItemindex = 6;
					break;

				}

				current_week_select = WeeklistItemindex;

				if (WeeklistItemindex >= 0) {

					getEpgEventData(curChannelIndex, WeeklistItemindex);
					EpgEventListRefresh(SimpleAdapterEventdata);
				}

			}
		}
	};

	private List<Map<String, Object>> GetWeekDate() {

		List<Map<String, Object>> WeekDateInfo = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = new HashMap<String, Object>();

		/*
		 * Calendar localCalendar = Calendar.getInstance(); int i =
		 * localCalendar.get(2);
		 * 
		 * mDay = Integer.toString(localCalendar.get(5)-1);//日
		 * 
		 * int j = -1+ localCalendar.get(7);
		 * 
		 * mWeek= getResources().getStringArray(R.array.week)[j];//周 mMonth =
		 * getResources().getStringArray(R.array.month)[i];//月
		 * 
		 * Log.i("ChannelList", " month ------ day------ week----j"+mMonth+
		 * "    " +mDay+"   "+mWeek+"  "+j);
		 */

		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		mYear = String.valueOf(c.get(Calendar.YEAR));
		mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		mWeek = String.valueOf(c.get(Calendar.DAY_OF_WEEK));

		if (c.get(Calendar.MONTH) + 1 < 10) {
			mMonth = "0" + String.valueOf(c.get(Calendar.MONTH) + 1);
		} else {
			mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
		}
		if (c.get(Calendar.DAY_OF_MONTH) < 10) {
			mDay = "0" + String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		} else {
			mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		}

		String tmpDayName = null;
		String tmpDateName = null;
		int curDayWeek = 0;
		int tmpDayWeek = 0;

		curDayWeek = c.get(Calendar.DAY_OF_WEEK) - 1;

		tmpDayWeek = curDayWeek;

		Log.i("ChannelList",
				" String.valueOf(c.get(Calendar.YEAR) ------ c.get(Calendar.MONTH) ------ c.get(Calendar.DAY_OF_MONTH)----c.get(Calendar.DAY_OF_WEEK)"
						+ c.get(Calendar.YEAR) + "    " + c.get(Calendar.MONTH) + "   " + c.get(Calendar.DAY_OF_MONTH)
						+ "  " + c.get(Calendar.DAY_OF_WEEK));

		int nowH = c.get(Calendar.HOUR_OF_DAY);
		int nowM = c.get(Calendar.MINUTE);
		int nowS = c.get(Calendar.SECOND);

		Log.i("ChannelList",
				" c.get(Calendar.HOUR_OF_DAY) ------ c.get(Calendar.MINUTE)------ c.get(Calendar.SECOND)----j" + nowH
						+ "    " + c.get(Calendar.MINUTE) + "   " + c.get(Calendar.SECOND) + "  ");

		for (int m = 0; m < 7; m++) {

			item = new HashMap<String, Object>();

			if (tmpDayWeek < 6) {
				tmpDayName = EpgWeek[tmpDayWeek];
				tmpDayWeek++;
			} else {
				tmpDayName = EpgWeek[tmpDayWeek];
				tmpDayWeek = 0;
			}

			Log.i("ChannelList", "curDayWeek------ tmpDayName" + curDayWeek + tmpDayName);

			item.put("DayWeek", tmpDayName);

			WeekDateInfo.add(item);
		}

		HashMap<String, Object> zitem = new HashMap<String, Object>();

		String string = null;

		string = "今天" + mMonth + "/" + mDay;

		zitem.put("DayWeek", string);

		WeekDateInfo.set(0, zitem);

		return WeekDateInfo;

	}

	private static class UI_Handler extends Handler {
		WeakReference<Epg_z> mActivity;

		public UI_Handler(Epg_z activity) {
			mActivity = new WeakReference<Epg_z>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			final Epg_z theActivity = mActivity.get();
			switch (msg.what) {

			case FINISH:

				theActivity.finish();

				break;

			case MSG_BOOK_NEW:

				Log.i("ChannelList", " MSG_BOOK_NEW");

				mBookInfos = objApplication.dvbBookDataBase.GetBookInfo();

				for (int m = 0; m < mBookInfos.size(); m++) {

					Log.i("ChannelList",
							" MSG_BOOK_NEW    mBookInfos  ----bookChannelName---bookEnventName---bookDay-----bookTimeStart"
									+ m + "  " + mBookInfos.get(m).bookChannelName + "   "
									+ mBookInfos.get(m).bookEnventName

					+ "  " + mBookInfos.get(m).bookDay + "  " + mBookInfos.get(m).bookTimeStart);

				}

				epgEventListAdapter.notifyDataSetChanged();

				break;

			case MESSAGE_DISAPPEAR_PROMPT:

				if (theActivity.epg_prompt1.getVisibility() == View.VISIBLE) {

					theActivity.epg_prompt1.setVisibility(View.INVISIBLE);

				}

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
				new String[] { "id", "name", "serviceid" }, new int[] { R.id.chanId, R.id.chanName, R.id.chanIndex });
		skipListView.setAdapter(adapter);
		skipListView.setOnItemClickListener(skipItemClickListener);
		skipWindow = new PopupWindow(skipView, dip2px(280), 720);
		skipWindow.setFocusable(true);
		skipWindow.setOutsideTouchable(true);
		skipWindow.setBackgroundDrawable(new BitmapDrawable());

		skipWindow.setAnimationStyle(R.style.pop_anim);

		skipWindow.showAtLocation(getWindow().getDecorView(), Gravity.LEFT, dip2px(280), 0);
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
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			Toast.makeText(Epg_z.this, "添加" + list.get(position).get("name") + "到频道列表中", Toast.LENGTH_SHORT).show();
			String channelid = list.get(position).get("id");
			Log.i("xm", channelid);
			objApplication.dvbDatabase.updateChannel(Integer.parseInt(channelid), "skip", String.valueOf(UNSKIP));
			adapter = new SimpleAdapter(Epg_z.this, getSkipData(), R.layout.channelitem,
					new String[] { "id", "name", "serviceid" },
					new int[] { R.id.chanId, R.id.chanName, R.id.chanIndex });
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
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN: {

			return true;
		}
		case KeyEvent.KEYCODE_VOLUME_UP: {

			return true;
		}
		case KeyEvent.KEYCODE_VOLUME_MUTE: {
			return true;
		}

		case KeyEvent.KEYCODE_BACK:

			if (epgPromptDiaog != null && epgPromptDiaog.isShowing()) {
				epgPromptDiaog.cancel();
			} else {

				finish();
			}
			return true;

		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY:

			if (channelListView.hasFocus()) {

				weekday_z[current_week_select]
						.setBackgroundDrawable(getResources().getDrawable(R.drawable.week_button));

				weekday_z[0].requestFocus();
				weekday_z[0].setBackgroundDrawable(getResources().getDrawable(R.drawable.week_button));

				return true;
			} else if (epgEventListview.hasFocus()) {

				epgEventListview.requestFocus();
				return true;
			}

			break;
		case Class_Constant.KEYCODE_LEFT_ARROW_KEY:

			if (weekday_z[0].hasFocus()) {

				weekday_z[0].setBackgroundDrawable(getResources().getDrawable(R.drawable.epgbkgz97));
				channelListView.requestFocus();
				return true;
			} else if (epgEventListview.hasFocus()) {

				channelListView.requestFocus();
				return true;

			}

			break;

		case Class_Constant.KEYCODE_UP_ARROW_KEY:

			if (epgEventListview.hasFocus()) {

				weekday_z[current_week_select].requestFocus();
				weekday_z[current_week_select]
						.setBackgroundDrawable(getResources().getDrawable(R.drawable.week_button));

				return true;

			} else if (channelListView.hasFocus()) {

				channelListView.requestFocus();

				return true;

			}

			break;

		case Class_Constant.KEYCODE_DOWN_ARROW_KEY:

			if (weekday_z[current_week_select].hasFocus()) {

				if (SimpleAdapterEventdata.size() > 0)
					weekday_z[current_week_select]
							.setBackgroundDrawable(getResources().getDrawable(R.drawable.epgbkgz97));

				epgEventListview.requestFocus();
				return true;

			}

			break;

		}

		return super.onKeyDown(keyCode, event);
	}

	/* pro guide runnable */
	public Runnable proGuideRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getYiHanProGuideAD(curChannelIndex);
			Log.i("zyt", "节目指南时候的频道号" + SysApplication.iCurChannelId);
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (IsRegisterStopReceiver) {
			IsRegisterStopReceiver = false;
			unregisterReceiver(stopReceiver);
		}

		unregisterReceiver(mHomeReceiver);
	}

	class ChannelAdapter extends BaseAdapter {

		LayoutInflater inflater = LayoutInflater.from(context);

		private int selectedPosition = -1;// 选中的位置

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		class ViewHolder {
			TextView channelId;
			TextView channelIndex;
			TextView channelName;
			ImageView favView;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.channelitem, null);
				holder = new ViewHolder();
				holder.channelId = (TextView) convertView.findViewById(R.id.chanId);
				holder.channelIndex = (TextView) convertView.findViewById(R.id.chanIndex);
				holder.channelName = (TextView) convertView.findViewById(R.id.chanName);
				holder.favView = (ImageView) convertView.findViewById(R.id.chan_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Channel Channel = mCurChannels.get(position);

			if (Channel.logicNo < 10) {
				holder.channelIndex.setText("00" + Channel.logicNo);
			} else if (Channel.logicNo < 100) {
				holder.channelIndex.setText("0" + Channel.logicNo);
			} else {
				holder.channelIndex.setText("" + Channel.logicNo);
			}

			if (Channel.chanId < 10) {
				holder.channelId.setText("00" + Channel.chanId);
			} else if (Channel.chanId < 100) {
				holder.channelId.setText("0" + Channel.chanId);
			} else {
				holder.channelId.setText("" + Channel.chanId);
			}

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
		if (intent.hasExtra("_scene") && intent.getStringExtra("_scene").equals("com.changhong.app.dtv:ChannelList")) {
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
		sceneJson = "{" + "\"_scene\": \"com.changhong.app.dtv:ChannelList\"," + "\"_commands\": {"
				+ "\"key1\": [ \"下一个频道\", \"频道加\" ]," + "\"key2\": [ \"上一个频道\", \"频道减\" ]" + "}" + "}";
		return sceneJson;
	}

	private String INTENT_CHANNEL_NEXT = "channelnext";
	private String INTENT_CHANNEL_PRE = "channelpre";
	private String INTENT_CHANNEL_ONLYINFO = "channelonlyinfo";

	private void changenextchannel() {
		if ((SysApplication.iCurChannelId != -1) && (objApplication.dvbDatabase.getChannelCount() >= 0)) {
			objApplication.playNextChannel(true);
			banner.show(SysApplication.iCurChannelId);
			finish();
		}
	}

	private Banner banner;

	private void changeprechannel() {
		if ((SysApplication.iCurChannelId != -1) && (objApplication.dvbDatabase.getChannelCount() >= 0)) {
			objApplication.playPreChannel(true);
			banner.show(SysApplication.iCurChannelId);
			finish();
		}
	}

	public void EpgEventListRefresh(List<Map<String, Object>> EventdataList) {

		epgEventListAdapter = new EventListAdapter(context, SimpleAdapterEventdata);
		epgEventListview.setAdapter(epgEventListAdapter);
		epgEventListview.setSelection(0);

	}

	protected class EventListAdapter extends BaseAdapter {

		private Context context;
		List<Map<String, Object>> EpgEventdata = null;

		public EventListAdapter(Context context, List<Map<String, Object>> EpgEventdata) {
			this.context = context;
			this.EpgEventdata = EpgEventdata;

			mBookInfos = objApplication.dvbBookDataBase.GetBookInfo();

		}

		@Override
		public int getCount() {
			return EpgEventdata.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			LayoutInflater inflater = LayoutInflater.from(context);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.epg_main_eventitem, null);
			}

			DVB_EPG_Event epgEvent = (DVB_EPG_Event) EpgEventdata.get(position).get("tag");
			convertView.setTag(epgEvent);
			TextView timeView = (TextView) convertView.findViewById(R.id.epg_event_Tview_time);

			ImageView eventisplay = (ImageView) convertView.findViewById(R.id.eventisplay);

			LinearLayout eventitem_Linearlayout = (LinearLayout) convertView.findViewById(R.id.eventitem_Linearlayout);

			String[] tmpStrings = ((String) EpgEventdata.get(position).get("time")).split(" ~ ");
			String startTime = tmpStrings[0];
			timeView.setText(startTime);
			TextView eventView = (TextView) convertView.findViewById(R.id.epg_event_Tview_info);
			eventView.setText((String) EpgEventdata.get(position).get("event"));
			ImageView bookView = (ImageView) convertView.findViewById(R.id.epg_event_Tview_timer);

			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.DAY_OF_MONTH, current_week_select);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
			String tmpDayString = dateFormat.format(c.getTime());

			/*
			 * if (position % 2 == 0) {
			 * eventitem_Linearlayout.setBackgroundDrawable(getResources()
			 * .getDrawable(R.drawable.epgeventbkg2_z)); } else {
			 * eventitem_Linearlayout.setBackgroundDrawable(getResources()
			 * .getDrawable(R.drawable.epgeventbkg1_z));
			 * 
			 * }
			 */

			eventitem_Linearlayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.epgeventbkg2_z));

			if (isAlreadyBook(startTime, tmpDayString, curChannelIndex)) {

				bookView.setVisibility(View.VISIBLE);

				Log.i("ChannelList", "isAlreadyBook----- bookView.getVisibility>>" + bookView.getVisibility());

			} else {

				bookView.setVisibility(View.INVISIBLE);
				Log.i("ChannelList", "notAlreadyBook----- bookView.getVisibility>>" + bookView.getVisibility());

			}

			if (current_week_select == 0) {
				int startH = epgEvent.getStartTime().getHour();
				int startM = epgEvent.getStartTime().getMinute();
				int startS = epgEvent.getStartTime().getSecond();

				int endH = epgEvent.getStartTime().getHour() + epgEvent.getDurationHour();
				int endM = epgEvent.getStartTime().getMinute() + epgEvent.getDurationMinute();

				int endS = epgEvent.getStartTime().getSecond() + epgEvent.getDurationSecond();

				int nowH = c.get(Calendar.HOUR_OF_DAY);
				int nowM = c.get(Calendar.MINUTE);
				int nowS = c.get(Calendar.SECOND);

				boolean ifCarryOver = false;

				if (endM >= 60) {
					ifCarryOver = true;
					endM = endM - 60;
				}
				endH = ifCarryOver ? endH + 1 : endH;

				Log.i("time", "   " + nowH + "   " + nowM + "    " + nowS + "   " + endH + "   " + endM + "   " + endS
						+ "   " + startH + "     " + startM + "  " + startS);

				Boolean isplaying;

				if (startH < nowH) {

					if (endH > nowH) {

						isplaying = true;

					} else if (endH == nowH && endM >= nowM) {

						isplaying = true;
					} else {

						isplaying = false;
					}

				} else if (startH == nowH && startM < nowM) {

					if (endH > nowH) {

						isplaying = true;

					} else if (endH == nowH && endM >= nowM) {

						isplaying = true;
					} else {

						isplaying = false;
					}

				} else {

					isplaying = false;
				}

				if (isplaying) {

					eventisplay.setVisibility(View.VISIBLE);
					bookView.setVisibility(View.GONE);

				} else {
					eventisplay.setVisibility(View.GONE);

				}

			}

			if (isAlreadyBook(startTime, tmpDayString, curChannelIndex)) {

				bookView.setVisibility(View.VISIBLE);
				eventisplay.setVisibility(View.GONE);

				Log.i("ChannelList", "isAlreadyBook----- bookView.getVisibility>>" + bookView.getVisibility());

			} else {

				bookView.setVisibility(View.INVISIBLE);
				Log.i("ChannelList", "notAlreadyBook----- bookView.getVisibility>>" + bookView.getVisibility());

			}

			return convertView;
		}
	}

	private boolean isAlreadyBook(String startTime, String dayString, int mChannelID) {

		Log.i("ChannelList", " isAlreadyBook    ---dayString---startTime--" + dayString + "  " + startTime);

		boolean isBook = false;

		if (mBookInfos != null && mBookInfos.size() > 0) {
			for (int i = 0; i < mBookInfos.size(); i++) {

				Log.i("ChannelList",
						" isAlreadyBook    mBookInfos  ----bookChannelName---bookEnventName---bookDay-----bookTimeStart"
								+ i + "  " + mBookInfos.get(i).bookChannelName + "   "
								+ mBookInfos.get(i).bookEnventName + "  " + mBookInfos.get(i).bookDay + "  "
								+ mBookInfos.get(i).bookTimeStart);

				if (mBookInfos.get(i).bookTimeStart.equals(startTime)
						&& mBookInfos.get(i).bookChannelIndex == mChannelID
						&& mBookInfos.get(i).bookDay.equals(dayString)) {
					isBook = true;
					return isBook;
				} else {
				}
			}
		} else {
		}
		return isBook;
	}

	private String getStartEndTime(int startHour, int startMinute, int durationHour, int durationMinute) {

		String formatString = "  :   ~   :  ";
		String startTimeString = timeFormat(startHour, startMinute);

		boolean ifCarryOver = false;

		endMinute = startMinute + durationMinute;

		if (endMinute >= 60) {
			ifCarryOver = true;
			endMinute = endMinute - 60;
		}
		endHour = ifCarryOver ? startHour + durationHour + 1 : startHour + durationHour;

		String endTimeString = timeFormat(endHour, endMinute);

		formatString = startTimeString + " ~ " + endTimeString;
		return formatString;
	}

	private String timeFormat(int hour, int minute) {

		String startHourString = String.valueOf(hour);
		String startMinuteString = String.valueOf(minute);

		if (startHourString.length() < 2)
			startHourString = "0" + startHourString;

		if (startMinuteString.length() < 2)
			startMinuteString = "0" + startMinuteString;

		return startHourString + ":" + startMinuteString;
	}

	public void getEpgEventData(int Channel, int WeekIndex) {

		// 根据频道ID和第几天得到EpgEventData
		// SimpleAdapterEventdata

		SimpleAdapterEventdata = new ArrayList<Map<String, Object>>();
		DVB_EPG_SCH schInfo = null;
		Channel curChannel = objApplication.dvbDatabase.getChannel(Channel);
		schInfo = objApplication.dvbEpg.getSchInfo(curChannel);
		// schInfo = objApplication.dvbEpg.getSchInfo(curChannel);

		Log.i(TAG, "schInfo.getSchEventCount() ----->schInfo--->" + schInfo.getSchEventCount() + "   ");

		if (null == schInfo || schInfo.getSchEventCount() == 0) {

			Log.i(TAG, "schInfo null");
			Common.LOGD("schInfo null");
			return;
		}

		ReturnEPGSearch = true;

		Map<String, Object> item = null;
		int startHour = 0;
		int startMinute = 0;
		int durationHour = 0;
		int durationMinute = 0;
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		int curDayWeek = c.get(Calendar.DAY_OF_WEEK);

		for (int i = 0; i < schInfo.getSchEventCount(); i++) {

			Log.i(TAG,
					"schInfo.getSchEvent(i).getStartTime().getWeekday() ----->WeekIndex+ curDayWeek - 1--->WeekIndex-----curDayWeek"
							+ schInfo.getSchEvent(i).getStartTime().getWeekday() + "   " + (WeekIndex + curDayWeek - 1)
							+ "  " + WeekIndex + "  " + curDayWeek);

			if (schInfo.getSchEvent(i).getStartTime().getWeekday() == (WeekIndex + curDayWeek - 1)) {

				item = new HashMap<String, Object>();

				// item.put("time", schInfo.getSchEvent(i).getStartTime()
				// .getHour()
				// + ":"
				// + schInfo.getSchEvent(i).getStartTime().getMinute());

				startHour = schInfo.getSchEvent(i).getStartTime().getHour();
				startMinute = schInfo.getSchEvent(i).getStartTime().getMinute();
				durationHour = schInfo.getSchEvent(i).getDurationHour();
				durationMinute = schInfo.getSchEvent(i).getDurationMinute();

				item.put("tag", schInfo.getSchEvent(i));

				int nowH = c.get(Calendar.HOUR_OF_DAY);
				int nowM = c.get(Calendar.MINUTE);
				int nowS = c.get(Calendar.SECOND);

				String StartEndTime = getStartEndTime(startHour, startMinute, durationHour, durationMinute);

				Log.i("time", "   " + nowH + "   " + nowM + "   " + endHour + "   " + endMinute + "   " + startHour
						+ "  " + startMinute + " ");

				if ((curDayWeek - 1) == schInfo.getSchEvent(i).getStartTime().getWeekday()) {

					if (endHour < nowH) {
						continue;
					} else if (endHour == nowH && endMinute < nowM) {

						continue;

					}

				}

				item.put("time", StartEndTime);

				item.put("event", schInfo.getSchEvent(i).getName());

				Log.i(TAG,
						"time ----->event--->" + getStartEndTime(startHour, startMinute, durationHour, durationMinute)
								+ "   " + schInfo.getSchEvent(i).getName());

				SimpleAdapterEventdata.add(item);
			}
		}

	}

	// 周一...周日改变时触发
	private OnItemSelectedListener WeekInfoItemSelected = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			getEpgEventData(curChannelIndex, arg2);
			EpgEventListRefresh(SimpleAdapterEventdata);

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};
}
