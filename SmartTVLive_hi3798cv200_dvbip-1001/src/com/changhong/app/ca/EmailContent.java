package com.changhong.app.ca;

import java.util.HashMap;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.changhong.app.dtv.R;
import com.changhong.dvb.CA;
import com.changhong.dvb.CA_MailContent;
import com.changhong.dvb.CA_Mail_Head;
import com.changhong.dvb.DVB;
import com.changhong.dvb.DVBManager;
import com.changhong.dvb.ProtoMessage.DVB_CA_TYPE;

public class EmailContent extends Activity {

	private int miId;
	private String miTitle;
	private TextView headView, contentView;
	private DVBManager dvbManager;
	private CA thisCa;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yj_content_z);
		Intent intent = getIntent();
		dvbManager = DVB.getManager();
		thisCa = dvbManager.getCaInstance();
		initViewAndEvent();
	}

	private void initViewAndEvent() {

		headView = (TextView) findViewById(R.id.yj_lmdnrbt_z);
		contentView = (TextView) findViewById(R.id.yj_lmdnrnr_z);
		showNewMail();
	}

	private void showNewMail() {
		HashMap<String, Object> temMap = getNewEmailData();
		if (null == temMap || temMap.isEmpty()) {
			finish();
			return;
		}
		miId = (Integer) temMap.get("miId");
		miTitle = (String) temMap.get("title");

		if (null != miTitle)
			headView.setText(miTitle);

		// 获取邮件信息
		Log.i("Email_z", "mailContent----------------------->miId=" + miId);
		if (miId >= 0) {
			CA_MailContent mailContent = thisCa.getMailContent(miId);

			if (mailContent != null) {
				DVB_CA_TYPE curCaType = thisCa.getCurType();
				if (DVB_CA_TYPE.CA_NOVEL == curCaType) {
					for (int i = 0; i < 50; i++) {				
					  mailContent.mCaMailContentNovel.mContent+="ceshiceshiceshiceshiceshiceshiceshiceshiceshiceshiceshiceshiceshiceshi";
					}
					contentView
							.setText(mailContent.mCaMailContentNovel.mContent);
				} else if (DVB_CA_TYPE.CA_SUMA == curCaType) {
					contentView
							.setText(mailContent.mCaMailContentSuma.mContent);
				}
			}
		}
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
					if (flag == 0)
						continue;

					hashMap.put("miId", mailHeads[i].mCaMailHeadNovel.miId);
					hashMap.put("title", mailHeads[i].mCaMailHeadNovel.mTitile);
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
					break;
				}

			}
		} else {
			Log.i("Email_z",
					" getEmailData()----->mailHeads  is null -----mailHeads.length");
		}

		return hashMap;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean isAction = false;
		switch (keyCode) {
		
		case KeyEvent.KEYCODE_PAGE_UP:
		case KeyEvent.KEYCODE_PAGE_DOWN:
		case KeyEvent.KEYCODE_CHANNEL_UP:
		case KeyEvent.KEYCODE_CHANNEL_DOWN:
			contentView.scrollTo(-900, -450);
			isAction = true;
			break;
		}
		return isAction ? true : super.onKeyDown(keyCode, event);
	}
}
