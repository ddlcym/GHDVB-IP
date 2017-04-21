package com.changhong.app.dtv;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import zmq.Mailbox;

import com.changhong.app.constant.Advertise_Constant;
import com.changhong.app.timeshift.common.NetworkUtils;
import com.changhong.app.utils.CustomToast;
import com.changhong.dvb.Channel;
import com.changhong.dvb.DVB;
import com.changhong.dvb.PlayingInfo;
import com.changhong.dvb.ProtoMessage.DVB_EPG_PF;
import com.xormedia.adplayer.AdItem;
import com.xormedia.adplayer.AdItem;
import com.xormedia.adplayer.AdOperation;
import com.xormedia.adplayer.AdOperation;
import com.xormedia.adplayer.AdPlayer;
import com.xormedia.adplayer.AdPlayer;
import com.xormedia.adplayer.AdStrategy;
import com.xormedia.adplayer.AdStrategy;
import com.xormedia.adplayer.IAdPlayerCallbackListener;
import com.xormedia.adplayer.IAdPlayerCallbackListener;
import com.xormedia.adplayer.IAdStrategyResponseListener;
import com.xormedia.adplayer.IAdStrategyResponseListener;

import android.R.bool;
import android.R.integer;
import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * show banner using toast,more effective.
 * 
 * @author yangtong
 *
 */
public class Banner {
	public static final String   TAG = "GHLive";
	private static Banner banner;
	public static Toast bannerToast = null;
	public static CustomToast customBannerToast = null;
	
	private static Handler mHandlerBar = new Handler();
	private static SysApplication sysApplication;
	private Context mContext;
	private View bannerView;

	Channel channelInBar;
	
	String[] sWeek;
	String sMonth;
	String sDay;
	String PF_channel_name = new String();
	String PF_time_P = new String();
	String PF_time_F = new String();
	String PF_enventName_P = new String();
	String PF_enventName_F = new String();
	String PF_timeshiftsupport = new String();
	ImageView adv_image = null;

	com.changhong.app.dtv.TextMarquee channel_name = null;
	TextView service_id = null;
	TextView PF_dtw = null;
	TextView PF_P = null;
	TextView PF_F = null;

	TextView banner_PF_F_time, banner_PF_P_time;
	ImageView channel_vid_3d,channel_vid_hd,channel_multAudio,channel_subtitle,channel_Dolby,channel_mail;

	SeekBar progress = null;
	
	private AdPlayer adPlayer;
	private ArrayList<AdItem> adList = new ArrayList<AdItem>();
	private AdStrategy ads;
	private Handler processADHandler;
	private String tempStr = "";
	private static boolean isBannerRunning=false;

	public Banner() {
		// TODO Auto-generated constructor stub
	}
	private static Runnable rCancelBar;
    
    private static void showToast(View view,int duration) {
        
    	mHandlerBar.removeCallbacks(rCancelBar);
       
        mHandlerBar.postDelayed(rCancelBar, duration);

        if(customBannerToast!=null){
        	customBannerToast.update(view);
        	setBannerDisStatus(true);
        	customBannerToast.show(); 
        }
        	
    }
    
	public static void setBannerDisStatus(boolean newReq) {
		isBannerRunning = newReq;
	}
	public static boolean getBannerDisStatus() {
		return isBannerRunning;
	}	
	private Banner(Context context) {
		mContext = context.getApplicationContext();	
	}

	public void show(int chanId) {
		show(chanId,5000);
	}
	
	/**
	 * public void show(int chanId,int DurationInSecond)
	 * @param
	 * int chanId: 频道在数据库中的ID
	 * int DurationInSecond: 显示时长MS
	 * */
	public void show(int chanId,int Duration) {
		Log.i(TAG, "show bar>> chanId==" + chanId+",Duration="+Duration);
		
		initViewInBar();	
		channelInBar = sysApplication.dvbDatabase.getChannel(chanId);		
		updatePFInfo();
		updateBanner();
		updateChannelStatus(channelInBar,true);
		updateDateTime();
		bannerToast.setView(bannerView);
		
		showToast(bannerView,Duration);
		if(Duration==999999999){
			mHandlerTimer.sendEmptyMessageDelayed(0, 5000);
		}
	}

	private Handler mHandlerTimer = new Handler() {

		public void handleMessage(Message msg) {
			if(getBannerDisStatus()){
				PlayingInfo pi = sysApplication.dvbDatabase.getSavedPlayingInfo();
				if(pi!=null && channelInBar.chanId!=pi.mChannelId){
					channelInBar = sysApplication.dvbDatabase.getChannel(pi.mChannelId);
				}				
				updatePFInfo();
				updateBanner();
				updateDateTime();		
				mHandlerTimer.sendEmptyMessageDelayed(0, 5000);
			}
		}
	};

	/**
	 * 
	 */
	private void initViewInBar() {
		if (sysApplication == null) {
			sysApplication = SysApplication.getInstance();
			sysApplication.initBookDatabase(mContext);
		}
		if (bannerToast == null) {
			bannerToast = new Toast(mContext);
			bannerToast.setGravity(Gravity.BOTTOM, 0, 0);
			customBannerToast = new CustomToast(bannerToast);
		}
		if (bannerView == null) {
			LayoutInflater mInflater = LayoutInflater.from(mContext);
			bannerView = mInflater.inflate(R.layout.banner, null);
			findView();
		}
		if(volume_bar!=null && volume_bar.getVisibility()==View.VISIBLE){
			volume_bar.setVisibility(View.INVISIBLE);
		}
		if(banner_p!=null && banner_p.getVisibility()!=View.VISIBLE)	{	
			banner_p.setVisibility(View.VISIBLE);
			banner_f.setVisibility(View.VISIBLE);
		}
	}	
	/**
	 * public void show(int chanId,int style)
	 * @param
	 * int vol_adj: adjust volume
	 * boolean multe: true is mute
	 * */
	public void showVolume(int chanId,int vol_adj,boolean mute) {

		if (sysApplication == null) {
			sysApplication = SysApplication.getInstance();
			sysApplication.initBookDatabase(mContext);
		}
		if (bannerToast == null) {
			bannerToast = new Toast(mContext);
			bannerToast.setGravity(Gravity.BOTTOM, 0, 0);
			customBannerToast = new CustomToast(bannerToast);
		}
		if (bannerView == null) {
			LayoutInflater mInflater = LayoutInflater.from(mContext);
			bannerView = mInflater.inflate(R.layout.banner, null);
			findView();
		}	
		
		if (banner_p!=null && banner_p.getVisibility() == View.VISIBLE) {
			banner_p.setVisibility(View.INVISIBLE);
			banner_f.setVisibility(View.INVISIBLE);
		}
		if (volume_bar!=null && volume_bar.getVisibility() != View.VISIBLE) {
			volume_bar.setVisibility(View.VISIBLE);
		}
		
		if(mute){
			v_status_id.setBackgroundResource(R.drawable.v_mult);
		}else{
			v_status_id.setBackgroundResource(R.drawable.v_laba);
		}
		
		//if(vol_adj>=0 && vol_adj<=11000)
		v_vol_drawable.setLevel(vol_adj*666);

		channelInBar = sysApplication.dvbDatabase.getChannel(chanId);
		
		updatePFInfo();
		updateBanner();
		updateChannelStatus(channelInBar,false);
		updateDateTime();
		bannerToast.setView(bannerView);
		showToast(bannerView,5000);
	}

	public static synchronized Banner getInstance(Context context) {
		
		if (banner == null) {
			banner = new Banner(context);
		}
		if(rCancelBar==null){
		rCancelBar = new Runnable() {
			
	        public void run() {
	        	Log.i("GHLive", "rCancelBar now----->");
	        	if(customBannerToast!=null){
	        		customBannerToast.hide();
	        		Main.showTimeShiftIcon(false);
	        		setBannerDisStatus(false);
	        	}
	        }
	    };	
	    }
		return banner;
	}

	private void updateBanner() {
		String channelname = new String();
		// TODO channelname PF_channel_name diffs?
		channelname = channelInBar.name;

		progress.setProgress(getPlayingProgress());

		Log.i("banner", "channel.logicNo----->" + channelInBar.logicNo);

		if (channelInBar.logicNo < 10) {
			service_id.setText("00" + channelInBar.logicNo);
		} else if (channelInBar.logicNo < 100) {
			service_id.setText("0" + channelInBar.logicNo);
		} else {
			service_id.setText("" + channelInBar.logicNo);
		}
		channel_name.setText(channelname);
		// textview_timeshift_support.setText(getTimeShiftSupportString(channel.chanId));

		PF_P.setText(PF_enventName_P);
		PF_F.setText(PF_enventName_F);

		banner_PF_P_time.setText(PF_time_P);
		banner_PF_F_time.setText(PF_time_F);

	}
	
	public void cancel(){
		if(customBannerToast!=null){
			customBannerToast.hide();
			setBannerDisStatus(false);
		}
	}

	private void updateChannelStatus(Channel curChannel, boolean bAllowTtvTag) {
		
		if(curChannel==null)
			return;
		
		boolean bHD=false, b3D=false, bSubTitle=false,bAc3=false,bNewMail=false,bttv=false;
		int iAudioChannel=0; //0: 单声道，不显示图标;=2 显示CN 左声道; =3 显示 EN 立体声 
		if(bAllowTtvTag && curChannel.is_ttv.equals("1")&& NetworkUtils.isConnectInternet(mContext))//支持时移且网络连接
		{
			bttv = true; 
		}
		if(curChannel.sortId==0xe4||curChannel.sortId==0xe6||curChannel.sortId==0xe8||
				curChannel.sortId==0xe9||curChannel.sortId==0xf9)
		{
			bHD = true;
		}
		if(curChannel.audioStreamType==0x06) //AC-3 audio/Dolby Digital Plus
		{
			bAc3 = true;
		}
		if(curChannel.audioMode==2||curChannel.audioMode==3){
			iAudioChannel = curChannel.audioMode;
		}
		/*
		if(curChannel.subtitle!=null && !curChannel.subtitle.equals("null"))
		{
			bSubTitle = true;
		}*/
		bNewMail=Main.getNewMailStatus();		
		Log.i("BBY", "SWITCH>>>chid="+curChannel.chanId+",logic="+curChannel.logicNo+",ttv="+curChannel.is_ttv+",net="+NetworkUtils.isConnectInternet(mContext)
				+",newmail="+bNewMail);
		
		Main.showTimeShiftIcon(bttv);
		
		if(iAudioChannel==2){
			channel_multAudio.setBackgroundResource(R.drawable.icon_cn);
			channel_multAudio.setVisibility(View.VISIBLE);
		}else if(iAudioChannel==3){
			channel_multAudio.setBackgroundResource(R.drawable.icon_en);
			channel_multAudio.setVisibility(View.VISIBLE);
		}else {
			channel_multAudio.setVisibility(View.INVISIBLE);
		}
		/*
		if(bSubTitle)
			channel_subtitle.setVisibility(View.VISIBLE);
		else {
			channel_subtitle.setVisibility(View.INVISIBLE);
		}*/
		if(bAc3)
			channel_Dolby.setVisibility(View.VISIBLE);
		else {
			channel_Dolby.setVisibility(View.INVISIBLE);
		}
		/*
		if(bNewMail)
			channel_mail.setVisibility(View.VISIBLE);
		else {
			channel_mail.setVisibility(View.INVISIBLE);
		}*/			
		if(b3D){
			channel_vid_hd.setVisibility(View.GONE);
			channel_vid_3d.setVisibility(View.VISIBLE);			
		}else if(bHD){
			channel_vid_3d.setVisibility(View.GONE);			
			channel_vid_hd.setVisibility(View.VISIBLE);
		}else{
			channel_vid_hd.setVisibility(View.INVISIBLE);
			channel_vid_3d.setVisibility(View.GONE);	
		}

	}
	
	private ImageView v_vol;
	private ClipDrawable v_vol_drawable;
	private RelativeLayout volume_bar;
	private RelativeLayout banner_f;
	private RelativeLayout banner_p;
	private ImageView v_status_id;

	private int getPlayingProgress() {
		DVB_EPG_PF pfInfo = DVB.getManager().getEpgInstance().getPfInfo(channelInBar);
		int startTime;
		if (pfInfo == null) {
			startTime = 0;
		} else {
			int startH = pfInfo.getPresent().getStartTime().getHour();
			int startM = pfInfo.getPresent().getStartTime().getMinute();
			int startS = pfInfo.getPresent().getStartTime().getSecond();
			startTime = startH * 60 * 60 + startM * 60 + startS;// Start
																// time:second
		}

		int endTime = 0;
		if (pfInfo == null) {
			endTime = 0;
		} else {
			int endH = pfInfo.getFollowing().getStartTime().getHour();
			int endM = pfInfo.getFollowing().getStartTime().getMinute();
			int endS = pfInfo.getFollowing().getStartTime().getSecond();
			endTime = endH * 60 * 60 + endM * 60 + endS;// End time:second
		}

		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		int nowH = c.get(Calendar.HOUR_OF_DAY);
		int nowM = c.get(Calendar.MINUTE);
		int nowS = c.get(Calendar.SECOND);
		int nowTime = nowH * 60 * 60 + nowM * 60 + nowS;

		int duration = getDuration(startTime, endTime);
		int played = getDuration(startTime, nowTime);

		if (duration == 0) {
			return 0;
		}
		return played * 100 / duration;
	}

	private int getDuration(int start, int end) {
		int duration = end - start;
		if (duration < 0) {// start 23:23 end 00:15
			duration = end + 24 * 60 * 60 - start;
		}
		return duration;
	}

	private void updateDateTime() {
		/*String mYear;
		String mMonth;
		String mDay;
		String mWeek;*/
		String mHour, mMinute;
		//String[] week = mContext.getResources().getStringArray(R.array.str_dtv_epg_week_name);

		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		/*
		mYear = String.valueOf(c.get(Calendar.YEAR));
		mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		mWeek = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		*/
		if (c.get(Calendar.HOUR_OF_DAY) < 10) {
			mHour = "0" + String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		} else {
			mHour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		}
		if (c.get(Calendar.MINUTE) < 10) {
			mMinute = "0" + String.valueOf(c.get(Calendar.MINUTE));
		} else {
			mMinute = String.valueOf(c.get(Calendar.MINUTE));
		}
		/*if ("1".equals(mWeek)) {
			mWeek = week[6];
		} else if ("2".equals(mWeek)) {
			mWeek = week[0];
		} else if ("3".equals(mWeek)) {
			mWeek = week[1];
		} else if ("4".equals(mWeek)) {
			mWeek = week[2];
		} else if ("5".equals(mWeek)) {
			mWeek = week[3];
		} else if ("6".equals(mWeek)) {
			mWeek = week[4];
		} else if ("7".equals(mWeek)) {
			mWeek = week[5];
		}*/

		Log.i("banner", "PF_dtw------" + mHour + "   " + mMinute);
		PF_dtw.setText(mHour + ":" + mMinute);
		Log.i("banner", "PF_dtw------over");
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

	private String getStartEndTime(int startHour, int startMinute, int durationHour, int durationMinute) {
		String formatString = "  :   -   :  ";
		String startTimeString = timeFormat(startHour, startMinute);
		int endHour = 0;
		int endMinute = 0;
		boolean ifCarryOver = false;
		endMinute = startMinute + durationMinute;
		if (endMinute >= 60) {
			ifCarryOver = true;
			endMinute = endMinute - 60;
		}
		endHour = ifCarryOver ? startHour + durationHour + 1 : startHour + durationHour;
		if (endHour >= 24)
			endHour = endHour - 24;
		String endTimeString = timeFormat(endHour, endMinute);
		formatString = startTimeString + "-" + endTimeString;
		return formatString;
	}

	private void updatePFInfo() {

		// get PF info
		// channel=
		// objApplication.dvbDatabase.getChannel(SysApplication.iCurChannelId);
		// ArrayList<DvbEpgEvent> pfInfo =
		// objApplication.dvbEpg.getPfInfo(channel);
		PF_time_P = "";
		// PF_time_P_end= "";
		PF_enventName_P = mContext.getResources().getString(R.string.noprogrampfinfo);
		PF_time_F = "";
		// PF_time_F_end= "";

		PF_enventName_F = mContext.getResources().getString(R.string.noprogrampfinfo);
		DVB_EPG_PF pfInfo = DVB.getManager().getEpgInstance().getPfInfo(channelInBar);
		if (pfInfo != null) {
			if (pfInfo.hasPresent()) {

				PF_time_P = getStartEndTime(pfInfo.getPresent().getStartTime().getHour(),
						pfInfo.getPresent().getStartTime().getMinute(), pfInfo.getPresent().getDurationHour(),
						pfInfo.getPresent().getDurationMinute());

				PF_enventName_P = pfInfo.getPresent().getName();
			}
			if (pfInfo.hasFollowing()) {
				PF_time_F = getStartEndTime(pfInfo.getFollowing().getStartTime().getHour(),
						pfInfo.getFollowing().getStartTime().getMinute(), pfInfo.getFollowing().getDurationHour(),
						pfInfo.getFollowing().getDurationMinute());
				PF_enventName_F = pfInfo.getFollowing().getName();
			}
			if (PF_time_P == null || PF_time_P.equals("")) {
				PF_time_P = mContext.getResources().getString(R.string.notimeinfo);
			}
			if (PF_time_F == null || PF_time_F.equals("")) {
				PF_time_F = mContext.getResources().getString(R.string.notimeinfo);
			}
			if (PF_enventName_P == null || PF_enventName_P.equals("")) {
				PF_enventName_P = mContext.getResources().getString(R.string.noprogrampfinfo);
			}
			if (PF_enventName_F == null || PF_enventName_F.equals("")) {
				PF_enventName_F = mContext.getResources().getString(R.string.noprogrampfinfo);
			}

		} else {
			P.e("TVbanner  obj_EpgEventInfo == null");
			return;
		}

	}

	private void findView() {

		// adv_image = (ImageView) bannerView.findViewById(R.id.banner_adv_id);
		channel_name = (com.changhong.app.dtv.TextMarquee) bannerView.findViewById(R.id.banner_channel_name_id);
		service_id = (TextView) bannerView.findViewById(R.id.banner_service_id);
		progress = (SeekBar) bannerView.findViewById(R.id.banner_progress_view);
		PF_dtw = (TextView) bannerView.findViewById(R.id.banner_DTW_id);
		PF_P = (TextView) bannerView.findViewById(R.id.banner_PF_P_id);
		PF_F = (TextView) bannerView.findViewById(R.id.banner_PF_F_id);

		banner_PF_F_time = (TextView) bannerView.findViewById(R.id.banner_PF_F_time);

		banner_PF_P_time = (TextView) bannerView.findViewById(R.id.banner_PF_P_time);

		// textview_timeshift_support =
		// (TextView)bannerView.findViewById(R.id.banner_tshift_support);
		// param
		channelInBar = sysApplication.dvbDatabase.getChannel(SysApplication.iCurChannelId);
		adPlayer = (AdPlayer) bannerView.findViewById(R.id.adplayer);
		adPlayer.setDefaultAd(R.drawable.default_img, 1);  

		channel_vid_3d = 	(ImageView) bannerView.findViewById(R.id.banner_channel_vid_3d);		
		channel_vid_hd = 	(ImageView) bannerView.findViewById(R.id.banner_channel_vid_hd);	
		channel_multAudio = 	(ImageView) bannerView.findViewById(R.id.banner_channel_multAudio);
		//channel_subtitle = 	(ImageView) bannerView.findViewById(R.id.banner_channel_subtitle);
		channel_Dolby = 	(ImageView) bannerView.findViewById(R.id.banner_channel_Dolby);
		//channel_mail = 	(ImageView) bannerView.findViewById(R.id.banner_channel_mail);
		
		v_vol = (ImageView)bannerView.findViewById(R.id.v_vol_id);
		v_vol_drawable = (ClipDrawable)v_vol.getDrawable(); 
		volume_bar = (RelativeLayout) bannerView.findViewById(R.id.volume_bar);		
		banner_f = (RelativeLayout) bannerView.findViewById(R.id.banner_f);	
		banner_p = (RelativeLayout) bannerView.findViewById(R.id.banner_p);
		v_status_id = (ImageView) bannerView.findViewById(R.id.v_status_id);
	}

	public static String formatLeftS(String str, int min_length) {
		String format = "%-" + (min_length < 1 ? 1 : min_length) + "s";
		return String.format(format, str);
	}

	public static String format0Right(long num, int min_length) {
		String format = "%0" + (min_length < 1 ? 1 : min_length) + "d";
		return String.format(format, num);
	}

	public static String format0Right(double d, int min_length, int precision) {
		String format = "%0" + (min_length < 1 ? 1 : min_length) + "." + (precision < 0 ? 0 : precision) + "f";
		return String.format(format, d);
	}
}
