package com.changhong.app.ca;

import com.changhong.dvb.CA;
import com.changhong.dvb.DVB;
import com.changhong.dvb.DVBManager;
import com.changhong.app.dtv.R;
import com.changhong.app.dtv.SysApplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CABase extends Activity implements OnClickListener{

	protected DVBManager dvbManager;
	protected CA thisCa;
	private homeReceiver mHomeReceiver = null;  //返回主页
	protected EditText pinVertify=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
	}

	protected void initViewAndData() {
		dvbManager = DVB.getManager();
		thisCa = dvbManager.getCaInstance();
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		if(null == mHomeReceiver){
			mHomeReceiver = new homeReceiver();
			registerReceiver(mHomeReceiver, new IntentFilter("HOME_PRESSED"));
		}
	}

	
	@Override
	protected void onPause() {
		if(null != mHomeReceiver){
			unregisterReceiver(mHomeReceiver);
			mHomeReceiver=null;
		}
		super.onPause();		
	}

	
	public void onDestroy() {
		super.onDestroy();
	}
	
	
	
	public class homeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			String strAction = arg1.getAction();

			if (strAction.equals("HOME_PRESSED")) {
				finish();
			}
		}

	}



	@Override
	public void onClick(View arg0) {
		
	}

	@Override
	public boolean onKeyDown(int arg0, KeyEvent arg1) {
		// TODO Auto-generated method stub
		return super.onKeyDown(arg0, arg1);
	}

	
	
	protected void onSubmit(String pinCode ){	
		
	}

	

}
