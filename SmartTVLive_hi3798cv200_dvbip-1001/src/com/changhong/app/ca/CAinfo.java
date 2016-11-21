package com.changhong.app.ca;

import com.changhong.app.dtv.R;
import com.changhong.dvb.CA;
import com.changhong.dvb.CA_Ac;
import com.changhong.dvb.CA_MotherChildCardInfo;
import com.changhong.dvb.CA_Worktime;
import com.changhong.dvb.DVB;
import com.changhong.dvb.DVBManager;
import com.changhong.dvb.ProtoMessage.DVB_CA_TYPE;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

public class CAinfo extends Activity{
	
	TextView cainfoTextView01,cainfoTextView02,cainfoTextView03,cainfoTextView04,cainfoTextView05,cainfoTextView06,
				cainfoTextView07,cainfoTextView08,cainfoTextView09,cainfoTextView10,cainfoTextView11,cainfoTextView12;
	private DVBManager moDvbManager = null;
	private CA thisCa = null;
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
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cainfo);
		moDvbManager = DVB.getManager();
        thisCa = moDvbManager.getCaInstance();
		initView();
		setview();
		mHomeReceiver = new homeReceiver();
		registerReceiver(mHomeReceiver, new IntentFilter("HOME_PRESSED"));
	}

	private void setview() {
		// TODO Auto-generated method stub
		if(0==thisCa.getSmartCardStatus()){
			cainfoTextView01.setText(thisCa.getSmartCardSn());
			cainfoTextView02.setText(""+thisCa.getCasCosVersion());
			cainfoTextView03.setText(""+toVersionFomat(Integer.toHexString(thisCa.getCaLibVersion())));
			cainfoTextView04.setText(""+thisCa.getCasManuName());
			cainfoTextView05.setText(""+thisCa.getAreaCode());
			if(thisCa.getAreaCodeLocked()){
				cainfoTextView06.setText("锁定");
			}else{
				cainfoTextView06.setText("未锁定");
			}
			if(thisCa.getAreaCodeRcvTime()!=null){
				cainfoTextView07.setText(""+toTimeFomat(thisCa.getAreaCodeRcvTime().miYear)+"/"+toTimeFomat(thisCa.getAreaCodeRcvTime().miMonth)
						+"/"+toTimeFomat(thisCa.getAreaCodeRcvTime().miDay)+" "+toTimeFomat(thisCa.getAreaCodeRcvTime().miHour)+":"+toTimeFomat(thisCa.getAreaCodeRcvTime().miHour)
						+":"+toTimeFomat(thisCa.getAreaCodeRcvTime().miSecond));
			}
			CA_MotherChildCardInfo  mcInfo = thisCa.getMotherCardInfo();
			if(mcInfo!=null){
				if(mcInfo.mCardType == CA_MotherChildCardInfo.CARD_TYPE_CHILD){
					cainfoTextView08.setText("子卡");
				}else if(mcInfo.mCardType == CA_MotherChildCardInfo.CARD_TYPE_MOTHER){
					cainfoTextView08.setText("母卡");
				}else{
					cainfoTextView08.setText("");
				}
				cainfoTextView09.setText(""+mcInfo.mMotherCardSn);
			}
			if(thisCa.getPinCodeLocked()==0){
				cainfoTextView10.setText("未锁定");
			}else{
				cainfoTextView10.setText("锁定");
			}
			cainfoTextView11.setText(""+thisCa.getRating());
			if(thisCa.getCurType()==DVB_CA_TYPE.CA_NOVEL){
				if(thisCa.getWorktime()!=null&&thisCa.getWorktime().mCaWorktimeNovel!=null){
					cainfoTextView12.setText(""+toTimeFomat(thisCa.getWorktime().mCaWorktimeNovel.miStartTimeHour)+":"+toTimeFomat(thisCa.getWorktime().mCaWorktimeNovel.miStartTimeMin)+":"
							+toTimeFomat(thisCa.getWorktime().mCaWorktimeNovel.miStartTimeSec)+" -- "+toTimeFomat(thisCa.getWorktime().mCaWorktimeNovel.miEndTimeHour)+":"+toTimeFomat(thisCa.getWorktime().mCaWorktimeNovel.miEndTimeMin)+
							":"+toTimeFomat(thisCa.getWorktime().mCaWorktimeNovel.miEndTimeSec));
				}
			}else if(thisCa.getCurType()==DVB_CA_TYPE.CA_SUMA){
				if(thisCa.getWorktime()!=null&&thisCa.getWorktime().mCaWorktimeSuma!=null){
					cainfoTextView12.setText(""+toTimeFomat(thisCa.getWorktime().mCaWorktimeSuma.miStartTimeHour)+":"+toTimeFomat(thisCa.getWorktime().mCaWorktimeSuma.miStartTimeMin)+":"
							+toTimeFomat(thisCa.getWorktime().mCaWorktimeSuma.miStartTimeSec)+" -- "+toTimeFomat(thisCa.getWorktime().mCaWorktimeSuma.miEndTimeHour)+":"+toTimeFomat(thisCa.getWorktime().mCaWorktimeSuma.miEndTimeMin)+
							":"+toTimeFomat(thisCa.getWorktime().mCaWorktimeSuma.miEndTimeSec));
				}
			}
		}else{
			cainfoTextView01.setText("未插卡");
			cainfoTextView02.setText("未插卡");
			cainfoTextView03.setText("未插卡");
			cainfoTextView04.setText("未插卡");
			cainfoTextView05.setText("未插卡");
			cainfoTextView06.setText("未插卡");
			cainfoTextView07.setText("未插卡");
			cainfoTextView08.setText("未插卡");
			cainfoTextView09.setText("未插卡");
			cainfoTextView10.setText("未插卡");
			cainfoTextView11.setText("未插卡");
			cainfoTextView12.setText("未插卡");
			Toast.makeText(CAinfo.this, "未插CA卡或CA卡异常！", 2000).show();
		}
	}
	
	private void setView(){
		cainfoTextView01.setText("CA卡数据读取中...");
		cainfoTextView02.setText("CA卡数据读取中...");
		cainfoTextView03.setText("CA卡数据读取中...");
		cainfoTextView04.setText("CA卡数据读取中...");
		cainfoTextView05.setText("CA卡数据读取中...");
		cainfoTextView06.setText("CA卡数据读取中...");
		cainfoTextView07.setText("CA卡数据读取中...");
		cainfoTextView08.setText("CA卡数据读取中...");
		cainfoTextView09.setText("CA卡数据读取中...");
		cainfoTextView10.setText("CA卡数据读取中...");
		cainfoTextView11.setText("CA卡数据读取中...");
		cainfoTextView12.setText("CA卡数据读取中...");
	}
	private void initView() {
		// TODO Auto-generated method stub
		cainfoTextView01 = (TextView)findViewById(R.id.cainfo01);
		cainfoTextView02 = (TextView)findViewById(R.id.cainfo02);
		cainfoTextView03 = (TextView)findViewById(R.id.cainfo03);
		cainfoTextView04 = (TextView)findViewById(R.id.cainfo04);
		cainfoTextView05 = (TextView)findViewById(R.id.cainfo05);
		cainfoTextView06 = (TextView)findViewById(R.id.cainfo06);
		cainfoTextView07 = (TextView)findViewById(R.id.cainfo07);
		cainfoTextView08 = (TextView)findViewById(R.id.cainfo08);
		cainfoTextView09 = (TextView)findViewById(R.id.cainfo09);
		cainfoTextView10 = (TextView)findViewById(R.id.cainfo10);
		cainfoTextView11 = (TextView)findViewById(R.id.cainfo11);
		cainfoTextView12 = (TextView)findViewById(R.id.cainfo12);
		
		IntentFilter intent = new IntentFilter();
		intent.addAction("chots.action.CaCard");
		registerReceiver(this.CaStatusReceiver, intent);
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
		case KeyEvent.KEYCODE_BACK:
			finish();
			break;

		}
		return super.onKeyDown(keyCode, arg1);
	}
	
	private static String toTimeFomat(int time){
		String timeString = "";
		if(time<10){
			timeString = "0"+time;
		}else{
			timeString = ""+time;
		}
		return timeString;
	}
	
	private static String  toVersionFomat(String HexString){
		String VersionString = "";
		if(HexString == null||HexString.equals("")||HexString.equals("null")){
			return "";
		}
		Log.e("CA", "->toVersionFomat1:"+HexString);
		int length = HexString.length();
		if(length<8){
			for(int i=0;i<(8-length);i++){
				HexString = "0"+HexString;
			}
		}
		Log.e("CA", "->toVersionFomat2:"+HexString);
		String v1 = ""+Integer.parseInt(HexString.substring(0, 2),16);
		String v2 = ""+Integer.parseInt(HexString.substring(2, 4),16);
		String v3 = ""+Integer.parseInt(HexString.substring(4, 6),16);
		String v4 = ""+Integer.parseInt(HexString.substring(6, 8),16);
		VersionString = v1+"."+v2+"."+v3+"."+v4;
		return VersionString;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(CaStatusReceiver);
		unregisterReceiver(mHomeReceiver);
	}

	private BroadcastReceiver CaStatusReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("chots.action.CaCard")){
				int status = intent.getIntExtra("card_in", -1);
				Log.e("CA", "CAcard status————>"+status);
				if(intent.getIntExtra("card_in", -1)==10){
					setView();
					Toast.makeText(context, "CA卡数据读取中，请稍后...", 5000).show();
				}else{
					setview();
				}
			}
		}
		
	};

}
