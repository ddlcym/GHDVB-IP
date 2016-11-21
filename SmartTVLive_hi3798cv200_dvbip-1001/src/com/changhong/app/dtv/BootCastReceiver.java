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
		P.d("get Time Set Changed !");
		dvbBookDataBase = new BookDataBase(arg0);

		new bookAddThread().start();

	}

	private class bookAddThread extends Thread {

		@Override
		public void run() {

			P.d("Book   Thread Start  !");
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

					P.i("现在的时间是" + System.currentTimeMillis());
					P.i(bookInfo.bookChannelName + ">>>"
							+ bookInfo.bookEnventName + ": 时间信息>>"
							+ bookInfo.bookDay + " " + bookInfo.bookTimeStart);

					String[] mTime1 = null, mTime2 = null, mTime3 = null;
					String mTime0 = null;
					boolean bDataErr = false;
					Calendar cal = Calendar.getInstance();

					if (bookInfo.bookDay == null
							|| bookInfo.bookDay.length() < 5
							|| bookInfo.bookTimeStart == null
							|| bookInfo.bookTimeStart.length() < 11) {
						bDataErr = true;
					} else {

						mTime0 = bookInfo.bookDay;
						mTime1 = bookInfo.bookTimeStart.split("~");
						if (mTime1.length >= 2) {
							mTime2 = mTime1[0].split(":");
							mTime3 = mTime1[1].split(":");
						}

						cal.setTimeInMillis(System.currentTimeMillis());
						cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
						try {
							cal.set(Calendar.MONTH,
									(Integer.parseInt(mTime0.substring(0, 1))) - 1);
							cal.set(Calendar.DAY_OF_MONTH,
									Integer.parseInt(mTime0.substring(3, 4)));
							cal.set(Calendar.HOUR_OF_DAY,
									Integer.parseInt(mTime3[0].trim()));
							cal.set(Calendar.MINUTE,
									Integer.parseInt(mTime3[1].trim()));
							cal.set(Calendar.SECOND, 0);
							cal.set(Calendar.MILLISECOND, 0);
							if (Integer.parseInt(mTime3[0].trim()) < Integer
									.parseInt(mTime2[0].trim()))// 跨天
							{
								cal.add(Calendar.HOUR_OF_DAY, 1);
							}

						} catch (Exception e) {
							e.printStackTrace();
							bDataErr = true;
						}
					}
					P.i("结束时间>>" + cal.getTimeInMillis() + ">>" + bDataErr);
					if (!bDataErr
							&& (cal.getTimeInMillis() > System
									.currentTimeMillis())) // 结束时间>当前时间
					{
						cal.setTimeInMillis(System.currentTimeMillis());
						cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
						try {
							cal.set(Calendar.MONTH,
									(Integer.parseInt(mTime0.substring(0, 1))) - 1);
							cal.set(Calendar.DAY_OF_MONTH,
									Integer.parseInt(mTime0.substring(3, 4)));
							cal.set(Calendar.HOUR_OF_DAY,
									Integer.parseInt(mTime2[0].trim()));
							cal.set(Calendar.MINUTE,
									Integer.parseInt(mTime2[1].trim()) - 1); // -1
																				// 提前1分钟
							cal.set(Calendar.SECOND, 0);
							cal.set(Calendar.MILLISECOND, 0);
						} catch (Exception e) {
							e.printStackTrace();
							bDataErr = true;
						}

						P.i("开始时间>>" + cal.getTimeInMillis() + ">>" + bDataErr);

						if (!bDataErr
								&& (cal.getTimeInMillis() >= System
										.currentTimeMillis())) // 开始时间>当前时间
						{
							P.i("即将开始播放  >>>" + bookInfo.bookEnventName);
							/*
							 * Intent mIntent = new
							 * Intent("android.intent.action.WAKEUP");
							 * mIntent.putExtra("bookinfo", bookInfo);
							 * 
							 * Intent myBookIntent = new
							 * Intent("android.intent.action.SmartTVBook");
							 * myBookIntent.putExtra("bookinfo", bookInfo);
							 * myBookIntent.putExtra("SmartTV_BookFlag", flag);
							 * mcontext.sendBroadcast(myBookIntent);
							 * 
							 * editor.putInt("id", flag + 1); editor.commit();
							 */

							Intent EpgWarn = new Intent(mcontext, EpgWarn.class);
							EpgWarn.putExtra("bookinfo", bookInfo);
							mcontext.startActivity(EpgWarn);
						}
					} else {

						bDataErr = true;
					}

					if (bDataErr) {
						P.i("删除过期预约的节目：" + bookInfo.bookEnventName);
						// objApplication.delBookChannel(bookInfo.bookDay,
						// bookInfo.bookTimeStart);
						delArray.add(bookInfo);
					}
				}
				if (delArray.size() > 0) {
					for (int i = 0; i < delArray.size(); i++) {
						dvbBookDataBase.RemoveOneBookInfo(
								delArray.get(i).bookDay,
								delArray.get(i).bookTimeStart);
					}
				}
			} else {
				P.e("ChannelBook list is null  !");
			}

			P.d("Book  Thread  finish!");

		}

	}

}
