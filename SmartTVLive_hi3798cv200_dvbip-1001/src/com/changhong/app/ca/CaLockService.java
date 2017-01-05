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
			if(arg0 == KeyEvent.KEYCODE_1){
				thisCa.setRating("000000", 1);
			}else if(arg0 == KeyEvent.KEYCODE_2){
				thisCa.setRating("000000", 2);
			}else if(arg0 == KeyEvent.KEYCODE_3){
				thisCa.setRating("000000", 3);
			}else if(arg0 == KeyEvent.KEYCODE_4){
				thisCa.setRating("000000", 4);
			}else if(arg0 == KeyEvent.KEYCODE_5){
				thisCa.setRating("000000", 5);
			}else if(arg0 == KeyEvent.KEYCODE_6){
				thisCa.setRating("000000", 6);
			}else if(arg0 == KeyEvent.KEYCODE_7){
				thisCa.setRating("000000", 7);
			}else if(arg0 == KeyEvent.KEYCODE_8){
				thisCa.setRating("000000", 8);
			}else if(arg0 == KeyEvent.KEYCODE_9){
				thisCa.setRating("000000", 9);
			}else if(arg0 == KeyEvent.KEYCODE_0){
				thisCa.setRating("000000", 0);
			}else if(arg0 == KeyEvent.KEYCODE_ALT_RIGHT){
				thisCa.setRating("000000", 10);
			}else if(arg0 == KeyEvent.KEYCODE_DPAD_DOWN){
				  Intent mIntentCa = new Intent("com.chots.app.ca.change.status");
					sendBroadcast(mIntentCa);	
				
			}else if(arg0 == KeyEvent.KEYCODE_DPAD_UP){
				  Intent mIntentCa = new Intent("com.chots.app.ca.change.status");
					sendBroadcast(mIntentCa);	
			}
		return true;
	}
	
	

}
