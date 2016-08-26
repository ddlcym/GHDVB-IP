package com.changhong.app.dtv;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class thirdPartyBroadcastReceiver extends BroadcastReceiver{

	private static final String TAG = "SmartTVLive-thirdPartyBroadcastReceiver";
	private static final String ACTION_HOME_PRESS = "HOME_PRESSED";
	
	private static final String RECORD_HISTORY = "com.chots.action.record_history";
	private static final String EXTRA_CHAN_ID = "extra.chan_id";
	
	private Context mContext = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String strAction = intent.getAction();
		Log.i(TAG, "this action is "+strAction);
		mContext = context;
		if(strAction.equals(ACTION_HOME_PRESS))
		{
			doHomePress();
		}
		else if(strAction.equals(RECORD_HISTORY))
		{
			int extra_chan_id = intent.getIntExtra(EXTRA_CHAN_ID, -1);
			if(extra_chan_id >= 0)
			{
				doRecordHistroy(extra_chan_id);
			}
		}
	}
	
	private void doHomePress()
	{
//		System.exit(0);
	}
	
	private void doRecordHistroy(int extra)
	{
		
	}
}
