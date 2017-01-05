package com.changhong.app.ca;

import com.changhong.app.dtv.LiveMail;
import com.changhong.app.dtv.Main;
import com.changhong.app.dtv.R;
import com.changhong.app.utils.UserDate;

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

	public CaMail() {
		Log.e(TAG,
				"CaMail::CaMail() --> not call this, call CaMail(Context) to set Context");
		return;
	}

	public CaMail(Context context) {
		mContext = context;
		return;
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
			switch (msg.what) {
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
				int eventData = msg.arg2;
				Log.i(TAG, "mail  :: showType" + showType + "eventData"
						+ eventData);
				if(mMailHandler.hasMessages(MESSAGE_CA_MAILCONTENT_SHOW)){
					mMailHandler.removeMessages(MESSAGE_CA_MAILCONTENT_SHOW);	
				}
				
				if (showType == 0)// hide
				{
					setFullMail(false);
					hideMailIcon(MESSAGE_NOVEL_CA_MAIL);
				} else if (showType == 1)// new
				{
					setFullMail(false);
					showMailIcon(MESSAGE_NOVEL_CA_MAIL);
					mMailHandler.sendEmptyMessageDelayed(
							MESSAGE_CA_MAILCONTENT_SHOW, 20000);

				} else if (showType == 2)// new space is full
				{
					setFullMail(true);
					showMailIconFull(MESSAGE_NOVEL_CA_MAIL);
					mMailHandler.sendEmptyMessageDelayed(MESSAGE_CA_MAIL_SHOW,
							1000);

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

	int iSecond=20;
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
					if(iSecond > 0){
						mMailHandler.sendEmptyMessageDelayed(MESSAGE_CA_MAIL_SHOW,1000);	
					}else{
						mMailHandler.sendEmptyMessage(MESSAGE_CA_MAILCONTENT_SHOW);	
						iSecond=20;
					}
				}
				break;
			case MESSAGE_CA_MAIL_HIDE:
				setFullMail(false);
				mMailHandler.removeMessages(MESSAGE_CA_MAIL_SHOW);
				break;

			case MESSAGE_CA_MAILCONTENT_SHOW:
				setFullMail(false);
				hideMailIcon(-1);
				Intent mIntent = new Intent(mContext, EmailContent.class);
				try {
					mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(mIntent);
					Log.i("livemail", "sent mail succeed!!!");
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("livemail", "sent mail fail!!!");
				}

				break;
			}

		}
	};

	public void hideMailIcon(int caType) {
		if (wm == null) {
			return;
		}
		wm.removeView(mailIconView);
		mailIconView = null;
		wm = null;
		wmParams = null;

		switch (caType) {
		case MESSAGE_NOVEL_CA_MAIL: {
			SystemProperties.set("persist.sys.novel.newMail", "0");
			break;
		}
		case MESSAGE_SUMA_CA_MAIL: {
			SystemProperties.set("persist.sys.suma.newMail", "0");
			break;
		}
		case MESSAGE_DVN_CA_MAIL: {
			SystemProperties.set("persist.sys.dvn.newMail", "0");
			break;
		}
		}
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

		switch (caType) {
		case MESSAGE_NOVEL_CA_MAIL: {
			SystemProperties.set("persist.sys.novel.newMail", "1");
			break;
		}
		case MESSAGE_SUMA_CA_MAIL: {
			SystemProperties.set("persist.sys.suma.newMail", "1");
			break;
		}
		case MESSAGE_DVN_CA_MAIL: {
			SystemProperties.set("persist.sys.dvn.newMail", "1");
			break;
		}
		}
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

		switch (caType) {
		case MESSAGE_NOVEL_CA_MAIL: {
			SystemProperties.set("persist.sys.novel.newMail", "2");
			break;
		}
		case MESSAGE_SUMA_CA_MAIL: {
			SystemProperties.set("persist.sys.suma.newMail", "2");
			break;
		}
		case MESSAGE_DVN_CA_MAIL: {
			SystemProperties.set("persist.sys.dvn.newMail", "2");
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

}
