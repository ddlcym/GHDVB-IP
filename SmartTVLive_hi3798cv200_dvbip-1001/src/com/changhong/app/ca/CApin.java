package com.changhong.app.ca;

import com.changhong.app.dtv.R;
import com.changhong.dvb.CA;
import com.changhong.dvb.DVB;
import com.changhong.dvb.DVBManager;

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

public class CApin extends Activity implements OnClickListener{

	private EditText oldPwd;
	private EditText newPwd;
	private EditText newSurePwd;
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
		setContentView(R.layout.capin);
		initView();
		mHomeReceiver = new homeReceiver();
		registerReceiver(mHomeReceiver, new IntentFilter("HOME_PRESSED"));
	}

	private void initView() {
		// TODO Auto-generated method stub
		dvbManager=DVB.getManager();
		thisCa=dvbManager.getCaInstance();
		oldPwd=(EditText)findViewById(R.id.pin_input_oldcode);
		newPwd=(EditText)findViewById(R.id.pin_input_newcode1);
		newSurePwd=(EditText)findViewById(R.id.pin_input_newcode2);
		sureBtn=(Button)findViewById(R.id.button_pin_sure);
		sureBtn.setOnClickListener(this);
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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		String oldPwdStr=oldPwd.getText().toString();
		String newPwdStr=newPwd.getText().toString();
		String newSurePwdStr=newSurePwd.getText().toString();
		if(newPwdStr.length()!=0&&newSurePwdStr.length()!=0)
		{
			if(newPwdStr.length()<=8&&newSurePwdStr.length()<=8){
				if(newPwdStr.equals(newSurePwdStr))
				{
					int result = thisCa.setNewPinCode(oldPwdStr,newPwdStr);
					if(result==0)
					{
						Toast.makeText(this, "PIN码修改成功！", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(this, "PIN码修改失败！", Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Toast.makeText(this, "新密码两次输入不一致！", Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(this, "密码最多输入8位！", Toast.LENGTH_SHORT).show();
			}
		}
		else{
			Toast.makeText(this, "密码输入不能为空", Toast.LENGTH_SHORT).show();
		}
	}

}
