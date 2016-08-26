package com.changhong.app.dtv;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.changhong.app.book.BookDataBase;
import com.changhong.app.book.BookInfo;

public class BootCastReceiver extends BroadcastReceiver {

	ArrayList<BookInfo> delArray = null;
	public BookDataBase dvbBookDataBase = null;
	private Context mcontext;

	@Override
	public void onReceive(Context arg0, Intent arg1) {

		mcontext = arg0;
		Common.LOGD("get Time Set Changed !");
		dvbBookDataBase = new BookDataBase(arg0);

		new bookAddThread().start();

	}

	private class bookAddThread extends Thread {

		@Override
		public void run() {

			Common.LOGD("Book   Thread Start  !");
			// try {
			// Thread.sleep(10000);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// TODO Auto-generated method stub
			// Toast.makeText(arg0, "boot completed", Toast.LENGTH_LONG).show();

			delArray = new ArrayList<BookInfo>();
			Vector<BookInfo> vector = dvbBookDataBase.GetBookInfo();
			if (vector != null) {
				for (BookInfo bookInfo : vector) {
					SharedPreferences sharedPre = mcontext.getSharedPreferences("id", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPre.edit();
					int flag = sharedPre.getInt("id", 0);

					Common.LOGI("现在的时间是" + System.currentTimeMillis());

					String[] mDay = (bookInfo.bookDay).split("-");
					String[] mTime = (bookInfo.bookTimeStart).split(":");

					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(System.currentTimeMillis());
					cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
					cal.set(Calendar.MONTH, (Integer.parseInt(mDay[0])) - 1);
					cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(mDay[1]));
					cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mTime[0]));
					cal.set(Calendar.MINUTE, Integer.parseInt(mTime[1]));
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					Common.LOGI(bookInfo.bookEnventName + "节目预约时间" + cal.getTimeInMillis());
					if (cal.getTimeInMillis() > System.currentTimeMillis()) {

						Common.LOGI("加入预约的节目：" + bookInfo.bookEnventName);

						Intent mIntent = new Intent("android.intent.action.WAKEUP");
						mIntent.putExtra("bookinfo", bookInfo);

						Intent myBookIntent = new Intent("android.intent.action.SmartTVBook");
						myBookIntent.putExtra("bookinfo", bookInfo);
						myBookIntent.putExtra("SmartTV_BookFlag", flag);
						mcontext.sendBroadcast(myBookIntent);

						editor.putInt("id", flag + 1);
						editor.commit();
					} else {
						Common.LOGI("删除过期预约的节目：" + bookInfo.bookEnventName);
						// objApplication.delBookChannel(bookInfo.bookDay,
						// bookInfo.bookTimeStart);
						delArray.add(bookInfo);
					}
				}
				if (delArray.size() > 0) {
					for (int i = 0; i < delArray.size(); i++) {
						dvbBookDataBase.RemoveOneBookInfo(delArray.get(i).bookDay, delArray.get(i).bookTimeStart);
					}
				}
			} else {
				Common.LOGE("vector  = null  !");
			}

			Common.LOGD("Book  Thread  finish!");

		}
	}

}
