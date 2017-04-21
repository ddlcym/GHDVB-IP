package com.changhong.app.dtv;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import org.json.JSONObject;

import android.R.bool;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ActivityManager.RunningTaskInfo;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
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
import com.changhong.app.ca.CaMailInfor;
import com.changhong.app.ca.CaService;
import com.changhong.app.constant.Advertise_Constant;
import com.changhong.app.constant.Class_Constant;
import com.changhong.app.constant.Class_Global;
import com.changhong.app.dtv.DialogUtil.DialogBtnOnClickListener;
import com.changhong.app.dtv.DialogUtil.DialogMessage;
import com.changhong.app.dtv.R.string;
import com.changhong.app.timeshift.common.CacheData;
import com.changhong.app.timeshift.common.MyApp;
import com.changhong.app.timeshift.common.NetworkUtils;
import com.changhong.app.timeshift.common.PlayVideo;
import com.changhong.app.timeshift.common.ProcessData;
import com.changhong.app.timeshift.common.ProgramInfo;
import com.changhong.app.timeshift.common.VolleyTool;
import com.changhong.app.timeshift.datafactory.BannerDialog;
import com.changhong.app.timeshift.datafactory.HandleLiveData;
import com.changhong.app.timeshift.datafactory.TtvSP;
import com.changhong.app.utils.OpJsonFile;
import com.changhong.app.utils.SortData;
import com.changhong.app.utils.TestFunc;
import com.changhong.app.utils.UserDate;
import com.changhong.dvb.CA;
import com.changhong.dvb.CA_Mail_Head;
import com.changhong.dvb.Channel;
import com.changhong.dvb.ChannelDB;
import com.changhong.dvb.DVB;
import com.changhong.dvb.PlayingInfo;
import com.changhong.dvb.ProtoMessage.DVB_CA_TYPE;
import com.changhong.dvb.ProtoMessage.DVB_RectSize;
import com.changhong.dvb.channelIds;
import com.iflytek.xiri.Feedback;
import com.iflytek.xiri.scene.ISceneListener;
import com.iflytek.xiri.scene.Scene;
import com.xormedia.adplayer.AdItem;
import com.xormedia.adplayer.AdPlayer;
import com.xormedia.adplayer.AdStrategy;
import com.xormedia.adplayer.IAdPlayerCallbackListener;
import com.xormedia.adplayer.IAdStrategyResponseListener;

public class Main extends Activity implements ISceneListener {
	public static final String TAG = "GHLive";
	public static final String DBG = "DBG";

	public SysApplication objApplication;
	public Context mContext;

	LinearLayout id_dtv_digital_root;
	//TextView id_dtv_channel_name;
	ImageView dtv_digital_1;
	ImageView dtv_digital_2;
	ImageView dtv_digital_3;
	private ImageView vol_mult_icon;

	int tempChannelID = -1;

	Dialog searchPromptDiaog = null;

	AudioManager mAudioManager;

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
	private static final int MESSAGE_CHANLIST_SHOW = 905;
	private static final int MESSAGE_BAR_SHOW = 906;
	/**
	 * intent message
	 */
	public static final String sGetcateinfo = "com.changhong.action.getCateinfo";
	public static final String sChkEpgTimer = "com.changhong.action.chkEpgTimer";
	public static final String sChkMail = "chots.action.mailEvent";
	/**
	 * the time delayed when change program
	 */
	private static final int TIME_CHANGE_DELAY = 200;

	/**
	 * Banner View
	 */
	private Banner banner;
	private Point point;

	/**
	 * Digital key
	 */
	private int iKeyNum = 0;
	private int iKey = 0;
	LinearLayout tvRootDigitalkey;
	private RelativeLayout tvRootDigitalKeyInvalid;
	Handler handler_digital = new Handler();
	/**
	 * CA INFO
	 */
	private RelativeLayout flCaInfo;
	// private CAMarquee tvCaSubtitleDown, tvCaSubtitleUp;
	//private TextView tvCaInfo;

	/**
	 * no program
	 */
	RelativeLayout flNoSignal;

	/**
	 * pft update
	 */
	private FrameLayout flPftUpdate;

	/**
	 * TimeShift
	 */
	private static FrameLayout flTimeShift = null;
	private boolean bSearchTtvData = false;
	/**
	 * vkey
	 */
	int iRootMenuVkey = -1;

	/**
	 * signal monitor
	 */
	private signalRecever msignalRecever;

	private homeReceiver mHomeReceiver;

	// 获取分类信息
	private CateinfoReceiver cateinfoReceiver;

	private void sendBroadcastInfo(String strInfo) {
		Intent intent = new Intent();
		intent.setAction(strInfo);
		sendBroadcast(intent);
		P.i(TAG, "send intent:" + strInfo);
	}

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

	/*
	 * timeshift data
	 */
	private VolleyTool volleyTool;
	private RequestQueue mReQueue;
	private ProcessData processData;
	private static BannerDialog programBannerDialog;
	private static List<ProgramInfo> curChannelPrograms = new ArrayList<ProgramInfo>();// 当前频道下的上一个节目，当前节目，下一个节目信息

	private SurfaceView surfaceView;
	int testInde=0;

	public static NitMonitor nMonitor=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Debug.startMethodTracing("CH_dtvApp#3");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		mContext = Main.this;
		context = Main.this;
		scene = new Scene(context);
		feedback = new Feedback(context);
		bAllowSignalDis = false;
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
		startTime = Utils.getCurTime();

		// init views
		findView();
		//initValue();

		checkChannel();
		registerBroadReceiver();
		//checkNetVailable();		

		//启动CaService
		startService(new Intent(this,CaService.class));
		if(nMonitor==null){
			nMonitor = new NitMonitor(mContext);
			Log.i(TAG, "create new NitMonitor()-->"+nMonitor);
		}		
	}

	/**
	 * 
	 */
	private void testEnv() {

		// 测试预约节目触发
		// new BootCastReceiver2().start(mContext);

		// 测试欢网应用商店需调用的第三方jar包中的接口
		try {
			TestFunc.testThirdPackage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	BootCastReceiver epgTime = new BootCastReceiver();

	private void registerBroadReceiver() {

		msignalRecever = new signalRecever();
		registerReceiver(msignalRecever, new IntentFilter(
				TunerInfo.TunerInfo_Intent_FilterName));

		mHomeReceiver = new homeReceiver();
		registerReceiver(mHomeReceiver, new IntentFilter("HOME_PRESSED"));

		cateinfoReceiver = new CateinfoReceiver();
		registerReceiver(cateinfoReceiver, new IntentFilter(sGetcateinfo));

		IntentFilter filter = new IntentFilter();
		filter.addAction("com.changhong.action.stoptvlive");
		registerReceiver(stopReceiver, filter);

		IntentFilter filter2 = new IntentFilter();
		filter2.addAction("showbanner");
		registerReceiver(showBannerReceiver, filter2);

		IntentFilter filter3 = new IntentFilter();
		filter3.addAction("showBanneForYuYueDialog");
		registerReceiver(showBanneForYuYueDialog, filter3);

		IntentFilter filter4 = new IntentFilter();
		filter4.addAction(sChkEpgTimer);
		registerReceiver(epgTime, filter4);

//		IntentFilter filter5 = new IntentFilter();
//		filter5.addAction(sChkMail);
//		registerReceiver(checkMailBR, filter5);

		IntentFilter filter6 = new IntentFilter();
		filter6.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		//filter6.addAction("com.changhong.action.rescan");
		filter6.addAction("com.changhong.ota.launch");
		registerReceiver(myNetChangedReceiver, filter6);

		// IntentFilter myIntentFilter = new IntentFilter();
		// myIntentFilter.addAction("FINISH");
		// registerReceiver(mFinishReceiver, myIntentFilter);

	}

	// ///////////监听网络状态变化的广播接收器
	private ConnectivityManager mConnectivityManager;
	private NetworkInfo netInfo;
	protected int curNitVerRequested=-1;

	/**
		 * 
		 */
	public boolean checkNetVailable() {
		mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		netInfo = mConnectivityManager.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isAvailable()) {

			// ///////////网络连接
			String name = netInfo.getTypeName();
			Utils.setProp("sys.home.key.ignored", "false");
			Log.i(TAG, "connected " + name + ",homekey enable");
			if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				// ///WiFi网络

			} else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
				// ///有线网络

			} else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				// ///////3g网络

			}
			return true;
		} else {
			// //////网络断开
			Utils.setProp("sys.home.key.ignored", "true");
			Log.i(TAG, "network is disconnect,homekey disable");
			return false;

		}
	}

	private BroadcastReceiver myNetChangedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				//checkNetVailable();
			}
			/*if (action.equals("com.changhong.action.rescan")) {
				int ver = SystemProperties.getInt("property.ots.nit.version", -2);
				Log.i(TAG, "GOT rescan broadcast:"+curNitVerRequested+" VS "+ver);				
				if((curNitVerRequested!=ver && ver!=-2 && isInDvbLive(context))){
					curNitVerRequested = ver;
					mUiHandler.sendEmptyMessage(MESSAGE_SHOW_AUTOSEARCH);
					SystemProperties.set("com.changhong.action.rescan","false");
				}
			}*/
			if (action.equals("com.changhong.ota.launch")) {
				int ota_status = intent.getIntExtra("ota_status", -1);
				Log.i(TAG, "GOT ota broadcast:"+ota_status);				
				if(ota_status==1){
					SetDtvStatus(false, false);
				}else if(ota_status==0){
					SetDtvStatus(true, false);
				}					
				
			}			
			

		}
	};

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

			// GH UI-1.3 换台不显示右上角频道号
			// Message msg = new Message();
			// msg.what = MESSAGE_SHOW_DIGITALKEY;
			// msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
			// mUiHandler.sendMessage(msg);

			banner.show(SysApplication.iCurChannelId);

		}
	};

	BroadcastReceiver showBanneForYuYueDialog = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			P.i("receive  showBanneForYuYueDialog  broadcast");
			// onVkey(Class_Constant.KEYCODE_INFO_KEY);
			banner.show(arg1
					.getIntExtra("chanid", SysApplication.iCurChannelId));

		}
	};
	
	private boolean isInDvbLive(Context context) {
		String curRunning=null;
		ActivityManager am;
		
		try {
			am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
						
			RunningTaskInfo runningTask = am.getRunningTasks(1).get(0);
			
			curRunning= runningTask.topActivity.getPackageName();
			
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
		if (curRunning!=null && curRunning.equals("com.changhong.app.dtv")) {
			return true;
		}
		return false;
	}
	
	List<CaMailInfor> mMailInforList = new ArrayList<CaMailInfor>();
	private static boolean bNewMailComing = false;
	protected CA thisCa = null;

	public static boolean getNewMailStatus() {
		return bNewMailComing;
	}

	private boolean fillMailData() {
		boolean bNewMail = false;
		thisCa = DVB.getManager().getCaInstance();
		mMailInforList.clear();
		CA_Mail_Head[] mailHeads = thisCa.getAllMailHeads();
		if (mailHeads != null && mailHeads.length > 0) {

			DVB_CA_TYPE curCaType = thisCa.getCurType();

			Log.i("Email_z", " getEmailData()----->curCaType--->" + curCaType);

			for (int i = 0; i < mailHeads.length; i++) {
				CaMailInfor mailInfor = new CaMailInfor();

				if (DVB_CA_TYPE.CA_NOVEL == curCaType) {
					if (mailHeads[i] == null) {
						Log.i("Email_z",
								" getEmailData()----->mailHeads[i]   is null--->");
						break;
					}
					mailInfor.id = mailHeads[i].mCaMailHeadNovel.miId;
					mailInfor.revDate = mailHeads[i].mCaMailHeadNovel.mCreateTime.miYear
							+ "/"
							+ mailHeads[i].mCaMailHeadNovel.mCreateTime.miMonth
							+ "/"
							+ mailHeads[i].mCaMailHeadNovel.mCreateTime.miDay;
					mailInfor.revTime = mailHeads[i].mCaMailHeadNovel.mCreateTime.miHour
							+ ":"
							+ mailHeads[i].mCaMailHeadNovel.mCreateTime.miMinute
							+ ":"
							+ mailHeads[i].mCaMailHeadNovel.mCreateTime.miSecond;
					mailInfor.isRead = (0 == mailHeads[i].mCaMailHeadNovel.mReaded) ? false
							: true;
					if (!mailInfor.isRead) {
						bNewMail = true;
					}
					mailInfor.title = mailHeads[i].mCaMailHeadNovel.mTitile;

				} else if (DVB_CA_TYPE.CA_SUMA == curCaType) {
					if (mailHeads[i] == null) {
						Log.i("Email_z",
								" getEmailData()----->mailHeads[i]   is null--->");
						break;
					}

					mailInfor.id = mailHeads[i].mCaMailHeadSuma.miId;
					mailInfor.revDate = mailHeads[i].mCaMailHeadSuma.mCreateTime.miYear
							+ "/"
							+ mailHeads[i].mCaMailHeadSuma.mCreateTime.miMonth
							+ "/"
							+ mailHeads[i].mCaMailHeadSuma.mCreateTime.miDay;
					mailInfor.revTime = mailHeads[i].mCaMailHeadSuma.mCreateTime.miHour
							+ ":"
							+ mailHeads[i].mCaMailHeadSuma.mCreateTime.miMinute
							+ ":"
							+ mailHeads[i].mCaMailHeadSuma.mCreateTime.miSecond;
					mailInfor.isRead = (0 == mailHeads[i].mCaMailHeadSuma.mReaded) ? false
							: true;
					if (!mailInfor.isRead) {
						bNewMail = true;
					}
					mailInfor.title = mailHeads[i].mCaMailHeadSuma.mTitile;
				}
				mMailInforList.add(mailInfor);
			}
		} else {
			Log.i("Email_list",
					" getEmailData()----->mailHeads  is null -----mailHeads.length");
		}
		Log.i(TAG, "Found total mail:" + mMailInforList.size() + ",newMail?:"
				+ bNewMail);
		return bNewMail;
	}

	private int iCallChanListId = -1;
	protected boolean bAllowSignalDis=false;
	private boolean bNeedPlay=true;

	private void checkChannel() {
		
		P.i("checkChannel()");
		int iBouqtID = getIntent().getIntExtra("bouquetId", -1);
		int iFreq = getIntent().getIntExtra("frequency", -1);
		int iSerId = getIntent().getIntExtra("serviceId", -1);
		int iTsId = getIntent().getIntExtra("tsId", -1);

		P.d("getIntent().getStringExtra>>> bouquetId =" + iBouqtID
				+ ",frequency=" + iFreq + ",serviceId=" + iSerId + ",tsId="
				+ iTsId);

		if (iSerId != -1 && iTsId != -1)// Live
		{
			Channel toPlayChannel = objApplication.dvbDatabase
					.getChannelByTsIdAndServiceId(iTsId, iSerId);
			if (toPlayChannel != null) {
				objApplication.SetUserInfo(toPlayChannel);
				P.d("GOT logicNo =" + toPlayChannel.logicNo);
				//return;
			}
		} else if (iBouqtID != -1) {

			Channel[] allChannels = objApplication.dvbDatabase.getChannelsAll();
			if (iBouqtID != -1) {
				int iMaxNo = 999999;
				int cateIdVal = 1 << iBouqtID;
				Channel curChan = null;
				for (Channel ch : allChannels) {
					if (((ch.favorite & cateIdVal) == cateIdVal)
							&& (ch.logicNo < iMaxNo)) {
						iMaxNo = ch.logicNo;
						curChan = ch;
					}
				}
				if (curChan != null) {
					objApplication.SetUserInfo(curChan);
					iCallChanListId = iBouqtID;
					P.d("GOT list[" + iBouqtID + "] logicNo ="
							+ curChan.logicNo);
					//return;
				}

			}

		}
		if(objApplication.playLastChannel()){
			bNeedPlay = false;
		}

	}

	@Override
	protected void onResume() {

		super.onResume();

		bAllowSignalDis = false;

		DVB_RectSize.Builder builder = DVB_RectSize.newBuilder().setX(0)
				.setY(0).setW(0).setH(0);

		if (objApplication.dvbPlayer != null) {

			P.i("resize video size");
			objApplication.dvbPlayer.setSize(builder.build());

		}

		P.d("Main onResume  start!");

		SetDtvStatus(true, true);

		nMonitor.startMonitor();
		
		int curChanCount = objApplication.dvbDatabase.getChannelCountSC();
		String reFlash = SystemProperties.get("persist.sys.live.refresh", "false");
		if(reFlash.equals("true")){
			int ver = SystemProperties.getInt("property.ots.nit.version", -2);
			Log.i(TAG, "check reflash=true:"+curNitVerRequested+" VS "+ver);				
			if((curNitVerRequested!=ver && ver!=-2)){
				curNitVerRequested = ver;
			}else{
				reFlash = "false";
			}
		}
		P.i("check db status>>total="+curChanCount+",reFlash="+reFlash);
		if (curChanCount <= 0/*||reFlash.equals("true")*/) {

			P.i("弹出没有节目，自动搜索的dialog");
			onVkey(Class_Constant.VKEY_EMPTY_DBASE);
			bSearchTtvData = false;
			SystemProperties.set("persist.sys.live.refresh", "false");
			return;

		} else {

			if (searchPromptDiaog != null && searchPromptDiaog.isShowing()) {
				searchPromptDiaog.cancel();
			}

		}

		sendBroadcastInfo(sChkEpgTimer);
		P.i(TAG, "cateversion=" + Utils.getProp("persist.sys.live.cateversion"));
		/*
		if (Utils.getProp("persist.sys.live.cateversion").equals("0")) {
			// sendBroadcastInfo(sGetcateinfo);
			if (!bSearchTtvData) {
				P.i(TAG, "REQ-2 catedata & timeshift data");
				initTimeshiftData();
				initCategoryData();
			} else {
				bSearchTtvData = false;
			}
			
		}	*/	
		if (Utils.getProp("persist.sys.live.cateversion").equals("0")||!bSearchTtvData) {
				initValue_OnResume();
				bSearchTtvData = true;
		}				
		checkSignal();
		//Debug.stopMethodTracing();		
	}

	private void checkSignal() {
		new Thread(){
			public void run() {
				int times=0,lockVnt=0;
				boolean bLock;
				while(++times<=10){
					bLock = objApplication.mo_Tunner.isLock(0);
					if(bLock){
						if(lockVnt<=0)
							lockVnt=1;
						else
							lockVnt++;
					}else{
						if(lockVnt>=0)
							lockVnt=-1;
						else
							lockVnt--;
					}
					Log.i(TAG, "check signal>>"+lockVnt);
					if(lockVnt>=3 || lockVnt<=(-8)){
						times = 10;
						break;
					}
					try {
						Thread.sleep(500);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				Log.i(TAG, "signal locked ?>>"+(lockVnt>=0?"true":"false"));
				bAllowSignalDis = true;
				objApplication.reqNotifySignalChanged(lockVnt<0?false:true);				
			}			
		}.start();
	}

	void DisplayBanner() {

		int curChanCount = objApplication.dvbDatabase.getChannelCountSC();

		if (curChanCount > 0 && SysApplication.iCurChannelId != -1) {

			banner.show(SysApplication.iCurChannelId);

			// surfaceView1.setVisibility(View.VISIBLE);
			// GH UI-1.3 换台不显示右上角频道号
			// Message msg = new Message();
			// msg.what = MESSAGE_SHOW_DIGITALKEY;
			// msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
			// mUiHandler.sendMessage(msg);

		}

	}

	public void onDestroy() {

		P.d("Main onDestroy !");

		if (banner != null)
			banner.cancel();

		objApplication.stopPipPlay();

		System.exit(0);
		unregisterReceiver(msignalRecever);
		unregisterReceiver(mHomeReceiver);
		unregisterReceiver(stopReceiver);
		unregisterReceiver(cateinfoReceiver);
		unregisterReceiver(epgTime);
//		unregisterReceiver(checkMailBR);
		unregisterReceiver(myNetChangedReceiver);

		objApplication.exit();
		android.os.Process.killProcess(android.os.Process.myPid());
		super.onDestroy();
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		P.i("IRkey value:" + keyCode);

		boolean bKeyUsed = false;
		switch (keyCode) {

		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:

			Channel channel2 = objApplication.getCurPlayingChannel();

			if (channel2 != null) {
				P.i(TAG, "channelId >>" + channel2.chanId);

				AudioManager am2 = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				boolean isMute2 = am2.isStreamMute(AudioManager.STREAM_MUSIC);
				int currentVolume3 = am2
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				int maxVolume = am2
						.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				int stepVolume = maxVolume / 15;
				P.e("get>>>> old vol---->" + currentVolume3 + ",mult---->"
						+ isMute2 + ",maxvol=" + maxVolume);

				if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
					if (currentVolume3 > 0) {
						am2.adjustStreamVolume(AudioManager.STREAM_MUSIC,
								AudioManager.ADJUST_LOWER, 0);
					}
				} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
					if (currentVolume3 < maxVolume) {
						am2.adjustStreamVolume(AudioManager.STREAM_MUSIC,
								AudioManager.ADJUST_RAISE, 0);
					}
					if (isMute2) {
						isMute2 = false;
						am2.setStreamMute(AudioManager.STREAM_MUSIC, isMute2);
						Intent mIntent20 = new Intent("chots.anction.muteon");
						sendBroadcast(mIntent20);
						vol_mult_icon.setVisibility(View.GONE);
						updateDtvStatus(3, false);

					}

				}

				currentVolume3 = am2.getStreamVolume(AudioManager.STREAM_MUSIC);

				if (currentVolume3 <= 0 && isMute2 == false) {
					isMute2 = true;
					am2.setStreamMute(AudioManager.STREAM_MUSIC, isMute2);
					Intent mIntent40 = new Intent("chots.anction.muteon");
					sendBroadcast(mIntent40);
					vol_mult_icon.setVisibility(View.VISIBLE);
					updateDtvStatus(3, true);
				}
				P.e("set>>>> new vol-->" + currentVolume3 + ",mult--->"
						+ isMute2);
				banner.showVolume(channel2.chanId, currentVolume3, isMute2);
				
				objApplication.saveVolMaster(keyCode == KeyEvent.KEYCODE_VOLUME_UP?1:-1);

			} else {
				P.e("channel is null");
				bKeyUsed = true;
				break;
			}
			return true;
		case KeyEvent.KEYCODE_VOLUME_MUTE:
			
			P.i("mute key arrived.");
			AudioManager am1 = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			boolean isMute1 = am1.isStreamMute(AudioManager.STREAM_MUSIC);
			// set mute state
			am1.setStreamMute(AudioManager.STREAM_MUSIC, !isMute1);
			// Assure the volume is old value.
			am1.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_SAME, 0);
			// send broadcast to show/hide mute icon
			Intent mIntent1 = new Intent("chots.anction.muteon");
			sendBroadcast(mIntent1);

			vol_mult_icon.setVisibility(isMute1 ? View.GONE : View.VISIBLE);
			updateDtvStatus(3, !isMute1);

			// get current volume value, here you can show your own volume bar
			// or do nothing.
			int currentVolume1 = am1.getStreamVolume(AudioManager.STREAM_MUSIC);

			P.i("mute key arrived-----currentVolume------>." + currentVolume1);
			Channel channel3 = objApplication.getCurPlayingChannel();
			if (channel3 != null && banner.getBannerDisStatus()) {
				banner.showVolume(channel3.chanId, currentVolume1, !isMute1);
			}

			return true;

		case KeyEvent.KEYCODE_CHANNEL_DOWN:
		case KeyEvent.KEYCODE_DPAD_DOWN:
		case KeyEvent.KEYCODE_PAGE_DOWN: {

			if ((SysApplication.iCurChannelId != -1)
					&& (objApplication.dvbDatabase.getChannelCount() >= 0)) {

				// GH UI-1.3 换台不显示右上角频道号
				// Message msg = new Message();
				// msg.what = MESSAGE_SHOW_DIGITALKEY_FOR_PRE_OR_NEXT_KEY;

				Channel channel = objApplication.getPreChannel(tempChannelID);
				if (channel == null || channel.chanId == -1) {// if pre channel
																// invalid,show
																// curChannel
					P.i("pre channel is null!");
					channel = objApplication.getCurPlayingChannel();
				}
				if (channel != null) {
					P.i("channelId >>" + channel.chanId);

					banner.show(channel.chanId);
					tempChannelID = channel.chanId;

					// msg.arg1 = channel.logicNo;
				} else {
					P.e("channel is null");
					bKeyUsed = true;
					break;
				}
				bKeyUsed = true;
				mUiHandler.removeMessages(MESSAGE_PLAY_PRE);
				mUiHandler.removeMessages(MESSAGE_PLAY_NEXT);
				mUiHandler.sendEmptyMessageDelayed(MESSAGE_PLAY_PRE,
						TIME_CHANGE_DELAY);
				// mUiHandler.sendMessage(msg);
			}
		}
			break;
		case KeyEvent.KEYCODE_CHANNEL_UP:
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_PAGE_UP: {
			if ((SysApplication.iCurChannelId != -1)
					&& (objApplication.dvbDatabase.getChannelCount() >= 0)) {

				// GH UI-1.3 换台不显示右上角频道号
				// Message msg = new Message();
				// msg.what = MESSAGE_SHOW_DIGITALKEY_FOR_PRE_OR_NEXT_KEY;

				Channel channel = objApplication.getNextChannel(tempChannelID);
				if (channel == null || channel.chanId == -1) {// if next channel
																// invalid,show
																// curChannel
					P.i("next channel is null!");
					channel = objApplication.getCurPlayingChannel();
				}
				if (channel != null) {
					P.i("yangtong", "channelId >>" + channel.chanId);

					banner.show(channel.chanId);
					tempChannelID = channel.chanId;

					// msg.arg1 = channel.logicNo;
				} else {
					P.e("channel is null");
					bKeyUsed = true;
					break;
				}
				bKeyUsed = true;
				mUiHandler.removeMessages(MESSAGE_PLAY_PRE);
				mUiHandler.removeMessages(MESSAGE_PLAY_NEXT);
				mUiHandler.sendEmptyMessageDelayed(MESSAGE_PLAY_NEXT,
						TIME_CHANGE_DELAY);
				// mUiHandler.sendMessage(msg);
			}
		}
			break;

		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER: {

			bKeyUsed = true;
			this.onVkey(keyCode);
		}
			break;

		case KeyEvent.KEYCODE_BACK: 
			if(event.getRepeatCount() == 0)
			{
	
				if (mUiHandler.hasMessages(MESSAGE_HANDLER_DIGITALKEY)) {
					mUiHandler.removeMessages(MESSAGE_HANDLER_DIGITALKEY);
					tvRootDigitalkey.setVisibility(View.GONE);
					tvRootDigitalKeyInvalid.setVisibility(View.INVISIBLE);
					iKeyNum = 0;
					iKey = 0;
					bKeyUsed = true;
				} else if (Banner.getBannerDisStatus()) {
					// 如果banner存在,则消除banner
					banner.cancel();
					showTimeShiftIcon(false);
					bKeyUsed = true;
				} else {
					objApplication.playPrePlayingChannel();
					banner.show(SysApplication.iCurChannelId);
					// GH UI-1.3 换台不显示右上角频道号
					// Message msg = new Message();
					// msg.what = MESSAGE_SHOW_DIGITALKEY;
					// msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
					// mUiHandler.sendMessage(msg);
					bKeyUsed = true;
				}
				return true;
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
			bKeyUsed = true;
			onVkey(keyCode);
		}

			break;

		// This is only for test
		case KeyEvent.KEYCODE_DPAD_LEFT:
	/*
			    Intent zbzcd_zIntent = new Intent(this, testCA.class); //启动菜单activity
				startActivity(zbzcd_zIntent);*/
			
			break;

		case KeyEvent.KEYCODE_MENU: {

			Intent mIntent = new Intent();
			mIntent.setComponent(new ComponentName("com.SysSettings.main",
					"com.SysSettings.main.MainActivity"));
			try {
				startActivity(mIntent);
				SetDtvStatus(false, true);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
			break;

		case KeyEvent.KEYCODE_DPAD_RIGHT: {
			if (objApplication.dvbDatabase.getChannelCount() > 0) {
				bKeyUsed = true;
				this.onVkey(keyCode);
			}
		}
			break;
		case KeyEvent.KEYCODE_F1:
			banner.show(SysApplication.iCurChannelId, 999999999);
			// show/hide pip
			/*
			 * if (!isPipPlay) { isPipPlay = true;
			 * objApplication.initPipPlayer(point.x, point.y);
			 * objApplication.startPipPlay(); } else { isPipPlay = false;
			 * objApplication.stopPipPlay(); }
			 */
			break;
		case KeyEvent.KEYCODE_F2:// Swap pip/main
			/*
			 * if (isPipPlay) { objApplication.swapPipMain(); }
			 */
			break;
		case KeyEvent.KEYCODE_F3:// play pip pre
			/*
			 * if (isPipPlay) { objApplication.playPrePipChannel(true); }
			 */
			break;
		case KeyEvent.KEYCODE_F4:// play pip next
			/*
			 * if (isPipPlay) { objApplication.playNextPipChannel(true); }
			 */
			break;

		/*
		 * Key deal for VOD of SCN.
		 */
		/*
		 * case Class_Constant.KEYCODE_KEY_PAUSE: { bKeyUsed = true;
		 * onVkey(keyCode); break; }
		 * 
		 * case Class_Constant.KEYCODE_KEY_FASTBACK: case KeyEvent.KEYCODE_F8: {
		 * bKeyUsed = true; onVkey(keyCode); break; }
		 */

		}

		if (bKeyUsed) {
			return bKeyUsed;
		} else {
			return super.onKeyDown(keyCode, event);
		}

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

			case MESSAGE_CHANLIST_SHOW:
				banner.show(msg.arg1, 999999999);
				Intent toChanList = new Intent(Main.this, ChannelList.class);
				toChanList.putExtra("curType", msg.arg2);
				startActivity(toChanList);
				break;			

			case MESSAGE_PLAY_PRE:

				theActivity.objApplication.playChannel(
						theActivity.tempChannelID, true);

				theActivity.tempChannelID = -1;

				break;
			case MESSAGE_PLAY_NEXT:

				theActivity.objApplication.playChannel(
						theActivity.tempChannelID, true);

				theActivity.tempChannelID = -1;

				break;
			case MESSAGE_SHOW_AUTOSEARCH: {

				Class_Global.SetAimMenuID(6);
				String strInfoString = null;
				int curChanCount = objApplication.dvbDatabase.getChannelCountSC();
				if(curChanCount<=0){
					strInfoString = theActivity.mContext.getString(
							R.string.str_AutoSearchPrompt).toString();
				}else{
					strInfoString = theActivity.mContext.getString(
							R.string.str_ChannelUpdatePrompt).toString();
				}
				theActivity.searchPromptDiaog = DialogUtil.showPromptDialog(
						theActivity.mContext,strInfoString,
						null, null, null, new DialogBtnOnClickListener() {

							@Override
							public void onSubmit(DialogMessage dialogMessage) {
								nMonitor.stopMonitor();
								Intent mIntent = new Intent();
								/*
								 * mIntent.setComponent(new ComponentName(
								 * "com.chonghong.dtv_scan",
								 * "com.chonghong.dtv_scan.Dtv_Scan_Enter"));
								 */
								mIntent.setComponent(new ComponentName(
										"com.SysSettings.main",
										"com.SysSettings.main.MainActivity"));

								mIntent.putExtra("StartId", 1); // autoscan
																// without
																// scan_enter_menu
								try {
									theActivity.startActivity(mIntent);
									SetDtvStatus(false, true);
								} catch (Exception e) {
									e.printStackTrace();
									// objApplication.SetDtvThreadOn(true);
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

				/*
				 * if (theActivity.iKey == 1111) {l; BookInfo info = new
				 * BookInfo(); info.bookTimeStart = "12:00";
				 * info.bookChannelIndex = 1; info.bookChannelName = "cctv";
				 * info.bookDay = "20141231"; info.bookEnventName =
				 * "zhenghuanchjuan";
				 * 
				 * theActivity.objApplication.dvbBookDataBase
				 * .BookInfoCommit(info); Toast.makeText(theActivity,
				 * "add book ", Toast.LENGTH_SHORT) .show(); theActivity.iKeyNum
				 * = 0; theActivity.iKey = 0; theActivity.mUiHandler
				 * .sendEmptyMessage(MESSAGE_DISAPPEAR_DIGITAL); } else if
				 * (theActivity.iKey == 2418) { Toast.makeText(theActivity,
				 * R.string.str_join_tiaoshi, Toast.LENGTH_LONG).show();
				 * theActivity.startActivity(new Intent(theActivity,
				 * Factory.class)); theActivity.iKeyNum = 0; theActivity.iKey =
				 * 0; theActivity.mUiHandler
				 * .sendEmptyMessage(MESSAGE_DISAPPEAR_DIGITAL); } else if
				 * (theActivity.iKey == 2048) {
				 * 
				 * 
				 * theActivity.tvRootDigitalkey.setVisibility(View.INVISIBLE);
				 * theActivity.tvRootDigitalKeyInvalid
				 * .setVisibility(View.VISIBLE);
				 * 
				 * theActivity.mUiHandler
				 * .removeMessages(MESSAGE_HANDLER_DIGITALKEY);
				 * theActivity.mUiHandler
				 * .sendEmptyMessage(MESSAGE_DISAPPEAR_DIGITAL);
				 * 
				 * theActivity.iKeyNum = 0; theActivity.iKey = 0;
				 * 
				 * } else
				 */
				P.i("exec logic_num   =  " + theActivity.iKey);
				if (theActivity.iKey < 0) {
					theActivity.tvRootDigitalkey.setVisibility(View.GONE);

					theActivity.tvRootDigitalKeyInvalid
							.setVisibility(View.VISIBLE);

					theActivity.mUiHandler
							.removeMessages(MESSAGE_HANDLER_DIGITALKEY);
					theActivity.mUiHandler.sendEmptyMessageDelayed(
							MESSAGE_DISAPPEAR_DIGITAL, 5000);

					theActivity.iKeyNum = 0;
					theActivity.iKey = 0;
				} else {

					int succ = theActivity.objApplication.playChannelByLogicNo(
							theActivity.iKey, true);
					// int succ =
					// theActivity.objApplication.playChannelKeyInput(theActivity.iKey,true);
					if (succ < 0) {
						P.i("exec logic_num error  =  " + theActivity.iKey);
						theActivity.tvRootDigitalkey.setVisibility(View.GONE);
						theActivity.tvRootDigitalKeyInvalid
								.setVisibility(View.VISIBLE);
						//theActivity.id_dtv_channel_name.setVisibility(View.INVISIBLE);
						theActivity.mUiHandler.sendEmptyMessageDelayed(
								MESSAGE_DISAPPEAR_DIGITAL, 5000);
					} else {

						theActivity.banner.show(SysApplication.iCurChannelId);

						theActivity.tvRootDigitalkey.setVisibility(View.GONE);
						/*
						 * Message msg2 = new Message(); msg2.what =
						 * MESSAGE_SHOW_DIGITALKEY; msg2.arg1 =
						 * theActivity.iKey; sendMessage(msg2);
						 */
					}
					theActivity.iKeyNum = 0;
					theActivity.iKey = 0;
					theActivity.mUiHandler
							.removeMessages(MESSAGE_HANDLER_DIGITALKEY);
					/*
					 * theActivity.mUiHandler.sendEmptyMessageDelayed(
					 * MESSAGE_DISAPPEAR_DIGITAL, 2000);
					 */
				}

			}
				break;

			case MESSAGE_SHOW_DIGITALKEY:

				theActivity.tvRootDigitalkey.setVisibility(View.VISIBLE);

				int channelId = msg.arg1;

				P.i("zhougang  main",
						"MESSAGE_SHOW_DIGITALKEY   -----channelId-------theActivity.UpOrDownIsPressed "
								+ channelId + "   "
								+ theActivity.tvRootDigitalkey.getVisibility());
				theActivity.tvRootDigitalKeyInvalid.setVisibility(View.GONE);

				theActivity.Display_Program_Num(channelId);

				theActivity.mUiHandler
						.removeMessages(MESSAGE_DISAPPEAR_DIGITAL);
				theActivity.mUiHandler.sendEmptyMessageDelayed(
						MESSAGE_DISAPPEAR_DIGITAL, 3500);
				break;

			case MESSAGE_SHOW_DIGITALKEY_FOR_PRE_OR_NEXT_KEY:
				int channelId2 = msg.arg1;

				P.i("zhougang  main",
						"MESSAGE_SHOW_DIGITALKEY   -----channelId------- "
								+ channelId2 + "   ");
				theActivity.tvRootDigitalKeyInvalid.setVisibility(View.GONE);

				if (theActivity.tvRootDigitalkey.getVisibility() != View.VISIBLE)
					theActivity.tvRootDigitalkey.setVisibility(View.VISIBLE);

				theActivity.Display_Program_Num(channelId2);

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
					theActivity.tvRootDigitalkey.setVisibility(View.GONE);
				}

				//theActivity.id_dtv_channel_name.setVisibility(View.INVISIBLE);
			}
				break;
/*
			case MESSAGE_CA_SHOWNOTICE: {
				if (updateDtvStatus(2, true)) {
					theActivity.flCaInfo.setVisibility(View.VISIBLE);
				}
				String tmp = msg.obj.toString();
				if (tmp != null) {
					theActivity.tvCaInfo.setText(tmp);
				}

			}
				break;

			case MESSAGE_CA_HIDENOTICE: {
				if (updateDtvStatus(2, false)) {
					theActivity.flCaInfo.setVisibility(View.INVISIBLE);
				}
			}
				break;
*/				
			case MESSAGE_START_RECORD: {

			}
				break;
			case MESSAGE_STOP_RECORD: {

			}
				break;
			case Class_Constant.BACK_TO_LIVE:
				if (programBannerDialog != null) {
					programBannerDialog.dismiss();
					Toast.makeText(MyApp.getContext(), "退回到直播模式",
							Toast.LENGTH_SHORT).show();
					P.i("mmmm", "退回到直播模式");
					{
						// 恢复播放当前频道

						/*
						 * objApplication.playChannel(
						 * objApplication.getCurPlayingChannel(), false);
						 * DisplayBanner();
						 */
						SetDtvStatus(true, false);
						/*
						 * Message msg2 = new Message(); msg2.what =
						 * MESSAGE_SHOW_DIGITALKEY; msg2.arg1 =
						 * objApplication.getCurPlayingChannel().logicNo;
						 * mUiHandler.sendMessage(msg2);
						 */
					}
				}
				break;
			case Class_Constant.TOAST_BANNER_PROGRAM_PASS:
				curChannelPrograms = CacheData.getCurPrograms();

				// 临时针对现在频道支持时移，但是又没的时移的情况，服务器完善后取消即可
				if (null == curChannelPrograms
						|| curChannelPrograms.size() <= 0) {
					// Toast.makeText(Main.this, "节目信息为空，不能进入时移模式",
					// Toast.LENGTH_SHORT).show();
					SetDtvStatus(true, false);
					return;
				}

				// 隐藏直播的banner
				if (Banner.getBannerDisStatus()) {
					// 如果banner存在,则消除banner
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

		int num[] = { R.drawable.num_0, R.drawable.num_1, R.drawable.num_2,
				R.drawable.num_3, R.drawable.num_4, R.drawable.num_5,
				R.drawable.num_6, R.drawable.num_7, R.drawable.num_8,
				R.drawable.num_9 };
		int pos1 = 0, pos2 = 0, pos3 = 0;

		if (channelId < 1000) {
			pos1 = channelId / 100;
			pos2 = (channelId - pos1 * 100) / 10;
			pos3 = (channelId - pos1 * 100 - pos2 * 10);
		}

		dtv_digital_1.setBackgroundResource(num[pos1]);
		dtv_digital_2.setBackgroundResource(num[pos2]);
		dtv_digital_3.setBackgroundResource(num[pos3]);

	}

	/*
	 * void Display_Program_Num(int channelId) {
	 * 
	 * if (channelId < 10) { switch (channelId % 10) { case 0:
	 * 
	 * dtv_digital_3.setBackgroundDrawable(getResources().getDrawable(
	 * R.drawable.num_0)); break; case 1:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_1)); break; case 2:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_2)); break; case 3:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_3)); break;
	 * 
	 * case 4: dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_4)); break; case 5:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_5)); break; case 6:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_6)); break; case 7:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_7)); break; case 8:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_8)); break;
	 * 
	 * case 9: dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_9)); break;
	 * 
	 * }
	 * 
	 * dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_0));
	 * 
	 * dtv_digital_1.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_0)); } else if (channelId >= 10 && channelId
	 * < 100) {
	 * 
	 * switch (channelId / 10) { case 0:
	 * 
	 * dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_0)); break; case 1:
	 * dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_1)); break; case 2:
	 * dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_2)); break; case 3:
	 * dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_3)); break;
	 * 
	 * case 4: dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_4)); break; case 5:
	 * dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_5)); break; case 6:
	 * dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_6)); break; case 7:
	 * dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_7)); break; case 8:
	 * dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_8)); break;
	 * 
	 * case 9: dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_9)); break; } switch (channelId % 10) { case
	 * 0:
	 * 
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_0)); break; case 1:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_1)); break; case 2:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_2)); break; case 3:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_3)); break;
	 * 
	 * case 4: dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_4)); break; case 5:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_5)); break; case 6:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_6)); break; case 7:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_7)); break; case 8:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_8)); break;
	 * 
	 * case 9: dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_9)); break;
	 * 
	 * }
	 * 
	 * dtv_digital_1.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_0));
	 * 
	 * } else if (channelId >= 100 && channelId <=999) {
	 * 
	 * switch (channelId / 100) { case 0:
	 * 
	 * dtv_digital_1.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_0)); break; case 1:
	 * dtv_digital_1.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_1)); break; case 2:
	 * dtv_digital_1.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_2)); break; case 3:
	 * dtv_digital_1.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_3)); break;
	 * 
	 * case 4: dtv_digital_1.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_4)); break; case 5:
	 * dtv_digital_1.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_5)); break; case 6:
	 * dtv_digital_1.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_6)); break; case 7:
	 * dtv_digital_1.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_7)); break; case 8:
	 * dtv_digital_1.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_8)); break;
	 * 
	 * case 9: dtv_digital_1.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_9)); break;
	 * 
	 * }
	 * 
	 * switch ((channelId / 10) % 10) { case 0:
	 * 
	 * dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_0)); break; case 1:
	 * dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_1)); break; case 2:
	 * dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_2)); break; case 3:
	 * dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_3)); break;
	 * 
	 * case 4: dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_4)); break; case 5:
	 * dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_5)); break; case 6:
	 * dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_6)); break; case 7:
	 * dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_7)); break; case 8:
	 * dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_8)); break;
	 * 
	 * case 9: dtv_digital_2.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_9)); break; } switch (channelId % 10) { case
	 * 0:
	 * 
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_0)); break; case 1:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_1)); break; case 2:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_2)); break; case 3:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_3)); break;
	 * 
	 * case 4: dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_4)); break; case 5:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_5)); break; case 6:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_6)); break; case 7:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_7)); break; case 8:
	 * dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_8)); break;
	 * 
	 * case 9: dtv_digital_3.setBackgroundDrawable(getResources()
	 * .getDrawable(R.drawable.num_9)); break;
	 * 
	 * }
	 * 
	 * }
	 * 
	 * //banner.show(SysApplication.iCurChannelId);
	 * 
	 * }
	 */

	public boolean onVkey(int ri_KeyCode) {

		boolean b_Result = false;
		P.d(" main->onVkey: " + ri_KeyCode);
		switch (ri_KeyCode) {
		case Class_Constant.KEYCODE_INFO_KEY: {

			objApplication.playLastChannel();
			banner.show(SysApplication.iCurChannelId);

			// GH UI-1.3 换台不显示右上角频道号
			// Message msg = new Message();
			// msg.what = MESSAGE_SHOW_DIGITALKEY;
			// msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
			// mUiHandler.sendMessage(msg);
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

			if (iKeyNum != 0 && tvRootDigitalkey.isShown()) {// 如果数字键存在，则响应为快速切换到数字指定的频道
				mUiHandler.sendEmptyMessage(MESSAGE_HANDLER_DIGITALKEY);
			} else {
				// 时移模块
				dealOnKeyUp(ri_KeyCode);
			}

		}
			break;
		case Class_Constant.KEYCODE_RIGHT_ARROW_KEY: {
			// show channel list

			Intent toChanList = new Intent(Main.this, ChannelList.class);
			toChanList.putExtra("curType", 0);
			startActivity(toChanList);
			nMonitor.stopMonitor();
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

			if (iKeyNum > 0 && iKeyNum <= 3) {
				iKey = (ri_KeyCode - Class_Constant.KEYCODE_KEY_DIGIT0) + iKey
						* 10;
			}

			P.i("iKey value   =  " + iKey);

			tvRootDigitalKeyInvalid.setVisibility(View.GONE);
			tvRootDigitalkey.setVisibility(View.VISIBLE);

			Display_Program_Num(iKey);

			if (iKeyNum>=3 /*iKey >= 100*/) {
				//mUiHandler.sendEmptyMessage(MESSAGE_HANDLER_DIGITALKEY); // 输入3位数字立即切台
				mUiHandler.sendEmptyMessageDelayed(MESSAGE_HANDLER_DIGITALKEY,200);//如果立即切台，输入第三位数字看不见
			} else {
				mUiHandler.sendEmptyMessageDelayed(MESSAGE_HANDLER_DIGITALKEY,
						4000);
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
		P.d("Main onStart!");

		super.onStart();
		scene.init(this);
	}

	@Override
	protected void onStop() {
		P.d("Main onStop!");
		/*
		 if(Main.nMonitor!=null)
		 	nMonitor.stopMonitor();		
		*/		 	
		SetDtvStatus(false, true);
		super.onStop();
		scene.release();
	}

	private void findView() {
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		//flCaInfo = (RelativeLayout) findViewById(R.id.id_root_CA_info);
		// tvCaSubtitleDown = (CAMarquee) findViewById(R.id.id_ca_subtitleDown);
		// tvCaSubtitleUp = (CAMarquee) findViewById(R.id.id_ca_subtitleUp);
		//tvCaInfo = (TextView) findViewById(R.id.id_root_ca_init_textview);

		flNoSignal = (RelativeLayout) findViewById(R.id.id_root_nosignal_info);

		tvRootDigitalkey = (LinearLayout) findViewById(R.id.id_dtv_digital_root);
		tvRootDigitalKeyInvalid = (RelativeLayout) findViewById(R.id.id_dtv_digital_root_invalid);

		//id_dtv_channel_name = (TextView) findViewById(R.id.id_dtv_channel_name);
		dtv_digital_1 = (ImageView) findViewById(R.id.dtv_digital_1);
		dtv_digital_2 = (ImageView) findViewById(R.id.dtv_digital_2);
		dtv_digital_3 = (ImageView) findViewById(R.id.dtv_digital_3);

		flTimeShift = (FrameLayout) findViewById(R.id.id_root_timeshift_support);
		vol_mult_icon = (ImageView) findViewById(R.id.mute_icon);

		// layout_set_activity_z = (LinearLayout)
		// findViewById(R.id.layout_set_activity_z);

	}
	private void initValue_OnResume() {
		if(volleyTool==null)
			volleyTool = VolleyTool.getInstance();
		if(mReQueue==null)
			mReQueue = volleyTool.getRequestQueue();
		if (null == processData) {
			processData = new ProcessData();
		}
		initTimeshiftData();
		initCategoryData();		
		P.i(TAG, "exec catedata & timeshift data");
	}
	private void initValue() {

		// string array
		/*
		 * str_title = getResources().getString(R.string.str_zhn_information);
		 * str_details_exitdtv = getResources().getString(
		 * R.string.str_zhn_isexitdtv); s_IsAutoScan =
		 * getResources().getString(R.string.str_zhn_diaissearch); s_IsUpdate =
		 * getResources().getString(R.string.str_zhn_updatesearch);
		 * 
		 * IntentFilter filter = new IntentFilter();
		 * filter.addAction("com.changhong.action.DTV_CHANGED");
		 * filter.addAction("com.changhong.action.CTL_CHANGED");
		 * registerReceiver(dtvctlReceiver, filter);
		 */
		volleyTool = VolleyTool.getInstance();
		mReQueue = volleyTool.getRequestQueue();
		if (null == processData) {
			processData = new ProcessData();
		}
		if (!bSearchTtvData) {
			P.i(TAG, "REQ-1 catedata & timeshift data");
			initTimeshiftData();
			initCategoryData();
			bSearchTtvData = true;
		}
		// checkAndUpdateCateinfo();
	}

	/**
	 * 
	 */
	private Thread cateThread = null;

	private void checkAndUpdateCateinfo() {
		if (cateThread == null) {
			P.d("begin>>> request timeshift and category data!");
			cateThread = new Thread() {
				public void run() {
					initCategoryData();
				}
			};
			cateThread.start();
		}
		P.d("end>>> request timeshift and category data!");
	}

	// 用于同步直播和时移切换时的状态同步
	private static boolean[] bOnDtvThread = new boolean[10];

	// int index: 0:dtv status,1:signal status;2 sc status: 3 mult status 4
	// avplay status 5 ca status
	public void SetDtvStatus(boolean status, boolean act) {

		if (bOnDtvThread[0] == status) {
			P.d("SetDtvStatus >>> the same status, return!");
			return;
		}

		bOnDtvThread[0] = status;

		if (status == false)// 隐藏显示
		{
			act = true; //强制黑屏
			
			if (banner != null)
				banner.cancel();

			showTimeShiftIcon(false);
			objApplication.showAudioPlaying(false);

			if (objApplication != null) {
				objApplication.dvbPlayer.stop();
				if(act)	
				{
					objApplication.dvbPlayer.blank(); 
					objApplication.dvbPlayer.release();					
				}
					
				bOnDtvThread[4] = false;
			}

			if (bOnDtvThread[1] && flNoSignal.getVisibility() == View.VISIBLE) {
				flNoSignal.setVisibility(View.INVISIBLE);
				Log.i(TAG, "hide nosignal window");
			}
			/*if (bOnDtvThread[2] && flCaInfo.getVisibility() == View.VISIBLE) {
				flCaInfo.setVisibility(View.INVISIBLE);
			}*/
			if (objApplication != null){
				//objApplication.hideCaMsgBoxBroadcast();
				objApplication.switchCaCtr(false);//disable ca
			}
			
			if (bOnDtvThread[3]
					&& vol_mult_icon.getVisibility() == View.VISIBLE) {
				vol_mult_icon.setVisibility(View.GONE);
			}
			if (bOnDtvThread[5]
					&& objApplication.getCaLayout().getVisibility() == View.VISIBLE) {
				objApplication.getCaLayout().setVisibility(View.INVISIBLE);
			}

		} else // 恢复显示
		{
			if (objApplication != null)
				objApplication.switchCaCtr(true);//enable ca
			
			// 恢复播放
			if (!bOnDtvThread[4]) {
				bOnDtvThread[4] = true;
				// onVkey(Class_Constant.KEYCODE_INFO_KEY);
				if (objApplication != null && bNeedPlay)
						objApplication.playLastChannel();
				
				if (iCallChanListId != -1) {
					Message msg = new Message();
					msg.what = MESSAGE_CHANLIST_SHOW;
					msg.arg1 = SysApplication.iCurChannelId;
					msg.arg2 = iCallChanListId;
					mUiHandler.sendMessage(msg);
				} else if(SysApplication.iCurChannelId!=-1){
					banner.show(SysApplication.iCurChannelId);
				}
				
				iCallChanListId = -1;
				bNeedPlay = true;
			}

			if (!act && bOnDtvThread[1] && flNoSignal.getVisibility() == View.INVISIBLE) {
				flNoSignal.setVisibility(View.VISIBLE);
				Log.i(TAG, "display nosignal window");
			}

			/*
			if (bOnDtvThread[2] && flCaInfo.getVisibility() == View.INVISIBLE) {
				flCaInfo.setVisibility(View.VISIBLE);
			}*/

			Log.d("INFO", "" + bOnDtvThread[5]
					+ objApplication.getCaLayout().getVisibility());
			if (bOnDtvThread[5]
					&& objApplication.getCaLayout().getVisibility() == View.INVISIBLE) {
				objApplication.getCaLayout().setVisibility(View.VISIBLE);
			}

			AudioManager am1 = (AudioManager) mContext.getApplicationContext()
					.getSystemService(Context.AUDIO_SERVICE);
			boolean isMute1 = am1.isStreamMute(AudioManager.STREAM_MUSIC);
			bOnDtvThread[3] = isMute1;
			if (bOnDtvThread[3]
					&& vol_mult_icon.getVisibility() != View.VISIBLE) {
				vol_mult_icon.setVisibility(View.VISIBLE);
			}
		}

	}

	// 返回false则忽略调用该函数所在函数的后续处理
	public static boolean updateDtvStatus(int index, boolean status) {
		Log.i(TAG, "updateDtvStatus：inx=" + index + ",val=" + status + ",ret="
				+ bOnDtvThread[0]);
		if (index <= 0 || index >= 9)
			return false;
		bOnDtvThread[index] = status;
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
					// GH UI-1.3 换台不显示右上角频道号
					// Message msg = new Message();
					// msg.what = MESSAGE_SHOW_DIGITALKEY;
					// msg.arg1 = objApplication.getCurPlayingChannel().logicNo;
					// mUiHandler.sendMessage(msg);
				} else {

				}
			}
		}
	};

	private class signalRecever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if(!bAllowSignalDis){
				Log.i(TAG, "signalRecever>>forbid#2");
				return;
			}
			Bundle myBundle = intent.getExtras();
			boolean bIsLocked = myBundle.getBoolean(TunerInfo.TunerInfo_Locked);
			Log.i(TAG, "get tuner intent>>>"+bIsLocked);
			if (!updateDtvStatus(1, (!bIsLocked))) {
				Log.i(TAG, "signalRecever>>forbid#1");
				return;
			}

			if (!bIsLocked) {
				flNoSignal.setVisibility(View.VISIBLE);
				objApplication.blackScreen();
				Log.i(TAG, "display no signal>>>");
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
				P.i(TAG, "HOME_PRESSED");
				if (banner != null)
					banner.cancel();
				nMonitor.stopMonitor();
				//finish();
			}

		}

	}

	private class CateinfoReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			String strAction = arg1.getAction();

			if (strAction.equals(sGetcateinfo)) {
				P.i(TAG, "get intent:" + sGetcateinfo);
				//checkAndUpdateCateinfo();
				initTimeshiftData();
				initCategoryData();
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

	@Override
	public void onExecute(Intent intent) {
		P.d("Main onExecute !");
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
		P.d("Main onQuery !");
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

	private void initTimeshiftData() {
		Channel DBchan = null;
		// 获取频道是否支持时移和频道logoURL

		// 获取当前频道信息
		ChannelDB db = DVB.getManager().getChannelDBInstance();
		PlayingInfo thisPlayingInfo = DVB.getManager().getChannelDBInstance()
				.getSavedPlayingInfo();
		// 获取当前Channel详细信息
		if (thisPlayingInfo != null) {
			DBchan = db.getChannel(thisPlayingInfo.mChannelId);
		}
		P.i("mmmm", "Main=initTimeshiftData_DBchan:" + DBchan
				+ "thisPlayingInfo:" + thisPlayingInfo);
		// if(null==DBchan||TextUtils.isEmpty(DBchan.is_ttv))
		// {
		getIsTTVData();
		// }

	}

	private void getIsTTVData() {
		// 这个IPTV定义的接口，2016年12月9日付岩确认弃用，以后可能会重新启用
		// String URL = processData.getChannelsInfo();
		// JsonObjectRequest jsonObjectRequest = new
		// JsonObjectRequest(Request.Method.GET, URL, null,
		// new Response.Listener<org.json.JSONObject>() {
		//
		// @Override
		// public void onResponse(org.json.JSONObject arg0) {
		// // TODO Auto-generated method stub
		// P.i("mmmm", "Main=getUserChannel:" + arg0);
		//
		// HandleLiveData.getInstance().dealChannelExtra(arg0);
		// }
		// }, errorListener);
		// jsonObjectRequest.setTag(Main.class.getSimpleName());//
		// 设置tag,cancelAll的时候使用
		// mReQueue.add(jsonObjectRequest);
		mReQueue.cancelAll(Main.class.getSimpleName());
		String URL = processData.getChannelList();
		Log.i("mmmm", "getIsTTVData_url:" + URL);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.GET, URL, null,
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

	private void initCategoryData() {
		{
			mReQueue.cancelAll(Main.class.getSimpleName()+"_forCata");
			String URL2 = processData.getCategoryString();
			P.i("mmmm", "Main=initCategoryData:" + URL2);
			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
					Request.Method.GET, URL2, null,
					new Response.Listener<org.json.JSONObject>() {

						@Override
						public void onResponse(org.json.JSONObject arg0) {
							// TODO Auto-generated method stub
							String newVer = HandleLiveData.getInstance()
									.dealCategoryVer(arg0);
							String oldVer = Utils
									.getProp("persist.sys.live.cateversion");
							P.i("cateversion>>>>> old: " + oldVer + " vs new:"
									+ newVer);
							if (oldVer == null || newVer != null
									&& !newVer.equals(oldVer)) {
								P.i("initSortData_new:" + arg0);
								SortData.saveSortNameList(HandleLiveData
										.getInstance().dealCategoryName(arg0));
								HandleLiveData.getInstance().dealCategoryData(
										arg0);
								Utils.setProp("persist.sys.live.cateversion",
										newVer);
								P.i("save new cateversion:" + newVer);
								OpJsonFile.writeJSONObj(
										"/data/changhong/dvb/catedata.json",
										arg0);
							}
							cateThread = null;
						}
					}, errorListener_sort);
			jsonObjectRequest.setTag(Main.class.getSimpleName()+"_forCata");// 设置tag,cancelAll的时候使用
			mReQueue.add(jsonObjectRequest);
		}
	}

	private Response.ErrorListener errorListener_sort = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			P.i(TAG, "sortOP_error：" + arg0);
			// 从本地获取文件进行分析
			JSONObject obj;
			obj = OpJsonFile.readJSONObj("/data/changhong/dvb/catedata.json");
			if (obj != null) {
				String newVer = HandleLiveData.getInstance().dealCategoryVer(
						obj);
				SortData.saveSortNameList(HandleLiveData.getInstance()
						.dealCategoryName(obj));
				HandleLiveData.getInstance().dealCategoryData(obj);
				Utils.setProp("persist.sys.live.cateversion", newVer);
				P.i(TAG, "save cateversion:" + newVer + " from local file");
			} else {
				P.i(TAG, "NOT FOUND LOCAL catedata.json");
			}
			cateThread = null;
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

		// 获取当前道信息
		ChannelDB db = DVB.getManager().getChannelDBInstance();
		PlayingInfo thisPlayingInfo = db.getSavedPlayingInfo();
		// 获取当前Channel详细信息
		Channel DBchan = db.getChannel(thisPlayingInfo.mChannelId);

		if (programBannerDialog != null) {
			programBannerDialog.cancel();
		}
		mAudioManager = (AudioManager) getApplicationContext()
				.getSystemService(AUDIO_SERVICE);
		programBannerDialog = new BannerDialog(Main.this, DBchan,
				curChannelPrograms, mUiHandler, surfaceView, mAudioManager);

		programBannerDialog.show();
	}

	private boolean dealOnKeyUp(int keyCode) {
		switch (keyCode) {
		case Class_Constant.KEYCODE_OK_KEY:
			// if (tvRootDigitalkey.isShown()) {
			// //当数字键显示的时候，响应数字换台逻辑
			//
			//
			// } else {
			if (!NetworkUtils.isConnectInternet(Main.this)) {
				Toast.makeText(Main.this, "网络不可用，请检查!", Toast.LENGTH_SHORT)
						.show();
			} else {

				// 获取当前道信息
				ChannelDB db = DVB.getManager().getChannelDBInstance();
				PlayingInfo thisPlayingInfo = db.getSavedPlayingInfo();
				// 获取当前Channel详细信息
				Channel DBchan = db.getChannel(thisPlayingInfo.mChannelId);
				// 判断是否可以时移，如果不可以则不进入,is_ttv 0不支持，1支持
				Log.i(TAG, "DBchan.isttv" + DBchan.is_ttv);
				if (DBchan.is_ttv.equals("1")) {
					SetDtvStatus(false, false);
					// 获取节目
					PlayVideo.getInstance().getProgramInfo(mUiHandler, DBchan);
				}

			}
			// }
			break;
		}
		return true;
	}

	public static void showTimeShiftIcon(boolean bDisplay) {
		if (flTimeShift != null) {
			if (bDisplay && flTimeShift.getVisibility() != View.VISIBLE) {
				flTimeShift.setVisibility(View.VISIBLE);
				// Log.i(TAG, "flTimeShift enable");
			} else if (!bDisplay && flTimeShift.getVisibility() == View.VISIBLE) {
				flTimeShift.setVisibility(View.GONE);
				// Log.i(TAG, "flTimeShift disable");
			} else {
				// Log.i(TAG,
				// "flTimeShift keep>>"+bDisplay+",?"+flTimeShift.getVisibility());
			}
		} else {
			Log.i(TAG, "flTimeShift is null");
		}
	}
	
	//for test
	private void startAutoSearch(){
		try {
			Intent intent = new Intent();
			ComponentName name = new ComponentName("com.SysSettings.main",
					"com.SysSettings.main.MainActivity");
			intent.setComponent(name);
			intent.putExtra("StartId", 1);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			if(Main.nMonitor!=null)
			Main.nMonitor.stopMonitor();//发起搜台成功时，需要关闭nit监控			
		} catch (Exception e) {
			Toast.makeText(this, "请先安装该app", Toast.LENGTH_SHORT).show();
		}
	}
}
