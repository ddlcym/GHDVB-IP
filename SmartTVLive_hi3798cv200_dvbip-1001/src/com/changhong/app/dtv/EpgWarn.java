package com.changhong.app.dtv;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.changhong.app.book.BookInfo;

public class EpgWarn extends Activity implements OnClickListener {

	SysApplication objApplication;
	Context context;
	//private final UI_Handler mUiHandler = new UI_Handler(this);
	// private TextView tvSecond;
	private static int iSecond = 60;

	private Button buttonok, buttoncancel;

	private TextView text0View;
	private com.changhong.app.dtv.TextMarquee textView;
	private BookInfo bookInfo;
	private Button btnOK;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.epgwarn);

		context = EpgWarn.this;
		Log.i("EpgWarn", "EpgWarn   ----》onCreate");

		objApplication = SysApplication.getInstance();
		objApplication.initDtvApp(this);

		bookInfo = (BookInfo) getIntent().getSerializableExtra("bookinfo");

		Log.i("EpgWarn",
				"Reciver  is   running ------ bookInfo.bookTimeStart-----bookInfo.bookChannelName ---- bookInfo.bookEnventName"
						+ bookInfo.bookTimeStart
						+ "    "
						+ bookInfo.bookChannelName
						+ " "
						+ bookInfo.bookEnventName);
		
		iSecond = 60; 

		//mUiHandler.sendEmptyMessage(0);


		/* 暂时屏蔽 , 测试显示效果 */
		if (!objApplication.isBookedChannel(bookInfo)) {
			P.e("Book channel is deleted,exit !");

			Log.i("EpgWarn", "Book channel is deleted,exit");

			sendBroadcastInfo(Main.sChkEpgTimer);

			finish();
			// objApplication.exit();
			// android.os.Process.killProcess(android.os.Process.myPid());
		}

		objApplication.delBookChannel(bookInfo.bookDay, bookInfo.bookTimeStart);

		initView();
		text0View.setText(bookInfo.bookChannelName);
		textView.setText("<" + bookInfo.bookEnventName + ">");

		// doFinish();
		buttonok.setOnClickListener(this);
		buttoncancel.setOnClickListener(this);
		initEvent();
	}

	private void doFinish() {
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				finish();

				objApplication.exit();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}, 0);
	}

	private void initView() {
		// TODO Auto-generated method stub

		buttonok = (Button) findViewById(R.id.guankanjiemu);
		buttoncancel = (Button) findViewById(R.id.cancelguankanjiemu);
		// tvSecond=(TextView)findViewById(R.id.idsecond);
		buttonok.setFocusable(true);
		buttonok.setFocusableInTouchMode(true);
		buttonok.requestFocus();
		buttonok.requestFocusFromTouch();

		text0View = (TextView) findViewById(R.id.jiemuname);
		textView = (com.changhong.app.dtv.TextMarquee) findViewById(R.id.jiemuinfo);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0.getId() == R.id.guankanjiemu) {

			objApplication.playChannel(bookInfo.bookChannelIndex, false);

			Intent showBanneForYuYueDialog = new Intent();
			showBanneForYuYueDialog.setAction("showBanneForYuYueDialog");
			showBanneForYuYueDialog.putExtra("chanid", bookInfo.bookChannelIndex);
			context.sendBroadcast(showBanneForYuYueDialog);
			//mUiHandler.removeMessages(0);

			Intent intent = new Intent(EpgWarn.this, Main.class);
			startActivity(intent);

		} else {
			//mUiHandler.removeMessages(0);
		}
		sendBroadcastInfo(Main.sChkEpgTimer);
		finish();

	}

	@Override
	protected void onPause() {

		super.onPause();

		//if (mUiHandler != null) {
		//	mUiHandler.removeMessages(0);
		//}

	}

	public void initEvent() {
		/* 计时器 */
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				// TODO Auto-generated method stub
				super.handleMessage(message);
				switch (message.what) {
				case 0x01:

					break;

				default:
					break;
				}
			}
		};
		mHandler.postDelayed(CountRunnable, 1000);

	}
	public Runnable CountRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			iSecond--;
			if (iSecond > 0) {
				buttonok.setText(context.getString(
						R.string.str_OKButton).toString()
						+ "(" + iSecond + "s)");	
				buttonok.invalidate();				
				mHandler.postDelayed(CountRunnable, 1000);
			}
			if (iSecond == 0) {

				Log.i("Epgwarn", "Start main !");

				objApplication.playChannel(
						bookInfo.bookChannelIndex, false);
				Intent intent = new Intent(EpgWarn.this, Main.class);
				startActivity(intent);
				finish();
				
			}
		}

	};	
/*
	static class UI_Handler extends Handler {
		WeakReference<EpgWarn> mActivity;

		UI_Handler(EpgWarn activity) {
			mActivity = new WeakReference<EpgWarn>(activity);
		}

		@Override
		public void handleMessage(Message msg) {

			EpgWarn theActivity = mActivity.get();

			Log.i("Epgwarn", "rev second  ->  " + iSecond+",act="+theActivity);
			if (iSecond > 0) {
				theActivity.buttonok.setText(theActivity.context.getString(
						R.string.str_OKButton).toString()
						+ "(" + iSecond + "s)");	
				theActivity.buttonok.invalidate();
				iSecond--;		
				if (iSecond <= 0) {
					Log.i("Epgwarn", "Start main !");

					theActivity.objApplication.playChannel(
							theActivity.bookInfo.bookChannelIndex, false);
					Intent intent = new Intent(theActivity, Main.class);
					theActivity.startActivity(intent);
					theActivity.finish();
				}
				theActivity.mUiHandler.removeMessages(0);				
				theActivity.mUiHandler.sendEmptyMessageDelayed(0, 1000);
				theActivity.mUiHandler.sendEmptyMessageDelayed(0, 1000);
				Log.i("Epgwarn", "send second  ->  " + iSecond);
			}

		}
	}
*/
	public void regOneShotTimer(long new_time/* ,BookInfo bookInfo */) {
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(EpgWarn.this, AlarmReceiver.class); // 创建Intent对象
		// intent.putExtra("bookinfo", bookInfo);
		PendingIntent pi = PendingIntent.getBroadcast(EpgWarn.this, 0, intent,
				0);
		// alarmManager.set(AlarmManager.RTC_WAKEUP,
		// c.getTimeInMillis(), pi); //设置闹钟
		alarmManager.set(AlarmManager.RTC_WAKEUP, new_time, pi); // 设置闹钟
	}

	private void sendBroadcastInfo(String strInfo) {
		Intent intent = new Intent();
		intent.setAction(strInfo);
		sendBroadcast(intent);
		P.i("YBYB", "send intent:" + strInfo);
	}
}
