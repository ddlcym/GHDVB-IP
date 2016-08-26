package com.changhong.app.dtv;

import java.io.File;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class Factory extends Activity implements OnClickListener{

	private Button clearDateButton;
	private Button clearChannelsButton;
	 SysApplication objApplication;
	
	private static final String prefName="user";
	private static final String keyLastChanId = "lastChannelId";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.factory);
		objApplication=SysApplication.getInstance();
		clearDateButton=(Button)findViewById(R.id.cleardata);
		clearChannelsButton=(Button)findViewById(R.id.clearchannels);
		clearChannelsButton.setOnClickListener(this);
		clearDateButton.setOnClickListener(this);
	}
	private void clearData()
	{
		File hisFile = new File("/data/changhong/dvb/main-play-channel");
		if(hisFile.exists())
		{
			hisFile.delete();
		}
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.cleardata:
			clearData();
			Toast.makeText(Factory.this, R.string.str_factory_clear_data, 
					Toast.LENGTH_SHORT).show();
			break;

		case R.id.clearchannels:
			 objApplication.clearChannel();
			Toast.makeText(Factory.this, R.string.str_factory_clear_channelist, 
					Toast.LENGTH_SHORT).show();
			break;
		}		
	}

}
