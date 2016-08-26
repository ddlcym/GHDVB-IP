package com.changhong.app.ca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.changhong.app.dtv.MainMenu_z.homeReceiver;
import com.changhong.app.dtv.R;
import com.changhong.dvb.CA;
import com.changhong.dvb.CA_Entitle;
import com.changhong.dvb.CA_Operator;
import com.changhong.dvb.DVB;
import com.changhong.dvb.DVBManager;
import com.changhong.dvb.ProtoMessage.DVB_CA_TYPE;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.TextView;

public class CAoperator extends Activity {

	private DVBManager moDvbManager = null;
	private CA thisCa = null;
	
	private int total = 0;
	
	TextView txtOperator,txtTotal;
	ListView listOperator;
	String operatorName = "";
	String operatorID = "";
	List<HashMap<String,String>> OperatorList = new ArrayList<HashMap<String,String>>();
	
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
		super.onCreate(savedInstanceState);
		setContentView(R.layout.caoperator);
		moDvbManager = DVB.getManager();
        thisCa = moDvbManager.getCaInstance();
        operatorID = this.getIntent().getStringExtra("id");
        operatorName = this.getIntent().getStringExtra("name");
        CA_Operator[] operators = thisCa.getOperators();
        if(operators!=null){
//        	for(int i = 0;i<operators.length;i++){
        		if(thisCa.getCurType()==DVB_CA_TYPE.CA_NOVEL){
        			Log.e("CA","Novel>>>"+operatorName+"  id>>>"+operatorID);
        			CA_Entitle[] mCA_Entitles=thisCa.getEntitles(Integer.parseInt(operatorID));
        			if(mCA_Entitles!=null&&mCA_Entitles.length>0){
        				for(int id=0;id<mCA_Entitles.length;id++){
        					if(mCA_Entitles[id]!=null){
        						HashMap<String,String> itemData = new HashMap<String,String>();
            					Log.e("CA","mCA_Entitles>>>"+mCA_Entitles[id].mCaEntitleNovel.mBeginTime.convertSecondsToDateTimeByString("YYYY/MM/DD HH:MM:SS"));
                				Log.e("CA","mCA_Entitles>>>"+mCA_Entitles[id].mCaEntitleNovel.mExpiredTime.convertSecondsToDateTimeByString("YYYY/MM/DD HH:MM:SS"));
                				itemData.put("Name", "");
                				itemData.put("StartTime", ""+mCA_Entitles[id].mCaEntitleNovel.mBeginTime.convertSecondsToDateTimeByString("YYYY/MM/DD HH:MM:SS"));
                				itemData.put("ExpireTime", ""+mCA_Entitles[id].mCaEntitleNovel.mExpiredTime.convertSecondsToDateTimeByString("YYYY/MM/DD HH:MM:SS"));
                				itemData.put("EntitleTime", "");
                				OperatorList.add(itemData);
        					}
        				}
        			}
        		}else{
    				Log.e("CA","Suma>>>"+operatorName+"  id>>>"+operatorID);
        			CA_Entitle[] mCA_Entitles=thisCa.getEntitles(Integer.parseInt(operatorID));
        			if(mCA_Entitles!=null&&mCA_Entitles.length>0){
        				for(int id=0;id<mCA_Entitles.length;id++){
        					if(mCA_Entitles[id]!=null){
        						HashMap<String,String> itemData = new HashMap<String,String>();
            					Log.e("CA","mCA_Entitles>>>"+mCA_Entitles[id].mCaEntitleSuma.mName);
                				Log.e("CA","mCA_Entitles>>>"+mCA_Entitles[id].mCaEntitleSuma.mBeginTime.convertSecondsToDateTimeByString("YYYY/MM/DD HH:MM:SS"));
                				Log.e("CA","mCA_Entitles>>>"+mCA_Entitles[id].mCaEntitleSuma.mExpiredTime.convertSecondsToDateTimeByString("YYYY/MM/DD HH:MM:SS"));
                				Log.e("CA","mCA_Entitles>>>"+mCA_Entitles[id].mCaEntitleSuma.mEntitleTime.convertSecondsToDateTimeByString("YYYY/MM/DD HH:MM:SS"));
                				itemData.put("Name", ""+mCA_Entitles[id].mCaEntitleSuma.mName);
                				itemData.put("StartTime", ""+mCA_Entitles[id].mCaEntitleSuma.mBeginTime.convertSecondsToDateTimeByString("YYYY/MM/DD HH:MM:SS"));
                				itemData.put("ExpireTime", ""+mCA_Entitles[id].mCaEntitleSuma.mExpiredTime.convertSecondsToDateTimeByString("YYYY/MM/DD HH:MM:SS"));
                				itemData.put("EntitleTime", ""+mCA_Entitles[id].mCaEntitleSuma.mEntitleTime.convertSecondsToDateTimeByString("YYYY/MM/DD HH:MM:SS"));
                				OperatorList.add(itemData);
        					}
        				}
        			}
        		}
//        	}
        }
        
		initView();
		
		OperatorAdapter mOperatorAdapter = new OperatorAdapter(CAoperator.this, OperatorList);
		listOperator.setAdapter(mOperatorAdapter);
		
		
		if(operators!=null){
			txtOperator.setText("运营商名称："+operatorName);
			txtTotal.setText("总的授权个数："+OperatorList.size());
		}else{
			txtOperator.setText("运营商名称："+operatorName);
			txtTotal.setText("总的授权个数：0");
		}
		
		mHomeReceiver = new homeReceiver();
		registerReceiver(mHomeReceiver, new IntentFilter("HOME_PRESSED"));
	}

	private void initView() {
		txtOperator = (TextView)findViewById(R.id.operator);
		txtTotal = (TextView)findViewById(R.id.total);
		listOperator = (ListView)findViewById(R.id.listoperators);
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
