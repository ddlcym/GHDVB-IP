package com.changhong.app.ads;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ADPicService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		ADPicJsonParser adPicJsonParser = ADPicJsonParser.getADPicJsonParserInstance();	
		Log.i("ADV", "parse ad data Start!OBJ="+adPicJsonParser.toString());
		adPicJsonParser.doParse();
		Log.i("ADV", "parse ad data end  !");
		return super.onStartCommand(intent, flags, startId);
	}	
}
