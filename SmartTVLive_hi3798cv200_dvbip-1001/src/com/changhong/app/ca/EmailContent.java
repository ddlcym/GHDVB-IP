package com.changhong.app.ca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.changhong.app.dtv.R;
import com.changhong.dvb.CA;
import com.changhong.dvb.CA_MailContent;
import com.changhong.dvb.CA_Mail_Head;
import com.changhong.dvb.DVB;
import com.changhong.dvb.DVBManager;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL;
import com.changhong.dvb.ProtoMessage.DVB_CA_TYPE;

public class EmailContent extends Activity {

	private TextView headView, contentView;
	private DVBManager dvbManager;
	private CA thisCa;
	private BroadcastReceiver mReceiver = null;
	private RelativeLayout nextMailBtn=null;
	private ScrollView mScrollView=null;
	List<NewMail> newMailList = new ArrayList<NewMail>();
	private int curMailIndex = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yj_content_z);
		dvbManager = DVB.getManager();
		thisCa = dvbManager.getCaInstance();
		curMailIndex = 0;
		initViewAndEvent();
	}

	private void initViewAndEvent() {

		mScrollView=(ScrollView) findViewById(R.id.sv_lmdnrnr_z);
		headView = (TextView) findViewById(R.id.yj_lmdnrbt_z);
		contentView = (TextView) findViewById(R.id.yj_lmdnrnr_z);
		nextMailBtn =(RelativeLayout) findViewById(R.id.footer_next);		
		updateNewEmailData();
		showNewMail();
	}

	private void showNewMail() {

		if (newMailList.size() <= 0) {
			finish();
			return;
		}
		curMailIndex=newMailList.size()-1;
		NewMail mail = newMailList.get(curMailIndex);
		if (null != mail.title) {
			headView.setText(mail.title);
		}

		// 获取邮件信息
		Log.i("Email_z", "mailContent----------------------->miId=" + mail.id);
		if (mail.id >= 0) {
			CA_MailContent mailContent = thisCa.getMailContent(mail.id);

			if (mailContent != null) {
				DVB_CA_TYPE curCaType = thisCa.getCurType();
				if (DVB_CA_TYPE.CA_NOVEL == curCaType) {
					contentView
							.setText(mailContent.mCaMailContentNovel.mContent);
				} else if (DVB_CA_TYPE.CA_SUMA == curCaType) {
					contentView
							.setText(mailContent.mCaMailContentSuma.mContent);
				}
			}
		}
	}

	private void updateNewEmailData() {

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

					int isReaded = mailHeads[i].mCaMailHeadNovel.mReaded;
					if (isReaded != 0)continue;
					NewMail newmail = new NewMail();
					newmail.id = mailHeads[i].mCaMailHeadNovel.miId;
					newmail.title = mailHeads[i].mCaMailHeadNovel.mTitile;
					if(!isExist(newmail.id))newMailList.add(newmail);
				} else if (DVB_CA_TYPE.CA_SUMA == curCaType) {
					if (mailHeads[i] == null) {
						Log.i("Email_z",
								" getEmailData()----->mailHeads[i]   is null--->");
					}

					int isReaded = mailHeads[i].mCaMailHeadSuma.mReaded;
					if (isReaded != 0)
						continue;

					NewMail newmail = new NewMail();
					newmail.id = mailHeads[i].mCaMailHeadSuma.miId;
					newmail.title = mailHeads[i].mCaMailHeadSuma.mTitile;
					if(!isExist(newmail.id))newMailList.add(newmail);

				}

			}
		} else {
			Log.i("Email_z"," getEmailData()----->mailHeads  is null -----");
		}
		
//		showNextMailBtn();
	}
	
	
	private void showNextMailBtn(){
		if (newMailList.size() > 1) {
			nextMailBtn.setVisibility(View.VISIBLE);
		}
		Log.i("Email_z"," newMailList.size()  = "+newMailList.size());

	}
	
	private boolean isExist(int mailID){
		for(NewMail mail:newMailList){
			if(null != mail && mailID==mail.id){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean isAction = false;
		switch (keyCode) {

		case KeyEvent.KEYCODE_CHANNEL_UP:
//			if (curMailIndex > 0) {
//				curMailIndex--;
//				showNewMail();
//				isAction = true;
//			}		
			pageUp();
			break;
		case KeyEvent.KEYCODE_CHANNEL_DOWN:
//			if (curMailIndex < newMailList.size() - 1) {
//				curMailIndex++;
//				showNewMail();
//				isAction = true;
//			}
			pageDown();
			break;
		}
		return isAction ? true : super.onKeyDown(keyCode, event);
	}
	
	
	public void pageUp(){
		int x=mScrollView.getScrollX();
		int y=mScrollView.getScrollY();
		int height=contentView.getHeight();
		int offH=Math.min(y, height);
		if(y>0 ){
		   mScrollView.scrollTo(x, y-offH);
		}
	}
	
    public void pageDown(){
    	int x=mScrollView.getScrollX();
		int y=mScrollView.getScrollY();
		int height=contentView.getHeight();
	    mScrollView.scrollTo(x, height);

	}
	
	/***************************************** 注册 广播 接收新邮件消息 *****************************************************************/
	/**
	 * 注册CA设置命令接收广播
	 * */
	private void registerNewMailReceiver() {
		if (null == mReceiver) {
			IntentFilter mFilter = new IntentFilter();
			mFilter.addAction(CaConfig.CA_MSG_SHOW_NEW_MAIL);
			mReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					updateNewEmailData();
				}
			};
			registerReceiver(mReceiver, mFilter);
		}
	}
	
	
	
	
	
	

	@Override
	protected void onResume() {
		super.onResume();
		registerNewMailReceiver();

	}

	@Override
	protected void onPause() {
        if(null !=mReceiver){
        	unregisterReceiver(mReceiver);
        	mReceiver=null;
        }
		super.onPause();
	}

	class NewMail {
		int id;
		String title;
	}
}
