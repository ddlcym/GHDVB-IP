package com.changhong.app.dtv;

import com.changhong.dvb.CA;
import com.changhong.dvb.DVB;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class testCA extends Activity{
    CA thisCa=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.ca_test);	
		thisCa = DVB.getManager().getCaInstance();
	}

	@Override
	public boolean onKeyDown(int arg0, KeyEvent arg1) {

          boolean isAction=false;
		if(arg0 == KeyEvent.KEYCODE_1){
			thisCa.setRating("000000", 1);
			isAction=true;
		}else if(arg0 == KeyEvent.KEYCODE_2){
			thisCa.setRating("000000", 2);
			isAction=true;
		}else if(arg0 == KeyEvent.KEYCODE_3){
			thisCa.setRating("000000", 3);
			isAction=true;
		}else if(arg0 == KeyEvent.KEYCODE_4){
			thisCa.setRating("000000", 4);
			isAction=true;
		}else if(arg0 == KeyEvent.KEYCODE_5){
			thisCa.setRating("000000", 5);
			isAction=true;
		}else if(arg0 == KeyEvent.KEYCODE_6){
			thisCa.setRating("000000", 6);
			isAction=true;
		}else if(arg0 == KeyEvent.KEYCODE_7){
			thisCa.setRating("000000", 7);
			isAction=true;
		}else if(arg0 == KeyEvent.KEYCODE_8){
			thisCa.setRating("000000", 8);
			isAction=true;
		}else if(arg0 == KeyEvent.KEYCODE_9){
			thisCa.setRating("000000", 9);
			isAction=true;
		}else if(arg0 == KeyEvent.KEYCODE_0){
			thisCa.setRating("000000", 0);
			isAction=true;
		}else if(arg0 == KeyEvent.KEYCODE_ALT_RIGHT){
			thisCa.setRating("000000", 10);
			isAction=true;
		}						
		return isAction?true:super.onKeyDown(arg0, arg1);
	}
	
	
	
}
