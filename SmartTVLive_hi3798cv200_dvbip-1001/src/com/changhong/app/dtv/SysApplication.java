package com.changhong.app.dtv;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changhong.app.book.BookDataBase;
import com.changhong.app.book.BookInfo;
import com.changhong.app.ca.CaConfig;
import com.changhong.app.ca.CaLockService;
import com.changhong.app.timeshift.common.MyApp;
import com.changhong.app.utils.Utils;
import com.changhong.dvb.CA;
import com.changhong.dvb.CAListener;
import com.changhong.dvb.Channel;
import com.changhong.dvb.ChannelDB;
import com.changhong.dvb.DVB;
import com.changhong.dvb.DVBManager;
import com.changhong.dvb.LivePlayer;
import com.changhong.dvb.PVR;
import com.changhong.dvb.PlayingInfo;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_Data;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_MSG_CODE;
import com.changhong.dvb.ProtoCaSuma.DVB_CA_SUMA;
import com.changhong.dvb.ProtoCaSuma.DVB_CA_SUMA_Data;
import com.changhong.dvb.ProtoCaSuma.DVB_CA_SUMA_MSG_CODE;
import com.changhong.dvb.ProtoMessage.DVB_CA;
import com.changhong.dvb.ProtoMessage.DVB_CAR_EVENT;
import com.changhong.dvb.ProtoMessage.DVB_CA_TYPE;
import com.changhong.dvb.ProtoMessage.DVB_PLAYER_SYNC_CODE;
import com.changhong.dvb.ProtoMessage.DVB_PVR_RecAttr;
import com.changhong.dvb.ProtoMessage.DVB_RectSize;
import com.changhong.dvb.Scan;
import com.changhong.dvb.Table;
import com.changhong.dvb.Tuner;
import com.changhong.dvb.TunerListener;
/**
 * 
 * @author Administrator
 * 
 */
public class SysApplication extends Application implements CAListener {
	
	private List<Activity> mList = new LinkedList<Activity>();
	
	int cpuNums = Runtime.getRuntime().availableProcessors();
	//  获取CPU数目
	public ExecutorService mExeCutorService =Executors.newFixedThreadPool(cpuNums*3);
	//	设置线程池数目

	
	private static SysApplication instance;
	private static Context	mContext;
	public 	static boolean 	bNeedFirstBootIn		=	true;	
	public 	static boolean 	bNeedFirstBootInBanner	=	true;	
	public 	static boolean	bNeedTunnerInfoThread	=	true;
	
	public 	static	int		iCurChannelId	=	-1	;
	
	private static final String keyLastChanId = "lastChannelId";
	/**
	 * for dtv obj
	 */
	public   DVBManager 		dvbManager 		= null;
	public   LivePlayer 		dvbPlayer 		= null;
	public   ChannelDB 			dvbDatabase 	= null;
	public   com.changhong.dvb.EPG 				dvbEpg 			= null;
	public   Table	 			dvbTable 		= null;
	public   Scan 				mo_Scan 		= null;	
	public 	 Tuner				mo_Tunner		= null;	
	public 	 CA 				mo_Ca			= null;
//	public   DvbDt				mo_DvbDt 		= null;

	
	private	static	final	int	MESSAGE_CA_SHOWNOTICE		=	204;
	private	static	final	int	MESSAGE_CA_HIDENOTICE		=	205;
	
	private	static	final	int	MESSAGE_CA_SHOWOSDROLL		=	206;
	private	static	final	int	MESSAGE_CA_HIDEOSDROLL		=	207;
	
	//private static	 CaDialog mdialog;
	private WindowManager mWindowManager;  
	private WindowManager.LayoutParams param;  
	private	RelativeLayout ll_newchannelmode;
	private	TextView	tvinfo,ca_exter_info;
	
	private	FrameLayout ll_audioplaying = null;
	private	TextView	text_audioplaying = null;
	private WindowManager.LayoutParams audioPlayingParam = null; 
	
	private FrameLayout ll_osdroll;
	private CAMarquee 	text_osdroll_top;
	private CAMarquee	text_osdroll_buttom;
	 
	private boolean bInit	=	false;
	
	
	public 	 BookDataBase		dvbBookDataBase	= null;
	
	private int[] mOsdShowTime = new int[2];//Vanlen add
	
	 private WindowManager.LayoutParams wmParams=new WindowManager.LayoutParams();

	   
	   
	 public WindowManager.LayoutParams getMywmParams(){  
	  return wmParams;  
	 } 

	 
	private SysApplication() {
		mContext=MyApp.getContext();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		
		bNeedTunnerInfoThread	=	false;
		
	}

	public synchronized static SysApplication getInstance() {
		if (null == instance) {
			instance = new SysApplication();
		}
		return instance;
	}
	void readScreenKG(){
		
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		
		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
		@SuppressWarnings("deprecation")
		int height = wm.getDefaultDisplay().getHeight();
		Log.e("SCREEN","1>> width="+width+",hight="+height);
/*
		WindowManager wm1 = this.getWindowManager();
		int width1 = wm1.getDefaultDisplay().getWidth();
		int height1 = wm1.getDefaultDisplay().getHeight();
		Log.e("SCREEN","2>> width="+width+",hight="+height);
		
		WindowManager manager = this.getWindowManager();
		DisplayMetrics outMetrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(outMetrics);
		int width2 = outMetrics.widthPixels;
		int height2 = outMetrics.heightPixels;
		Log.e("SCREEN","1>> width="+width+",hight="+height);
		
		Resources resources = this.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		float density1 = dm.density;
		int width3 = dm.widthPixels;
		int height3 = dm.heightPixels;		
		Log.e("SCREEN","1>> width="+width+",hight="+height);
		*/
	}
	public void initDtvApp(Context mcontext)
	{
		mContext	=	mcontext;
		
		
		P.i("initDtvApp  bInit -> " +bInit);
		if(bInit)
		{
			return ;
		}
		else
		{
			bInit = true;
		}
				
		//init dvbplayer
		dvbManager	=	DVB.getManager();
		if (dvbManager == null)
		{
			P.e("get dvbManager faied, exit.");
			System.exit(0);
		}
		
		//init dvbplayer
		dvbPlayer 	= dvbManager.getDefaultLivePlayer();
		if (dvbPlayer == null)
		{
			P.e("createLivePlayer faied, exit.");
			System.exit(0);
		}
		
		mo_Scan = dvbManager.getScanInstance();
		
		//use demux 0 set epg
		dvbEpg	=	dvbManager.getEpgInstance();
		
		mo_Ca = dvbManager.getCaInstance();
		//mo_DvbDt = DvbDt.getInstance();
		
		//set ca 
		//set ca type
		ArrayList<DVB_CA_TYPE> caType = new ArrayList<DVB_CA_TYPE>();
		
		//Vanlen change this order
		//caType.add(DVB_CA_TYPE.CA_SUMA);
		caType.add(DVB_CA_TYPE.CA_NOVEL);
		
		mo_Ca.setType(caType);
		//mo_Ca.start();
		
		mo_Ca.setListener(this);
		
		//set timezone
		//mo_DvbDt.setTimeZone(8*3600);
		
		//new table  && init table
		dvbTable = dvbManager.getTableInstance();
	
		//init database 
		dvbDatabase	=	dvbManager.getChannelDBInstance();
				
		// set sync mode
		//dvbPlayer.setPlayAttr(DvbLivePlayer.PLAY_ATTR_SYNC_MODE, 0);	
		dvbPlayer.setSyncMode(DVB_PLAYER_SYNC_CODE.SYNC_REF_NONE);
		
		
		//init book database
		dvbBookDataBase	=	new BookDataBase(mcontext);
		
		//init tunner object
		mo_Tunner	=	dvbManager.getTunerInstance();
		
		mo_Tunner.setListener(new TunerListener() {
			
			@Override
			public void tunerCallback(DVB_CAR_EVENT arg0, int arg1, Object arg2) {
				// TODO Auto-generated method stub
				// if(!CheckDtvThreadOn()) return;
				
				switch(arg0)
				{
					case CAR_EVENT_BER_CHG:
						break;
					case CAR_EVENT_LOCK_CHG:
					{	
						reqNotifySignalChanged(arg1 == 0?false:true);
						break;
					}
					case CAR_EVENT_SIG_QUALITY_CHG:
						break;
					case CAR_EVENT_SIG_STRENGTH_CHG:
						break;
					case CAR_EVENT_SNR_CHG:
						break;
					default:
						break;
				}
			}
		});
		
		//init dt time,use as system time 
		//mo_DvbDt.usedAsSystemTime(true);		
		
		//init ca notify view
		initCaInfoView(mcontext);
		
		//init audio playing view
		initAudioPlayingView(mcontext);
		
		initOsdRollView(mcontext);
		
		readScreenKG();
	}

	/**
	 * 
	 */
	public void reqNotifySignalChanged(boolean locked) {
		Intent mTunerInfo = new Intent(TunerInfo.TunerInfo_Intent_FilterName);
		Bundle bundle = new Bundle();
		bundle.putBoolean(TunerInfo.TunerInfo_Locked, locked);
		mTunerInfo.putExtras(bundle);
		mContext.sendBroadcast(mTunerInfo);
		Log.i("DBG", ">>>>tuner locked: "+locked);
	}
	
	public void initBookDatabase(Context mContext)
	{
		dvbBookDataBase	=	new BookDataBase(mContext);
	}	
		
	public void initCaInfoView(Context mContext)
	{

		LayoutInflater li_inflater=LayoutInflater.from(mContext);
		ll_newchannelmode=(RelativeLayout) li_inflater.inflate(
							R.layout.ca, null);
		
	     tvinfo = (TextView)ll_newchannelmode.findViewById(R.id.id_root_ca_info);
	     ca_exter_info = (TextView)ll_newchannelmode.findViewById(R.id.id_ca_exter_info);	     
	    
	     	//获取WindowManager  
	     if(mWindowManager == null)
	     {
	    	 mWindowManager=(WindowManager)mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
	     }
	     	     
	     //设置LayoutParams(全局变量）相关参数  
	     param = getMywmParams();  
	     P.d(param.x +","+param.y+","+param.width+","+param.height);
	     
	     param.type=WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;     // 系统提示类型,重要  
	     param.format=1;  
	     param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点  

	     param.alpha = 1.0f;  
	           
	     param.gravity=Gravity.CENTER;  //调整悬浮窗口至左上角  
	        //以屏幕左上角为原点，设置x、y初始值  
	//     param.x=448;  
	//     param.y=220;  
	           
	        //设置悬浮窗口长宽数据  
	     if(mWindowManager.getDefaultDisplay().getWidth()==1920){
		     param.width=800; //600;  520 -> 800
		     param.height=400;//450;  256 -> 400
	     }else{
		     param.width=520; //600;  520 -> 800
		     param.height=256;//450;  256 -> 400	    	 
	     }
	           
	        //显示myCaDialog图像  
	     mWindowManager.addView(ll_newchannelmode, param);  
		     
	}
	public void initAudioPlayingView(Context mContext)
	{

		LayoutInflater li_inflater=LayoutInflater.from(mContext);
		ll_audioplaying=(FrameLayout) li_inflater.inflate(
							R.layout.audio, null);
		text_audioplaying = (TextView)ll_audioplaying.findViewById(R.id.id_audio_playing_text);
		text_audioplaying.setText(R.string.str_audio_playing);
	     //获取WindowManager  
	     if(mWindowManager == null)
	     {
	    	 mWindowManager=(WindowManager)mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
	     }
	        //设置LayoutParams(全局变量）相关参数  
	     audioPlayingParam = getMywmParams();  
     
	     audioPlayingParam.type=WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;     // 系统提示类型,重要  
	     audioPlayingParam.format=1;  
	     audioPlayingParam.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点  

	     audioPlayingParam.alpha = 1.0f;  
	           
	     audioPlayingParam.gravity=Gravity.TOP|Gravity.LEFT;   //调整悬浮窗口至左上角  
	        //以屏幕左上角为原点，设置x、y初始值  
	     audioPlayingParam.x=448;  
	     audioPlayingParam.y=220;  
	           
	        //设置悬浮窗口长宽数据  
	     audioPlayingParam.width=384;  
	     audioPlayingParam.height=204;  
	           
	        //显示myCaDialog图像  
	     mWindowManager.addView(ll_audioplaying, audioPlayingParam);  
		     
	}
	
	public void initOsdRollView(Context mContext)
	{
		LayoutInflater li_inflater=LayoutInflater.from(mContext);
		ll_osdroll = (FrameLayout)li_inflater.inflate(R.layout.osd_roll, null);
	     text_osdroll_top = (CAMarquee)ll_osdroll.findViewById(R.id.id_osd_roll_top);
	     text_osdroll_buttom = (CAMarquee)ll_osdroll.findViewById(R.id.id_osd_roll_buttom);
	     
	     if(mWindowManager == null)
	     {
	    	 mWindowManager=(WindowManager)mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
	     }
	     
	     param = getMywmParams();  
	     P.d(param.x +","+param.y+","+param.width+","+param.height);
	     
	     param.type=WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
	     param.format=1;  
	     param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

	     param.alpha = 1.0f;  
	           
	     param.gravity=Gravity.TOP|Gravity.LEFT;

	     param.x=0;  
	     param.y=0;  
	           
	     param.width=1920;  
	     param.height=1080;  

	     mWindowManager.addView(ll_osdroll, param);  
	}
	

	public Handler	mAppHandler  = new Handler(){

		@Override
		public void handleMessage(Message msg) {
	
			super.handleMessage(msg);
			
		{
			switch(msg.what)
			{
				case MESSAGE_CA_SHOWNOTICE:
				{
					if(msg.obj!=null)
					{
						showCainfo(msg.obj.toString());
					}
					else
					{
						P.e("MESSAGE_CA_SHOWNOTICE = null !");
					}
				}
				break;
				
				case MESSAGE_CA_HIDENOTICE:
				{
					
					hideCainfo();
					
				}
				break;
				
				case MESSAGE_CA_SHOWOSDROLL:
				{
					showOsdRoll(msg.arg1, (String)msg.obj);
					break;
				}
				
				case MESSAGE_CA_HIDEOSDROLL:
				{
					hideOsdRoll(msg.arg1);
					break;
				}
			
			}
			
		}
			
			
		}
		
	};
	
	/*
		Vanlen Begin
		Define this handler to clean the osd scrolling(for SUMA CA)
	*/
	public Handler	mOsdCleanHandler  = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what)
			{
				case 0:
				{
					Message msg1 = new Message();
					if(msg.arg1 == 1)//top
					{
						mOsdShowTime[0] += 1000;
						if(text_osdroll_top.isScrolling())
						{
							msg1.what = 0;
							msg1.arg1 = 1;
							this.sendMessageDelayed(msg1, 1000);
						}
						else
						{					
							msg1.what = MESSAGE_CA_HIDEOSDROLL;
							msg1.arg1 = 1;
							mAppHandler.sendMessage(msg1);
						}
					}
					else
					{
						mOsdShowTime[1] += 1000;
						
						if(text_osdroll_buttom.isScrolling())
						{
							msg1.what = 0;
							msg1.arg1 = 0;
							this.sendMessageDelayed(msg1, 1000);
						}
						else
						{					
							msg1.what = MESSAGE_CA_HIDEOSDROLL;
							msg1.arg1 = 0;
							mAppHandler.sendMessage(msg1);
						}
					}
					
					
					break;
				}
			}
		}
	};
	/*
		Vanlen END
	*/
	
	/**
	 * set ca listener
	 * @param mlisListener
	 */
	public void setCaListener(CAListener mlisListener)
	{
		if(mlisListener != null)
		{
			P.i("setCaListener !");
			mo_Ca.setListener(mlisListener);
		}
		else
		{
			P.e("setCaListener err !");
		}
	}
	
	
	
	// add Activity
	public void addActivity(Activity activity) {
		mList.add(activity);
	}

	public void exit() {
		try {
			for (Activity activity : mList) {
				if (activity != null)
					activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}
	
	public int playChannelByLogicNo(int logicNo,boolean isCheckPlaying){
		if(logicNo==-1){
			return -1;
		}
		Channel channel = dvbDatabase.getChannelByLogicNo(logicNo);
		if(channel==null){
			return -1;
		}
		if(channel.chanId==iCurChannelId&&isCheckPlaying){
			P.i("playChannel the channel is playing,return direct .");
			return iCurChannelId;
		}
		dvbPlayer.stop();	
		/* If it is audio channel, blank the screen */
		if(channel.sortId == 2 || channel.videoPid == 0x0 || channel.videoPid == 0x1fff)
		{
			dvbPlayer.blank();
			showAudioPlaying(true);
		}
		else
		{
			showAudioPlaying(false);
		}

		dvbPlayer.play(channel);
		
		//mo_Ca.channelNotify(curChannel);
		
		iCurChannelId = channel.chanId;
		
		//SetUserInfo(keyLastChanId, iCurChannelId);
		SetUserInfo(channel);
		updateCaStatusBroadcast();
		return 0;
	}
	
	
	public int playChannel(int channelId,boolean isCheckPlaying)
	{
		
		if(channelId == iCurChannelId  && isCheckPlaying)
		{
			P.i("playChannel the channel is playing,return direct .#1");
			return channelId;
		}

		Channel curChannel = dvbDatabase.getChannelSC(channelId);			
		if (curChannel == null)
		{
			P.e("Cann't get valid channel.");
			return -1;
		}
		/*
		if(curChannel.sortId != 0x1 && curChannel.sortId != 0x11 &&
				curChannel.sortId != 0x16 && curChannel.sortId != 0x19 &&
				(curChannel.videoPid == 0x0 || curChannel.videoPid == 0x1fff))
		{
			return -1;
		}
		*/
		
		P.i("playChannel#1>> chid="+channelId+",logicNo=" + curChannel.logicNo);
		
		dvbPlayer.stop();	
		/* If it is audio channel, blank the screen */
		if(curChannel.sortId == 2 || curChannel.videoPid == 0x0 || curChannel.videoPid == 0x1fff)
		{
			dvbPlayer.blank();
			showAudioPlaying(true);
		}
		else
		{
			showAudioPlaying(false);
		}

		dvbPlayer.play(curChannel);
		
		//mo_Ca.channelNotify(curChannel);
		
		iCurChannelId = channelId;
		
		//SetUserInfo(keyLastChanId, iCurChannelId);
    	SetUserInfo(curChannel);
		updateCaStatusBroadcast();

		return 0;
	}
	
	
	
	public int playChannelCa(int channelId, boolean isCheckPlaying) {

		P.d("playChannel(int channelId)  Now channel ->  " + iCurChannelId
				+ " ,  try to play ->  " + channelId);

		if (channelId == iCurChannelId && isCheckPlaying) {
			P.i("playChannel the channel is playing,return direct .");
			return channelId;
		}

		Channel curChannel = dvbDatabase.getChannelSC(channelId);
		if (curChannel == null) {
			P.e("Cann't get valid channel.");
			return -1;
		}
		/*
		 * if(curChannel.sortId != 0x1 && curChannel.sortId != 0x11 &&
		 * curChannel.sortId != 0x16 && curChannel.sortId != 0x19 &&
		 * (curChannel.videoPid == 0x0 || curChannel.videoPid == 0x1fff)) {
		 * return -1; }
		 */

		dvbPlayer.stop();
		/* If it is audio channel, blank the screen */
		if (curChannel.sortId == 2 || curChannel.videoPid == 0x0
				|| curChannel.videoPid == 0x1fff) {
			dvbPlayer.blank();
			showAudioPlaying(true);
		} else {
			showAudioPlaying(false);
		}

		dvbPlayer.play(curChannel);

		// mo_Ca.channelNotify(curChannel);

		iCurChannelId = channelId;

		SetUserInfo(keyLastChanId, iCurChannelId);

		return 0;
	}
	
	public int playChannelKeyInput(int keyInput,boolean isCheckPlaying)
	{
		
		Channel[] channels = dvbDatabase.getChannelsByServiceId(keyInput, 1000);
		if(channels == null || channels.length <= 0)
		{
			return -1;
		}
		Channel curChannel = channels[0];
		if(curChannel.chanId == iCurChannelId  && isCheckPlaying)
		{
			P.i("playChannel the channel is playing,return direct .#2");
			return curChannel.chanId;
		}
		
		/*		
		if (curChannel == null)
		{
			Common.LOGE("Cann't get valid channel.");
			return -1;
		}

		if(curChannel.sortId != 0x1 && curChannel.sortId != 0x11 &&
				curChannel.sortId != 0x16 && curChannel.sortId != 0x19 &&
				(curChannel.videoPid == 0x0 || curChannel.videoPid == 0x1fff))
		{
			return -1;
		}
		*/
		P.i("playChannel#2>> chid="+curChannel.chanId+",logicNo=" + curChannel.logicNo);
		
		dvbPlayer.stop();		
		
		/* If it is audio channel, blank the screen */
		if(curChannel.sortId == 2 || curChannel.videoPid == 0x0 || curChannel.videoPid == 0x1fff)
		{
			dvbPlayer.blank();
			showAudioPlaying(true);
		}
		else
		{
			showAudioPlaying(false);
		}

		dvbPlayer.play(curChannel);
		
		//mo_Ca.channelNotify(curChannel);
		
		iCurChannelId = curChannel.chanId;
		
		//SetUserInfo(keyLastChanId, iCurChannelId);
		SetUserInfo(curChannel);
		updateCaStatusBroadcast();

		return 0;
	}
	
	public int playChannel(Channel channel,boolean isCheckPlaying)
	{
		if (channel == null)
		{
			P.e("invalid channel.");
			return -1;
		}
		
		if(channel.chanId == iCurChannelId  && isCheckPlaying)
		{
			P.i("playChannel the channle is playing,return direct .#3");
			return channel.chanId;
		}
		
		P.i("playChannel#3>> chid="+channel.chanId+",logicNo=" + channel.logicNo);

		dvbPlayer.stop();
		
		/* If it is audio channel, blank the screen */
		if(channel.sortId == 2 || channel.videoPid == 0x0 || channel.videoPid == 0x1fff)
		{
			dvbPlayer.blank();
			showAudioPlaying(true);
		}
		else
		{
			showAudioPlaying(false);
		}

		dvbPlayer.play(channel);
		
		//mo_Ca.channelNotify(channel);
		
		iCurChannelId = channel.chanId;
    	
    	//SetUserInfo(keyLastChanId, iCurChannelId);
    	SetUserInfo(channel);
		updateCaStatusBroadcast();

		return 0;
	}
	
	// 根据频道名字播放节目
	public int playChannel(String chName,boolean isCheckPlaying)
	{
		if(chName=="")
		{
			return 0;
		}
		Channel[] channels=dvbDatabase.getChannelsAll();
		for(Channel channel :channels)
		{
			//Skip the radio and invalid channels.
			if(channel.sortId != 0x1 && channel.sortId != 0x11 &&
					channel.sortId != 0x16 && channel.sortId != 0x19 &&
					(channel.videoPid == 0x0 || channel.videoPid == 0x1fff))
			{
				continue;
			}
			if(channel.name.equals(chName))
			{
				playChannel(channel, false);
				return 1;
			}
		}
		return 0;
	}
	
	public Channel getCurPlayingChannel()
	{
		Channel curChannel = dvbDatabase.getChannel(iCurChannelId);
		return curChannel;
	}
	
	public Channel getPreChannel(int channId){
		Channel channel = null;
		if(channId!=-1){
			channel = dvbDatabase.getNextChannelSC(channId, -1, true);
		}else {
			channel = dvbDatabase.getNextChannelSC(iCurChannelId, -1, true);
		}
		return channel;
	}
	
	public Channel getNextChannel(int channId){
		Channel channel = null;
		if(channId!=-1){
			channel = dvbDatabase.getNextChannelSC(channId, 1, true);
		}else {
			channel = dvbDatabase.getNextChannelSC(iCurChannelId, 1, true);
		}
		return channel;
	}
	
	public boolean playLastChannel()
    {
    	String value = getUserInfo(keyLastChanId);
    	if (value == null)
    	{
    		Channel mo_CurChannel 	= null;	
			mo_CurChannel = dvbDatabase.getChannelBySortIdAndIndex(Channel.CHAN_SORT_TV, 0);
			if (mo_CurChannel != null)
			{
				P.i( "find last channel ->  " + mo_CurChannel.name);
		    	playChannel(mo_CurChannel,false);
		    	return true;
			}
			else
			{
				P.e( "no channel.");	
				return false;
			}
    	}
    	
    	iCurChannelId	=	Integer.parseInt(value);
    	Channel temp_CurChannel = dvbDatabase.getChannel(iCurChannelId);	
    	if(iCurChannelId <=0 || temp_CurChannel == null)
    	{
    		Channel 		mo_CurChannel 	= null;	
			mo_CurChannel = dvbDatabase.getChannelBySortIdAndIndex(Channel.CHAN_SORT_TV, 0);
			if (mo_CurChannel != null)
			{
				P.i( "find last channel ->  " + mo_CurChannel.name);
		    	playChannel(mo_CurChannel,false);
		    	return true;
			}
			else
			{
				P.e( "no channel.");	
				return false;
			}
    	}
    	
    	playChannel(Integer.parseInt(value),false);
    	return true;
    }
	
	public boolean	playNextChannel(boolean rbSameSorted)
	{
		Channel mo_CurChannel =dvbDatabase.getNextChannelSC(iCurChannelId, 1, rbSameSorted);
		if(mo_CurChannel != null)
		{
			playChannel(mo_CurChannel,true);
		}
		else
		{
			P.e("HANDLE_MSG_TYPE_NEW_CHANNEL_NEXT get null progrom ");
		}

		return true;
	}

	public boolean	playPreChannel(boolean rbSameSorted)
	{
		P.i("playPreChannel");
		Channel mo_CurChannel =dvbDatabase.getNextChannelSC(iCurChannelId, -1, rbSameSorted);
		if(mo_CurChannel != null)
		{
			playChannel(mo_CurChannel,true);
		}
		else
		{
			P.e("HANDLE_MSG_TYPE_NEW_CHANNEL_NEXT get null progrom ");
		}

		return true;
	}
	
	public boolean playPrePlayingChannel(){
		P.i("playPrePlayingChannel");
		Channel mo_PrePlayingChannel = dvbDatabase.getPrePlayingChannel();
		if(mo_PrePlayingChannel!=null){
			playChannel(mo_PrePlayingChannel, true);
		}else {
			P.e("can't get previous playing program");
			return false;
		}
		return true;
	}

	public int	getLastProgram()
	{
		String value = getUserInfo(keyLastChanId);
		if(value != null)
		{
			iCurChannelId	=	Integer.parseInt(value);
		}
		
		return iCurChannelId;
	}
	
	public int blackScreen()
	{
		dvbPlayer.blank();
		return 0;
	}
	/***************************************
	 *            Pip Play
	 ****************************************/
	public LivePlayer pipPlayer = null;
	public int pipCurChannelId = -1;  
	/**
	 * init pip player before using
	 * sizeX:screen x, sizeY:screen y
	 */
	public void initPipPlayer(int sizeX,int sizeY){
		pipPlayer = new LivePlayer(1);
		pipPlayer.prepare();		
		DVB_RectSize.Builder builder = DVB_RectSize.newBuilder().
				setX(sizeX*2/3-20).setY(sizeY*2/3-10)
				.setW(sizeX/3).setH(sizeY/3);
		pipPlayer.setSize(builder.build());
	}
	
	public boolean startPipPlay(){
		if(pipCurChannelId>0){
			return playPipChannel(dvbDatabase.getChannel(pipCurChannelId));
		}else {
			return playPipChannel(dvbDatabase.getNextChannel(iCurChannelId, 1, true));
		}
	}
	
	public void stopPipPlay(){
		//pipPlayer.hide();
		if(pipPlayer!=null){
			pipPlayer.stop();
			pipPlayer.blank();
			pipPlayer.release();
			pipPlayer = null;
		}
	}
		
	public boolean playPipChannel(Channel channel){
		if(null==channel){
			P.e("invalid channel");
			return false;
		}		
		pipCurChannelId = channel.chanId;
		pipPlayer.stop();
		pipPlayer.play(channel);
		return true;
	}
	
	public boolean playPipChannel(int chanID){
		return playPipChannel(dvbDatabase.getChannel(chanID));
	}
	
	public boolean playNextPipChannel(boolean sameSorted){
		P.i("playPipPreChannel");
		Channel nextChannel =dvbDatabase.getNextChannel(pipCurChannelId, 1, sameSorted);
		if(nextChannel != null)
		{
			return playPipChannel(nextChannel);
		}
		else
		{
			P.e("HANDLE_MSG_TYPE_NEW_CHANNEL_NEXT get null progrom ");
			return false;
		}
	}


	public boolean playPrePipChannel(boolean sameSorted){
		P.i("playPipPreChannel");
		Channel preChannel =dvbDatabase.getNextChannel(pipCurChannelId, -1, sameSorted);
		if(preChannel != null)
		{
			return playPipChannel(preChannel);
		}
		else
		{
			P.e("HANDLE_MSG_TYPE_NEW_CHANNEL_PRE get null progrom ");
			return false;
		}
	}
	
	/**
	 * swap the main player and pip player
	 */
	public void swapPipMain(){
		int tmp = iCurChannelId;
		playChannel(pipCurChannelId, true);
		playPipChannel(tmp);
	}
	
	
	/********************************************
	 * 		Record Current Program
	 * 			added:2015.08.26 author:yangtong
	 ********************************************/
	PVR mPvr = null;
	public static final int ERROR_NO_USB = -2;
	public static final int ERROR_INVALID_PATH = -1;
	
	public int startRecord(int chanId,Context context)
	{	
		int ret = 0;
		DVB_PVR_RecAttr.Builder attr = DVB_PVR_RecAttr.newBuilder();
		
		Channel chanRec = dvbDatabase.getChannel(chanId);			
		if (chanRec == null)
		{
			Log.i("dtv", "Cann't get valid channel.");
			return -1;
		}
		
		String usbdir = Utils.getExternalStorageDirectory();
		if (usbdir == null)
		{
			//sendMessage(MSG_SHOW_TOAST, 0,0,getNoticeContent(R.string.no_usb));			
			return ERROR_NO_USB;			
		}		
		//start record		
		String path = getFilePath(usbdir, chanRec);		
		if (path == null)
		{
			Log.i("dtv", "can't get file path.");
			return ERROR_INVALID_PATH;			
		}
		
		attr.setFilePath(path);
		attr.setFileName("record.ts");		
		attr.setFileTimeMaxMs(60*1000);
		attr.setIsClearStream(true);
		attr.setIsRewind(true);
		
		if(mPvr==null)
			mPvr = dvbManager.getPvrInstance();
		ret = mPvr.rec_start(chanRec, attr.build());
		if (ret != 0)
		{
			Log.i("dtv", "start record failed");
			return -1;		
		}	
//		getTimer().setBase(SystemClock.elapsedRealtime()); 
//		getTimer().setVisibility(View.VISIBLE);	
//		getTimer().start();			
		return 0;
	}
	
	public int stopRecord(){
		
		return 0;
	}
	
	/**
	 * get the file path to store recording file
	 * @return path string
	 */
	private String getFilePath(String path, Channel chan)
	{
		if (path == null)
		{
			Log.e("dtv", "usb path is null");
			return null;			
		}
		
		String recdir = path + chan.name + "_" + Utils.getCurTime();
		
		if(!Utils.creatFolderIfNotExists(recdir))
		{
			Log.e("dtv", "path: " + recdir);
			return null;
		}		
		return recdir;
	}
	
    private String getUserInfo(String key)
    {
    		int channelId = getLastPlayingChannelId();
    	if(channelId < 0)
    	{
    		return null;
    	}
    	return (""+channelId);
    }
    
    private void SetUserInfo(String key, int channelId)
    {
    	Channel tChannel = DVB.getManager().getChannelDBInstance().getChannel(channelId);
    	saveLastPlayingInfo(tChannel);
    }
    public void SetUserInfo(Channel playingChannel)
    {
    	saveLastPlayingInfo(playingChannel);
    }
    
    private void saveLastPlayingInfo(Channel playingChannel)
    {
    	if(playingChannel == null)
    	{
    		return;
    	}
    	PlayingInfo playingRecord = new PlayingInfo();
    	playingRecord.mFreqKHz = playingChannel.frequencyKhz;
    	playingRecord.mSymbolRate = playingChannel.symbolRateKbps;
    	playingRecord.mModulationMode = playingChannel.modulation;
    	playingRecord.mSpectrum = playingChannel.spectrum;
    	playingRecord.mChannelId = playingChannel.chanId;
    	DVB.getManager().getChannelDBInstance().savePlayingInfo(playingRecord);
    }
    
    private int getLastPlayingChannelId()
    {
    	PlayingInfo playingRecord = DVB.getManager().getChannelDBInstance().getSavedPlayingInfo();
    	if(playingRecord == null)
    	{
    		return -1;
    	}
    	return playingRecord.mChannelId;
    }
    
    
    //@sym查询出所有的节目信息
   public Vector<BookInfo> queryBookChannels()
   {
	if(dvbBookDataBase.BookInfoNum()==0)
	{
		return null;
	}
	else 
	{
		return dvbBookDataBase.GetBookInfo();
	}
   }
    
   //@sym查询出所有的节目信息
  public boolean isBookedChannel(BookInfo data)
  {
	  
	  return dvbBookDataBase.isThisBookInfoExist(data);
  }
  
   //@sym删除预约中指定的节目
   public void delBookChannel(String bookDate,String startTime)
   {
	  dvbBookDataBase.RemoveOneBookInfo(bookDate, startTime);
   }
   
    //@sym清空数据库的频道
	public void clearChannel()
	{
		if(dvbDatabase!=null)
		dvbDatabase.emptyChannel();
	}	
	
	public  void showCainfo(String info)
	{
		P.e(info);
		

		if(tvinfo != null && ll_newchannelmode != null)
		{			
			//blackScreen();
		    tvinfo.setText(info);
		    
		    try
			{
				 if(info!=null&&info.equals(mContext.getResources().getString(R.string.MESSAGE_INSERTCARD_TYPE))){
				    	ca_exter_info.setVisibility(View.VISIBLE);
				    }else{
				    	ca_exter_info.setVisibility(View.INVISIBLE);
				    }				
			}
			catch(NotFoundException e)
			{
				ca_exter_info.setVisibility(View.INVISIBLE);
			}
					    
			if(!Main.updateDtvStatus(5,true)){
				return;
			}
			
		    ll_newchannelmode.setVisibility(View.VISIBLE); 
		}
		else if(ll_newchannelmode != null)
		{
			ll_newchannelmode.setVisibility(View.INVISIBLE);     
			Main.updateDtvStatus(5,false);
		}
		     
	}
	public  void hideCainfo()
	{
		if(ll_newchannelmode != null)
		{
			ll_newchannelmode.setVisibility(View.INVISIBLE);     
		}
		
		Main.updateDtvStatus(5,false);

	}
	public RelativeLayout getCaLayout()
	{
		return ll_newchannelmode;
	}
	public void showOsdRoll(int style, String text)
	{
		String osdRollText = text;
		if(style == 1)//top
		{
			text_osdroll_top.setText(osdRollText);
			if(!ll_osdroll.isShown() || !text_osdroll_top.isShown())
			{
				ll_osdroll.setVisibility(View.VISIBLE);
				text_osdroll_top.setVisibility(View.VISIBLE);
				text_osdroll_top.init(mWindowManager);
				text_osdroll_top.startScroll();
			}
		}
		else//buttom, fullscreen, halfscreen
		{
			text_osdroll_buttom.setText(osdRollText);
			if(!ll_osdroll.isShown() || !text_osdroll_buttom.isShown())
			{
				ll_osdroll.setVisibility(View.VISIBLE);
				text_osdroll_buttom.setVisibility(View.VISIBLE);
				text_osdroll_buttom.init(mWindowManager);
				text_osdroll_buttom.startScroll();
			}
		}
		
		/*
			Vanlen Begin
			Send a message to clean the osd scrolling.
		*/
		Message myMessage = new Message();
		myMessage.what = 0;
		myMessage.arg1 = style;
		mOsdCleanHandler.sendMessageDelayed(myMessage, 500);
		if(style == 1)//top
		{
			mOsdShowTime[0] = 0;
		}
		else
		{
			mOsdShowTime[1] = 0;
		}
		/*
			Vanlen END
		*/
	}
	
	public void hideOsdRoll(int style)
	{
		if(style == 1)//top
		{
			text_osdroll_top.setText("");
			if(text_osdroll_top.isShown() || ll_osdroll.isShown())
			{
				ll_osdroll.setVisibility(View.GONE);
				text_osdroll_top.setVisibility(View.GONE);
			}
		}
		else//buttom, fullscreen, halfscreen
		{
			text_osdroll_buttom.setText("");
			if(text_osdroll_buttom.isShown())
			{
				ll_osdroll.setVisibility(View.GONE);
				text_osdroll_buttom.setVisibility(View.GONE);
			}
		}
		/*
			Vanlen Begin
			Notify the Ca lib that the osd showing is over.
		*/
		if(style == 1)//top
		{
			Log.i("Vanlen-SUMA", "notify osd show over");
			DVB.getManager().getCaInstance().notifyOsdShowOver(mOsdShowTime[0]);
		}
		else
		{
			Log.i("Vanlen-SUMA", "notify osd show over");
			DVB.getManager().getCaInstance().notifyOsdShowOver(mOsdShowTime[1]);
		}
		/*
			Vanlen END
		*/
	}
	
	private void showAudioPlaying(boolean show)
	{
		/*
		if(show)
		{
			if(ll_audioplaying != null && !ll_audioplaying.isShown())
			{
				ll_audioplaying.setVisibility(View.VISIBLE); 
			}
		}
		else
		{
			if(ll_audioplaying != null && ll_audioplaying.isShown())
			{
				ll_audioplaying.setVisibility(View.INVISIBLE);     
			}
		}
		*/
	}

	private String getNovelNoticeContent(DVB_CA_NOVEL_MSG_CODE code)
	{//因为有另一个apk监控卡是否插入以及是否有效卡,所以此处屏蔽部分提示语,避免OSD重复提示.
	// NOVEL_MSG_CODE_INSERTCARD_TYPE, NOVEL_MSG_CODE_BADCARD_TYPE
		String noticeContent = null;
		int stringId = 0;
		Log.i("YYY", "CA index="+code);
		switch(code)
		{
			case NOVEL_MSG_CODE_BADCARD_TYPE:
			{
				//stringId = R.string.MESSAGE_CALLBACK_TYPE;
				stringId = R.string.MESSAGE_BADCARD_TYPE;
				//break;
				return null;
			}
			case NOVEL_MSG_CODE_EXPICARD_TYPE:
			{
				stringId = R.string.MESSAGE_EXPICARD_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_INSERTCARD_TYPE:
			{
				stringId = R.string.MESSAGE_INSERTCARD_TYPE;
				//break;
				return null;
			}
			case NOVEL_MSG_CODE_NOOPER_TYPE:
			{
				stringId = R.string.MESSAGE_NOOPER_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_BLACKOUT_TYPE:
			{
				stringId = R.string.MESSAGE_BLACKOUT_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_OUTWORKTIME_TYPE:
			{
				stringId = R.string.MESSAGE_OUTWORKTIME_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_WATCHLEVEL_TYPE:
			{
				stringId = R.string.MESSAGE_WATCHLEVEL_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_PAIRING_TYPE:
			{
				stringId = R.string.MESSAGE_PAIRING_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_NOENTITLE_TYPE:
			{
				stringId = R.string.MESSAGE_NOENTITLE_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_DECRYPTFAIL_TYPE:
			{
				stringId = R.string.MESSAGE_DECRYPTFAIL_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_NOMONEY_TYPE:
			{
				stringId = R.string.MESSAGE_NOMONEY_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_ERRREGION_TYPE:
			{
				stringId = R.string.MESSAGE_ERRREGION_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_NEEDFEED_TYPE:
			{
				stringId = R.string.MESSAGE_NEEDFEED_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_ERRCARD_TYPE:
			{
				stringId = R.string.MESSAGE_ERRCARD_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_UPDATE_TYPE:
			{
				stringId = R.string.MESSAGE_UPDATE_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_LOWCARDVER_TYPE:
			{
				stringId = R.string.MESSAGE_LOWCARDVER_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_VIEWLOCK_TYPE:
			{
				stringId = R.string.MESSAGE_VIEWLOCK_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_MAXRESTART_TYPE:
			{
				stringId = R.string.MESSAGE_MAXRESTART_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_FREEZE_TYPE:
			{
				stringId = R.string.MESSAGE_FREEZE_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_CALLBACK_TYPE:
			{
				stringId = R.string.MESSAGE_CALLBACK_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_CURTAIN_TYPE:
			{
				stringId = R.string.MESSAGE_CURTAIN_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_CARDTESTSTART_TYPE:
			{
				stringId = R.string.MESSAGE_CARDTESTSTART_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_CARDTESTFAILD_TYPE:
			{
				stringId = R.string.MESSAGE_CARDTESTFAILD_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_CARDTESTSUCC_TYPE:
			{
				stringId = R.string.MESSAGE_CARDTESTSUCC_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_NOCALIBOPER_TYPE:
			{
				stringId = R.string.MESSAGE_NOCALIBOPER_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_STBLOCKED_TYPE:
			{
				stringId = R.string.MESSAGE_STBLOCKED_TYPE;
				break;
			}
			case NOVEL_MSG_CODE_STBFREEZE_TYPE:
			{
				stringId = R.string.MESSAGE_STBFREEZE_TYPE;
				break;
			}
			default:
			{
				return null;
			}
		}
		
		try
		{
			noticeContent = mContext.getResources().getString(stringId);
		}
		catch(NotFoundException e)
		{
			noticeContent = null;
		}	
		
		return noticeContent;
	}
	
	
	
	
	private String getSumaNoticeContent(DVB_CA_SUMA_MSG_CODE code)
	{
		String noticeContent = null;
		int stringId = 0;

		switch(code)		
		{
			case SUMA_MSG_CODE_RATING_TOO_LOW:
			{
				stringId = R.string.MESSAGE_RATING_TOO_LOW;
				break;
			}
			case SUMA_MSG_CODE_NOT_IN_WATCH_TIME:
			{
				stringId = R.string.MESSAGE_NOT_IN_WATCH_TIME;
				break;
			}
			case SUMA_MSG_CODE_NOT_PAIRED:
			{
				stringId = R.string.MESSAGE_NOT_PAIRED;
				break;
			}
			case SUMA_MSG_CODE_PLEASE_INSERT_CARD:
			{
				stringId = R.string.MESSAGE_PLEASE_INSERT_CARD;
				break;
			}
			case SUMA_MSG_CODE_NO_ENTITLE:
			{
				stringId = R.string.MESSAGE_NO_ENTITLE;
				break;
			}
			case SUMA_MSG_CODE_PRODUCT_RESTRICT:
			{
				stringId = R.string.MESSAGE_PRODUCT_RESTRICT;
				break;
			}
			case SUMA_MSG_CODE_AREA_RESTRICT:
			{
				stringId = R.string.MESSAGE_AREA_RESTRICT;
				break;
			}
			case SUMA_MSG_CODE_MOTHER_RESTRICT:
			{
				stringId = R.string.MESSAGE_MOTHER_RESTRICT;
				break;
			}
			case SUMA_MSG_CODE_NO_MONEY:
			{
				stringId = R.string.MESSAGE_NO_MONEY;
				break;
			}
			case SUMA_MSG_CODE_IPPV_NO_CONFIRM:
			{
				stringId = R.string.MESSAGE_IPPV_NO_CONFIRM;
				break;
			}
			case SUMA_MSG_CODE_IPPV_NO_BOOK:
			{
				stringId = R.string.MESSAGE_IPPV_NO_BOOK;
				break;
			}
			case SUMA_MSG_CODE_IPPT_NO_CONFIRM:
			{
				stringId = R.string.MESSAGE_IPPT_NO_CONFIRM;
				break;
			}
			case SUMA_MSG_CODE_IPPT_NO_BOOK:
			{
				stringId = R.string.MESSAGE_IPPT_NO_BOOK;
				break;
			}
			case SUMA_MSG_CODE_DATA_INVALID:
			{
				stringId = R.string.MESSAGE_DATA_INVALID;
				break;
			}
			case SUMA_MSG_CODE_SC_NOT_SERVER:
			{
				stringId = R.string.MESSAGE_SC_NOT_SERVER;
				break;
			}
			case SUMA_MSG_CODE_KEY_NOT_FOUND:
			{
				stringId = R.string.MESSAGE_KEY_NOT_FOUND;
				break;
			}
			case SUMA_MSG_CODE_IPPNEED_CALLBACK:
			{
				stringId = R.string.MESSAGE_IPPNEED_CALLBACK;
				break;
			}
			case SUMA_MSG_CODE_FREE_PREVIEWING:
			{
				stringId = R.string.MESSAGE_FREE_PREVIEWING;
				break;
			}		
			default:
			{
				return null;
			}
		}
		
		try
		{
			noticeContent = mContext.getResources().getString(stringId);
		}
		catch(NotFoundException e)
		{
			noticeContent = null;
		}	
		
		return noticeContent;
	}
	

	public void caCallbackNovel_new(DVB_CA_NOVEL data, Object reserved)
	{
		P.i("caCallbackNovel");
		switch(data.getCode())
		{
			case NOVEL_CODE_CB_SHOW_BUY_MSG:
			{
				if (!data.hasMsgcode())
				{
					break;
				}				
				Message message = new Message(); 
				
				if (DVB_CA_NOVEL_MSG_CODE.NOVEL_MSG_CODE_CANCEL_VALUE == data.getMsgcode().getNumber())
				{
					message.what = MESSAGE_CA_HIDENOTICE;  
				}
				else
				{
					message.what = MESSAGE_CA_SHOWNOTICE;  
					message.arg1 = data.getCode().getNumber();
					message.arg2 = 0;
					message.obj = getNovelNoticeContent(data.getMsgcode());
				}

				mAppHandler.sendMessage(message); 
				break;
			}
			
			case NOVEL_CODE_CB_SHOW_OSD:
			{
				if (!data.hasData())
				{
					break;
				}				
				Message message = new Message(); 
				message.what = MESSAGE_CA_SHOWOSDROLL;
				
				DVB_CA_NOVEL_Data novel_data = data.getData();
				if(novel_data.getDataIntCount() == 0)
				{
					message.arg1 = 1;
				}
				int novel_data_int = novel_data.getDataInt(0);
				message.arg1 = novel_data_int;
				if(novel_data.getDataStringCount() == 0)
				{
					message.what = MESSAGE_CA_HIDEOSDROLL;
				}
				message.obj = novel_data.getDataString(0);
				if(message.obj == null)
				{
					message.what = MESSAGE_CA_HIDEOSDROLL;
				}

				mAppHandler.sendMessage(message); 
				break;
			}
			case NOVEL_CODE_CB_HIDE_OSD:
			{
				if (!data.hasData())
				{
					break;
				}				
				Message message = new Message(); 
				message.what = MESSAGE_CA_HIDEOSDROLL;
				DVB_CA_NOVEL_Data novel_data = data.getData();
				if(novel_data.getDataIntCount() == 0)
				{
					message.arg1 = 1;
				}
				int novel_data_int = novel_data.getDataInt(0);
				message.arg1 = novel_data_int;
				mAppHandler.sendMessage(message); 
				break;
			}
						
			default:
			{
				return;
			}
		}		
	}	
	
	public void caCallbackNovel(DVB_CA_NOVEL data, Object reserved)
	{
		P.i("caCallbackNovel");
		
		switch (data.getCode()) {
		
		case NOVEL_CODE_CB_SHOW_BUY_MSG: {
			if (!data.hasMsgcode()) {
				break;
			}
			Message message = new Message();

			if (DVB_CA_NOVEL_MSG_CODE.NOVEL_MSG_CODE_CANCEL_VALUE == data.getMsgcode().getNumber()) {
				message.what = MESSAGE_CA_HIDENOTICE;
			} else {
				message.what = MESSAGE_CA_SHOWNOTICE;
				message.arg1 = data.getCode().getNumber();
				message.arg2 = 0;
				message.obj = getNovelNoticeContent(data.getMsgcode());
			}
			mAppHandler.sendMessage(message);
			break;
		}

		case NOVEL_CODE_CB_SHOW_OSD: {
			if (!data.hasData()) {
				break;
			}
			Message message = new Message();
			message.what = MESSAGE_CA_SHOWOSDROLL;

			DVB_CA_NOVEL_Data novel_data = data.getData();
			if (novel_data.getDataIntCount() == 0) {
				message.arg1 = 1;
			}
			int novel_data_int = novel_data.getDataInt(0);
			message.arg1 = novel_data_int;
			if (novel_data.getDataStringCount() == 0) {
				message.what = MESSAGE_CA_HIDEOSDROLL;
			}
			message.obj = novel_data.getDataString(0);
			if (message.obj == null) {
				message.what = MESSAGE_CA_HIDEOSDROLL;
			}
			mAppHandler.sendMessage(message);
			break;
		}
		case NOVEL_CODE_CB_HIDE_OSD: {
			if (!data.hasData()) {
				break;
			}
			Message message = new Message();
			message.what = MESSAGE_CA_HIDEOSDROLL;
			DVB_CA_NOVEL_Data novel_data = data.getData();
			if (novel_data.getDataIntCount() == 0) {
				message.arg1 = 1;
			}
			int novel_data_int = novel_data.getDataInt(0);
			message.arg1 = novel_data_int;
			mAppHandler.sendMessage(message);
			break;
		}
		default: {
			int cmdCode=data.getCode().getNumber();
			Intent novelIntent = new Intent();
			novelIntent.setAction(CaConfig.CA_MSG_NOVEL_EVENT);
			novelIntent.putExtra("eventType", 2);//CA_NOVEL
			novelIntent.putExtra("cmdCode", cmdCode);
			novelIntent.putExtra("caData", data);
			sendBroadcast(novelIntent);
			return;
		}
		}		
	}	
	
    public void caCallbackSuma(DVB_CA_SUMA data, Object reserved)
    {
    	P.i("caCallbackSuma");
    	switch(data.getCode())
    	{
			case SUMA_CODE_CB_HIDE_IPPV_DLG:
				break;
			case SUMA_CODE_CB_LOCK_SERVICE:
				break;
			case SUMA_CODE_CB_MAIL:
				break;
			case SUMA_CODE_CB_PARENT_FEED:
				break;
			case SUMA_CODE_CB_PROMPT_MESSAGE:
			{
				if (!data.hasMsgcode())
				{
					break;
				}
				
				Message message = new Message(); 
				
				if (DVB_CA_SUMA_MSG_CODE.SUMA_MSG_CODE_CANCEL_VALUE == data.getMsgcode().getNumber())
				{
					message.what = MESSAGE_CA_HIDENOTICE;  
				}
				else
				{
					message.what = MESSAGE_CA_SHOWNOTICE;  
					message.arg1 = data.getCode().getNumber();
					message.arg2 = 0;
					message.obj = getSumaNoticeContent(data.getMsgcode());
				}

				Log.i("ca","MESSAGE_CA_SHOWNOTICE  message.arg1  message.obj"+message.arg1+" "+message.obj);
			
				mAppHandler.sendMessage(message); 
				
				break;
			}
			case SUMA_CODE_CB_SHOW_FINGER:
				break;
			case SUMA_CODE_CB_SHOW_IPPV_DLG:
				break;
			case SUMA_CODE_CB_SHOW_OSD:
			{
				if (!data.hasData())
				{
					break;
				}				
				Message message = new Message(); 
				message.what = MESSAGE_CA_SHOWOSDROLL;
				
				DVB_CA_SUMA_Data suma_data = data.getData();
				if(suma_data.getDataIntCount() == 0)
				{
					message.arg1 = 1;
				}
				int novel_data_int = suma_data.getDataInt(0);
				message.arg1 = novel_data_int;
				if(suma_data.getDataStringCount() == 0)
				{
					message.what = MESSAGE_CA_HIDEOSDROLL;
				}
				message.obj = suma_data.getDataString(0);
				if(message.obj == null)
				{
					message.what = MESSAGE_CA_HIDEOSDROLL;
				}

				mAppHandler.sendMessage(message); 
				break;
			}
			case SUMA_CODE_CB_HIDE_OSD:
			{
				if (!data.hasData())
				{
					break;
				}				
				Message message = new Message(); 
				message.what = MESSAGE_CA_HIDEOSDROLL;
				DVB_CA_SUMA_Data suma_data = data.getData();
				if(suma_data.getDataIntCount() == 0)
				{
					message.arg1 = 1;
				}
				int novel_data_int = suma_data.getDataInt(0);
				message.arg1 = novel_data_int;
				mAppHandler.sendMessage(message); 
				break;
			}
			case SUMA_CODE_CB_UNLOCK_SERVICE:
				break;
			default:
				break;    		
    	}
    }


	@Override
	public void caCallback(DVB_CA data,Object reserved) {


		// TODO Auto-generated method stub
		P.i("APP  caCallback.....ca: " + data.getType());
		
		switch(data.getType())
		{
			case CA_NONE:
			{
				//caSumaCallback(ri_Event,ri_EventData,ro_PriData);
				break;
			}
		case CA_NOVEL: {
			if (data.hasNovel()) {
				//caCallbackNovel(data.getNovel(), reserved);
				}				
				break;
			}
			case CA_SUMA:
			{
				if (data.hasSuma())
				{
					caCallbackSuma(data.getSuma(), reserved);
				}
				break;
			}

			default:
			{
				return;
			}	
		}
	
	}
	

	

	/**
	 * 解锁服务
	 */
	public void unForcedShow() {
		if(mList.size() <=0)return;
		for (Activity activity : mList) {
			if (activity.getClass().equals(CaLockService.class)){
				activity.finish();
				mList.remove(activity);
				activity=null;
				return;
			}
		}		
	}

	
	public void finishActivity(Object className) {
		if(mList.size() <=0)return;
		for (Activity activity : mList) {		
			if(null == activity)continue;			
			if (activity.getClass().equals(className)){
				activity.finish();
				mList.remove(activity);
				activity=null;
				return;
			}
		}		
	}
	
	
	public String getChanelNameByID(int mProductId) {
		String channelName="";
		Channel[] channels = dvbDatabase.getChannelsAll();
		for (Channel channel : channels) {
			if (channel.chanId == mProductId ) {
				channelName=channel.name;
				break;
			}
		}
		return channelName;
	}
	
	private void updateCaStatusBroadcast(){	
		mContext=MyApp.getContext();
	    Intent mIntentCa = new Intent("com.chots.app.ca.change.status");
	    mContext.sendBroadcast(mIntentCa);	
	}
	
}
