package com.changhong.app.ca;

import com.changhong.app.dtv.R;
import com.changhong.dvb.CA;
import com.changhong.dvb.CA_Worktime;
import com.changhong.dvb.DVB;
import com.changhong.dvb.DVBManager;
import com.changhong.dvb.ProtoMessage.DVB_CA_TYPE;

import android.app.Activity;
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

public class CAworkTime extends Activity implements OnClickListener {
	private EditText startH;
	private EditText startM;
	private EditText endH;
	private EditText endM;
	private EditText pwdText;
	private Button sureBtn;

	private DVBManager dvbManager;
	private CA thisCa;
private homeReceiver mHomeReceiver;
	
	public class homeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			String strAction = arg1.getAction();

			if (strAction.equals("HOME_PRESSED")) {
				finish();
			}

		}

	}
	
	public void onDestroy() {
		unregisterReceiver(mHomeReceiver);
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.caworktime);
		initView();
		mHomeReceiver = new homeReceiver();
		registerReceiver(mHomeReceiver, new IntentFilter("HOME_PRESSED"));

	}

	private void initView() {
		// TODO Auto-generated method stub
		dvbManager = DVB.getManager();
		thisCa = dvbManager.getCaInstance();
		startH = (EditText) findViewById(R.id.worktime_input_starthour);
		startM = (EditText) findViewById(R.id.worktime_input_startmin);
		endH = (EditText) findViewById(R.id.worktime_input_endhour);
		endM = (EditText) findViewById(R.id.worktime_input_endmin);
		pwdText = (EditText) findViewById(R.id.worktime_input_code);
		sureBtn = (Button) findViewById(R.id.button_worktime_sure);
		sureBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		String startHour = startH.getText().toString();
		String startMinute = startM.getText().toString();
		String endHour = endH.getText().toString();
		String endMinute = endM.getText().toString();
		String pwd = pwdText.getText().toString();
		int sHour = Integer.parseInt(startHour);
		int sMinute = Integer.parseInt(startMinute);
		int eHour = Integer.parseInt(endHour);
		int eMinute = Integer.parseInt(endMinute);
		if ((sHour >= 0 && sHour < 24) && (sMinute >= 0 && sMinute < 60)
				&& (eHour >= 0 && eHour < 24) && (eMinute >= 0 && eMinute < 60)) {
			CA_Worktime workTime = new CA_Worktime();
			DVB_CA_TYPE curCaType = thisCa.getCurType();
			if (DVB_CA_TYPE.CA_NOVEL == curCaType) {
				workTime.mCaWorktimeNovel.miStartTimeHour = sHour;
				workTime.mCaWorktimeNovel.miStartTimeMin = sMinute;
				workTime.mCaWorktimeNovel.miStartTimeSec = 0;
				workTime.mCaWorktimeNovel.miEndTimeHour = eHour;
				workTime.mCaWorktimeNovel.miEndTimeMin = eMinute;
				workTime.mCaWorktimeNovel.miEndTimeSec = 0;
			} else if (DVB_CA_TYPE.CA_SUMA == curCaType) {
				workTime.mCaWorktimeSuma.miStartTimeHour = sHour;
				workTime.mCaWorktimeSuma.miStartTimeMin = sMinute;
				workTime.mCaWorktimeSuma.miStartTimeSec = 0;
				workTime.mCaWorktimeSuma.miEndTimeHour = eHour;
				workTime.mCaWorktimeSuma.miEndTimeMin = eMinute;
				workTime.mCaWorktimeSuma.miEndTimeSec = 0;
			}
			int result = thisCa.setWorkTime(pwd, workTime);
			if (result == 0) {
				finish();
				Toast.makeText(this, "工作时段设置成功", Toast.LENGTH_SHORT).show();
			}

			else {
				Toast.makeText(this, "工作时段设置失败，检查密码是否正确", Toast.LENGTH_SHORT)
						.show();
			}

		} else {
			Toast.makeText(this, "时间设置错误", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent arg1) {

		switch (keyCode) {

		case KeyEvent.KEYCODE_VOLUME_DOWN: {

			return true;
		}
		case KeyEvent.KEYCODE_VOLUME_UP: {

			return true;
		}
		case KeyEvent.KEYCODE_VOLUME_MUTE: {
			return true;
		}

		}
		return super.onKeyDown(keyCode, arg1);
	}

}
