package com.changhong.app.ca;

import com.changhong.app.dtv.R;
import com.changhong.dvb.CA_IppvProgramInfo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;



public class CaMainActivity extends FragmentActivity {

	private static final String TAG = "CaMainActivity";
	/**
	 * message handler
	 */
	private Handler uiHandler = null;
	private CABaseFragment curFragment=null;

	/****************************** 定义fragment ***************************************/
	private FragmentManager fragmentManager;

	/******************************************** 系统方法重载部分 *******************************************************/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_ca_root);
		initViewAndEvent();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(curFragment.onKeyDown(keyCode, event)){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	
   /******************************************** 类方法定义部分*******************************************************/


	private void initViewAndEvent() {
		/***************************** 初始化fragment ****************************************/
		fragmentManager = getFragmentManager();
	    String title=getIntent().getStringExtra("title");
	    switchFragment(title);
	}
	
	
	private void switchFragment(String title){	
		curFragment = getFragmentByTitle(title);
        if(null == curFragment)return;
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 增加fragment到backstack。
		transaction.replace(R.id.ca_maincontent, curFragment);
		transaction.addToBackStack(null);
		// transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		// 提交修改
		transaction.commit();		
	}

	

	/**
	 * 获取fragment实例对象。
	 * 
	 * @param index
	 *            索引值。
	 * @return
	 */
	public CABaseFragment getFragmentByTitle(String title) {
		CABaseFragment fragment = null;
//		if(title.contains("CaMCFeed") ||title.contains("CAWalletInfo")
//				||title.contains("CABookIPPRecords") ){  
//			fragment = new CaOperatorFragment(title);
//		}else if(title.contains("CaClearFlash")){
//			fragment = new CaClearFlash();
//		}else if(title.contains("CAworkTime")){
//			fragment = new CAworkTime();
//		}else if(title.contains("CApin")){
//			fragment = new CApin();
//		}else if(title.contains("CAclass")){
//			fragment = new CAclass();
//		}else if(title.contains("CAManualPaired")){
//			fragment = new CAManualPaired();
//		}else if(title.contains("CAauthor")){
//			fragment = new CAauthor();
//		}else if(title.contains("CAoperatorInfo")){
//			fragment = new CAoperatorInfo();
//		}else if(title.contains("Email")){
//			fragment = new Email();
//		}else if(title.contains("CAinfo")){
//			fragment = new CAinfo();
//		}else if(title.contains("CAAdSecurity")){
//			fragment = new CAAdSecurity();
//		}else if(title.contains("CAWatchedIPPShow")){
//			fragment = new CAWatchedIPPShow();
//		}else if(title.contains("CaTVInfor")){
//			fragment = new CaTVInfor();
//		}else
			
		if(title.contains("CAIPProgram")){
			fragment = new CAIPProgram((String) getIntent().getExtra("ippvPro"));
		}

		return fragment;
	}	
}
