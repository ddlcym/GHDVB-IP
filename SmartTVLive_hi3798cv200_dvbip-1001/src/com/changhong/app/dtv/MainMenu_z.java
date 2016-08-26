package com.changhong.app.dtv;

import java.lang.ref.WeakReference;

import com.changhong.app.constant.Class_Global;
import com.changhong.app.dtv.DialogUtil.DialogBtnOnClickListener;
import com.changhong.app.dtv.DialogUtil.DialogMessage;
import com.changhong.app.dtv.Main.UI_Handler;
import com.changhong.dvb.Channel;

import android.R.integer;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenu_z extends Activity {

	LinearLayout layout_set_activity_z, program_menu2;
	RelativeLayout program_menu1;
	Button program_menu_return2;

	SysApplication objApplication;

	Button

	program_menu_return1,

	program_menu_pinxian,

	program_menu_msg,

	program_menu_set,

	program_menu_main,

	program_menu_yudingguanli,

	program_menu_pindaoxinxi,

	program_menu_xiai,

	program_menu_huikan,

	program_menu_huantai;

	int REQUESTCODE = 10;

	Channel Currentchannel;

	ProgressBar volume_progress_view;
	TextView volume_value;
	RelativeLayout volume_layout;
	private final UI_Handler mUiHandler = new UI_Handler(this);
	private static final int MESSAGE_VOLUME_DISAPPEAR = 902;
	private static final int MESSAGE_VOLUME_SHOW = 903;
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

		
		Log.i("Main", "MainMenu_z OnCreate !" );
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.zhibo_menu_z);
		
		Banner banner = Banner.getInstance(this);
		if(banner.bannerToast!=null)
			banner.bannerToast.cancel();

		program_menu1 = (RelativeLayout) findViewById(R.id.program_menu1);
		// program_menu2 = (LinearLayout) findViewById(R.id.program_menu2);

		// program_menu_return2 = (Button)
		// findViewById(R.id.program_menu_return2);
		program_menu_return1 = (Button) findViewById(R.id.program_menu_return1);
		// program_menu_pinxian = (Button)
		// findViewById(R.id.program_menu_pinxian);
		program_menu_msg = (Button) findViewById(R.id.program_menu_msg);
		program_menu_set = (Button) findViewById(R.id.program_menu_set);
		program_menu_main = (Button) findViewById(R.id.program_menu_main);
		program_menu_yudingguanli = (Button) findViewById(R.id.program_menu_yudingguanli);
		program_menu_pindaoxinxi = (Button) findViewById(R.id.program_menu_pindaoxinxi);
		program_menu_xiai = (Button) findViewById(R.id.program_menu_xiai);
		program_menu_huantai = (Button) findViewById(R.id.program_menu_huantai);
		// program_menu_huikan = (Button)
		// findViewById(R.id.program_menu_huikan);

		// program_menu_return2.setOnClickListener(buttOnClickListener);
		program_menu_return1.setOnClickListener(buttOnClickListener);
		// program_menu_pinxian.setOnClickListener(buttOnClickListener);
		program_menu_msg.setOnClickListener(buttOnClickListener);
		program_menu_set.setOnClickListener(buttOnClickListener);
		program_menu_main.setOnClickListener(buttOnClickListener);
		program_menu_yudingguanli.setOnClickListener(buttOnClickListener);
		program_menu_pindaoxinxi.setOnClickListener(buttOnClickListener);
		program_menu_xiai.setOnClickListener(buttOnClickListener);
		program_menu_huantai.setOnClickListener(buttOnClickListener);

		volume_progress_view = (ProgressBar) findViewById(R.id.volume_progress_view_z_menu);
		volume_value = (TextView) findViewById(R.id.volume_value_z_menu);
		volume_layout = (RelativeLayout) findViewById(R.id.volume_layout_z_menu);

		// program_menu_huikan.setOnClickListener(buttOnClickListener);

		// program_menu_huantai.requestFocus();

		objApplication = SysApplication.getInstance();
		mHomeReceiver = new homeReceiver();
		registerReceiver(mHomeReceiver, new IntentFilter("HOME_PRESSED"));

	}

	@Override
	protected void onResume() {
		
		Log.i("Main", "MainMenu_z  onResume !" );

		if (objApplication.dvbDatabase != null)
			Currentchannel = objApplication.dvbDatabase
					.getChannel(SysApplication.iCurChannelId);

		if (Currentchannel != null) {

			if (Currentchannel.favorite == 1) {

				program_menu_xiai.setText("删除喜爱");

			} else if (Currentchannel.favorite == 0) {

				program_menu_xiai.setText("加入喜爱");
			}

		}
		super.onResume();
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			String action = intent.getAction();
			if (action.equals("FINISH")) {
				finish();
			}

		}

	};

	/*
	 * public void onActivityResult(int requestCode, int resultCode, Intent
	 * data){ switch (requestCode) { case 10: if(resultCode==Activity.RESULT_OK)
	 * finish(); break;
	 * 
	 * } }
	 */

	OnClickListener buttOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

			switch (arg0.getId()) {

			case R.id.program_menu_return1:

				finish();

				break;

			case R.id.program_menu_msg:

				Intent Email_zIntent = new Intent(MainMenu_z.this,
						Email_z.class);

				startActivity(Email_zIntent);

			//	finish();

				break;
			case R.id.program_menu_set:

			//	IntentFilter myIntentFilter = new IntentFilter();
			//	myIntentFilter.addAction("FINISH");
			//	registerReceiver(mBroadcastReceiver, myIntentFilter);

				Intent layout_set_activity_zmIntent = new Intent(
						MainMenu_z.this, SetMenu_z.class);

				startActivity(layout_set_activity_zmIntent);
				//finish();

				break;
			case R.id.program_menu_main:

				Intent callDesktop = new Intent(Intent.ACTION_MAIN);
				callDesktop.addCategory(Intent.CATEGORY_HOME);
				callDesktop.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(callDesktop);
				android.os.Process.killProcess(android.os.Process.myPid());

				break;
			case R.id.program_menu_yudingguanli:

				Intent mIntent = new Intent(MainMenu_z.this,
						Epg_z.class);

				startActivity(mIntent);

				break;

			case R.id.program_menu_pindaoxinxi:

				Intent pindaoxinxiIntentz = new Intent(MainMenu_z.this,
						Channel_message_z.class);

				startActivity(pindaoxinxiIntentz);

				break;

			case R.id.program_menu_xiai:

				if (Currentchannel != null) {

					if (Currentchannel.favorite == 1) {

						program_menu_xiai.setText("加入喜爱");

						Toast.makeText(MainMenu_z.this, "成功取消喜爱节目",
								Toast.LENGTH_SHORT).show();

						objApplication.dvbDatabase.updateChannel(
								SysApplication.iCurChannelId, "favorite", "0");

						Currentchannel.favorite = 0;

					} else if (Currentchannel.favorite == 0) {

						program_menu_xiai.setText("删除喜爱");

						Toast.makeText(MainMenu_z.this, "成功加入喜爱节目",
								Toast.LENGTH_SHORT).show();

						objApplication.dvbDatabase.updateChannel(
								SysApplication.iCurChannelId, "favorite", "1");
						Currentchannel.favorite = 1;
					}

				}

				break;
			case R.id.program_menu_huantai:

				Intent Huantai_fast_zIntentz = new Intent(MainMenu_z.this,
						FastChangeChannel_z.class);

				startActivity(Huantai_fast_zIntentz);

				finish();

				break;
			// case R.id.program_menu_huikan:

			// break;

			default:
				break;
			}

		}

	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int arg0, KeyEvent arg1) {
		// charle 哎，改不来，总是要死机
		switch (arg0) {
		case KeyEvent.KEYCODE_VOLUME_DOWN: {
			AudioManager am2 = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			boolean isMute2 = am2.isStreamMute(AudioManager.STREAM_MUSIC);

			if (isMute2)// current state is mute
			{
				am2.setStreamMute(AudioManager.STREAM_MUSIC, !isMute2);
				// Assure the volume is old value.
				am2.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_SAME, 0);
				// send broadcast to show/hide mute icon
				Intent mIntent2 = new Intent("chots.anction.muteon");
				sendBroadcast(mIntent2);
			} else// Current state is not mute
			{
				// Raise the volume, by calling AudioManager's API.

				int currentVolume3 = am2
						.getStreamVolume(AudioManager.STREAM_MUSIC);

				if (currentVolume3 > 0)
					am2.adjustStreamVolume(AudioManager.STREAM_MUSIC,
							AudioManager.ADJUST_LOWER, 0);

			}
			// get current volume value, here you can show/set your own volume
			// bar or do nothing.

			int currentVolume2 = am2.getStreamVolume(AudioManager.STREAM_MUSIC);

			/*
			 * if(currentVolume2==0&&isMute2==false){
			 * 
			 * am2.setStreamMute(AudioManager.STREAM_MUSIC, !isMute2); Intent
			 * mIntent2 = new Intent("chots.anction.muteon");
			 * sendBroadcast(mIntent2);
			 * 
			 * }
			 */

			Log.i("Main",
					"  volume down  key arrived-   currentVolume--------------->  "
							+ currentVolume2);
			volume_progress_view.setProgress(currentVolume2);
			volume_value.setText("" + currentVolume2);

			mUiHandler.removeMessages(MESSAGE_VOLUME_DISAPPEAR);
			mUiHandler.sendEmptyMessageDelayed(MESSAGE_VOLUME_DISAPPEAR, 2000);

			Log.i("Main-Menu",
					"layout visibility:" + volume_layout.getVisibility());
			// if( volume_layout.getVisibility()==View.INVISIBLE)
			// volume_layout.setVisibility(View.VISIBLE);
			mUiHandler.sendEmptyMessage(MESSAGE_VOLUME_SHOW);
			return true;
		}
		case KeyEvent.KEYCODE_VOLUME_UP: {
			AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			boolean isMute = am.isStreamMute(AudioManager.STREAM_MUSIC);
			if (isMute)// current state is mute
			{
				am.setStreamMute(AudioManager.STREAM_MUSIC, !isMute);
				// Assure the volume is old value.
				am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_SAME, 0);
				// send broadcast to show/hide mute icon
				Intent mIntent = new Intent("chots.anction.muteon");
				sendBroadcast(mIntent);
			} else// Current state is not mute
			{
				// Raise the volume, by calling AudioManager's API.
				am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_RAISE, 0);
			}

			// get current volume value, here you can show/set your own volume
			// bar or do nothing.
			int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);

			volume_progress_view.setProgress(currentVolume);
			volume_value.setText("" + currentVolume);

			Log.i("Main",
					"  volume up  key arrived-   currentVolume--------------->  "
							+ currentVolume);

			Log.i("Main-Menu",
					"layout visibility:" + volume_layout.getVisibility());
			// if( volume_layout.getVisibility()==View.INVISIBLE)
			// volume_layout.setVisibility(View.VISIBLE);
			mUiHandler.sendEmptyMessage(MESSAGE_VOLUME_SHOW);

			mUiHandler.removeMessages(MESSAGE_VOLUME_DISAPPEAR);
			mUiHandler.sendEmptyMessageDelayed(MESSAGE_VOLUME_DISAPPEAR, 2000);

			// TODO: show/set your volume bar.
			return true;
		}
		

			
		case KeyEvent.KEYCODE_VOLUME_MUTE: {
			Log.i("MUTE", "mute key arrived.");
			AudioManager am1 = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			boolean isMute1 = am1.isStreamMute(AudioManager.STREAM_MUSIC);
			// set mute state
			am1.setStreamMute(AudioManager.STREAM_MUSIC, !isMute1);
			// Assure the volume is old value.
			am1.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_SAME, 0);
			// send broadcast to show/hide mute icon
			Intent mIntent1 = new Intent("chots.anction.muteon");
			sendBroadcast(mIntent1);
			// get current volume value, here you can show your own volume bar
			// or do nothing.
			int currentVolume1 = am1.getStreamVolume(AudioManager.STREAM_MUSIC);

			Log.i("MUTE", "mute key arrived-----currentVolume------>."
					+ currentVolume1);

			return true;
		}

		}
		return super.onKeyDown(arg0, arg1);
	}

	public static class UI_Handler extends Handler {
		WeakReference<MainMenu_z> mActivity;

		UI_Handler(MainMenu_z activity) {
			mActivity = new WeakReference<MainMenu_z>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			final MainMenu_z theActivity = mActivity.get();

			switch (msg.what) {

			case MESSAGE_VOLUME_DISAPPEAR:

				theActivity.volume_layout.setVisibility(View.GONE);

				break;

			case MESSAGE_VOLUME_SHOW:
				if (theActivity.volume_layout.getVisibility() != View.VISIBLE)
					theActivity.volume_layout.setVisibility(View.VISIBLE);
				break;
			}
		}
	}
}