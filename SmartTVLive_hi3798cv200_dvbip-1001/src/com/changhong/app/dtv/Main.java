package com.changhong.app.dtv;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageParser.NewPermissionInfo;
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
import com.changhong.app.book.BookDataBase;
import com.changhong.app.book.BookInfo;
import com.changhong.app.constant.Advertise_Constant;
import com.changhong.app.constant.Class_Constant;
import com.changhong.app.constant.Class_Global;
import com.changhong.app.dtv.DialogUtil.DialogBtnOnClickListener;
import com.changhong.app.dtv.DialogUtil.DialogMessage;
import com.changhong.app.timeshift.common.CacheData;
import com.changhong.app.timeshift.common.MyApp;
import com.changhong.app.timeshift.common.NetworkUtils;
import com.changhong.app.timeshift.common.PlayVideo;
import com.changhong.app.timeshift.common.ProcessData;
import com.changhong.app.timeshift.common.ProgramInfo;
import com.changhong.app.timeshift.common.VolleyTool;
import com.changhong.app.timeshift.datafactory.BannerDialog;
import com.changhong.app.timeshift.datafactory.HandleLiveData;
import com.changhong.app.utils.SortData;
import com.changhong.app.utils.TestFunc;
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
    public static final String   TAG = "GHLive";
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
//	private CAMarquee tvCaSubtitleDown, tvCaSubtitleUp;
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
	private static FrameLayout flTimeShift=null;

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
		
		P.d("Main OnCreate !");
		startTime  = Utils.getCurTime();
		
		// init views
		findView();
		initValue();
		
	    checkChannel();
		registerBroadReceiver();
		SetDtvStatus(true);		
		
		//下面是测试函数
		//testEnv(); 
		
		
	}

	/**
	 * 
	 */
	private void testEnv() {
		
		//测试预约节目触发
		new BootCastReceiver2().start(mContext); 
		
		//测试欢网应用商店需调用的第三方jar包中的接口
		try {
			TestFunc.testThirdPackage(); 
		} catch (Exception e) {  
			e.printStackTrace();
		}
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
			
			
			
		P.i("receive  showBanneForYuYueDialog  broadcast" );
		//	onVkey(Class_Constant.KEYCODE_INFO_KEY);
	
		}
	};

	private void checkChannel() {
		
		
		P.i("checkChannel()");
	   //getIntent().getIntExtra for GHprj
		if (getIntent().getStringExtra("param1") != null)// Live
		{
			P.d("getIntent().getStringExtra(\"param1\")=" + getIntent().getStringExtra("param1"));		
		}
		
		if (getIntent().getStringExtra("param2") != null)// Live
		{
			P.d("getIntent().getStringExtra(\"param2\")=" + getIntent().getStringExtra("param2"));		
		}
		
		int iBouqtID = getIntent().getIntExtra("bouquetId", -1);
		int iFreq = getIntent().getIntExtra("frequency", -1);
		int iSerId = getIntent().getIntExtra("serviceId", -1);
		int iTsId = getIntent().getIntExtra("tsId", -1);
		
		P.d("getIntent().getStringExtra(num3~6)=" + iBouqtID+","+iFreq+","+iSerId+","+iTsId);	
		
		if ( iSerId!= -1 && iTsId!=-1)// Live
		{
			Channel toPlayChannel = objApplication.dvbDatabase
					.getChannelByTsIdAndServiceId(iTsId, iSerId);
			if (toPlayChannel != null) {
				objApplication.playChannel(toPlayChannel.chanId, false);
				P.d("GOT and play channel pointed by Intent params ts&serId");
				banner.show(toPlayChannel.chanId);				
				Message msg = new Message();
				msg.what = MESSAGE_SHOW_DIGITALKEY;
				msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
				mUiHandler.sendMessage(msg);				
			}else {
				P.d("DONT get channel pointed by Intent from Launcher ");	
			}
			return;
		} else if (iBouqtID != -1 || iFreq != -1) {
			Channel[] allChannels = objApplication.dvbDatabase.getChannelsAll();
			
			if (iBouqtID != -1) {
				for (Channel ch : allChannels) {
					if (ch.favorite == iBouqtID) {
						objApplication.playChannel(ch.chanId, false);
						P.d("GOT and play channel pointed by Intent params iBouqtID ");
						banner.show(ch.chanId);
						Message msg = new Message();
						msg.what = MESSAGE_SHOW_DIGITALKEY;
						msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
						mUiHandler.sendMessage(msg);
						return;
					}
				}

			}
			if (iFreq != -1) {
				for (Channel ch1 : allChannels) {
					if (ch1.frequencyKhz == iFreq) {
						objApplication.playChannel(ch1.chanId, false);
						P.d("GOT and play channel pointed by Intent params iFreq ");
						banner.show(ch1.chanId);
						Message msg = new Message();
						msg.what = MESSAGE_SHOW_DIGITALKEY;
						msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
						mUiHandler.sendMessage(msg);
						return;
					}
				}
			}

		}
		
		if (getIntent().getIntExtra("subType", -1) == 1)// Live
		{
			int tsId = getIntent().getIntExtra("param1", -1);
			int serviceId = getIntent().getIntExtra("param2", -1);
			if (tsId < 0 || serviceId < 0) {
				P.d("The Param1 or Param2 passed-in is invalid");
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
			
			P.d("getIntExtra(chnumber)");
			
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
			
			P.i("resize video size");
		   objApplication.dvbPlayer.setSize(builder.build());
		   
		}
		
		
		
		P.d("Main onResume  start!" );
		
		SetDtvStatus(true);
		
		int curChanCount = objApplication.dvbDatabase.getChannelCountSC();

		if (curChanCount <= 0) {
			
			
			P.i("弹出没有节目，自动搜索的dialog");
			onVkey(Class_Constant.VKEY_EMPTY_DBASE);
			
			return;
			
		} else {
	
			if (searchPromptDiaog!= null&&searchPromptDiaog.isShowing()) {
				searchPromptDiaog.cancel();
			}
		
		}

		
		P.i("SysApplication.bNeedFirstBootIn="+SysApplication.bNeedFirstBootIn);
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
			P.i("GetRootMenuVKey =   -----" + iRootMenuVkey);			
			onVkey(iRootMenuVkey);
			
			//恢复时需要播放当前频道
			onVkey(Class_Constant.KEYCODE_INFO_KEY);
			
		}
		
		P.i("curChanCount =   -----" + curChanCount);


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

		P.d("Main onDestroy !");
		
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
private ImageView vol_mult_icon;
	@SuppressLint("NewApi")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		P.i("IR-key value:" + keyCode);

		boolean bKeyUsed = false;
		switch (keyCode) {
	
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			
		Channel	channel2 = objApplication.getCurPlayingChannel();
		
		if (channel2 != null) {
			P.i(TAG, "channelId >>" + channel2.chanId);
	
			AudioManager am2 = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			boolean isMute2 = am2.isStreamMute(AudioManager.STREAM_MUSIC);
			int currentVolume3 = am2.getStreamVolume(AudioManager.STREAM_MUSIC);	
			int maxVolume = am2.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			int stepVolume = maxVolume/15;
			P.e("banner", "get>>>> old vol---->"+currentVolume3+",mult---->"+isMute2+",maxvol="+maxVolume);
			
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
						vol_mult_icon.setVisibility(View.INVISIBLE);
						updateDtvStatus(3,false);
						
					}
				
			}
			
			currentVolume3 = am2.getStreamVolume(AudioManager.STREAM_MUSIC);
			
			if(currentVolume3 <=0 && isMute2==false){
				isMute2 = true; 
				am2.setStreamMute(AudioManager.STREAM_MUSIC, isMute2);				
				Intent mIntent40 = new Intent("chots.anction.muteon");
				sendBroadcast(mIntent40);		
				vol_mult_icon.setVisibility(View.VISIBLE);
				updateDtvStatus(3,true);
			}
			P.e("set>>>> new vol-->"+currentVolume3+",mult--->"+isMute2);
		    banner.show(channel2.chanId,currentVolume3,isMute2); 
	        
		} else {
			P.e("yangtong", "channel is null");
			bKeyUsed = true;
			break;
		}			
			return true;
		case KeyEvent.KEYCODE_VOLUME_MUTE:
			
			P.i("MUTE", "mute key arrived.");
			AudioManager am1 = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			boolean isMute1 = am1.isStreamMute(AudioManager.STREAM_MUSIC);
			//set mute state
			am1.setStreamMute(AudioManager.STREAM_MUSIC, !isMute1);
			//Assure the volume is old value.
			am1.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, 0); 
			//send broadcast to show/hide mute icon
			Intent mIntent1 = new Intent("chots.anction.muteon");
			sendBroadcast(mIntent1);
			
			vol_mult_icon.setVisibility(isMute1? View.INVISIBLE:View.VISIBLE);
			updateDtvStatus(3,!isMute1);
			
			//get current volume value, here you can show your own volume bar or do nothing.
			int currentVolume1 = am1.getStreamVolume(AudioManager.STREAM_MUSIC);
			
			P.i("MUTE", "mute key arrived-----currentVolume------>."+currentVolume1);
			Channel	channel3 = objApplication.getCurPlayingChannel();
			if (channel3 != null) {			
				banner.show(channel3.chanId,currentVolume1,!isMute1); 
			}
			
			
			return true;		
		
		case KeyEvent.KEYCODE_CHANNEL_DOWN:
		case KeyEvent.KEYCODE_DPAD_DOWN:
		case KeyEvent.KEYCODE_PAGE_DOWN: {
			
			if ((SysApplication.iCurChannelId != -1)
					&& (objApplication.dvbDatabase.getChannelCount() >= 0)) {

				Message msg = new Message();
				msg.what = MESSAGE_SHOW_DIGITALKEY_FOR_PRE_OR_NEXT_KEY;

				Channel channel = objApplication.getPreChannel(tempChannelID);
				if (channel == null || channel.chanId == -1) {// if pre channel
																// invalid,show
																// curChannel
					P.i("yangtong", "pre channel is null!");
					channel = objApplication.getCurPlayingChannel();
				}
				if (channel != null) {
					P.i("yangtong", "channelId >>" + channel.chanId);
					
					if(	volume_layout.getVisibility()==View.VISIBLE)
						volume_layout.setVisibility(View.INVISIBLE);
					
				    banner.show(channel.chanId);
					tempChannelID= channel.chanId;
	
					msg.arg1 = channel.logicNo;
				} else {
					P.e("yangtong", "channel is null");
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
					P.i("yangtong", "next channel is null!");
					channel = objApplication.getCurPlayingChannel();
				}
				if (channel != null) {
					P.i("yangtong", "channelId >>" + channel.chanId);
					if(	volume_layout.getVisibility()==View.VISIBLE)
						volume_layout.setVisibility(View.INVISIBLE);
					banner.show(channel.chanId);
					tempChannelID= channel.chanId;
			
					msg.arg1 = channel.logicNo;
				} else {
					P.e("yangtong", "channel is null");
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
			P.i("exit:" + 1);
			if (volume_layout.getVisibility() == View.VISIBLE) {
				P.i("exit:" + 2);
				volume_layout.setVisibility(View.INVISIBLE);
			}

			else if (mUiHandler.hasMessages(MESSAGE_HANDLER_DIGITALKEY)) {
				P.i("exit:" + 3);
				mUiHandler.removeMessages(MESSAGE_HANDLER_DIGITALKEY);				
				tvRootDigitalkey.setVisibility(View.INVISIBLE);
				tvRootDigitalKeyInvalid.setVisibility(View.INVISIBLE);
				iKeyNum = 0;
				iKey = 0;
				bKeyUsed = true;
			} else if(Banner.getBannerDisStatus()){
				//如果banner存在,则消除banner
				P.i("exit:" + 4 );
				banner.cancel();
				bKeyUsed = true;
				
			}
			else{

				reportToServer();
				P.i("exit:" + 5);
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
			
		/*
			 BookInfo curBookInfo = new BookInfo();
				
			 curBookInfo.bookDay = "1";
			 curBookInfo.bookEnventName ="请不要为我哭泣";
			 curBookInfo.bookTimeStart = "3";
			 curBookInfo.bookChannelName ="北京科教";
			 curBookInfo.bookChannelIndex = 5;
			
			
			Intent EpgWarn = new Intent(Main.this, EpgWarn.class);

			
			EpgWarn.putExtra("bookinfo", curBookInfo);
			startActivity(EpgWarn);
		*/
		
		/*z	banner.show(SysApplication.iCurChannelId);
			Message msg = new Message();
			msg.what = MESSAGE_SHOW_DIGITALKEY;
			msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
			mUiHandler.sendMessage(msg);*/
		}

			break;

		case KeyEvent.KEYCODE_MENU: {

			Intent mIntent = new Intent();
			mIntent.setComponent(new ComponentName(
					"com.SysSettings.main",
					"com.SysSettings.main.MainActivity"));
			try { 
				startActivity(mIntent);
				SetDtvStatus(false);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
			break;

		case KeyEvent.KEYCODE_DPAD_RIGHT: {
			if(objApplication.dvbDatabase.getChannelCount()>0){
				bKeyUsed = true;
				this.onVkey(keyCode);
			}
		}
			break;
		case KeyEvent.KEYCODE_F1:
			banner.show(SysApplication.iCurChannelId,999999999);
			// show/hide pip
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
		ads = new AdStrategy(mContext, Advertise_Constant.LIVEPLAY_ID_VOLUME, P.getParams(
				curChannel.serviceId + "", Advertise_Constant.LIVEPLAY_ID_VOLUME, Advertise_Constant.TEMP_IP_ADDRESS));
		// 请求广告决策
		ads.request();

		adPlayer.setIAdPlayerCallbackListener(new IAdPlayerCallbackListener() {

			@Override
			public void onPrepared(AdPlayer v) {
				// 广告内容准备就绪后播放
				P.e("FSLog", "视频准备完成------------onPrepared");
				// adPlayer.play("");
			}

			@Override
			public void onError(AdPlayer v, int errorCode, String message) {
				// 广告控件处理中发生错误抛出后处理代码
				P.e("FSLog", "播放异常------------onError");
			}

			@Override
			public void onCompleted(AdPlayer v) {
				// 广告控件中广告内容播放完毕后代码
				P.e("FSLog", "广告播放结束-----------onCompleted");
				adPlayer.stop();
			}
		});

		ads.setOnResponseListener(new IAdStrategyResponseListener() {

			@Override
			public void onResponse(ArrayList<AdItem> items) {
				// 获得广告策略结果后处理代码
				P.i("zyt", "AdStrategy-------------------------onResponse");
				P.i("zyt", "AdStrategy-------------------------onResponse  之前广告列表的长度" + items.size());
				if (items != null && items.size() > 0) {
					AdItem temp = items.get(0);
					// P.i("zyt", " " + items.size());
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
					P.i("zyt", "AdStrategy---------------onResponse:items is null or size is 0");
				}
			}

			@Override
			public void onError(int errorCode, String message) {
				// 获得广告策略结果出错
				P.i("zyt", "AdStrategy---------------onError");
			}
		});

		// 将广告策略设置到广告控件中
		adPlayer.setAdStrategy(ads);
	}

	public void playAd(String id) {
		adPlayer.setVisibility(View.VISIBLE);
		adPlayer.play(id);
	}
	public  class UI_Handler extends Handler {
		
		WeakReference<Main> mActivity;

		UI_Handler(Main activity) {
			mActivity = new WeakReference<Main>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final Main theActivity = mActivity.get();

			switch (msg.what) {
			
			case MESSAGE_VOLUME_SHOW:
				
				P.i(TAG, "UI_Handler---->MESSAGE_VOLUME_SHOW");
				
				if(theActivity.banner.bannerToast!=null)
					theActivity.banner.bannerToast.cancel();
				
			if(	theActivity.volume_layout.getVisibility()==View.INVISIBLE){
				
				P.i(TAG, "UI_Handler---->MESSAGE_VOLUME_SHOW------>volume_layout.getVisibility()="+theActivity.volume_layout.getVisibility());
				theActivity.volume_layout.setVisibility(View.VISIBLE);
		         theActivity.volume_layout.requestLayout();
		
			}

			P.i(TAG, "UI_Handler---->MESSAGE_VOLUME_SHOW------>volume_layout.getVisibility()="+theActivity.volume_layout.getVisibility());
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
				
				theActivity.searchPromptDiaog = DialogUtil.showPromptDialog(theActivity.mContext, theActivity.mContext.getString(R.string.str_AutoSearchPrompt).toString(),null, null, null,
						new DialogBtnOnClickListener() {

							@Override
							public void onSubmit(DialogMessage dialogMessage) {
								
			
								Intent mIntent = new Intent();
								/*
								mIntent.setComponent(new ComponentName(
										"com.chonghong.dtv_scan",
										"com.chonghong.dtv_scan.Dtv_Scan_Enter"));
								*/
								mIntent.setComponent(new ComponentName(
										"com.SysSettings.main",
										"com.SysSettings.main.MainActivity"));
								
								mIntent.putExtra("StartId", 1); //autoscan without scan_enter_menu
								try { 
									theActivity.startActivity(mIntent);
									SetDtvStatus(false);
								} catch (Exception e) {
									e.printStackTrace();
									//objApplication.SetDtvThreadOn(true);
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

				P.i("zhougang  main",
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

				P.i("zhougang  main",
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
			 if(updateDtvStatus(2,true)){	
				 theActivity.flCaInfo.setVisibility(View.VISIBLE); 
		      }
				String tmp = msg.obj.toString();
				if (tmp != null) {
					theActivity.tvCaInfo.setText(tmp);
				}

			}
				break;

			case MESSAGE_CA_HIDENOTICE: {
				if(updateDtvStatus(2,false)){
				theActivity.flCaInfo.setVisibility(View.INVISIBLE);
				}
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
					P.i("mmmm", "退回到直播模式");
					{
						// 恢复播放当前频道

						objApplication.playChannel(
								objApplication.getCurPlayingChannel(), false);
						DisplayBanner();
						SetDtvStatus(true);
						Message msg2 = new Message();
						msg2.what = MESSAGE_SHOW_DIGITALKEY;
						msg2.arg1 = objApplication.getCurPlayingChannel().logicNo;
						mUiHandler.sendMessage(msg2);
					}
				}
				break;
			case Class_Constant.TOAST_BANNER_PROGRAM_PASS:
				curChannelPrograms = CacheData.getCurPrograms();
				
				//临时针对现在频道支持时移，但是又没的时移的情况，服务器完善后取消即可
				if(null==curChannelPrograms||curChannelPrograms.size()<=0){
//					Toast.makeText(Main.this, "节目信息为空，不能进入时移模式", Toast.LENGTH_SHORT).show();
					SetDtvStatus(true);
					return;
				}
				
				//隐藏直播的banner
				if(Banner.getBannerDisStatus()){
					//如果banner存在,则消除banner
					banner.cancel();
				}
				showDialogBanner();
				break;
				
			case Class_Constant.DIALOG_ONKEY_UP:
				dealOnKeyUp(msg.arg1);
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
		P.d(" main->onVkey: "+ri_KeyCode);
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
			P.d("no program,notify autosearch !");
			mUiHandler.sendEmptyMessage(MESSAGE_SHOW_AUTOSEARCH);
		}
			break;

		case Class_Constant.KEYCODE_MENU_KEY: {

		}
			break;
		case Class_Constant.KEYCODE_OK_KEY:
		case KeyEvent.KEYCODE_ENTER: {
			
			if (iKeyNum!=0 && tvRootDigitalkey.isShown())
			{//如果数字键存在，则响应为快速切换到数字指定的频道
				mUiHandler.sendEmptyMessageDelayed(
						MESSAGE_HANDLER_DIGITALKEY, 100);				
			}else{			
			//时移模块
				dealOnKeyUp(ri_KeyCode);
			}

		}
			break;
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY: {
			// show channel list		

			Intent toChanList = new Intent(Main.this,
					ChannelList.class);			
			startActivity(toChanList);
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
			P.i("onVkey-key<" + iKey + ">");

			if (iKeyNum > 0 && iKeyNum <= 3) {
				iKey = (ri_KeyCode - Class_Constant.KEYCODE_KEY_DIGIT0)+iKey * 10;
			}

				P.i("get digital key   =  " + ri_KeyCode);
				P.i("iKey value   =  " + iKey);

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
			P.i("TimeShift::curChannel.frequencyKhz = "
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
//		tvCaSubtitleDown = (CAMarquee) findViewById(R.id.id_ca_subtitleDown);
//		tvCaSubtitleUp = (CAMarquee) findViewById(R.id.id_ca_subtitleUp);
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
		vol_mult_icon = (ImageView) findViewById(R.id.mute_icon);
		
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
		checkAndUpdateCateinfo();
	}

	/**
	 * 
	 */
	private void checkAndUpdateCateinfo() {
		P.d("begin>>> request timeshift and category data!");
		new Thread() {
			public void run() {				
				initCategoryData();
			}
		}.start();
		P.d("end>>> request timeshift and category data!");
	}

	//用于同步直播和时移切换时的状态同步	
	private static boolean []bOnDtvThread=new boolean[10];  
	//int index: 0:dtv status,1:signal status;2 sc status: 3 mult status 4 avplay status 5 ca status
	public void SetDtvStatus(boolean status) {

		bOnDtvThread[0] = status;

		if (status == false)// 隐藏显示
		{
			if(banner!=null)
				banner.cancel();
			
			showTimeShiftIcon(false);	

			if(objApplication!=null){
				objApplication.dvbPlayer.stop();
//				objApplication.dvbPlayer.blank();	
				bOnDtvThread[4]=false;
			}
			
			if (bOnDtvThread[1] && flNoSignal.getVisibility() == View.VISIBLE) {
				flNoSignal.setVisibility(View.INVISIBLE);
			}
			if (bOnDtvThread[2] && flCaInfo.getVisibility() == View.VISIBLE) {
				flCaInfo.setVisibility(View.INVISIBLE);
			}
			if (bOnDtvThread[3]
					&& vol_mult_icon.getVisibility() == View.VISIBLE) {
				vol_mult_icon.setVisibility(View.INVISIBLE);
			}
			if (bOnDtvThread[5] && objApplication.getCaLayout().getVisibility() == View.VISIBLE) {
				objApplication.getCaLayout().setVisibility(View.INVISIBLE);
			}			
			
			
		} else // 恢复显示
		{
			//恢复播放
			if(!bOnDtvThread[4]){
				bOnDtvThread[4] = true;
				//onVkey(Class_Constant.KEYCODE_INFO_KEY);
				if(objApplication!=null)
				objApplication.playLastChannel();
			}			
			
			if (bOnDtvThread[1] && flNoSignal.getVisibility() == View.INVISIBLE) {
				flNoSignal.setVisibility(View.VISIBLE);
			}
			
			if (bOnDtvThread[2] && flCaInfo.getVisibility() == View.INVISIBLE) {
				flCaInfo.setVisibility(View.VISIBLE);
			}
			
			Log.d("INFO",""+bOnDtvThread[5] + objApplication.getCaLayout().getVisibility());
			if (bOnDtvThread[5] && objApplication.getCaLayout().getVisibility() == View.INVISIBLE) {
				objApplication.getCaLayout().setVisibility(View.VISIBLE);
			}
			
			AudioManager am1 = (AudioManager)mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
			boolean isMute1 = am1.isStreamMute(AudioManager.STREAM_MUSIC);
			bOnDtvThread[3] = isMute1;
			if (bOnDtvThread[3]
					&& vol_mult_icon.getVisibility() == View.INVISIBLE) {
				vol_mult_icon.setVisibility(View.VISIBLE);
			}
		}

	}
	//返回false则忽略调用该函数所在函数的后续处理
	public static boolean updateDtvStatus(int index, boolean status) {
		if(index<=0||index>=9)
			return false;		
		bOnDtvThread[index]=status;
		return bOnDtvThread[0];
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
			if(!updateDtvStatus(1,!bIsLocked)) return;
			
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

				P.i("jump >>  " + iKey + "  program");

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
					P.i("jump fail !");
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
				P.i("jump fail !");
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
		P.i("enterSubMenu->Intent.subType:" + subType);

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
			//YBDEL toDoIntent = new Intent(this, Epg_z.class);
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
			P.e("DVB", "content>>>"+content);
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
		P.i("mmmm", "Main=initTimeshiftData_DBchan:"+DBchan+"thisPlayingInfo:"+thisPlayingInfo);
//		if(null==DBchan||TextUtils.isEmpty(DBchan.is_ttv))
//		{
			getIsTTVData();
//		}
		
		
	}
	
	private void getIsTTVData(){
		//这个IPTV定义的接口，2016年12月9日付岩确认弃用，以后可能会重新启用
//		String URL = processData.getChannelsInfo();
//		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
//				new Response.Listener<org.json.JSONObject>() {
//
//					@Override
//					public void onResponse(org.json.JSONObject arg0) {
//						// TODO Auto-generated method stub
//						P.i("mmmm", "Main=getUserChannel:" + arg0);
//
//						HandleLiveData.getInstance().dealChannelExtra(arg0);
//					}
//				}, errorListener);
//		jsonObjectRequest.setTag(Main.class.getSimpleName());// 设置tag,cancelAll的时候使用
//		mReQueue.add(jsonObjectRequest); 
		
		String URL = processData.getChannelList();
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						P.i("mmmm", "Main=getUserChannel:" + arg0);

						HandleLiveData.getInstance().dealChannelIsTTV(arg0);
					}
				}, errorListener);
		jsonObjectRequest.setTag(Main.class.getSimpleName());// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest); 
	}
	
	private void initCategoryData(){
		{
			String URL2 = processData.getCategoryString();
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL2, null,
					new Response.Listener<org.json.JSONObject>() {

						@Override
						public void onResponse(org.json.JSONObject arg0) {
							// TODO Auto-generated method stub
							String newVer = HandleLiveData.getInstance().dealCategoryVer(arg0);
							String oldVer = Utils.getProp("persist.sys.live.cateversion");
							P.i("cateversion>>>>> old: " + oldVer + " vs new:" + newVer);
							if(oldVer==null||newVer!=null&&!newVer.equals(oldVer)){
								P.i("initSortData_new:" + arg0);								
								SortData.saveSortNameList(HandleLiveData.getInstance().dealCategoryName(arg0));		
								HandleLiveData.getInstance().dealCategoryData(arg0); 
								Utils.setProp("persist.sys.live.cateversion",newVer);
								P.i("save new cateversion:" + newVer);
							}
						}
					}, errorListener_sort);
			jsonObjectRequest.setTag(Main.class.getSimpleName());// 设置tag,cancelAll的时候使用
			mReQueue.add(jsonObjectRequest);
		}		
	}
	private Response.ErrorListener errorListener_sort = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			P.i("mmmm", "sortOP_error：" + arg0);
			//从本地获取文件进行分析
		}
	};
	private Response.ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			P.i("mmmm", "Main=error：" + arg0);
		}
	};
	
	/* show time-shifting banner dialog */
	public void showDialogBanner() {
		
		//获取当前道信息
		ChannelDB db=DVB.getManager().getChannelDBInstance();
		PlayingInfo thisPlayingInfo = db.getSavedPlayingInfo();
		//获取当前Channel详细信息
		Channel DBchan = db.getChannel(thisPlayingInfo.mChannelId );
		
		
		
		if (programBannerDialog != null) {
			programBannerDialog.cancel();
		}
		mAudioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
		programBannerDialog = new BannerDialog(Main.this, DBchan, curChannelPrograms, mUiHandler, surfaceView,mAudioManager);
		
		programBannerDialog.show();
	}
	
	private boolean dealOnKeyUp(int keyCode){
		switch (keyCode) {
		case Class_Constant.KEYCODE_OK_KEY:
//			if (tvRootDigitalkey.isShown()) {
//				//当数字键显示的时候，响应数字换台逻辑
//				
//				
//			} else {
			if (!NetworkUtils.isConnectInternet(Main.this)) {
				Toast.makeText(Main.this, "网络不可用，请检查!", Toast.LENGTH_SHORT)
						.show();
			} else {

				SetDtvStatus(false);

				// 获取当前道信息
				ChannelDB db = DVB.getManager().getChannelDBInstance();
				PlayingInfo thisPlayingInfo = db.getSavedPlayingInfo();
				// 获取当前Channel详细信息
				Channel DBchan = db.getChannel(thisPlayingInfo.mChannelId);
				//判断是否可以时移，如果不可以则不进入,is_ttv 0不支持，1支持
				Log.i("mmmm", "DBchan.isttv"+DBchan.is_ttv);
				if(DBchan.is_ttv.equals("0")){
					break;
				}
				
				// 获取节目
				PlayVideo.getInstance().getProgramInfo(mUiHandler, DBchan);
			}
//			}
			break;
		}
		return true;
	}


	public static void showTimeShiftIcon(boolean bDisplay) {
		if (flTimeShift != null) {
			if (bDisplay && flTimeShift.getVisibility() != View.VISIBLE) {
				flTimeShift.setVisibility(View.VISIBLE);
			} else if (!bDisplay && flTimeShift.getVisibility() == View.VISIBLE) {
				flTimeShift.setVisibility(View.INVISIBLE);
			}
		}
	}	
}
//for test only 
class BootCastReceiver2 {

	ArrayList<BookInfo> delArray = null;
	private BookDataBase dvbBookDataBase = null;
	private Context mcontext;

	public void start(Context arg0) {

		mcontext = arg0;
		P.d("get Time Set Changed !");
		dvbBookDataBase = new BookDataBase(arg0);

		new bookAddThread2().start();

	}

	private class bookAddThread2 extends Thread {

		@Override
		public void run() {

			P.d("Book   Thread Start  !");
			// try {
			// Thread.sleep(10000);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// TODO Auto-generated method stub
			// Toast.makeText(arg0, "boot completed", Toast.LENGTH_LONG).show();

			delArray = new ArrayList<BookInfo>();
			Vector<BookInfo> vector = dvbBookDataBase.GetBookInfo();
			if (vector != null) {
				for (BookInfo bookInfo : vector) {
					//SharedPreferences sharedPre = mcontext.getSharedPreferences("id", Context.MODE_PRIVATE);
					//SharedPreferences.Editor editor = sharedPre.edit();
					//int flag = sharedPre.getInt("id", 0);

					P.i("现在的时间是" + System.currentTimeMillis());
					P.i(bookInfo.bookChannelName + ">>>" + bookInfo.bookEnventName + ": 时间信息>>" + bookInfo.bookDay +" "+ bookInfo.bookTimeStart);

					//String[] mDay = (bookInfo.bookDay).split("-");
					//String[] mTime = (bookInfo.bookTimeStart).split(":");
					String[] mTime1=null,mTime2=null,mTime3 = null;
					String mTime0=null;
					boolean bDataErr = false;
					Calendar cal = Calendar.getInstance();
					
					if(bookInfo.bookDay==null||bookInfo.bookDay.length()<5||bookInfo.bookTimeStart==null || bookInfo.bookTimeStart.length()<11){
						bDataErr=true;
					}else{
						
					mTime0=bookInfo.bookDay;
					mTime1 = bookInfo.bookTimeStart.split("~");
					if(mTime1.length>=2){
						mTime2 = mTime1[0].split(":");
						mTime3 = mTime1[1].split(":");
					}
					
							
					cal.setTimeInMillis(System.currentTimeMillis());
					cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
					try {
						cal.set(Calendar.MONTH, (Integer.parseInt(mTime0.substring(0, 1))) - 1);
						cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(mTime0.substring(3, 4)));
						cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mTime3[0].trim()));
						cal.set(Calendar.MINUTE, Integer.parseInt(mTime3[1].trim()));
						cal.set(Calendar.SECOND, 0);
						cal.set(Calendar.MILLISECOND, 0);
						if(Integer.parseInt(mTime3[0].trim())<Integer.parseInt(mTime2[0].trim()))//跨天
						{
							cal.add(Calendar.HOUR_OF_DAY, 1);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						bDataErr=true;
					}					
					}
					P.i("结束时间>>" + cal.getTimeInMillis()+">>"+bDataErr);
					if (!bDataErr /*&& (cal.getTimeInMillis() > System.currentTimeMillis())*/) //结束时间>当前时间
					{
						cal.setTimeInMillis(System.currentTimeMillis());
						cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
						try {
							cal.set(Calendar.MONTH, (Integer.parseInt(mTime0.substring(0, 1))) - 1);
							cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(mTime0.substring(3, 4)));
							cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mTime2[0].trim()));
							cal.set(Calendar.MINUTE, Integer.parseInt(mTime2[1].trim())-1);	//-1 提前1分钟
							cal.set(Calendar.SECOND, 0);
							cal.set(Calendar.MILLISECOND, 0);
						} catch (Exception e) {
							e.printStackTrace();
							bDataErr=true;
						}		
						
						P.i("开始时间>>" + cal.getTimeInMillis()+">>"+bDataErr);
						
						if (!bDataErr /*&& (cal.getTimeInMillis() >= System.currentTimeMillis())*/) //开始时间>当前时间
						{						
							P.i("即将开始播放  >>>" + bookInfo.bookEnventName);
							/*
							Intent mIntent = new Intent("android.intent.action.WAKEUP");
							mIntent.putExtra("bookinfo", bookInfo);
	
							Intent myBookIntent = new Intent("android.intent.action.SmartTVBook");
							myBookIntent.putExtra("bookinfo", bookInfo);
							myBookIntent.putExtra("SmartTV_BookFlag", flag);
							mcontext.sendBroadcast(myBookIntent);
	
							editor.putInt("id", flag + 1);
							editor.commit();*/
		
							Intent EpgWarn = new Intent(mcontext, EpgWarn.class);
							EpgWarn.putExtra("bookinfo", bookInfo);
							mcontext.startActivity(EpgWarn);
						}											
					}else{			
						
						bDataErr = true;
					} 
					
					if(bDataErr)
					{
						P.i("删除过期预约的节目：" + bookInfo.bookEnventName);
						// objApplication.delBookChannel(bookInfo.bookDay,
						// bookInfo.bookTimeStart);
						delArray.add(bookInfo);
					}
				}
				if (delArray.size() > 0) {
					for (int i = 0; i < delArray.size(); i++) {
						dvbBookDataBase.RemoveOneBookInfo(delArray.get(i).bookDay, delArray.get(i).bookTimeStart);
					}
				}
			} else {
				P.e("ChannelBook list is null  !");
			}

			P.d("Book  Thread  finish!");

		}
	}

}
