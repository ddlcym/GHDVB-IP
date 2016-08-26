package com.changhong.app.ca;

import com.changhong.app.dtv.R;
import com.changhong.dvb.CA;
import com.changhong.dvb.DVB;
import com.changhong.dvb.DVBManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

public class CAMotherChildCopy extends Activity{

	private TextView txtCAcopyStatus;
	private DVBManager dvbManager;
	private CA thisCa;
	boolean isReadMotherInfo = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camotherchild);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		dvbManager=DVB.getManager();
		thisCa=dvbManager.getCaInstance();
		txtCAcopyStatus = (TextView)findViewById(R.id.cacopystatus);
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch(keyCode){
		
		case KeyEvent.KEYCODE_VOLUME_DOWN: {

			return true;
		}
		case KeyEvent.KEYCODE_VOLUME_UP: {

			return true;
		}
		case KeyEvent.KEYCODE_VOLUME_MUTE: {
			return true;
		}
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if(!isReadMotherInfo){
				//读母卡
				int status = thisCa.readFeedDataFromMotherCard();
				if(status==0){
					txtCAcopyStatus.setText("请插入要配对的子卡");
					isReadMotherInfo = true;
				}else{
					txtCAcopyStatus.setText("配对失败，请插入要配对的母卡！");
					isReadMotherInfo = false;
				}
			}else{
				//写子卡
				int status = thisCa.writeFeedDataFromMotherCard();
				if(status==0){
					txtCAcopyStatus.setText("恭喜，配对成功。请插入其他子卡！");
				}else{
					txtCAcopyStatus.setText("配对失败，请确认插入的是要配对的子卡！");
				}
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}


}
