package com.changhong.app.ca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class CAoperatorInfo extends Activity {

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
		setContentView(R.layout.caoperatorinfo);
		moDvbManager = DVB.getManager();
        thisCa = moDvbManager.getCaInstance();
        CA_Operator[] operators = thisCa.getOperators();
        if(operators!=null){
        	for(int i = 0;i<operators.length;i++){
        		Boolean isAdd = false;
        		HashMap<String,String> itemData = new HashMap<String,String>();
        		if(thisCa.getCurType()==DVB_CA_TYPE.CA_NOVEL){
        			if(operators[i]!=null&&operators[i].mCaOperatorNovel!=null){
        				operatorName = operators[i].mCaOperatorNovel.descriptor;
        				operatorID = ""+operators[i].mCaOperatorNovel.id;
            			Log.e("CA","Novel>>>"+operators[i].mCaOperatorNovel.descriptor+"  id>>>"+operators[i].mCaOperatorNovel.id);
            			itemData.put("operator", operatorName);
            			itemData.put("operatorid", operatorID);
            			isAdd = true;
        			}
        		}else{
        			if(operators[i]!=null&&operators[i].mCaOperatorSuma!=null){
        				Log.e("CA","Suma>>>"+operators[i].mCaOperatorSuma.descriptor+"  id>>>"+operators[i].mCaOperatorSuma.id);
        				operatorName = operators[i].mCaOperatorSuma.descriptor;
        				operatorID = ""+operators[i].mCaOperatorSuma.id;
        				itemData.put("operator", operatorName);
            			itemData.put("operatorid", operatorID);
            			isAdd = true;
        			}
        		}
        		if(isAdd){
        			OperatorList.add(itemData);
        		}
        	}
        }
        
		initView();
		mHomeReceiver = new homeReceiver();
		registerReceiver(mHomeReceiver, new IntentFilter("HOME_PRESSED"));
		
		OperatorInfoAdapter mOperatorAdapter = new OperatorInfoAdapter(CAoperatorInfo.this, OperatorList);
		listOperator.setAdapter(mOperatorAdapter);
		
		
		if(operators!=null){
			txtTotal.setText("运营商个数："+OperatorList.size());
		}else{
			txtTotal.setText("运营商个数：0");
		}
	}

	private void initView() {
		txtOperator = (TextView)findViewById(R.id.operator);
		txtTotal = (TextView)findViewById(R.id.total);
		listOperator = (ListView)findViewById(R.id.listoperators);
		
		listOperator.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(CAoperatorInfo.this,CAoperator.class);
				intent.putExtra("id", OperatorList.get(position).get("operatorid"));
				intent.putExtra("name", OperatorList.get(position).get("operator"));
				startActivity(intent);
				
			}
		});
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getWindow().getDecorView().setVisibility(View.VISIBLE);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getWindow().getDecorView().setVisibility(View.INVISIBLE);
	}

}
