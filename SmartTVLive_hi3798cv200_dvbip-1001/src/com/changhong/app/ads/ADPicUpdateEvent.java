package com.changhong.app.ads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

public class ADPicUpdateEvent {
	public static final String Action_Status2Activity = "com.changhong.advservice.outstatus";
	private static BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Action_Status2Activity.equals(action)) {
				int type = intent.getIntExtra("status", -1);
				Log.i("ADV",
						"get a broadcast of ghadv data update module:"
								+ type);
				if (type == 4) {
					Log.i("ADV", " call updateADPicSource begin");
					ADPicDisplay adPicDisplay = ADPicDisplay
							.getInstance();
					adPicDisplay.updateADPicSource();
					Log.i("ADV", " call updateADPicSource end");
				}
			}
		}
	};

	public static void registerAdReceiver(Context context) {
		Log.i("ADV", " registerAdReceiver begin");
		IntentFilter filter = new IntentFilter();
		filter.addAction(Action_Status2Activity);
		context.registerReceiver(mReceiver, filter);	
		Log.i("ADV", " registerAdReceiver end");
	}
	public static void unRegisterAdReceiver(Context context){
		
		if(mReceiver!=null){
			context.unregisterReceiver(mReceiver);
			Log.i("ADV", " unRegisterAdReceiver ");
		}else {
			Log.e("ADV", " unRegisterAdReceiver error>> mReceiver is null");
		}
	}
}
