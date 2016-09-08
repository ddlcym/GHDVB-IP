package com.changhong.app.dtv;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;

import com.changhong.app.constant.Advertise_Constant;
import com.changhong.dvb.Channel;
import com.changhong.dvb.DVB;
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
	private static Banner banner;
	public static Toast bannerToast = null;
	private static SysApplication sysApplication;
	private Context mContext;
	private View bannerView;

	Channel channel;
	HashMap<String, Integer> hs = null;
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
	private int curId = 0;
	private Handler processADHandler;
	private String tempStr = "";

	public Banner() {
		// TODO Auto-generated constructor stub
	}

	private Banner(Context context) {
		mContext = context.getApplicationContext();
		new Thread(new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try {
					// ReturnStr1 = java.net.InetAddress.getByName(host);
					tempStr = java.net.InetAddress.getByName(Advertise_Constant.ADNET_ADDRESS).getHostAddress();
					Log.i("zyt", tempStr);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		AdOperation.setAquaPassAddress(Advertise_Constant.ADNET_ADDRESS);
		AdOperation.setAdItemAddress(Advertise_Constant.ADNET_ADDRESS);
		// AdOperation.setAquaPassAddress(tempStr);
		// AdOperation.setAdItemAddress(tempStr);
		processADHandler = new Handler(mContext.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case Advertise_Constant.PFBANNER_CONS:
					String ADidString = msg.getData().getString("PLAY_AD");
					playAd(ADidString);
					break;		
					
				case 0x1234:
					if(bannerToast!=null)
						bannerToast.cancel();
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	public void show(int chanId) {
		curId = chanId;
		Log.i("banner", "chanId----->" + chanId);

		if (sysApplication == null) {
			sysApplication = SysApplication.getInstance();
			sysApplication.initBookDatabase(mContext);
		}
		if (bannerToast == null) {
			bannerToast = new Toast(mContext);
			bannerToast.setGravity(Gravity.BOTTOM, 0, 0);
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
		
		channel = sysApplication.dvbDatabase.getChannel(chanId);
		updatePFInfo();
		updateBanner();
		updateChannelStatus(chanId);
		updateDateTime();
		if (pFBannerYiHanRunnable != null) {
			processADHandler.removeCallbacks(pFBannerYiHanRunnable);
		}
		processADHandler.post(pFBannerYiHanRunnable);
		bannerToast.setView(bannerView);
		bannerToast.setDuration(Toast.LENGTH_LONG);
		bannerToast.show();
		/* add for get servcieId */
		Channel curChannel = DVB.getManager().getChannelDBInstance().getChannel(chanId);
		Log.i("zyt", "����bannerʱ ����ȡ����serviceId ��" + curChannel.serviceId + "");
	}
	/**
	 * public void show(int chanId,int style)
	 * @param
	 * int vol_adj: adjust volume
	 * boolean multe: true is mute
	 * */
	public void show(int chanId,int vol_adj,boolean mute) {
		
		//Log.e("banner", "dis>>>> chanId----->" + chanId+",vol---->"+vol_adj+",mult---->"+mute);

		
		curId = chanId;

		if (sysApplication == null) {
			sysApplication = SysApplication.getInstance();
			sysApplication.initBookDatabase(mContext);
		}
		if (bannerToast == null) {
			bannerToast = new Toast(mContext);
			bannerToast.setGravity(Gravity.BOTTOM, 0, 0);
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
		
		channel = sysApplication.dvbDatabase.getChannel(chanId);
		updatePFInfo();
		updateBanner();
		updateChannelStatus(chanId);
		updateDateTime();
		if (pFBannerYiHanRunnable != null) {
			processADHandler.removeCallbacks(pFBannerYiHanRunnable);
		}
		processADHandler.post(pFBannerYiHanRunnable);
		bannerToast.setView(bannerView);
		bannerToast.setDuration(Toast.LENGTH_LONG);
		bannerToast.show();
		
		/*
		Message delayMsg = new Message();
		delayMsg.what=0x1234;  
		delayMsg.arg1 = 5000;
		processADHandler.sendMessageDelayed(delayMsg, 5000); */
        
		/* add for get servcieId */
		Channel curChannel = DVB.getManager().getChannelDBInstance().getChannel(chanId);
		Log.i("zyt", "����bannerʱ ����ȡ����serviceId ��" + curChannel.serviceId + "");
	}

	public static synchronized Banner getInstance(Context context) {
		if (banner == null) {
			banner = new Banner(context);
		}
		return banner;
	}

	private void updateBanner() {
		String channelname = new String();
		// TODO channelname PF_channel_name diffs?
		channelname = channel.name;
		if (hs == null) {
			hs = new HashMap<String, Integer>();
			CH_TVlogo_Mapping();
		}
		progress.setProgress(getPlayingProgress());

		Log.i("banner", "channel.logicNo----->" + channel.logicNo);

		if (channel.logicNo < 10) {
			service_id.setText("00" + channel.logicNo);
		} else if (channel.logicNo < 100) {
			service_id.setText("0" + channel.logicNo);
		} else {
			service_id.setText("" + channel.logicNo);
		}
		channel_name.setText(channelname);
		// textview_timeshift_support.setText(getTimeShiftSupportString(channel.chanId));

		PF_P.setText(PF_enventName_P);
		PF_F.setText(PF_enventName_F);

		banner_PF_P_time.setText(PF_time_P);
		banner_PF_F_time.setText(PF_time_F);

	}
	
	public void cancel(){
		if(bannerToast!=null)
		bannerToast.cancel();
	}

	private void updateChannelStatus(int chanId) {
		//TODO 
		Channel curChannel = DVB.getManager().getChannelDBInstance().getChannel(chanId);
		
		if(curChannel==null)
			return;
		
		boolean bHD=false, b3D=false, bMulAudio=false,bSubTitle=false,bAc3=false,bNewMail=false;
		
		if(curChannel.signalType==0xe4||curChannel.signalType==0xe6||curChannel.signalType==0xe8||
				curChannel.signalType==0xe9||curChannel.signalType==0xf9)
		{
			bHD = true;
		}
		if(curChannel.audioStreamType==0x06) //AC-3 audio/Dolby Digital Plus
		{
			bAc3 = true;
		}
		if(curChannel.subtitle!=null && !curChannel.subtitle.equals("null"))
		{
			bSubTitle = true;
		}
		if(bMulAudio)
			channel_multAudio.setVisibility(View.VISIBLE);
		else {
			channel_multAudio.setVisibility(View.INVISIBLE);
		}
		if(bSubTitle)
			channel_subtitle.setVisibility(View.VISIBLE);
		else {
			channel_subtitle.setVisibility(View.INVISIBLE);
		}
		if(bAc3)
			channel_Dolby.setVisibility(View.VISIBLE);
		else {
			channel_Dolby.setVisibility(View.INVISIBLE);
		}
		if(bNewMail)
			channel_mail.setVisibility(View.VISIBLE);
		else {
			channel_mail.setVisibility(View.INVISIBLE);
		}			
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
/*
 * Service_type Designation Standard Comments
0x00
reserved for future use DVB-SI V1.3.1
0x01
digital television service
0x02
digital radio sound service
0x03
teletext service
0x04
NVOD reference service
0x05
NVOD time-shifted service
0x06
mosaic service
0x07
PAL coded signal
0x08
SECAM coded signal
0x09
D/D2-MAC
0x0A
FM Radio
0x0B
NTSC coded signal
0x0C
Data broadcast service
0x0D to 0x7F
reserved for future use
0xC5
C+T Hidden Digital TV service
C+ Private
0xC6
C+T Hidden Digital audio service
0xE0
PushVOD
BGCTV Private
0xE1
HTML browser（only for interactive-STB）
0xE2
Front Page for interactive-HDSTB
0xE3
H.264（SDTV）
0xE4
H.264（HDTV）
0xE5
AVS（SDTV）
0xE6
AVS（HDTV）
0xE7
H.265（SDTV）
0xE8
H.265（HDTV）
0xE9
4K
0xEA to 0xEF
reserved for future use
0xF0
Changhong Stock
0xF1
HTML browser
0xF2
Games of C+
0xF3
Games of BGCTV
0xF4
EPG
0xF5
System setting
0xF6
NVOD
0xF7
Software upgrade
0xF8
IP HTML browser
0xF9
HDTV
0xFA
VOD
0xFB
nPVR
0xFC
Mosaic（MiddleWare）
0xFD
EPG（MiddleWare）
0xFE
NVOD（MiddleWare）
0xFF
reserved for future use
DVB-SI
 * */		
		try {
			Log.e("YYBB",
					curChannel.audioMode+","+
					curChannel.audioStreamType+","+
					curChannel.signalType+","+
					curChannel.sortId+","+
					curChannel.subtitle+","+
					curChannel.videoStreamType);			// 0,3,0,1,null,2
		} catch (Exception e) {
			e.printStackTrace();
		}


		/*
		Random random=new Random();
		int num = random.nextInt(5);
		if(num==0)
			channel_multAudio.setVisibility(View.INVISIBLE);
		else {
			channel_multAudio.setVisibility(View.VISIBLE);
		}
		if(num==1)
			channel_subtitle.setVisibility(View.INVISIBLE);
		else {
			channel_subtitle.setVisibility(View.VISIBLE);
		}
		if(num==2)
			channel_Dolby.setVisibility(View.INVISIBLE);
		else {
			channel_Dolby.setVisibility(View.VISIBLE);
		}
		if(num==3)
			channel_mail.setVisibility(View.INVISIBLE);
		else {
			channel_mail.setVisibility(View.VISIBLE);
		}			
		if(num==4){
			channel_vid_3d.setVisibility(View.VISIBLE);
			channel_vid_hd.setVisibility(View.GONE);
		} else {
			channel_vid_hd.setVisibility(View.VISIBLE);
			channel_vid_3d.setVisibility(View.GONE);
		}	*/
	}
	
	public class ADThread extends Thread {
		private Handler mHandler;

		public ADThread(Handler parentHandler) {
			// TODO Auto-generated constructor stub
			mHandler = parentHandler;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Channel curChannel = DVB.getManager().getChannelDBInstance().getChannel(curId);
			ads = new AdStrategy(mContext, Advertise_Constant.LIVEPLAY_ID_PFBANNER,
					Common.getParams(curChannel.serviceId + "", Advertise_Constant.LIVEPLAY_ID_PFBANNER,
							Advertise_Constant.TEMP_IP_ADDRESS));
			// ���������
			ads.request();

			adPlayer.setIAdPlayerCallbackListener(new IAdPlayerCallbackListener() {

				@Override
				public void onPrepared(AdPlayer v) {
					// �������׼�������󲥷�
					Log.e("FSLog", "��Ƶ׼�����------------onPrepared");
					// adPlayer.play("");
				}

				@Override
				public void onError(AdPlayer v, int errorCode, String message) {
					// ���ؼ������з��������׳��������
					Log.e("FSLog", "�����쳣------------onError");
				}

				@Override
				public void onCompleted(AdPlayer v) {
					// ���ؼ��й�����ݲ�����Ϻ����
					Log.e("FSLog", "��沥�Ž���-----------onCompleted");
					adPlayer.stop();
				}
			});

			ads.setOnResponseListener(new IAdStrategyResponseListener() {

				@Override
				public void onResponse(ArrayList<AdItem> items) {
					// ��ù����Խ���������
					Log.i("zyt", "AdStrategy-------------------------onResponse");
					Log.i("zyt", "AdStrategy-------------------------onResponse  ֮ǰ����б�ĳ���" + items.size());
					if (items != null && items.size() > 0) {
						AdItem temp = items.get(0);
						// Log.i("zyt", " " + items.size());
						if (temp.type == AdItem.TYPE_SELF_PLAY) {// �������
						} else {// �ǿ�����棬��ȡ�岥�������λ��
							adList = items;
							if (temp.type == AdItem.TYPE_SELF_PLAY) {// �������
							} else {// �ǿ�����棬��ȡ�岥�������λ��
								// ����ͼƬ
								// playAd(items.get(0).Id);
								Message msg = new Message();
								msg.what = Advertise_Constant.PFBANNER_CONS;
								Bundle bundle = new Bundle();
								bundle.putString("PLAY_AD", items.get(0).Id);
								msg.setData(bundle);
								mHandler.sendMessage(msg);

							}
						}
					} else {
						Log.i("zyt", "AdStrategy---------------onResponse:items is null or size is 0");
					}
				}

				@Override
				public void onError(int errorCode, String message) {
					// ��ù����Խ������
					Log.i("zyt", "AdStrategy---------------onError");
				}
			});

			// �����������õ����ؼ���
			adPlayer.setAdStrategy(ads);

		}
	}

	public Runnable pFBannerYiHanRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			startAdsRequestAD(curId);
		}
	};
	private ImageView v_vol;
	private ClipDrawable v_vol_drawable;
	private RelativeLayout volume_bar;
	private RelativeLayout banner_f;
	private RelativeLayout banner_p;
	private ImageView v_status_id;

	public void startAdsRequestAD(int chanId) {
		Channel curChannel = DVB.getManager().getChannelDBInstance().getChannel(chanId);
		ads = new AdStrategy(mContext, Advertise_Constant.LIVEPLAY_ID_PFBANNER,
				Common.getParams(curChannel.serviceId + "", Advertise_Constant.LIVEPLAY_ID_PFBANNER,
						Advertise_Constant.TEMP_IP_ADDRESS));
		ads.request();

		adPlayer.setIAdPlayerCallbackListener(new IAdPlayerCallbackListener() {

			@Override
			public void onPrepared(AdPlayer v) {
				Log.e("FSLog", "adPlayer------------onPrepared");
			}

			@Override
			public void onError(AdPlayer v, int errorCode, String message) {
				Log.e("FSLog", "adPlayer------------onError");
			}

			@Override
			public void onCompleted(AdPlayer v) {
				Log.e("FSLog", "adPlayer-----------onCompleted");
				adPlayer.stop();
			}
		});

		ads.setOnResponseListener(new IAdStrategyResponseListener() {

			@Override
			public void onResponse(ArrayList<AdItem> items) {
				Log.i("zyt", "AdStrategy-------------------------onResponse");
				Log.i("zyt", "AdStrategy-------------------------onResponse" + items.size());
				if (items != null && items.size() > 0) {
					AdItem temp = items.get(0);
					// Log.i("zyt", " " + items.size());
					if (temp.type == AdItem.TYPE_SELF_PLAY) {
					} else {
						adList = items;
						if (temp.type == AdItem.TYPE_SELF_PLAY) {
						} else {

							playAd(items.get(0).Id);
						}
					}
				} else {
					Log.i("zyt", "AdStrategy---------------onResponse:items is null or size is 0");
				}
			}

			@Override
			public void onError(int errorCode, String message) {
				Log.i("zyt", "AdStrategy---------------onError");
				Log.i("zyt", "AdStrategy---------------onError-----eeeee");
				Log.i("zyt", "AdStrategy---------------onError-----eeeee + tempstr" + tempStr);
			}
		});

		adPlayer.setAdStrategy(ads);
	}

	public void playAd(String id) {
		adPlayer.setVisibility(View.VISIBLE);
		adPlayer.play(id);
	}

	private String getTimeShiftSupportString(int chanId) {
		boolean bSupport = DVB.getManager().getChannelDBInstance().getChannelExtendFieldValue(chanId,
				"timeshift_support", false);
		if (bSupport) {
			return mContext.getString(R.string.timeshift_support);
		}
		return "";
	}

	private int getPlayingProgress() {
		DVB_EPG_PF pfInfo = DVB.getManager().getEpgInstance().getPfInfo(channel);
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
		String mYear;
		String mMonth;
		String mDay;
		String mWeek;
		String mHour, mMinute;
		String[] week = mContext.getResources().getStringArray(R.array.str_dtv_epg_week_name);

		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		mYear = String.valueOf(c.get(Calendar.YEAR));
		mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		mWeek = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
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
		if ("1".equals(mWeek)) {
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
		}

		Log.i("banner", "PF_dtw------" + mHour + "   " + mMinute);
		PF_dtw.setText(mHour + ":" + mMinute);
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
		DVB_EPG_PF pfInfo = DVB.getManager().getEpgInstance().getPfInfo(channel);
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
			Common.LOGE("TVbanner  obj_EpgEventInfo == null");
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
		channel = sysApplication.dvbDatabase.getChannel(SysApplication.iCurChannelId);
		adPlayer = (AdPlayer) bannerView.findViewById(R.id.adplayer);
		adPlayer.setDefaultAd(R.drawable.default_img, 1);

		channel_vid_3d = 	(ImageView) bannerView.findViewById(R.id.banner_channel_vid_3d);		
		channel_vid_hd = 	(ImageView) bannerView.findViewById(R.id.banner_channel_vid_hd);	
		channel_multAudio = 	(ImageView) bannerView.findViewById(R.id.banner_channel_multAudio);
		channel_subtitle = 	(ImageView) bannerView.findViewById(R.id.banner_channel_subtitle);
		channel_Dolby = 	(ImageView) bannerView.findViewById(R.id.banner_channel_Dolby);
		channel_mail = 	(ImageView) bannerView.findViewById(R.id.banner_channel_mail);
		
		v_vol = (ImageView)bannerView.findViewById(R.id.v_vol_id);
		v_vol_drawable = (ClipDrawable)v_vol.getDrawable(); 
		volume_bar = (RelativeLayout) bannerView.findViewById(R.id.volume_bar);		
		banner_f = (RelativeLayout) bannerView.findViewById(R.id.banner_f);	
		banner_p = (RelativeLayout) bannerView.findViewById(R.id.banner_p);
		v_status_id = (ImageView) bannerView.findViewById(R.id.v_status_id);
	}

	private void getValue() {
		sWeek = mContext.getResources().getStringArray(R.array.str_dtv_epg_week_name);
		sMonth = mContext.getResources().getString(R.string.moon);
		sDay = mContext.getResources().getString(R.string.day);
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

	private void CH_TVlogo_Mapping() {
		{
			hs.clear();

			hs.put(mContext.getResources().getString(R.string.cctv1_1), R.drawable.cctv1);
			hs.put(mContext.getResources().getString(R.string.cctv1_2), R.drawable.cctv1);
			hs.put(mContext.getResources().getString(R.string.cctv1_3), R.drawable.cctv1);
			hs.put(mContext.getResources().getString(R.string.cctv1_4), R.drawable.cctv1);
			hs.put(mContext.getResources().getString(R.string.cctv1hd_1), R.drawable.cctv1);
			hs.put(mContext.getResources().getString(R.string.cctv1hd_2), R.drawable.cctv1);
			hs.put(mContext.getResources().getString(R.string.cctv2_1), R.drawable.cctv2);
			hs.put(mContext.getResources().getString(R.string.cctv2_2), R.drawable.cctv2);
			hs.put(mContext.getResources().getString(R.string.cctv2_3), R.drawable.cctv2);
			hs.put(mContext.getResources().getString(R.string.cctv2_4), R.drawable.cctv2);
			hs.put(mContext.getResources().getString(R.string.cctv3_1), R.drawable.cctv3);
			hs.put(mContext.getResources().getString(R.string.cctv3_2), R.drawable.cctv3);
			hs.put(mContext.getResources().getString(R.string.cctv3_3), R.drawable.cctv3);
			hs.put(mContext.getResources().getString(R.string.cctv3_4), R.drawable.cctv3);
			hs.put(mContext.getResources().getString(R.string.cctv3hd), R.drawable.cctv3);
			hs.put(mContext.getResources().getString(R.string.cctv4_1), R.drawable.cctv4);
			hs.put(mContext.getResources().getString(R.string.cctv4_2), R.drawable.cctv4);
			hs.put(mContext.getResources().getString(R.string.cctv4_3), R.drawable.cctv4);
			hs.put(mContext.getResources().getString(R.string.cctv4_4), R.drawable.cctv4);
			hs.put(mContext.getResources().getString(R.string.cctv5hd), R.drawable.cctv5);
			hs.put(mContext.getResources().getString(R.string.cctv5hd_1), R.drawable.cctv5hd);
			hs.put(mContext.getResources().getString(R.string.cctv5_1), R.drawable.cctv5);
			hs.put(mContext.getResources().getString(R.string.cctv5_2), R.drawable.cctv5);
			hs.put(mContext.getResources().getString(R.string.cctv5_3), R.drawable.cctv5);
			hs.put(mContext.getResources().getString(R.string.cctv5_4), R.drawable.cctv5);
			hs.put(mContext.getResources().getString(R.string.cctv6_1), R.drawable.cctv6);
			hs.put(mContext.getResources().getString(R.string.cctv6_2), R.drawable.cctv6);
			hs.put(mContext.getResources().getString(R.string.cctv6_3), R.drawable.cctv6);
			hs.put(mContext.getResources().getString(R.string.cctv6_4), R.drawable.cctv6);
			hs.put(mContext.getResources().getString(R.string.cctv6hd), R.drawable.cctv6);
			hs.put(mContext.getResources().getString(R.string.cctv7_1), R.drawable.cctv7);
			hs.put(mContext.getResources().getString(R.string.cctv7_2), R.drawable.cctv7);
			hs.put(mContext.getResources().getString(R.string.cctv7_3), R.drawable.cctv7);
			hs.put(mContext.getResources().getString(R.string.cctv7_4), R.drawable.cctv7);
			hs.put(mContext.getResources().getString(R.string.cctv8_1), R.drawable.cctv8);
			hs.put(mContext.getResources().getString(R.string.cctv8_2), R.drawable.cctv8);
			hs.put(mContext.getResources().getString(R.string.cctv8_3), R.drawable.cctv8);
			hs.put(mContext.getResources().getString(R.string.cctv8_4), R.drawable.cctv8);
			hs.put(mContext.getResources().getString(R.string.cctv8hd), R.drawable.cctv8);
			hs.put(mContext.getResources().getString(R.string.cctv9_1), R.drawable.cctv9);
			hs.put(mContext.getResources().getString(R.string.cctv9_2), R.drawable.cctv9);
			hs.put(mContext.getResources().getString(R.string.cctv9_3), R.drawable.cctv9);
			hs.put(mContext.getResources().getString(R.string.cctv10_1), R.drawable.cctv10);
			hs.put(mContext.getResources().getString(R.string.cctv10_2), R.drawable.cctv10);
			hs.put(mContext.getResources().getString(R.string.cctv10_3), R.drawable.cctv10);
			hs.put(mContext.getResources().getString(R.string.cctv10_4), R.drawable.cctv10);
			hs.put(mContext.getResources().getString(R.string.cctv11_1), R.drawable.cctv11);
			hs.put(mContext.getResources().getString(R.string.cctv11_2), R.drawable.cctv11);
			hs.put(mContext.getResources().getString(R.string.cctv11_3), R.drawable.cctv11);
			hs.put(mContext.getResources().getString(R.string.cctv11_4), R.drawable.cctv11);
			hs.put(mContext.getResources().getString(R.string.cctv12_1), R.drawable.cctv12);
			hs.put(mContext.getResources().getString(R.string.cctv12_2), R.drawable.cctv12);
			hs.put(mContext.getResources().getString(R.string.cctv12_3), R.drawable.cctv12);
			hs.put(mContext.getResources().getString(R.string.cctv12_4), R.drawable.cctv12);
			hs.put(mContext.getResources().getString(R.string.cctv13_1), R.drawable.cctvnews);
			hs.put(mContext.getResources().getString(R.string.cctv13_2), R.drawable.cctvnews);
			hs.put(mContext.getResources().getString(R.string.cctv13_3), R.drawable.cctvnews);
			hs.put(mContext.getResources().getString(R.string.cctv13_4), R.drawable.cctvnews);
			hs.put(mContext.getResources().getString(R.string.cctv13_5), R.drawable.cctvnews);
			hs.put(mContext.getResources().getString(R.string.cctv13_6), R.drawable.cctvnews);
			hs.put(mContext.getResources().getString(R.string.cctv13_7), R.drawable.cctvnews);
			hs.put(mContext.getResources().getString(R.string.cctv13_8), R.drawable.cctvnews);
			hs.put(mContext.getResources().getString(R.string.cctv13_9), R.drawable.cctvnews);
			hs.put(mContext.getResources().getString(R.string.cctv14_1), R.drawable.cctv14);
			hs.put(mContext.getResources().getString(R.string.cctv14_2), R.drawable.cctv14);
			hs.put(mContext.getResources().getString(R.string.cctv14_3), R.drawable.cctv14);
			hs.put(mContext.getResources().getString(R.string.cctv14_4), R.drawable.cctv14);
			hs.put(mContext.getResources().getString(R.string.cctv14_5), R.drawable.cctv14);
			hs.put(mContext.getResources().getString(R.string.cctv14_6), R.drawable.cctv14);
			hs.put(mContext.getResources().getString(R.string.cctv14_7), R.drawable.cctv14);
			hs.put(mContext.getResources().getString(R.string.cctv15_1), R.drawable.cctv15);
			hs.put(mContext.getResources().getString(R.string.cctv15_2), R.drawable.cctv15);
			hs.put(mContext.getResources().getString(R.string.cctv15_3), R.drawable.cctv15);
			hs.put(mContext.getResources().getString(R.string.cctveyu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.cctvalaboyu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.cctvguide), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.anhuiweishi), R.drawable.logoanhui);
			hs.put(mContext.getResources().getString(R.string.beijingweishi), R.drawable.logobeijing);
			hs.put(mContext.getResources().getString(R.string.chongqingweishi), R.drawable.logochongqing);
			hs.put(mContext.getResources().getString(R.string.dongfangweishi), R.drawable.logodongfang);
			hs.put(mContext.getResources().getString(R.string.shanghaiweishi), R.drawable.logodongfang);
			hs.put(mContext.getResources().getString(R.string.dongnanweishi), R.drawable.logodongnan);
			hs.put(mContext.getResources().getString(R.string.fujianweishi), R.drawable.logodongnan);
			hs.put(mContext.getResources().getString(R.string.guangdongweishi), R.drawable.logoguangdong);
			hs.put(mContext.getResources().getString(R.string.guangxiweishi), R.drawable.logoguangxi);
			hs.put(mContext.getResources().getString(R.string.guizhouweishi), R.drawable.logoguizhou);
			hs.put(mContext.getResources().getString(R.string.hebeiweishi), R.drawable.logohebei);
			hs.put(mContext.getResources().getString(R.string.henanweishi), R.drawable.logohenan);
			hs.put(mContext.getResources().getString(R.string.heilongjiangtai), R.drawable.logoheilongjiang);
			hs.put(mContext.getResources().getString(R.string.heilongjiangweishi), R.drawable.logoheilongjiang);
			hs.put(mContext.getResources().getString(R.string.hubeiweishi), R.drawable.logohubei);
			hs.put(mContext.getResources().getString(R.string.hunanweishi), R.drawable.logohunan);
			hs.put(mContext.getResources().getString(R.string.jiangsuweishi), R.drawable.logojiangsu);
			hs.put(mContext.getResources().getString(R.string.jiangxiweishi), R.drawable.logojiangxi);
			hs.put(mContext.getResources().getString(R.string.jilinweishi), R.drawable.logojiling);
			hs.put(mContext.getResources().getString(R.string.liaoningweishi), R.drawable.logoliaoning);
			hs.put(mContext.getResources().getString(R.string.neimenggutai), R.drawable.logoneimenggu);
			hs.put(mContext.getResources().getString(R.string.neimengguweishi), R.drawable.logoneimenggu);
			hs.put(mContext.getResources().getString(R.string.ningxiaweishi), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.shandongweishi), R.drawable.logoshandong);
			hs.put(mContext.getResources().getString(R.string.shanxiweishi), R.drawable.logoshanxixi);
			hs.put(mContext.getResources().getString(R.string.shanxi1weishi), R.drawable.logoshanxi);
			hs.put(mContext.getResources().getString(R.string.sichuanweishi), R.drawable.logosichuan);
			hs.put(mContext.getResources().getString(R.string.tianjinweishi), R.drawable.logotianjin);
			hs.put(mContext.getResources().getString(R.string.yunnanweishi), R.drawable.logoyunnan);
			hs.put(mContext.getResources().getString(R.string.xizangweishi), R.drawable.logoxizang);
			hs.put(mContext.getResources().getString(R.string.xinjiangweishi), R.drawable.logoxinjiang);
			hs.put(mContext.getResources().getString(R.string.zhejiangweishi), R.drawable.logozhejiang);
			hs.put(mContext.getResources().getString(R.string.zhejiangweishi_1), R.drawable.logozhejiang);
			hs.put(mContext.getResources().getString(R.string.shenzhengweishi), R.drawable.logoshenzhen);
			hs.put(mContext.getResources().getString(R.string.fenghuangweishi), R.drawable.logofenghuang);
			hs.put(mContext.getResources().getString(R.string.gansuweishi), R.drawable.logogansu);
			hs.put(mContext.getResources().getString(R.string.qinghaiweishi), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sichuangaoqing), R.drawable.logosichuan);
			hs.put(mContext.getResources().getString(R.string.sichuanweishigaoqing), R.drawable.logosichuan);
			hs.put(mContext.getResources().getString(R.string.sichuanyingshigaoqing), R.drawable.logosichuan);
			hs.put(mContext.getResources().getString(R.string.zhejiangweishigaoqing), R.drawable.logozhejiang);
			hs.put(mContext.getResources().getString(R.string.zhejianggaoqing), R.drawable.logozhejiang);
			hs.put(mContext.getResources().getString(R.string.beijingweishigaoqing), R.drawable.logobeijing);
			hs.put(mContext.getResources().getString(R.string.beijinggaoqing), R.drawable.logobeijing);
			hs.put(mContext.getResources().getString(R.string.shanghaiweishigaoqing), R.drawable.logodongfang);
			hs.put(mContext.getResources().getString(R.string.shanghaigaoqing), R.drawable.logodongfang);
			hs.put(mContext.getResources().getString(R.string.guangdongweishigaoqing), R.drawable.guangdongweishi);
			hs.put(mContext.getResources().getString(R.string.guangdonggaoqing), R.drawable.guangdongweishi);
			hs.put(mContext.getResources().getString(R.string.shenzhengweishigaoqing), R.drawable.logoshenzhen);
			hs.put(mContext.getResources().getString(R.string.jiangsuweishigaoqing), R.drawable.logojiangsu);
			hs.put(mContext.getResources().getString(R.string.jiangsugaoqing), R.drawable.logojiangsu);
			hs.put(mContext.getResources().getString(R.string.heilongjiangweishigaoqing), R.drawable.logoheilongjiang);
			hs.put(mContext.getResources().getString(R.string.heilongjianggaoqing), R.drawable.logoheilongjiang);
			hs.put(mContext.getResources().getString(R.string.hunanweishigaoqing), R.drawable.logohunan);
			hs.put(mContext.getResources().getString(R.string.hunangaoqing), R.drawable.logohunan);
			hs.put(mContext.getResources().getString(R.string.hubeigaoqing), R.drawable.logohubei);
			hs.put(mContext.getResources().getString(R.string.shandonggaoqing), R.drawable.logoshandong);
			hs.put(mContext.getResources().getString(R.string.shenzhenggaoqing), R.drawable.logoshenzhen);
			hs.put(mContext.getResources().getString(R.string.tianjingaoqing), R.drawable.logotianjin);
			hs.put(mContext.getResources().getString(R.string.quanjishi), R.drawable.logoquanjishi);
			hs.put(mContext.getResources().getString(R.string.guofangjunshi), R.drawable.logoguofangjunshi);
			hs.put(mContext.getResources().getString(R.string.dieshipindao), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.dongfangcaijin), R.drawable.logodongfangcaijin);
			hs.put(mContext.getResources().getString(R.string.dongmanxiuchang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.dushijuchang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.emeidianying), R.drawable.logoemei);
			hs.put(mContext.getResources().getString(R.string.sihaidiaoyu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.fazhitiandi), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.fengyunzuqiu), R.drawable.logofengyunzuqiu);
			hs.put(mContext.getResources().getString(R.string.huanxiaojuchang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.jiayougouwu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.jiatinglicai), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.jinsepindao), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.jingbaotiyu), R.drawable.logojinbaotiyu);
			hs.put(mContext.getResources().getString(R.string.jinyingkatong), R.drawable.logojinyingkatong);
			hs.put(mContext.getResources().getString(R.string.jisuqiche), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.kuailechongwu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.laogushi), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.liangzhuangpindao), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.liuxueshijie), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.lvyoupindao), R.drawable.logolvyou);
			hs.put(mContext.getResources().getString(R.string.lvyouweishi), R.drawable.logolvyou);
			hs.put(mContext.getResources().getString(R.string.meiliyinyue), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.shenghuoshishang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.shijiedili), R.drawable.logoshijiedili);
			hs.put(mContext.getResources().getString(R.string.tianyuanweiqi), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.weishengjiankang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.yingyufudao), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.yingyufudao1), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.youxifengyun), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.youxifengyun1), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.youxijingji), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.yougouwu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.yunyuzhinan), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.zhengquanzixun), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.jiaoyupindao), R.drawable.logozhongguojiaoyu);
			hs.put(mContext.getResources().getString(R.string.zhongguojiaoyu1), R.drawable.logozhongguojiaoyu);
			hs.put(mContext.getResources().getString(R.string.cetv1), R.drawable.logozhongguojiaoyu);
			hs.put(mContext.getResources().getString(R.string.cetv2), R.drawable.logozhongguojiaoyu);
			hs.put(mContext.getResources().getString(R.string.zhongguoqixiang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.zhongguoqixiangtai), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.zhongguoqixiang1), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.jingcaisichuan), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xinyule), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xingyule), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xingyuanyinhua), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xingyuanjuchang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xingyuanxinzhi), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xingyuanxinyi), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xingyuanai), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xingyuanchengzhang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xingyuanoumeijuchang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xingyuanoumeiyuanxian), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xingyuanshouying), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xingyuanxinzhi1), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xingyuanyazhoujuchang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xingyuanyazhouyuanxian), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.jiatingjuchangdianshiju), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.jingdianjuchangdianshiju), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.threeDpindao), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.HBO), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.TVB8), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.BBC), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.KBS), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.NHK), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sichuan2), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sichuanwenhualvyou), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sichuan3), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sichuanjingji), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sichuan4), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sichuanxinwentongxun), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sichuan5), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sichuanyingshiwenhua), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sichuan6), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sichuanxingkonggouwu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sichuan7), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sichuanfunvertong), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sichuan8), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sichuankejiao), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sctv09), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sichuan9), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.mianyang1), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.mianyang1_1), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.mianyang2), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.mianyang2_1), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.mianyang3), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.mianyang3_1), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.mianyang4), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.mianyang4_1), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.santai1), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.santai2), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.santai3), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.santai4), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.beichuan1), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.beichuan2), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.zitong3), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.zitong4), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.zitong5), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.zitongzibanjiemu1), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.zitongzibanjiemu2), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.cdtv1), R.drawable.logochengdu);
			hs.put(mContext.getResources().getString(R.string.cdtv2), R.drawable.logochengdu);
			hs.put(mContext.getResources().getString(R.string.cdtv3), R.drawable.logochengdu);
			hs.put(mContext.getResources().getString(R.string.cdtv4), R.drawable.logochengdu);
			hs.put(mContext.getResources().getString(R.string.cdtv5), R.drawable.logochengdu);
			hs.put(mContext.getResources().getString(R.string.cdtv6), R.drawable.logochengdu);
			hs.put(mContext.getResources().getString(R.string.cdtv7), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.cdtv8), R.drawable.logochengdu);
			hs.put(mContext.getResources().getString(R.string.cdtvgaoqing), R.drawable.logochengdu);
			hs.put(mContext.getResources().getString(R.string.CHCdongzuoyingyuan), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.CHCgaoqing), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.DOXyinxiangshijie), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.DVshenghuo), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.IjiaTV), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.SCTV1), R.drawable.logosichuan);
			hs.put(mContext.getResources().getString(R.string.SCTV2), R.drawable.logoemei);
			hs.put(mContext.getResources().getString(R.string.SCTV3), R.drawable.logoemei);
			hs.put(mContext.getResources().getString(R.string.SCTV4), R.drawable.logoemei);
			hs.put(mContext.getResources().getString(R.string.SCTV5), R.drawable.logoemei);
			hs.put(mContext.getResources().getString(R.string.SCTV6), R.drawable.logoemei);
			hs.put(mContext.getResources().getString(R.string.SCTV7), R.drawable.logoemei);
			hs.put(mContext.getResources().getString(R.string.SCTVemei), R.drawable.logoemei);
			hs.put(mContext.getResources().getString(R.string.SCTVgonggong), R.drawable.logoemei);
			hs.put(mContext.getResources().getString(R.string.SCTVkejiao), R.drawable.logoemei);
			hs.put(mContext.getResources().getString(R.string.SCTVxingkong), R.drawable.logoemei);
			hs.put(mContext.getResources().getString(R.string.SCTVgaoqing), R.drawable.logosichuan);
			hs.put(mContext.getResources().getString(R.string.baixingjiankang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.baobeijia), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.bingtuanweishi), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.caifutianxia), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.caiminzaixian), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.chemi), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.chengdujiaotongguangbo), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.chengdujingjiguangbo), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.chengduwangluoguangbo), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.chengduxinwenguangbo), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.chengduxiuxianguangbo), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.chengshijianshe), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.diyijuchang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.dianzitiyu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.dieshi), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.dushizhisheng), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.dushu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.faxianzhilv), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.fengshanggouwu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.fengyunjuchang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.fengyunyinyue), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.gaoerfu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.gaoerfuwangqiu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.haoxianggouwu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.huaxiazhisheng), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.huaijiujuchang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.huanqiugouwu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.huanqiulvyou), R.drawable.logohuanqiulvyou);
			hs.put(mContext.getResources().getString(R.string.huanqiuzixunguangbo), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.jiajiagouwu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.jiatingjiankang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.jiazhengpindao), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.jiajiakatong), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.jinniuyouxiantai), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.jingjizhisheng), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.jingpindaoshi), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.kakukatong), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.kangbaweishi), R.drawable.logoemei);
			hs.put(mContext.getResources().getString(R.string.kaoshizaixian), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.kuailegouwu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.laonianfu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.liyuan), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.liangzhuang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.minzuzhisheng), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.nvxingshishang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.ouzhouzuqiu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.qicaixiju), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.qimo), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.quanyuchengduguangbo), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.renwu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.rongchengxianfeng), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.sheying), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.shenzhouzhisheng), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.shidaichuxing), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.shidaifengshang), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.shidaijiaju), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.shidaimeishi), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.shishanggouwu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.shoucangtianxia), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.shuhua), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.shuowenjiezi), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.tianyinweiyiyinyue), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.wangluoqipai), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.wenhuajingpin), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.wenwubaoku), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.wenyizhisheng), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.wushushijie), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xianfengjilu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xiandainvxing), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xindongman), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xinkedongman), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xingfucai), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.xinyuezhisheng), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.youyoubaobei), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.zaoqijiaoyu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.zhiyezhinan), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.zhongguotianqi), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.zhongguozhisheng), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.zhonghuazhisheng), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.zhongshigouwu), R.drawable.logotv);
			hs.put(mContext.getResources().getString(R.string.zhongxuesheng), R.drawable.logotv);
		}
	}
}
