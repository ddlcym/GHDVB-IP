package com.changhong.app.ca;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import com.changhong.app.dtv.R;
import com.changhong.dvb.CA;
import com.changhong.dvb.CA_Mail_Head;
import com.changhong.dvb.DVB;
import com.changhong.dvb.ProtoMessage.DVB_CA_TYPE;

public class CaMail {

	private static final String TAG = "CaMailEvent";
	private static final int MESSAGE_NOVEL_CA_MAIL = 0;
	private static final int MESSAGE_SUMA_CA_MAIL = 1;
	private static final int MESSAGE_DVN_CA_MAIL = 2;
	private static final int MESSAGE_CA_MAIL_SHOW = 0;
	private static final int MESSAGE_CA_MAIL_HIDE = 1;
	private static final int MESSAGE_CA_MAILCONTENT_SHOW = 2;
	private Context mContext = null;
	private static WindowManager wm = null;
	private static WindowManager.LayoutParams wmParams = null;
	private static ImageView mailIconView = null;
	private static String isFullMail = "false";
	private int mCaType = -1;
	private int iSecond = 20;
	private int newMailSize = 0;
	protected CA thisCa = null;

	public CaMail() {
		Log.e(TAG,
				"CaMail::CaMail() --> not call this, call CaMail(Context) to set Context");
		return;
	}

	public CaMail(Context context) {
		mContext = context;
		thisCa = DVB.getManager().getCaInstance();
		newMailSize = 0;
		return;
	}

	/**
	 * mReaded=0 未读 ;mReaded=1 已读
	 * @return
	 */
	private int newMailSize() {
		int  size=0;
		CA_Mail_Head[] mailHeads = thisCa.getAllMailHeads();
		if (mailHeads != null && mailHeads.length > 0) {
			DVB_CA_TYPE curCaType = thisCa.getCurType();
			for (int i = 0; i < mailHeads.length; i++) {
				if (DVB_CA_TYPE.CA_NOVEL == curCaType) {
					if (mailHeads[i] != null) {					
					   boolean isRead = (0 == mailHeads[i].mCaMailHeadNovel.mReaded)?false:true;
					   if(!isRead)size++;
					}
				} else if (DVB_CA_TYPE.CA_SUMA == curCaType) {
					if (mailHeads[i] != null) {					
						boolean  isRead = (0 == mailHeads[i].mCaMailHeadSuma.mReaded)?false:true;
						 if(!isRead)size++;					}
				}
			}
		} 	
		return size;
	}


	public void ShowIcon(int caType, int showType, int eventData) {
		Log.i(TAG, "ShowIcon>>>>>>>");

		Message message = mAppHandler.obtainMessage();
		;
		if (caType == 3)// SUMA
		{
			message.what = MESSAGE_SUMA_CA_MAIL;
		} else if (caType == 2)// NOVEL
		{
			message.what = MESSAGE_NOVEL_CA_MAIL;

		} else if (caType == 4)// DVN
		{
			message.what = MESSAGE_DVN_CA_MAIL;
		} else {
			return;
		}
		message.arg1 = showType;
		message.arg2 = eventData;
		mAppHandler.sendMessage(message);

	}

	public void sumaShowIcon(int hasNewMailBootCheck) {

		int showType = -1;
		if (hasNewMailBootCheck == 0)// no mail
		{
			return;
		} else if (hasNewMailBootCheck == 1)// has new mail
		{
			showType = 0;
		} else if (hasNewMailBootCheck == 2)// full
		{
			showType = 1;
		}

		Message message = mAppHandler.obtainMessage();
		;
		message.what = MESSAGE_SUMA_CA_MAIL;
		message.arg1 = showType;
		message.arg2 = 0;
		mAppHandler.sendMessage(message);

	}

	public void novelShowIcon(int hasNewMailBootCheck) {
		Log.i(TAG, "novelShowIcon>>>>>>>");

		Message message = mAppHandler.obtainMessage();
		message.arg1 = hasNewMailBootCheck;
		message.arg2 = 0;
		message.what = MESSAGE_NOVEL_CA_MAIL;
		mAppHandler.sendMessage(message);

	}

	private Handler mAppHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			mCaType = msg.what;
			switch (mCaType) {
			case MESSAGE_SUMA_CA_MAIL: {
				int showType = msg.arg1;
				if (showType == 0)// new,show
				{
					showMailIcon(MESSAGE_SUMA_CA_MAIL);

				} else if (showType == 1)// new and space is full
				{
					showMailIconFull(MESSAGE_SUMA_CA_MAIL);
				} else if (showType == 0xff)// hide
				{
					hideMailIcon(MESSAGE_SUMA_CA_MAIL);
				}
			}
				break;

			case MESSAGE_NOVEL_CA_MAIL: {
				int showType = msg.arg1;
				Log.i(TAG, "mail  :: showType" + showType);
				setFullMail(false);
				if (showType == 0) {// hide

					hideMailIcon(MESSAGE_NOVEL_CA_MAIL);
				} else if (showType == 1) {// new
					showMailIcon(MESSAGE_NOVEL_CA_MAIL);
					if (isShowingMail()) {
						mMailHandler.sendEmptyMessage(MESSAGE_CA_MAILCONTENT_SHOW);
					} else {
						mMailHandler.sendEmptyMessageDelayed(MESSAGE_CA_MAILCONTENT_SHOW, 20000);
					}

				} else if (showType == 2) {// new space is full
					showMailIcon(MESSAGE_NOVEL_CA_MAIL);
					setFullMail(true);
					if (isShowingMail()) {
						mMailHandler.sendEmptyMessage(MESSAGE_CA_MAILCONTENT_SHOW);
					} else {
						mMailHandler.sendEmptyMessageDelayed(MESSAGE_CA_MAIL_SHOW, 1000);
					}
				}
			}
				break;

			case MESSAGE_DVN_CA_MAIL: {
				int showType = msg.arg1;
				int eventData = msg.arg2;
				if (showType == 0)// hide
				{
					hideMailIcon(MESSAGE_DVN_CA_MAIL);
				} else if (showType == 1)// new
				{
					showMailIcon(MESSAGE_DVN_CA_MAIL);
				} else if (showType == 2)// new space is full
				{
					showMailIconFull(MESSAGE_DVN_CA_MAIL);
				}
			}
				break;
			}

		}
	};
	private Handler mMailHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MESSAGE_CA_MAIL_SHOW:
				Log.i(TAG, "mFullMailHandler mail full ");
				if (isFullMail() && null != mailIconView) {
					if (View.VISIBLE != mailIconView.getVisibility()) {
						mailIconView.setVisibility(View.VISIBLE);
					} else {
						mailIconView.setVisibility(View.INVISIBLE);
					}
					iSecond--;
					if (iSecond > 0) {
						mMailHandler.sendEmptyMessageDelayed(
								MESSAGE_CA_MAIL_SHOW, 1000);
					} else {
						mMailHandler
								.sendEmptyMessage(MESSAGE_CA_MAILCONTENT_SHOW);
						iSecond = 20;
					}
				} else {
					removeMessages(MESSAGE_CA_MAIL_SHOW);
				}
				break;
			case MESSAGE_CA_MAIL_HIDE:
				setFullMail(false);
				removeMessages(MESSAGE_CA_MAIL_SHOW);
				break;

			case MESSAGE_CA_MAILCONTENT_SHOW:		   
				if(newMailSize <= 0){
					newMailSize = newMailSize();
				}
				setFullMail(false);
				if (!isShowingMail()) {
					newMailSize = newMailSize();
					if(newMailSize-->0){
						startMailActivity();										
					}
				}
				Log.i(TAG,"newMailList size = "+newMailSize);
				// 判断是否显示完邮件
				if (newMailSize > 0) {
					Log.i(TAG," new mail don't show !!!");
					sendEmptyMessageDelayed(MESSAGE_CA_MAILCONTENT_SHOW, 5000);					
					if (null == mailIconView || View.VISIBLE != mailIconView.getVisibility()) {
						showMailIcon(MESSAGE_NOVEL_CA_MAIL);
						Log.i(TAG,"show MailIcon !");
					}
				} else {
					hideMailIcon(mCaType);
					Log.i(TAG," no new mail need to show and hideMailIcon()!");
				}
				break;
			}

		}
	};

	public void hideMailIcon(int caType) {
		if (wm != null) {
			wm.removeView(mailIconView);
			mailIconView = null;
			wm = null;
			wmParams = null;
		}
		saveMailProperties(caType, "0");

	}

	private void showMailIcon(int caType) {
		if (wm != null) {
			wm.removeView(mailIconView);
			mailIconView = null;
			wm = null;
			wmParams = null;
			Log.i(TAG, "remove mialicon");
		}
		mailIconView = new ImageView(mContext);
		mailIconView.setBackgroundResource(R.drawable.ca_mailicon);
		wm = (WindowManager) mContext.getApplicationContext().getSystemService(
				"window");
		wmParams = new WindowManager.LayoutParams();
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 设置成不能获取焦点
		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

		int width, height;
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		Log.i("Vanlen-NewMail", "width = " + width + ", height =  " + height);

		wmParams.width = 76 * width / 1280;
		wmParams.height = 76 * height / 720;
		wmParams.x = 80 * width / 1280;
		wmParams.y = 180 * height / 720;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.alpha = (float) 0.8;
		wm.addView(mailIconView, wmParams);
		saveMailProperties(caType, "1");

	}

	private void showMailIconFull(int caType) {
		if (wm != null) {
			wm.removeView(mailIconView);
			mailIconView = null;
			wm = null;
			wmParams = null;
		}
		mailIconView = new ImageView(mContext);
		mailIconView.setBackgroundResource(R.drawable.ca_mailicon);
		wm = (WindowManager) mContext.getApplicationContext().getSystemService(
				"window");

		wmParams = new WindowManager.LayoutParams();
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 设置成不能获取焦点
		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

		wmParams.width = 76;
		wmParams.height = 76;
		wmParams.x = 80;
		wmParams.y = 120;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.alpha = (float) 0.8;
		wm.addView(mailIconView, wmParams);
		saveMailProperties(caType, "2");
	}

	/**
	 * 保存新邮件系统属性
	 * 
	 * @param caType
	 * @param value
	 */
	public void saveMailProperties(int caType, String value) {
		switch (caType) {
		case MESSAGE_NOVEL_CA_MAIL: {
			SystemProperties.set("persist.sys.novel.newMail", value);
			break;
		}
		case MESSAGE_SUMA_CA_MAIL: {
			SystemProperties.set("persist.sys.suma.newMail", value);
			break;
		}
		case MESSAGE_DVN_CA_MAIL: {
			SystemProperties.set("persist.sys.dvn.newMail", value);
			break;
		}

		}
	}

	public boolean isFullMail() {
		boolean reValue = false;
		synchronized (isFullMail) {
			if (isFullMail.equals("true")) {
				reValue = true;
			}
		}
		return reValue;
	}

	public void setFullMail(boolean fullMail) {
		synchronized (isFullMail) {
			this.isFullMail = fullMail ? "true" : "false";

		}
	}

	private void startMailActivity() {
		Intent mIntent = new Intent(mContext, EmailContent.class);
		try {
			mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(mIntent);
			Log.i("livemail", "sent mail succeed!!!");

		} catch (Exception e) {
			e.printStackTrace();
			Log.i("livemail", "sent mail fail!!!");
		}
	}

	/**
	 * 正在显示新邮件
	 * 
	 * @return
	 */
	private boolean isShowingMail() {
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Activity.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		return cn.getClassName().contains("EmailContent");
	}
}
