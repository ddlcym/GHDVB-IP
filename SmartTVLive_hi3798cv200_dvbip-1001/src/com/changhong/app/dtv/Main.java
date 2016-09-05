package com.changhong.app.dtv;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.changhong.app.book.BookInfo;
import com.changhong.app.constant.Advertise_Constant;
import com.changhong.app.constant.Class_Constant;
import com.changhong.app.constant.Class_Global;
import com.changhong.app.dtv.DialogUtil.DialogBtnOnClickListener;
import com.changhong.app.dtv.DialogUtil.DialogMessage;
import com.changhong.app.timeshift.common.CacheData;
import com.changhong.app.timeshift.common.MyApp;
import com.changhong.app.timeshift.common.PlayVideo;
import com.changhong.app.timeshift.common.ProcessData;
import com.changhong.app.timeshift.common.ProgramInfo;
import com.changhong.app.timeshift.common.VolleyTool;
import com.changhong.app.timeshift.datafactory.BannerDialog;
import com.changhong.app.timeshift.datafactory.HandleLiveData;
import com.changhong.dvb.Channel;
import com.changhong.dvb.ChannelDB;
import com.changhong.dvb.DVB;
import com.changhong.dvb.PlayingInfo;
import com.changhong.dvb.ProtoMessage.DVB_RectSize;
import com.iflytek.xiri.Feedback;
import com.iflytek.xiri.scene.ISceneListener;
import com.iflytek.xiri.scene.Scene;
import com.xormedia.adplayer.AdItem;
import com.xormedia.adplayer.AdPlayer;
import com.xormedia.adplayer.AdStrategy;
import com.xormedia.adplayer.IAdPlayerCallbackListener;
import com.xormedia.adplayer.IAdStrategyResponseListener;

public class Main extends Activity implements ISceneListener {

	public SysApplication objApplication;
	public Context mContext;
    static String   tag="Main";
	LinearLayout id_dtv_digital_root;
	TextView id_dtv_channel_name;
	ImageView dtv_digital_1;
	ImageView dtv_digital_2;
	ImageView dtv_digital_3;
	TextView volume_value;
	
	int   tempChannelID=-1;
	
	Dialog searchPromptDiaog=null;
	
	RelativeLayout volume_layout;
	AudioManager mAudioManager ;
	
	ProgressBar volume_progress_view ; 

	/**
	 * for handler parm
	 */
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

	/**
	 * the time delayed when change program
	 */
	private static final int TIME_CHANGE_DELAY = 200;

	/**
	 * for the normal parm
	 */
	private String str_title, str_details_exitdtv, s_IsAutoScan, s_IsUpdate;

	/**
	 * for dialog type
	 */

	private static final int DIALOG_AUTOSCAN = 1;

	/**
	 * Banner View
	 */
	private Banner banner;
	private Point point;
	private String INTENT_CHANNEL_INDEX = "channelindex";
	private String INTENT_CHANNEL_NEXT = "channelnext";
	private String INTENT_CHANNEL_PRE = "channelpre";

	/**
	 * Digital key
	 */
	private int iKeyNum = 0;
	private int iKey = 0;
	LinearLayout tvRootDigitalkey;
	private RelativeLayout tvRootDigitalKeyInvalid;
	private boolean bDigitalKeyDown = false;
	Handler handler_digital = new Handler();
	private String INTENT_CHANNEL_ONLYINFO = "channelonlyinfo";
	/**
	 * CA INFO
	 */
	private RelativeLayout flCaInfo;
	private CAMarquee tvCaSubtitleDown, tvCaSubtitleUp;
	private TextView tvCaInfo;

	/**
	 * no program
	 */
	RelativeLayout flNoSignal;
	private int i_LockCount = 0;

	/**
	 * pft update
	 */
	private FrameLayout flPftUpdate;
	

	/**
	 * TimeShift
	 */
	private FrameLayout flTimeShift;

	/**
	 * vkey
	 */
	int iRootMenuVkey = -1;

	/**
	 * signal monitor
	 */
	private signalRecever msignalRecever;

	private homeReceiver mHomeReceiver;

	/**
	 * Pip Play
	 */
	// private boolean isPipControl = false;
	private boolean isPipPlay = false;
	private Context context;
	private String sceneJson;

	/**
	 * record
	 */
	private Chronometer mTimer = null;

	/**
	 * channId of the banner,when change channel by KeyCode up/down
	 */

	private Scene scene;
	private Feedback feedback;
	
	private String startTime = "";
	private String endTime = "";

/* yihan advertisement */
	private AdPlayer adPlayer;
	private ArrayList<AdItem> adList = new ArrayList<AdItem>();
	private AdStrategy ads;
	
	/*
	 * timeshift data
	 */
	private VolleyTool volleyTool;
	private RequestQueue mReQueue;
	private ProcessData processData;
	private static BannerDialog programBannerDialog;
	private static List<ProgramInfo> curChannelPrograms = new ArrayList<ProgramInfo>();// 当前频道下的上一个节目，当前节目，下一个节目信息
	
	private SurfaceView surfaceView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
	

		mContext = Main.this;
		context = Main.this;
		scene = new Scene(context);
		feedback = new Feedback(context);
		objApplication = SysApplication.getInstance();

		objApplication.initDtvApp(this);
		objApplication.addActivity(this);
		banner = Banner.getInstance(this);
		 mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		 
		point = new Point();
		getWindowManager().getDefaultDisplay().getSize(point);

		DVB_RectSize.Builder builder = DVB_RectSize.newBuilder().setX(0)
				.setY(0).setH(point.y).setW(point.x);
		
		objApplication.dvbPlayer.setSize(builder.build());
		
		Log.i("Main", "Main OnCreate !" );
		Common.LOGD("Main OnCreate !");
		
		startTime  = Utils.getCurTime();
		
		// init views
		findView();
		initValue();
		
	    checkChannel();
		registerBroadReceiver();
	}

	private void registerBroadReceiver() {
		
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.changhong.action.stoptvlive");
		registerReceiver(stopReceiver, filter);
		
		
		IntentFilter filter2 = new IntentFilter();
		filter2.addAction("showbanner");
		registerReceiver(showBannerReceiver, filter2);
		
		
		IntentFilter filter3 = new IntentFilter();
		filter3.addAction("showBanneForYuYueDialog");
		registerReceiver(showBanneForYuYueDialog, filter3);
		
		
//		IntentFilter myIntentFilter = new IntentFilter();
//	   myIntentFilter.addAction("FINISH");
 //    registerReceiver(mFinishReceiver, myIntentFilter);
		
		

		
		
	}
	
	

	private BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			
			String action = intent.getAction();
			if (action.equals("FINISH")) {
				finish();
			}

		}

	};

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
	
	
	
	BroadcastReceiver showBannerReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			
			
			Message msg = new Message();
			msg.what = MESSAGE_SHOW_DIGITALKEY;
			msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
	    	mUiHandler.sendMessage(msg);
	    
			
			banner.show(SysApplication.iCurChannelId);
	
		}
	};
	
	
	BroadcastReceiver showBanneForYuYueDialog = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			
			
			
			Log.i("Main", "receive  showBanneForYuYueDialog  broadcast" );
		//	onVkey(Class_Constant.KEYCODE_INFO_KEY);
	
		}
	};

	private void checkChannel() {
		
		
	   Log.i("gggggg","asdasda");
		
	
		
		if (getIntent().getIntExtra("subType", -1) == 1)// Live
		{
			int tsId = getIntent().getIntExtra("param1", -1);
			int serviceId = getIntent().getIntExtra("param2", -1);
			if (tsId < 0 || serviceId < 0) {
				Common.LOGD("The Param1 or Param2 passed-in is invalid");
				return;
			}
			Channel toPlayChannel = objApplication.dvbDatabase
					.getChannelByTsIdAndServiceId(tsId, serviceId);
			if (toPlayChannel != null) {
				objApplication.playChannel(toPlayChannel.chanId, false);
				
			
			}
			return;
		}

		// Following code is used on our platform project, remain it to
		// reference.
		if (getIntent().getIntExtra("chnumber", -1) != -1) {
			
			   Log.i("gggggg","asdasda222222222222");
			
	//		surfaceView1.setVisibility(View.INVISIBLE);

			int chNum = getIntent().getIntExtra("chnumber", -1);
			// 根据频道号切换频道
			if(chNum==99999 && 1<= objApplication.dvbDatabase.getChannelCount()){
				objApplication.playChannel(1, false);
				banner.show(SysApplication.iCurChannelId);
				Message msg = new Message();
				msg.what = MESSAGE_SHOW_DIGITALKEY;
				msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
				mUiHandler.sendMessage(msg);				
			}
			else if (chNum <= objApplication.dvbDatabase.getChannelCount()) {
				objApplication.playChannel(chNum, false);
			} else {

			}
		}
		if (getIntent().getStringExtra("chname") != null) {
			String chStr = getIntent().getStringExtra("chname");
			if (chStr == "next") {
				// 切换到下一个频道
				if ((SysApplication.iCurChannelId != -1)
						&& (objApplication.dvbDatabase.getChannelCount() >= 0)) {
					
					reportToServer();
					
					objApplication.playNextChannel(true);
					banner.show(SysApplication.iCurChannelId);
					Message msg = new Message();
					msg.what = MESSAGE_SHOW_DIGITALKEY;
					msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
					mUiHandler.sendMessage(msg);
				}
			} else if (chStr == "pre") {
				// 切换到上一个频道
				if ((SysApplication.iCurChannelId != -1)
						&& (objApplication.dvbDatabase.getChannelCount() >= 0)) {
					
					reportToServer();
					
					objApplication.playPreChannel(true);
					banner.show(SysApplication.iCurChannelId);
					Message msg = new Message();
					msg.what = MESSAGE_SHOW_DIGITALKEY;
					msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
					mUiHandler.sendMessage(msg);
				}
			} else if (chStr == "open") {
				// 打开数字电视
			} else {
				// 根据频道名称切换频道
				objApplication.playChannel(chStr, false);
			}
		}
	}

	@Override
	protected void onResume() {
		
		
		super.onResume();
		
		if(	volume_layout.getVisibility()==View.VISIBLE)
			volume_layout.setVisibility(View.INVISIBLE);
		
		DVB_RectSize.Builder builder = DVB_RectSize.newBuilder().setX(0)
				.setY(0).setW(0).setH(0);
		
		if(objApplication.dvbPlayer!=null){
			
			Log.i("Main","resize video size");
		   objApplication.dvbPlayer.setSize(builder.build());
		   
		}
		
		
		
		Log.i("Main", "onResume  start!" );
		int curChanCount = objApplication.dvbDatabase.getChannelCountSC();

		if (curChanCount <= 0) {
			
			
			Log.i("zhougang  main", "弹出没有节目，自动搜索的dialog");
			onVkey(Class_Constant.VKEY_EMPTY_DBASE);
			
			return;
			
		} else {
	
			if (searchPromptDiaog!= null&&searchPromptDiaog.isShowing()) {
				searchPromptDiaog.cancel();
			}
		
		}

		
		Log.i("zhougang  main", "SysApplication.bNeedFirstBootIn"+SysApplication.bNeedFirstBootIn);
		if (SysApplication.bNeedFirstBootIn) {
			

			if (curChanCount <= 0) {
			
			} else {
				
				if (enterSubMenu() < 0) {
					int iTmpIndex = getIntent().getIntExtra("ProIndex", -1);
					if (iTmpIndex != -1) {

						objApplication.playChannel(iTmpIndex, false);

					}

					onVkey(Class_Constant.KEYCODE_INFO_KEY);
				}

			}
			
			SysApplication.bNeedFirstBootIn = false;
			
			
		} else {
			// get the virtual key
			iRootMenuVkey = Class_Global.GetRootMenuVKey();
			Class_Global.SetRootMenuVKey(-1);
			onVkey(iRootMenuVkey);
			
			
			
			DisplayBanner();

			Message msg = new Message();
			msg.what = MESSAGE_SHOW_DIGITALKEY;
			msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
	    	mUiHandler.sendMessage(msg);
			
		}
		
		Log.i("zhougang  main",
				"curChanCount =   -----" + curChanCount);


	}
	
	
	void DisplayBanner(){
		
		int curChanCount = objApplication.dvbDatabase.getChannelCountSC();
		

		if (curChanCount > 0 && SysApplication.iCurChannelId != -1) {
			
			banner.show(SysApplication.iCurChannelId);
			
			// surfaceView1.setVisibility(View.VISIBLE);
			Message msg = new Message();
			msg.what = MESSAGE_SHOW_DIGITALKEY;
			msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
			mUiHandler.sendMessage(msg);
					
		}
		
		
		
		
		
	}

	public void onDestroy() {

		Common.LOGD("Main onDestroy !");
		
		reportToServer();

		if (objApplication.dvbPlayer != null) {
			// DVB_RectSize.Builder builder = DVB_RectSize.newBuilder();
			// builder.setX(87).setY(108).setW(493).setH(285);
			// objApplication.dvbPlayer.setSize(builder.build());

			// objApplication.dvbPlayer.stop();
			// objApplication.dvbPlayer.blank();
			// DVBManager.getInstance().destroyPlayer(objApplication.dvbPlayer);
			// objApplication.dvbPlayer = null;
		}

		objApplication.stopPipPlay();

		System.exit(0);
		unregisterReceiver(msignalRecever);
		unregisterReceiver(mHomeReceiver);
		unregisterReceiver(stopReceiver);
		objApplication.exit();
		android.os.Process.killProcess(android.os.Process.myPid());
		super.onDestroy();
	}

public Runnable volumeAdRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getYiHanVolumeAD(SysApplication.iCurChannelId);
		}
	};
	@SuppressLint("NewApi")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Log.i("Vanlen Debug", "smart live key response:" + keyCode);

		boolean bKeyUsed = false;
		switch (keyCode) {
	
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			
		Channel	channel2 = objApplication.getCurPlayingChannel();
		
		if (channel2 != null) {
			Log.i("yangtong", "channelId >>" + channel2.chanId);
	
			AudioManager am2 = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			boolean isMute2 = am2.isStreamMute(AudioManager.STREAM_MUSIC);
			int currentVolume3 = am2.getStreamVolume(AudioManager.STREAM_MUSIC);	
			int maxVolume = am2.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			int stepVolume = maxVolume/15;
			Log.e("banner", "get>>>> old vol---->"+currentVolume3+",mult---->"+isMute2+",maxvol="+maxVolume);
			
			if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
				if(currentVolume3>0){
					am2.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);	
					//am2.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume3-stepVolume, 0);
				}			
			}
			else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
				if(currentVolume3<maxVolume){
						am2.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
						//am2.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume3+stepVolume, 0);
					}
					if(isMute2){
						isMute2 = false;
						am2.setStreamMute(AudioManager.STREAM_MUSIC, isMute2);									
						Intent mIntent20 = new Intent("chots.anction.muteon");
						sendBroadcast(mIntent20);					
					}
				
			}
			
			currentVolume3 = am2.getStreamVolume(AudioManager.STREAM_MUSIC);
			
			if(currentVolume3 <=0 && isMute2==false){
				isMute2 = true; 
				am2.setStreamMute(AudioManager.STREAM_MUSIC, isMute2);				
				Intent mIntent40 = new Intent("chots.anction.muteon");
				sendBroadcast(mIntent40);					
			}
			Log.e("banner", "set>>>> new vol-->"+currentVolume3+",mult--->"+isMute2);
		    banner.show(channel2.chanId,currentVolume3,isMute2); 
	        
		} else {
			Log.e("yangtong", "channel is null");
			bKeyUsed = true;
			break;
		}			
			return true;
		case KeyEvent.KEYCODE_VOLUME_MUTE:
			
			Log.i("MUTE", "mute key arrived.");
			AudioManager am1 = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			boolean isMute1 = am1.isStreamMute(AudioManager.STREAM_MUSIC);
			//set mute state
			am1.setStreamMute(AudioManager.STREAM_MUSIC, !isMute1);
			//Assure the volume is old value.
			am1.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, 0); 
			//send broadcast to show/hide mute icon
			Intent mIntent1 = new Intent("chots.anction.muteon");
			sendBroadcast(mIntent1);
			//get current volume value, here you can show your own volume bar or do nothing.
			int currentVolume1 = am1.getStreamVolume(AudioManager.STREAM_MUSIC);
			
			Log.i("MUTE", "mute key arrived-----currentVolume------>."+currentVolume1);
			Channel	channel3 = objApplication.getCurPlayingChannel();
			
			if (channel3 != null) {			
				banner.show(channel3.chanId,currentVolume1,!isMute1); 
			}
			
			return true;		
		
		case KeyEvent.KEYCODE_CHANNEL_DOWN:
		case KeyEvent.KEYCODE_DPAD_DOWN:
		case KeyEvent.KEYCODE_PAGE_DOWN: {
			Common.LOGI("KEYCODE_DPAD_UP");
			if ((SysApplication.iCurChannelId != -1)
					&& (objApplication.dvbDatabase.getChannelCount() >= 0)) {

				Message msg = new Message();
				msg.what = MESSAGE_SHOW_DIGITALKEY_FOR_PRE_OR_NEXT_KEY;

				Channel channel = objApplication.getPreChannel(tempChannelID);
				if (channel == null || channel.chanId == -1) {// if pre channel
																// invalid,show
																// curChannel
					Log.i("yangtong", "pre channel is null!");
					channel = objApplication.getCurPlayingChannel();
				}
				if (channel != null) {
					Log.i("yangtong", "channelId >>" + channel.chanId);
					
					if(	volume_layout.getVisibility()==View.VISIBLE)
						volume_layout.setVisibility(View.INVISIBLE);
					
				    banner.show(channel.chanId);
					tempChannelID= channel.chanId;
	
					msg.arg1 = channel.logicNo;
				} else {
					Log.e("yangtong", "channel is null");
					bKeyUsed = true;
					break;
				}
				bKeyUsed = true;
				mUiHandler.removeMessages(MESSAGE_PLAY_PRE);
				mUiHandler.removeMessages(MESSAGE_PLAY_NEXT);
				mUiHandler.sendEmptyMessageDelayed(MESSAGE_PLAY_PRE,
						TIME_CHANGE_DELAY);
				mUiHandler.sendMessage(msg);
			}
		}
			break;
		case KeyEvent.KEYCODE_CHANNEL_UP:
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_PAGE_UP: {
			if ((SysApplication.iCurChannelId != -1)
					&& (objApplication.dvbDatabase.getChannelCount() >= 0)) {
				Message msg = new Message();
				msg.what = MESSAGE_SHOW_DIGITALKEY_FOR_PRE_OR_NEXT_KEY;

				Channel channel = objApplication.getNextChannel(tempChannelID);
				if (channel == null || channel.chanId == -1) {// if next channel
																// invalid,show
																// curChannel
					Log.i("yangtong", "next channel is null!");
					channel = objApplication.getCurPlayingChannel();
				}
				if (channel != null) {
					Log.i("yangtong", "channelId >>" + channel.chanId);
					if(	volume_layout.getVisibility()==View.VISIBLE)
						volume_layout.setVisibility(View.INVISIBLE);
					banner.show(channel.chanId);
					tempChannelID= channel.chanId;
			
					msg.arg1 = channel.logicNo;
				} else {
					Log.e("yangtong", "channel is null");
					bKeyUsed = true;
					break;
				}
				bKeyUsed = true;
				mUiHandler.removeMessages(MESSAGE_PLAY_PRE);
				mUiHandler.removeMessages(MESSAGE_PLAY_NEXT);
				mUiHandler.sendEmptyMessageDelayed(MESSAGE_PLAY_NEXT,
						TIME_CHANGE_DELAY);
				mUiHandler.sendMessage(msg);
			}
		}
			break;


		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER: {

			bKeyUsed = true;
			this.onVkey(keyCode);
		}
			break;

		case KeyEvent.KEYCODE_BACK: {

			/*
			 * if (layout_set_activity_z.getVisibility() == View.VISIBLE) {
			 * layout_set_activity_z.setVisibility(View.INVISIBLE); }
			 * 
			 * 
			 */
			
		if(	volume_layout.getVisibility()==View.VISIBLE){
				volume_layout.setVisibility(View.INVISIBLE);
			}

			if (mUiHandler.hasMessages(MESSAGE_HANDLER_DIGITALKEY)) {
				mUiHandler.removeMessages(MESSAGE_HANDLER_DIGITALKEY);
				
				tvRootDigitalkey.setVisibility(View.INVISIBLE);
				tvRootDigitalKeyInvalid.setVisibility(View.INVISIBLE);
				iKeyNum = 0;
				iKey = 0;
				bKeyUsed = true;
			} else {
				
				reportToServer();
				
				objApplication.playPrePlayingChannel();
				banner.show(SysApplication.iCurChannelId);

				
				Message msg = new Message();
				msg.what = MESSAGE_SHOW_DIGITALKEY;
				msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
				mUiHandler.sendMessage(msg);
				bKeyUsed = true;
			}
			return true;
		}

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
			bKeyUsed = true;
			onVkey(keyCode);
		}

			break;

		//This is only for test
		case KeyEvent.KEYCODE_DPAD_LEFT: {
			
			
			 BookInfo curBookInfo = new BookInfo();
				
			 curBookInfo.bookDay = "1";
			 curBookInfo.bookEnventName ="请不要为我哭泣";
			 curBookInfo.bookTimeStart = "3";
			 curBookInfo.bookChannelName ="4";
			 curBookInfo.bookChannelIndex = 5;
			
			
			Intent EpgWarn = new Intent(Main.this, EpgWarn.class);

			
			EpgWarn.putExtra("bookinfo", curBookInfo);
			startActivity(EpgWarn);
			
		
		/*z	banner.show(SysApplication.iCurChannelId);
			Message msg = new Message();
			msg.what = MESSAGE_SHOW_DIGITALKEY;
			msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
			mUiHandler.sendMessage(msg);*/
		}

			break;

		case KeyEvent.KEYCODE_MENU: {
/*
			Intent zbzcd_zIntent = new Intent(Main.this, zhibo_menu_z.class);

			startActivity(zbzcd_zIntent);
*/

			Intent mIntent = new Intent();
			mIntent.setComponent(new ComponentName(
					"com.SysSettings.main",
					"com.SysSettings.main.MainActivity"));
			
			try { 
				startActivity(mIntent);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
			break;

		case KeyEvent.KEYCODE_DPAD_RIGHT: {
			bKeyUsed = true;
			this.onVkey(keyCode);
		}
			break;
		case KeyEvent.KEYCODE_F1:// show/hide pip
			/*if (!isPipPlay) {
				isPipPlay = true;
				objApplication.initPipPlayer(point.x, point.y);
				objApplication.startPipPlay();
			} else {
				isPipPlay = false;
				objApplication.stopPipPlay();
			}*/
			break;
		case KeyEvent.KEYCODE_F2:// Swap pip/main
		/*	if (isPipPlay) {
				objApplication.swapPipMain();
			}*/
			break;
		case KeyEvent.KEYCODE_F3:// play pip pre
		/*	if (isPipPlay) {
				objApplication.playPrePipChannel(true);
			}*/
			break;
		case KeyEvent.KEYCODE_F4:// play pip next
		/*	if (isPipPlay) {
				objApplication.playNextPipChannel(true);
			}*/
			break;

		/*
		 * Key deal for VOD of SCN.
		 */
	/*	case Class_Constant.KEYCODE_KEY_PAUSE: {
			bKeyUsed = true;
			onVkey(keyCode);
			break;
		}

		case Class_Constant.KEYCODE_KEY_FASTBACK:
		case KeyEvent.KEYCODE_F8: {
			bKeyUsed = true;
			onVkey(keyCode);
			break;
		}*/
			
		}

		if (bKeyUsed) {
			return bKeyUsed;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

public void getYiHanVolumeAD(int channelId) {
		// ads = new AdStrategy(mContext, "yinhedtvpf", getParams());
		Channel curChannel = DVB.getManager().getChannelDBInstance().getChannel(channelId);
		ads = new AdStrategy(mContext, Advertise_Constant.LIVEPLAY_ID_VOLUME, Common.getParams(
				curChannel.serviceId + "", Advertise_Constant.LIVEPLAY_ID_VOLUME, Advertise_Constant.TEMP_IP_ADDRESS));
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
	public class UI_Handler extends Handler {
		
		WeakReference<Main> mActivity;

		UI_Handler(Main activity) {
			mActivity = new WeakReference<Main>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final Main theActivity = mActivity.get();

			switch (msg.what) {
			
			case MESSAGE_VOLUME_SHOW:
				
				Log.i(tag, "UI_Handler---->MESSAGE_VOLUME_SHOW");
				
				if(theActivity.banner.bannerToast!=null)
					theActivity.banner.bannerToast.cancel();
				
			if(	theActivity.volume_layout.getVisibility()==View.INVISIBLE){
				
				Log.i(tag, "UI_Handler---->MESSAGE_VOLUME_SHOW------>volume_layout.getVisibility()="+theActivity.volume_layout.getVisibility());
				theActivity.volume_layout.setVisibility(View.VISIBLE);
		         theActivity.volume_layout.requestLayout();
		
			}

			Log.i(tag, "UI_Handler---->MESSAGE_VOLUME_SHOW------>volume_layout.getVisibility()="+theActivity.volume_layout.getVisibility());
		    theActivity.mUiHandler.removeMessages(MESSAGE_VOLUME_DISAPPEAR);
			theActivity.mUiHandler.sendEmptyMessageDelayed(
					MESSAGE_VOLUME_DISAPPEAR, 2000);
			
			
			break;
			
			case MESSAGE_VOLUME_DISAPPEAR:
				
				if(	theActivity.volume_layout.getVisibility()==View.VISIBLE)
					theActivity.volume_layout.setVisibility(View.INVISIBLE);

			
				break;
			
			case MESSAGE_PLAY_PRE:
				
				theActivity.reportToServer();
				
			theActivity.objApplication.playChannel(theActivity.tempChannelID,
					true);
			
			theActivity.tempChannelID=-1;
			
	
				break;
			case MESSAGE_PLAY_NEXT:
				
				theActivity.reportToServer();
				
				theActivity.objApplication.playChannel(theActivity.tempChannelID,
						true);
				
				theActivity.tempChannelID=-1;
				
				break;
			case MESSAGE_SHOW_AUTOSEARCH: {
				
				
				Class_Global.SetAimMenuID(6);
				
				theActivity.searchPromptDiaog = DialogUtil.showPromptDialog(theActivity.mContext, "当前节目为空，请搜索！",null, null, null,
						new DialogBtnOnClickListener() {

							@Override
							public void onSubmit(DialogMessage dialogMessage) {
								
			
								Intent mIntent = new Intent();
								mIntent.setComponent(new ComponentName(
										"com.chonghong.dtv_scan",
										"com.chonghong.dtv_scan.Dtv_Scan_Enter"));
								mIntent.putExtra("ScanMode", 1); //autoscan without scan_enter_menu
								
								try { 
									theActivity.startActivity(mIntent);
								} catch (Exception e) {
									e.printStackTrace();
								}
								
								
									if (dialogMessage.dialog != null
											&& dialogMessage.dialog.isShowing()) {
										dialogMessage.dialog.cancel();
									}

							
							}

							@Override
							public void onCancel(DialogMessage dialogMessage) {
								if (dialogMessage.dialog != null
										&& dialogMessage.dialog.isShowing()) {
									dialogMessage.dialog.cancel();
								}
							}
						});

			}
			
				break;

			case MESSAGE_HANDLER_DIGITALKEY: {

				/*if (theActivity.iKey == 1111) {l;
					BookInfo info = new BookInfo();
					info.bookTimeStart = "12:00";
					info.bookChannelIndex = 1;
					info.bookChannelName = "cctv";
					info.bookDay = "20141231";
					info.bookEnventName = "zhenghuanchjuan";

					theActivity.objApplication.dvbBookDataBase
							.BookInfoCommit(info);
					Toast.makeText(theActivity, "add book ", Toast.LENGTH_SHORT)
							.show();
					theActivity.iKeyNum = 0;
					theActivity.iKey = 0;
					theActivity.mUiHandler
							.sendEmptyMessage(MESSAGE_DISAPPEAR_DIGITAL);
				} else if (theActivity.iKey == 2418) {
					Toast.makeText(theActivity, R.string.str_join_tiaoshi,
							Toast.LENGTH_LONG).show();
					theActivity.startActivity(new Intent(theActivity,
							Factory.class));
					theActivity.iKeyNum = 0;
					theActivity.iKey = 0;
					theActivity.mUiHandler
							.sendEmptyMessage(MESSAGE_DISAPPEAR_DIGITAL);
				} else if (theActivity.iKey == 2048) {


					theActivity.tvRootDigitalkey.setVisibility(View.INVISIBLE);
					theActivity.tvRootDigitalKeyInvalid
							.setVisibility(View.VISIBLE);

					theActivity.mUiHandler
							.removeMessages(MESSAGE_HANDLER_DIGITALKEY);
					theActivity.mUiHandler
							.sendEmptyMessage(MESSAGE_DISAPPEAR_DIGITAL);

					theActivity.iKeyNum = 0;
					theActivity.iKey = 0;

				} else */
				
				
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
					
					theActivity.reportToServer();
					
					int succ = theActivity.objApplication.playChannelByLogicNo(
							theActivity.iKey, true);
					// int succ =
					// theActivity.objApplication.playChannelKeyInput(theActivity.iKey,true);
					if (succ < 0) {
						theActivity.tvRootDigitalkey
								.setVisibility(View.INVISIBLE);
						theActivity.tvRootDigitalKeyInvalid
								.setVisibility(View.VISIBLE);
						theActivity.id_dtv_channel_name.setVisibility(View.INVISIBLE);
					} else {

						
						if(	theActivity.volume_layout.getVisibility()==View.VISIBLE)
							theActivity.volume_layout.setVisibility(View.INVISIBLE);

						theActivity.banner.show(SysApplication.iCurChannelId);
	
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
				theActivity.tvRootDigitalKeyInvalid.setVisibility(View.GONE);

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

				theActivity.id_dtv_channel_name.setVisibility(View.INVISIBLE);
			}
				break;

			case MESSAGE_CA_SHOWNOTICE: {
		      theActivity.flCaInfo.setVisibility(View.VISIBLE); 
				String tmp = msg.obj.toString();
				if (tmp != null) {
					theActivity.tvCaInfo.setText(tmp);
				}

			}
				break;

			case MESSAGE_CA_HIDENOTICE: {
				theActivity.flCaInfo.setVisibility(View.INVISIBLE);
			}
				break;
			case MESSAGE_START_RECORD: {

			}
				break;
			case MESSAGE_STOP_RECORD: {

			}
				break;
			case Class_Constant.BACK_TO_LIVE:
				if (programBannerDialog != null) {
					programBannerDialog.dismiss();
					Toast.makeText(MyApp.getContext(), "退回到直播模式", Toast.LENGTH_SHORT).show();
					Log.i("mmmm", "退回到直播模式");
				}
				break;
			case Class_Constant.TOAST_BANNER_PROGRAM_PASS:
				curChannelPrograms = CacheData.getCurPrograms();
				showDialogBanner();
				break;
			}
		}
	}

	public Chronometer getTimer() {
		return mTimer;
	}

	private void Display_Program_Num(int channelId) {

		int num[] = {R.drawable.num_0,R.drawable.num_1,R.drawable.num_2,R.drawable.num_3,R.drawable.num_4,R.drawable.num_5,
				R.drawable.num_6,R.drawable.num_7,R.drawable.num_8,R.drawable.num_9};
		int pos1=0,pos2=0,pos3=0;
		
		if(channelId<1000){
			pos1 = channelId/100;
			pos2 = (channelId-pos1*100)/10;
			pos3 = (channelId-pos1*100-pos2*10);
		}
		
		dtv_digital_1.setBackgroundResource(num[pos1]); 
		dtv_digital_2.setBackgroundResource(num[pos2]);
		dtv_digital_3.setBackgroundResource(num[pos3]);

	}
	/*
	void Display_Program_Num(int channelId) {

		if (channelId < 10) {
			switch (channelId % 10) {
			case 0:

				dtv_digital_3.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.num_0));
				break;
			case 1:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_1));
				break;
			case 2:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_2));
				break;
			case 3:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_3));
				break;

			case 4:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_4));
				break;
			case 5:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_5));
				break;
			case 6:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_6));
				break;
			case 7:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_7));
				break;
			case 8:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_8));
				break;

			case 9:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_9));
				break;

			}

			dtv_digital_2.setBackgroundDrawable(getResources()
					.getDrawable(R.drawable.num_0));

			dtv_digital_1.setBackgroundDrawable(getResources()
					.getDrawable(R.drawable.num_0));
		} else if (channelId >= 10 && channelId < 100) {

			switch (channelId / 10) {
			case 0:

				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_0));
				break;
			case 1:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_1));
				break;
			case 2:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_2));
				break;
			case 3:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_3));
				break;

			case 4:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_4));
				break;
			case 5:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_5));
				break;
			case 6:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_6));
				break;
			case 7:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_7));
				break;
			case 8:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_8));
				break;

			case 9:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_9));
				break;
			}
			switch (channelId % 10) {
			case 0:

				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_0));
				break;
			case 1:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_1));
				break;
			case 2:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_2));
				break;
			case 3:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_3));
				break;

			case 4:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_4));
				break;
			case 5:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_5));
				break;
			case 6:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_6));
				break;
			case 7:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_7));
				break;
			case 8:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_8));
				break;

			case 9:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_9));
				break;

			}

			dtv_digital_1.setBackgroundDrawable(getResources()
					.getDrawable(R.drawable.num_0));

		} else if (channelId >= 100 && channelId <=999) {

			switch (channelId / 100) {
			case 0:

				dtv_digital_1.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_0));
				break;
			case 1:
				dtv_digital_1.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_1));
				break;
			case 2:
				dtv_digital_1.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_2));
				break;
			case 3:
				dtv_digital_1.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_3));
				break;

			case 4:
				dtv_digital_1.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_4));
				break;
			case 5:
				dtv_digital_1.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_5));
				break;
			case 6:
				dtv_digital_1.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_6));
				break;
			case 7:
				dtv_digital_1.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_7));
				break;
			case 8:
				dtv_digital_1.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_8));
				break;

			case 9:
				dtv_digital_1.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_9));
				break;

			}

			switch ((channelId / 10) % 10) {
			case 0:

				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_0));
				break;
			case 1:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_1));
				break;
			case 2:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_2));
				break;
			case 3:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_3));
				break;

			case 4:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_4));
				break;
			case 5:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_5));
				break;
			case 6:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_6));
				break;
			case 7:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_7));
				break;
			case 8:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_8));
				break;

			case 9:
				dtv_digital_2.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_9));
				break;
			}
			switch (channelId % 10) {
			case 0:

				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_0));
				break;
			case 1:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_1));
				break;
			case 2:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_2));
				break;
			case 3:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_3));
				break;

			case 4:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_4));
				break;
			case 5:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_5));
				break;
			case 6:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_6));
				break;
			case 7:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_7));
				break;
			case 8:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_8));
				break;

			case 9:
				dtv_digital_3.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.num_9));
				break;

			}

		}
		
		//banner.show(SysApplication.iCurChannelId);

	}
*/

	private boolean onVkey(int ri_KeyCode) {

		boolean b_Result = false;
		Common.LOGD(" main->onVkey: "+ri_KeyCode);
		switch (ri_KeyCode) {
		case Class_Constant.KEYCODE_INFO_KEY: {
			
			reportToServer();
			
			objApplication.playLastChannel();
			banner.show(SysApplication.iCurChannelId);
			

			Message msg = new Message();
			msg.what = MESSAGE_SHOW_DIGITALKEY;
			msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
	    	mUiHandler.sendMessage(msg);
		}

			break;
		case Class_Constant.VKEY_EMPTY_DBASE: {
			Common.LOGD("no program,notify autosearch !");
			mUiHandler.sendEmptyMessage(MESSAGE_SHOW_AUTOSEARCH);
		}
			break;

		case Class_Constant.KEYCODE_MENU_KEY: {

		}
			break;
		case Class_Constant.KEYCODE_OK_KEY:
		case KeyEvent.KEYCODE_ENTER: {
			
			//这个是之前项目遗留下来的，用不上，后面会删掉
//			if (mUiHandler.hasMessages(MESSAGE_HANDLER_DIGITALKEY)) {
//				mUiHandler.removeMessages(MESSAGE_HANDLER_DIGITALKEY);
//				mUiHandler.sendEmptyMessage(MESSAGE_HANDLER_DIGITALKEY);
//
//			} else {
//				// show channel list
//				
//
//				Intent Huantai_fast_zIntentz = new Intent(Main.this,
//						FastChangeChannel_z.class);
//
//				startActivity(Huantai_fast_zIntentz);
//				
//	
//			}
			//时移模块
			if (tvRootDigitalkey.isShown()) {
				//当数字键显示的时候，响应数字换台逻辑
				
				
			} else {
				
				//获取当前道信息
				ChannelDB db=DVB.getManager().getChannelDBInstance();
				PlayingInfo thisPlayingInfo = db.getSavedPlayingInfo();
				//获取当前Channel详细信息
				Channel DBchan = db.getChannel(thisPlayingInfo.mChannelId );
				
				//获取节目
				PlayVideo.getInstance().getProgramInfo(mUiHandler, DBchan);
				
			}

		}
			break;
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY: {
			// show channel list		

			Intent Huantai_fast_zIntentz = new Intent(Main.this,
					ChannelList.class);

			startActivity(Huantai_fast_zIntentz);

		}

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
			mUiHandler.removeMessages(MESSAGE_SHOW_DIGITALKEY);
			mUiHandler.removeMessages(MESSAGE_DISAPPEAR_DIGITAL);

			iKeyNum++;
			Common.LOGI("onVkey-key<" + iKey + ">");

			if (iKeyNum > 0 && iKeyNum <= 3) {
				iKey = (ri_KeyCode - Class_Constant.KEYCODE_KEY_DIGIT0)+iKey * 10;
			}

				Common.LOGI("get digital key   =  " + ri_KeyCode);
				Common.LOGI("iKey value   =  " + iKey);

				bDigitalKeyDown = true;
				tvRootDigitalKeyInvalid.setVisibility(View.GONE);
				tvRootDigitalkey.setVisibility(View.VISIBLE);

				
				Display_Program_Num(iKey);
			

				if (iKey >= 100) {
					mUiHandler.sendEmptyMessageDelayed(
							MESSAGE_HANDLER_DIGITALKEY, 2000);
				} else {
					mUiHandler.sendEmptyMessageDelayed(
							MESSAGE_HANDLER_DIGITALKEY, 4000);
				}
			}
		

			break;

		/*
		 * Key deal for VOD of SCN.
		 */
		case Class_Constant.KEYCODE_KEY_PAUSE: {
			doTimeshift(0);
			break;
		}
		case Class_Constant.KEYCODE_KEY_FASTBACK:
		case KeyEvent.KEYCODE_F8: {
			doTimeshift(1);
			break;
		}
		}

		return b_Result;
	}

	/*
	 * Deal with Timeshift. Type: 0-pause, 1-fast back
	 */
	private void doTimeshift(int type) {
		String timeshiftTypeString = null;

		switch (type) {
		case 0:// Pause
			timeshiftTypeString = "pausetv";
			break;
		case 1:// fast back
			timeshiftTypeString = "ffbtv";
			break;
		default:
			return;
		}
		Toast infoToast = null;
		int chanId = SysApplication.iCurChannelId;
		Channel curChannel = DVB.getManager().getChannelDBInstance()
				.getChannel(chanId);
		if (curChannel == null) {
			infoToast = Toast.makeText(this, getString(R.string.timeshift_fail)
					+ Class_Constant.TIMESHIFT_ERRORCODE_INVALID_LIVE_CHAN,
					Toast.LENGTH_SHORT);
			infoToast.setGravity(Gravity.BOTTOM, 0, 200);
			infoToast.show();
			return;
		}
		boolean bSupport = DVB.getManager().getChannelDBInstance()
				.getChannelExtendFieldValue(chanId, "timeshift_support", false);
		if (bSupport) {
			DVB.getManager().getDefaultLivePlayer().stop();
			DVB.getManager().getDefaultLivePlayer().blank();

			Intent vodPauseIntent = new Intent();
			vodPauseIntent.setComponent(new ComponentName("com.changhong.vod",
					"com.changhong.vod.RootActivity"));
			vodPauseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			vodPauseIntent.putExtra("appType", timeshiftTypeString);
			vodPauseIntent.putExtra("frequency",
					(curChannel.frequencyKhz * 1000) + "");// Use Hz
			vodPauseIntent.putExtra("serviceid", curChannel.serviceId + "");
			Common.LOGI("TimeShift::curChannel.frequencyKhz = "
					+ curChannel.frequencyKhz + ", curChannel.serviceId = "
					+ curChannel.serviceId);
			try {
				startActivity(vodPauseIntent);
			} catch (ActivityNotFoundException e) {
				infoToast = Toast.makeText(this,
						R.string.timeshift_vodnotfound, Toast.LENGTH_SHORT);
				infoToast.setGravity(Gravity.BOTTOM, 0, 200);
				infoToast.show();
				return;
			} catch (Exception e) {
				infoToast = Toast.makeText(this,
						getString(R.string.timeshift_fail)
								+ Class_Constant.TIMESHIFT_ERRORCODE_FAIL,
						Toast.LENGTH_SHORT);
				infoToast.setGravity(Gravity.BOTTOM, 0, 200);
				infoToast.show();
				e.printStackTrace();
				return;
			}
			return;
		}
		infoToast = Toast.makeText(this, R.string.timeshift_dissupport,
				Toast.LENGTH_SHORT);
		infoToast.setGravity(Gravity.BOTTOM, 0, 200);
		infoToast.show();
	}

	@Override
	protected void onStart() {
		super.onStart();
		scene.init(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		scene.release();
	}

	private void findView() {
		surfaceView=(SurfaceView)findViewById(R.id.surfaceView1);
		flCaInfo = (RelativeLayout) findViewById(R.id.id_root_CA_info);
		tvCaSubtitleDown = (CAMarquee) findViewById(R.id.id_ca_subtitleDown);
		tvCaSubtitleUp = (CAMarquee) findViewById(R.id.id_ca_subtitleUp);
		tvCaInfo = (TextView) findViewById(R.id.id_root_ca_init_textview);

		flNoSignal = (RelativeLayout) findViewById(R.id.id_root_nosignal_info);
		
	    
		tvRootDigitalkey = (LinearLayout) findViewById(R.id.id_dtv_digital_root);
		tvRootDigitalKeyInvalid = (RelativeLayout) findViewById(R.id.id_dtv_digital_root_invalid);

		id_dtv_channel_name = (TextView) findViewById(R.id.id_dtv_channel_name);
		dtv_digital_1 = (ImageView) findViewById(R.id.dtv_digital_1);
		dtv_digital_2 = (ImageView) findViewById(R.id.dtv_digital_2);
		dtv_digital_3 = (ImageView) findViewById(R.id.dtv_digital_3);

		flPftUpdate = (FrameLayout) findViewById(R.id.id_root_PFTUpdate_info);
		flTimeShift = (FrameLayout) findViewById(R.id.id_root_timeshift_support);
		
		
		 volume_progress_view = (ProgressBar) findViewById(R.id.volume_progress_view);
		 volume_value = (TextView) findViewById(R.id.volume_value);
		 
		  volume_layout = (RelativeLayout) findViewById(R.id.volume_layout);
		 
adPlayer = (AdPlayer) findViewById(R.id.adplayer_volume);
		adPlayer.setDefaultAd(R.drawable.default_img, 1);

		// layout_set_activity_z = (LinearLayout)
		// findViewById(R.id.layout_set_activity_z);

	}

	private void initValue() {
		msignalRecever = new signalRecever();
		registerReceiver(msignalRecever, new IntentFilter(
				TunerInfo.TunerInfo_Intent_FilterName));

		mHomeReceiver = new homeReceiver();
		registerReceiver(mHomeReceiver, new IntentFilter("HOME_PRESSED"));

		// string array
		str_title = getResources().getString(R.string.str_zhn_information);
		str_details_exitdtv = getResources().getString(
				R.string.str_zhn_isexitdtv);
		s_IsAutoScan = getResources().getString(R.string.str_zhn_diaissearch);
		s_IsUpdate = getResources().getString(R.string.str_zhn_updatesearch);

		IntentFilter filter = new IntentFilter();
		filter.addAction("com.changhong.action.DTV_CHANGED");
		filter.addAction("com.changhong.action.CTL_CHANGED");
		registerReceiver(dtvctlReceiver, filter);
		
		volleyTool = VolleyTool.getInstance();
		mReQueue = volleyTool.getRequestQueue();
		if (null == processData) {
			processData = new ProcessData();
		}
		initTimeshiftData();
	}

	BroadcastReceiver dtvctlReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("com.changhong.action.DTV_CHANGED")) {
				// 处理数字电视的广播
				int chNum = intent.getIntExtra("number", 1);
				if (chNum <= objApplication.dvbDatabase.getChannelCount()) {
					objApplication.playChannel(chNum, false);
					banner.show(SysApplication.iCurChannelId);
					Message msg = new Message();
					
					msg.what = MESSAGE_SHOW_DIGITALKEY;
					msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
					mUiHandler.sendMessage(msg);
				} else {

				}
			}
		}
	};

	private class signalRecever extends BroadcastReceiver {

		String TunerInfo_Locked = "Z_SignalLocked";

		@Override
		public void onReceive(Context context, Intent intent) {

			Bundle myBundle = intent.getExtras();
			boolean bIsLocked = myBundle.getBoolean(TunerInfo_Locked);

			if (!bIsLocked) {
				flNoSignal.setVisibility(View.VISIBLE);
				objApplication.blackScreen();
			} else {
				flNoSignal.setVisibility(View.GONE);
			}
		}

	}

	private class homeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			String strAction = arg1.getAction();

			if (strAction.equals("HOME_PRESSED")) {
				finish();
			}

		}

	}

	Runnable digital_runable = new Runnable() {

		public void run() {
			if (tvRootDigitalkey.isShown() == true && iKeyNum != 0
					&& iKey != -1) {

				Common.LOGI("jump >>  " + iKey + "  program");

				// For backdoor
				if (iKey == 2416)// Open menu of scan
				{
					// Intent intent_search = new
					// Intent(TVroot.this,TVsearch_menu.class);
					// startActivity(intent_search);
					tvRootDigitalkey.setVisibility(View.GONE);
					iKeyNum = 0;
					iKey = 0;
					return;
				} else if (iKey == 2417)// Open menu of EPG
				{
					// Intent my_CAIntent =new Intent(TVroot.this,TVca.class);
					// startActivity(my_CAIntent);
					tvRootDigitalkey.setVisibility(View.GONE);
					iKeyNum = 0;
					iKey = 0;
					return;
				} else if (iKey == 2418)// Open menu of DTV setting
				{
					// Intent my_SetIntent =new Intent(TVroot.this,TVset.class);
					// startActivity(my_SetIntent);
					tvRootDigitalkey.setVisibility(View.GONE);
					iKeyNum = 0;
					iKey = 0;
					return;
				}

				Channel mo_CurChannel = objApplication.dvbDatabase
						.getChannel(iKey);

				if (mo_CurChannel == null) {
					Common.LOGI("jump fail !");
					tvRootDigitalkey.setVisibility(View.GONE);
					iKeyNum = 0;
					iKey = 0;
					tvRootDigitalKeyInvalid.setVisibility(View.VISIBLE);
					handler_digital.postDelayed(dissmiss_runnable, 2000);
					return;
				} else {

					// Intent mintent = new Intent(Main.this, Banner.class);
					// mintent.putExtra(INTENT_CHANNEL_INDEX, iKey);
					// startActivity(mintent);
					banner.show(SysApplication.iCurChannelId);
					tvRootDigitalkey.setVisibility(View.GONE);
					iKeyNum = 0;
					iKey = 0;
				}

			} else {
				Common.LOGI("jump fail !");
				tvRootDigitalkey.setVisibility(View.GONE);
				iKeyNum = 0;
				iKey = 0;
				tvRootDigitalKeyInvalid.setVisibility(View.VISIBLE);
				handler_digital.postDelayed(dissmiss_runnable, 2000);
			}
		}
	};

	Runnable dissmiss_runnable = new Runnable() {

		public void run() {

			tvRootDigitalKeyInvalid.setVisibility(View.GONE);

		}
	};

	/*
	 * needEnter: true-need enter false-dont need enter, if it is this,
	 * subMenuType is invalid. subMenuType: 0x1000-auto scan menu 0x1001-manual
	 * scan menu 0x1002-full scan menu 0x2000-epg menu 0x3000-CA setting
	 */
	private int enterSubMenu() {
		Intent thisIntent = null, toDoIntent = null;
		int returnValue = -1;
		int subType = -1;

		thisIntent = getIntent();
		subType = thisIntent.getIntExtra("subType", -1);
		Common.LOGI("enterSubMenu->Intent.subType:" + subType);

		switch (subType) {
		case 1:// Live, already dealed, here skip
		{
			break;
		}
		case 2:// NVOD
		{
			// TODO:
			break;
		}
		case 3:// watch back
		{
			// TODO:
			break;
		}
		case 4:// Broadcast
		{
			// TODO:
			break;
		}
		case 5:// BOOK
		{
			// TODO:
			break;
		}
		case 6:// Date to record
		{
			// TODO:
			break;
		}
		case 7:// My recorded
		{
			// TODO:
			break;
		}
		case 8:// channel list
		{
			objApplication.playLastChannel();

			returnValue = 0x1008;
			toDoIntent = new Intent(this, Epg_z.class);
			toDoIntent.putExtra("destMenu", returnValue);
			toDoIntent.putExtra("curType", getIntent()
					.getIntExtra("curType", 0));
			startActivity(toDoIntent);
			break;
		}
		case 9:// channel dating
		{
		/*	objApplication.playLastChannel();

			returnValue = 0x1009;
			toDoIntent = new Intent(this, Epg_z.class);
			toDoIntent.putExtra("destMenu", returnValue);
			startActivity(toDoIntent);
			break;*/
		}
		case 10:// scan
		{
			returnValue = 0x1010;
			toDoIntent = new Intent();
			toDoIntent.setComponent(new ComponentName("com.chonghong.dtv_scan",
					"com.chonghong.dtv_scan.Dtv_Scan_Enter"));
			toDoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(toDoIntent);
			finish();
			break;
		}
		case 11:// Ca info
		{
			// TODO:
			break;
		}
		}
		return returnValue;
	}

	@Override
	public void onExecute(Intent intent) {
		feedback.begin(intent);
		if (intent.hasExtra("_scene")
				&& intent.getStringExtra("_scene").equals(
						"com.changhong.app.dtv:Main")) {
			if (intent.hasExtra("_command")) {
				String command = intent.getStringExtra("_command");
				if ("key1".equals(command)) {
					changenextchannel();
					feedback.feedback("下一个频道", Feedback.SILENCE);
					// 频道切换处理

				}
				if ("key2".equals(command)) {
					changeprechannel();
					feedback.feedback("上一个频道", Feedback.SILENCE);
					// 频道切换处理

				}
			}
		}
	}

	@Override
	public String onQuery() {
		sceneJson = "{" + "\"_scene\": \"com.changhong.app.dtv:Main\","
				+ "\"_commands\": {" + "\"key1\": [ \"下一个频道\", \"频道加\" ],"
				+ "\"key2\": [ \"上一个频道\", \"频道减\" ]" + "}" + "}";
		return sceneJson;
	}

	private void changenextchannel() {
		if ((SysApplication.iCurChannelId != -1)
				&& (objApplication.dvbDatabase.getChannelCount() >= 0)) {
			objApplication.playNextChannel(true);
			banner.show(SysApplication.iCurChannelId);
			Message msg = new Message();
			msg.what = MESSAGE_SHOW_DIGITALKEY;
			msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
			mUiHandler.sendMessage(msg);
		}
	}

	private void changeprechannel() {
		if ((SysApplication.iCurChannelId != -1)
				&& (objApplication.dvbDatabase.getChannelCount() >= 0)) {
			objApplication.playPreChannel(true);
			banner.show(SysApplication.iCurChannelId);
			Message msg = new Message();
			msg.what = MESSAGE_SHOW_DIGITALKEY;
			msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
			mUiHandler.sendMessage(msg);
		}
	}
	
	private void reportToServer(){/*
		try{
			Intent ii=new Intent("com.guoantvbox.dataacquire.service");
			ii.putExtra("TIME",startTime); //yyyyMMddHHmmSS 为时间：年月日时分秒
			ii.putExtra("APPID", "com.changhong.app.dtv"); // mAppId 应用唯一标识，用于区分不同应用产生的用户行为数据，以应用的包名作为标识。;
			ii.putExtra("BUSIID", "01"); // mBusiid 业务标识  具体定义如下
			String content = startTime+";"+Utils.getCurTime()+";"+objApplication.getCurPlayingChannel().logicNo+";"+objApplication.getCurPlayingChannel().name
					+";"+objApplication.getCurPlayingChannel().serviceId+";"+objApplication.dvbEpg.getPfInfo(objApplication.getCurPlayingChannel()).getPresent().getName();
			ii.putExtra("CONTENT",content); // 内容，具体定义如下
			startService(ii);
			Log.e("DVB", "content>>>"+content);
			startTime  = Utils.getCurTime();
		}catch(Exception e){
			e.printStackTrace();
		}
	*/}

	private void initTimeshiftData(){
		Channel DBchan=null;
		// 获取频道是否支持时移和频道logoURL
		
		//获取当前频道信息
		ChannelDB db=DVB.getManager().getChannelDBInstance();
		PlayingInfo thisPlayingInfo = DVB.getManager().getChannelDBInstance().getSavedPlayingInfo();
		//获取当前Channel详细信息
		if(thisPlayingInfo!=null)
		{
			DBchan = db.getChannel(thisPlayingInfo.mChannelId );
		}
		if(null==DBchan||TextUtils.isEmpty(DBchan.is_ttv))
		{
			String URL = processData.getChannelsInfo();
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
					new Response.Listener<org.json.JSONObject>() {

						@Override
						public void onResponse(org.json.JSONObject arg0) {
							// TODO Auto-generated method stub
							Log.i("mmmm", "Main=getUserChannel:" + arg0);

							HandleLiveData.getInstance().dealChannelExtra(arg0);
						}
					}, errorListener);
			jsonObjectRequest.setTag(Main.class.getSimpleName());// 设置tag,cancelAll的时候使用
			mReQueue.add(jsonObjectRequest);
		}
	}
	private Response.ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			Log.i("mmmm", "Main=error：" + arg0);
		}
	};
	
	/* show time-shifting banner dialog */
	public void showDialogBanner() {
		
		//获取当前道信息
		ChannelDB db=DVB.getManager().getChannelDBInstance();
		PlayingInfo thisPlayingInfo = db.getSavedPlayingInfo();
		//获取当前Channel详细信息
		Channel DBchan = db.getChannel(thisPlayingInfo.mChannelId );
		
		
		if(null==curChannelPrograms||curChannelPrograms.size()<=0){
			Toast.makeText(Main.this, "节目信息为空，不能进入时移模式", Toast.LENGTH_SHORT).show();
			return;
		}
		if (programBannerDialog != null) {
			programBannerDialog.cancel();
		}
		programBannerDialog = new BannerDialog(this, DBchan, curChannelPrograms, mUiHandler, surfaceView);
		programBannerDialog.show();
	}
}
