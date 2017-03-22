package com.changhong.app.ca;

import com.changhong.app.dtv.R;
import com.changhong.app.dtv.SysApplication;
import com.changhong.dvb.CA;
import com.changhong.dvb.DVB;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

public class CaLockService extends Activity {
    CA thisCa=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ca_lockservice);
		SysApplication.getInstance().addActivity(this);
		thisCa = DVB.getManager().getCaInstance();

	}

	
	@Override
	public boolean onKeyDown(int arg0, KeyEvent arg1) {
		return true;
	}
	
	

}
