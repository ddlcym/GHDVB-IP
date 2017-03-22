package com.changhong.app.dtv;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import com.changhong.app.ca.CaMailInfor;
import com.changhong.dvb.CA;
import com.changhong.dvb.CA_MailContent;
import com.changhong.dvb.CA_Mail_Head;
import com.changhong.dvb.DVB;
import com.changhong.dvb.DVBManager;
import com.changhong.dvb.ProtoMessage.DVB_CA_TYPE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LiveMail extends Activity {

	private static String TAG = "livemail";
	private TextView livemail_title, livemail_revdate;
	private TextView livemail_content;
	private CaMailInfor curiMail;
	/** Called when the activity is first created. */
	private final UI_Handler mUiHandler = new UI_Handler(this);
	private static int iSecond = 120;

	private DVBManager dvbManager;
	private CA thisCa;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.livemail);
		dvbManager = DVB.getManager();
		thisCa = dvbManager.getCaInstance();
		Log.i(TAG, "LiveMail -> onCreate");

		curiMail = getNewMail();
		if (curiMail == null) {
			Log.i(TAG, "LiveMail -> mail is null");
			finish();
		}
		initViews(curiMail);
		Log.i(TAG, "LiveMail -> initViews ok");
//		mUiHandler.sendEmptyMessage(0);
	}

	private CaMailInfor getNewMail() {
	
		CaMailInfor mailInfor = null;
		HashMap<String, Object> temMap = getNewEmailData();
		int miId = (Integer) temMap.get("miId");
		String miTitle = (String) temMap.get("title");
		String  date=(String) temMap.get("date");

		// 获取邮件信息
		Log.i("Email_z", "mailContent----------------------->miId=" + miId);
		if (miId >= 0) {
			CA_MailContent mailContent = thisCa.getMailContent(miId);
			if (mailContent != null) {
				mailInfor = new CaMailInfor();
				mailInfor.title=miTitle;
				mailInfor.revDate=date;
				DVB_CA_TYPE curCaType = thisCa.getCurType();
				if (DVB_CA_TYPE.CA_NOVEL == curCaType) {
					mailInfor.content = mailContent.mCaMailContentNovel.mContent;
					
				} else if (DVB_CA_TYPE.CA_SUMA == curCaType) {
					mailInfor.content = mailContent.mCaMailContentSuma.mContent;
				}
			}
		}
		return mailInfor;
	}

	private HashMap<String, Object> getNewEmailData() {

		HashMap<String, Object> hashMap = new HashMap<String, Object>();

		CA_Mail_Head[] mailHeads = thisCa.getAllMailHeads();
		if (mailHeads != null && mailHeads.length > 0) {

			DVB_CA_TYPE curCaType = thisCa.getCurType();
			for (int i = 0; i < mailHeads.length; i++) {

				if (DVB_CA_TYPE.CA_NOVEL == curCaType) {
					if (mailHeads[i] == null) {

						Log.i("Email_z",
								" getEmailData()----->mailHeads[i]   is null--->");
						break;
					}

					int flag = mailHeads[i].mCaMailHeadNovel.mReaded;
					if (flag != 0)
						continue;

					hashMap.put("miId", mailHeads[i].mCaMailHeadNovel.miId);
					hashMap.put("title", mailHeads[i].mCaMailHeadNovel.mTitile);
					hashMap.put("date", mailHeads[i].mCaMailHeadNovel.mCreateTime.convertSecondsToDateTimeByString());

					break;
				} else if (DVB_CA_TYPE.CA_SUMA == curCaType) {
					if (mailHeads[i] == null) {
						Log.i("Email_z",
								" getEmailData()----->mailHeads[i]   is null--->");
					}

					int flag = mailHeads[i].mCaMailHeadSuma.mReaded;
					if (flag == 0)
						continue;
					hashMap.put("miId", mailHeads[i].mCaMailHeadSuma.miId);
					hashMap.put("title", ""
							+ mailHeads[i].mCaMailHeadSuma.mTitile);
					hashMap.put("date", mailHeads[i].mCaMailHeadSuma.mCreateTime.convertSecondsToDateTimeByString());

					break;
				}

			}
		} else {
			Log.i("Email_z",
					" getEmailData()----->mailHeads  is null -----mailHeads.length");
		}

		return hashMap;
	}

	private void initViews(CaMailInfor obj) {
		livemail_title = (TextView) findViewById(R.id.id_livemail_title);
		livemail_revdate = (TextView) findViewById(R.id.id_livemail_revdate);
		livemail_content = (TextView) findViewById(R.id.id_livemail_content);

		livemail_title.setText(obj.title);
		livemail_revdate.setText(obj.revDate + obj.revTime);
		livemail_content.setText(obj.content);
		// obj.content;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	protected void onDestory() {
		super.onDestroy();
	}

//	public void onAttachedToWindow() {
//		this.getWindow().setType(
//				WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
//		super.onAttachedToWindow();
//	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			break;
		}

		return super.onKeyDown(keyCode, event);

	}

	static class UI_Handler extends Handler {
		WeakReference<LiveMail> mActivity;

		UI_Handler(LiveMail activity) {
			mActivity = new WeakReference<LiveMail>(activity);
		}

		@Override
		public void handleMessage(Message msg) {

			LiveMail theActivity = mActivity.get();

			Log.i(TAG, "now second  ->  " + iSecond);
			if (iSecond > 0) {
				theActivity.livemail_revdate.setText("(" + iSecond + "s)");
				theActivity.mUiHandler.sendEmptyMessageDelayed(0, 1000);
				iSecond--;
				if (iSecond == 0) {
					Log.i(TAG, "work ok !");
					theActivity.finish();
				}
			}

		}
	}
}
