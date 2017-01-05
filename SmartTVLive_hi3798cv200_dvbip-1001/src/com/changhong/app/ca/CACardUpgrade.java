package com.changhong.app.ca;

import com.changhong.app.dtv.R;
import com.changhong.dvb.CA;
import com.changhong.dvb.DVB;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CACardUpgrade extends CaLockService {

	private ProgressBar mProgressBar;;
	private BroadcastReceiver mReceiver = null;
	private int mType= -1;
    CA thisCa=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ca_card_upgrate);
		initViewAndData();
		thisCa = DVB.getManager().getCaInstance();
	}

	protected void initViewAndData() {
		mProgressBar = (ProgressBar) findViewById(R.id.ca_cardupgrate_progressBar);

		int type = getIntent().getIntExtra("type", 0);
		int percent = getIntent().getIntExtra("percent", -1);
		mProgressBar.setMax(100);
		mProgressBar.setProgress(percent);
//		showNotice(type,percent);
	}
	
	
	
	private void showNotice(int type, int percent){
		if(type == mType )return;
        int resID=(0==type)?R.string.ca_progressstrip_dataloadown:R.string.ca_progressstrip_upgrate_progress;
        if(1== type && 100 == percent)resID=R.string.ca_progressstrip_upgrate_success;
		((TextView)findViewById(R.id.ca_cardupgrate_notice)).setText(resID);	
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerCaReceiver();
	}

	@Override
	protected void onPause() {
		if (null != mReceiver) {
			unregisterReceiver(mReceiver);
		}
		super.onPause();
	}

	/***************************************** 注册 广播 接收CA消息 *****************************************************************/
	/**
	 * 注册CA设置命令接收广播
	 * */
	private void registerCaReceiver() {
		if (null == mReceiver) {
			IntentFilter mFilter = new IntentFilter();
			mFilter.addAction(CaConfig.CA_MSG_CARD_UPGRATE);
			mReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String action = intent.getAction();
					if (CaConfig.CA_MSG_CARD_UPGRATE.equals(action)) {
						int type = getIntent().getIntExtra("type", -1);
						int percent = intent.getIntExtra("percent", -1);
						String msgType = intent.getStringExtra("msgType");

						if (msgType.equals("hide")) {
							CACardUpgrade.this.finish();
						} else {
							if (percent > 0 && percent <= 100) {
								mProgressBar.setProgress(percent);
							}
//							showNotice(type, percent);
						}
					}
				}
			};
			registerReceiver(mReceiver, mFilter);
		}
	}

	
	
	@Override
	public boolean onKeyDown(int arg0, KeyEvent arg1) {

          boolean isAction=false;
		if(arg0 == KeyEvent.KEYCODE_8){
			thisCa.setRating("000000", 8);
			isAction=true;
		}else if(arg0 == KeyEvent.KEYCODE_9){
			thisCa.setRating("000000", 9);
			isAction=true;
		}					
		return isAction?true:false;
	}
	
	
}
