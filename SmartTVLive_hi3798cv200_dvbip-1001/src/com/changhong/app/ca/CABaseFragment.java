package com.changhong.app.ca;

import com.changhong.dvb.CA;
import com.changhong.dvb.DVB;
import com.changhong.dvb.DVBManager;
import com.changhong.app.dtv.R;
import com.changhong.app.dtv.SysApplication;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
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

public class CABaseFragment extends Fragment implements OnClickListener {

	protected DVBManager dvbManager;
	protected CA thisCa;
	protected EditText pinVertify = null;
	protected CABaseFragment nextFragment = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dvbManager = DVB.getManager();
		thisCa = dvbManager.getCaInstance();
	}

	@Override
	public void onClick(View arg0) {

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (null != nextFragment && nextFragment.onKeyDown(keyCode, event)) {
			return true;
		}

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

		return false;
	}

	protected void onSubmit(String pinCode) {

	}

	protected void switchFragment(CABaseFragment fragment) {
		if (null == fragment)return;
		nextFragment = fragment;
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		// 增加fragment到backstack。
		transaction.replace(R.id.ca_maincontent, fragment);
		transaction.addToBackStack(null);
		// transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		// 提交修改
		transaction.commit();
	}

	
}
