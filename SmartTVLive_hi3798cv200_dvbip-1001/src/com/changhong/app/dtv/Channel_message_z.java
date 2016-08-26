package com.changhong.app.dtv;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.changhong.dvb.Channel;
import com.changhong.dvb.ChannelDB;
import com.changhong.dvb.DVB;
import com.changhong.dvb.DVBManager;
import com.changhong.dvb.TunerStatus;
import com.changhong.dvb.ProtoMessage.DVB_Carrier;
import com.changhong.dvb.ProtoMessage.DVB_SCAN_EVENT;
import com.changhong.dvb.ScanListener;
import com.changhong.dvb.ProtoMessage.DVB_CAR_EVENT;
import com.changhong.dvb.Tuner;
import com.changhong.dvb.TunerListener;

public class Channel_message_z extends Activity implements ScanListener {

	protected static final String TAG = "Pindaoxinxi_z";

	public SysApplication objApplication;

	private static SysApplication sysApplication;
	private DVBManager mo_DvbManager = null;
	private ChannelDB mo_Database = null;
	Channel channel = null;

	public Tuner mo_Tunner = null;

	TextView pdxx_programname_z, pdxx_shengdao_z, pdxxprogramid_z,
			pdxx_vedio_pid_z,

			pdxx_pcr_pid_z, pdxx_souquan_pid_z, pdxx_changshang_z,

			pdxx_audio_pid_z, pdxx_ip_z,

			pdxx_audio_type_z, pdxx_frequence_z, pdxx_symbol_z, pdxx_qam_z,

			pdxx_wumalv_percent, pdxx_xhzl_percent, pdxx_signaldp_percent;

	ProgressBar pdxx_signaldp_progress, pdxx_xhzl_progress,
			pdxx_wumalv_progress;
	private homeReceiver mHomeReceiver;

	private class homeReceiver extends BroadcastReceiver {

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

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.pindaoxinxi_z);
		mHomeReceiver = new homeReceiver();
		registerReceiver(mHomeReceiver, new IntentFilter("HOME_PRESSED"));

		initView();
		InitDtv_Obj();

		if (channel != null) {

			String sysAudio = null, disAudionString = null, slQam = null;

			sysAudio = SystemProperties.get("persist.sys.live.ao.channel",
					"STEREO");

			if ("STEREO".equals(sysAudio))
				disAudionString = "立体声";
			else if ("RIGHT".equals(sysAudio)) {
				disAudionString = "右声道";
			} else {
				disAudionString = "左声道";
			}

			pdxx_programname_z.setText("节目名称:" + channel.name);
			pdxx_shengdao_z.setText("声道:" + disAudionString);
			pdxxprogramid_z.setText("节目ID:" + channel.logicNo);
			pdxx_vedio_pid_z.setText("视频PID:" + channel.videoPid);

			pdxx_pcr_pid_z.setText("PCR PID:" + channel.pcrPid);
			// pdxx_ip_z.setText(channel.);
			pdxx_audio_type_z.setText("音频类型:" + channel.audioStreamType);
			pdxx_frequence_z.setText("频率:" + channel.frequencyKhz / 1000);
			pdxx_symbol_z.setText("符号率:" + channel.symbolRateKbps);

			if (channel.modulation == 1)
				slQam = "QAM8";
			else if (channel.modulation == 2)
				slQam = "QAM16";
			else if (channel.modulation == 3)
				slQam = "QAM32";
			else if (channel.modulation == 4)
				slQam = "QAM64";
			else if (channel.modulation == 5)
				slQam = "QAM128";
			else if (channel.modulation == 6)
				slQam = "QAM256";
			pdxx_qam_z.setText("调制方式:" + slQam);
			pdxx_audio_pid_z.setText("音频PID:" + channel.audioPid);
			pdxx_souquan_pid_z.setText("授权ID:");
			pdxx_changshang_z.setText("厂商:0005");

		}

		TunerStatus tunerstatus = mo_Tunner.getTunerStatus(0);

		if (tunerstatus != null) {
			Message msgMessage = new Message();
			msgMessage.what = 3;

			/*
			 * if (tunerstatus.miBitErrorRate < 0) tunerstatus.miBitErrorRate =
			 * 0; if (tunerstatus.miBitErrorRate > 100)
			 * tunerstatus.miBitErrorRate = 100;
			 */
			msgMessage.arg1 = tunerstatus.miBitErrorRate;

			Log.i(TAG, "oncreat------------->>>>  tunerstatus.miBitErrorRate "
					+ tunerstatus.miBitErrorRate);

			uiHandler.sendMessage(msgMessage);

			Message msgMessage1 = new Message();
			msgMessage1.what = 2;

			if (tunerstatus.miSignalQuality < 0)
				tunerstatus.miSignalQuality = 0;
			if (tunerstatus.miSignalQuality > 100)
				tunerstatus.miSignalQuality = 100;
			msgMessage1.arg1 = tunerstatus.miSignalQuality;

			Log.i(TAG, "oncreat------------->>>>  tunerstatus.miSignalQuality "
					+ tunerstatus.miSignalQuality);

			uiHandler.sendMessage(msgMessage1);

			Message msgMessage2 = new Message();
			msgMessage2.what = 1;

			if (tunerstatus.miSignalLevelValue < 0)
				tunerstatus.miSignalLevelValue = 0;
			if (tunerstatus.miSignalLevelValue > 100)
				tunerstatus.miSignalLevelValue = 100;
			msgMessage2.arg1 = tunerstatus.miSignalLevelValue;
			Log.i(TAG,
					"case R.id.manualscan_btn:  miSignalLevelValue ------------->>>>"
							+ msgMessage.arg1);
			uiHandler.sendMessage(msgMessage2);
		}

	}

	private void initView() {

		pdxx_programname_z = (TextView) findViewById(R.id.pdxx_programname_z);
		pdxx_shengdao_z = (TextView) findViewById(R.id.pdxx_shengdao_z);
		pdxxprogramid_z = (TextView) findViewById(R.id.pdxxprogramid_z);
		pdxx_vedio_pid_z = (TextView) findViewById(R.id.pdxx_vedio_pid_z);
		pdxx_pcr_pid_z = (TextView) findViewById(R.id.pdxx_pcr_pid_z);
		pdxx_ip_z = (TextView) findViewById(R.id.pdxx_ip_z);
		pdxx_audio_type_z = (TextView) findViewById(R.id.pdxx_audio_type_z);
		pdxx_frequence_z = (TextView) findViewById(R.id.pdxx_frequence_z);
		pdxx_symbol_z = (TextView) findViewById(R.id.pdxx_symbol_z);
		pdxx_qam_z = (TextView) findViewById(R.id.pdxx_qam_z);
		pdxx_audio_pid_z = (TextView) findViewById(R.id.pdxx_audio_pid_z);
		pdxx_changshang_z = (TextView) findViewById(R.id.pdxx_changshang_z);
		pdxx_souquan_pid_z = (TextView) findViewById(R.id.pdxx_souquan_pid_z);

		pdxx_wumalv_percent = (TextView) findViewById(R.id.pdxx_wumalv_percent);
		pdxx_xhzl_percent = (TextView) findViewById(R.id.pdxx_xhzl_percent);
		pdxx_signaldp_percent = (TextView) findViewById(R.id.pdxx_signaldp_percent);

		pdxx_signaldp_progress = (ProgressBar) findViewById(R.id.pdxx_signaldp_progress);
		pdxx_xhzl_progress = (ProgressBar) findViewById(R.id.pdxx_xhzl_progress);
		pdxx_wumalv_progress = (ProgressBar) findViewById(R.id.pdxx_wumalv_progress);

	}

	void InitDtv_Obj() {

		mo_DvbManager = DVB.getManager();
		objApplication = SysApplication.getInstance();

		if ((SysApplication.iCurChannelId != -1)
				&& (objApplication.dvbDatabase.getChannelCount() >= 0)) {

			if (sysApplication == null) {
				sysApplication = SysApplication.getInstance();
			}

			channel = sysApplication.dvbDatabase
					.getChannel(SysApplication.iCurChannelId);
		}

		// init tunner object
		mo_Tunner = mo_DvbManager.getTunerInstance();

		mo_Tunner.setListener(new TunerListener() {

			@Override
			public void tunerCallback(DVB_CAR_EVENT arg0, int arg1, Object arg2) {
				// TODO Auto-generated method stub

				Message msgMessage = new Message();

				switch (arg0) {

				case CAR_EVENT_SIG_QUALITY_CHG:

					msgMessage.what = 2;
					msgMessage.arg1 = arg1;
					Log.i(TAG, "CAR_EVENT_SIG_QUALITY_CHG ------------->>>>"
							+ arg1);

					uiHandler.sendMessage(msgMessage);

					break;

				case CAR_EVENT_SIG_STRENGTH_CHG:

					msgMessage.what = 1;
					msgMessage.arg1 = arg1;

					Log.i(TAG, "CAR_EVENT_SNR_CHG ----------->>>>" + arg1);

					uiHandler.sendMessage(msgMessage);
					break;

				case CAR_EVENT_BER_CHG:

					msgMessage.what = 3;
					msgMessage.arg1 = arg1;

					Log.i(TAG, "CAR_EVENT_BER_CHG ----------->>>>" + arg1);

					uiHandler.sendMessage(msgMessage);

					break;
				default:
					break;
				}
			}
		});
	}

	Handler uiHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {

			case 1:
				pdxx_signaldp_progress.setProgress(msg.arg1);
				pdxx_signaldp_percent.setText(msg.arg1 + "dbB");
				break;
			case 2:
				pdxx_xhzl_progress.setProgress(msg.arg1);
				pdxx_xhzl_percent.setText(msg.arg1 + "%");
				break;
			case 3:
				pdxx_wumalv_progress.setProgress(msg.arg1);
				pdxx_wumalv_percent.setText(msg.arg1 + "EB");
				break;
			}
		};
	};

	@Override
	public void scanCallback(DVB_SCAN_EVENT arg0, DVB_Carrier arg1,
			ArrayList<Channel> arg2, Object arg3) {
		// TODO Auto-generated method stub

	}

	public boolean onKeyDown(int arg0, KeyEvent arg1) {
		// charle 哎，改不来，总是要死机
		switch (arg0) {
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
		return super.onKeyDown(arg0, arg1);
	}
}