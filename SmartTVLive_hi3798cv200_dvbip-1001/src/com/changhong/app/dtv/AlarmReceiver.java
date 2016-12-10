package com.changhong.app.dtv;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import com.changhong.app.book.BookDataBase;
import com.changhong.app.book.BookInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	private static BookDataBase dvbBookDataBase = null;
	private static Map<Integer, Long> mapEpgList = new HashMap<Integer, Long>();
	Vector<BookInfo> vector = null;
	private static AlarmReceiver alarmRev;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, EpgWarn.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);		
	}
	
	private AlarmReceiver(Context arg0) {
		if (dvbBookDataBase == null)
			dvbBookDataBase = new BookDataBase(arg0);
	}
	public static AlarmReceiver getInstance(Context arg0) {
		if(alarmRev==null)
			alarmRev = new AlarmReceiver(arg0);
		return alarmRev;
	}
	
	public void checkEpgBook() {

		ArrayList<BookInfo> delArray = null;
		delArray = new ArrayList<BookInfo>();
		vector = dvbBookDataBase.GetBookInfo();
		mapEpgList.clear();

		P.i("现在的时间是" + System.currentTimeMillis());

		if (vector != null) {
			for (BookInfo bookInfo : vector) {

				P.i(bookInfo.bookChannelName + ">>>" + bookInfo.bookEnventName
						+ ": 时间信息>>" + bookInfo.bookDay + " "
						+ bookInfo.bookTimeStart);

				String[] mTime1 = null, mTime2 = null, mTime3 = null;
				String mTime0 = null;
				boolean bDataErr = false;
				Calendar cal = Calendar.getInstance();

				if (bookInfo.bookDay == null || bookInfo.bookDay.length() < 5
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
						&& (cal.getTimeInMillis() > System.currentTimeMillis())) // 结束时间>当前时间
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

					if (!bDataErr) {
						mapEpgList.put(bookInfo.bookChannelIndex,
								Long.valueOf(cal.getTimeInMillis()));
					}

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
						/*******************************************************
						 * Intent EpgWarn = new Intent(mcontext, EpgWarn.class);
						 * EpgWarn.putExtra("bookinfo", bookInfo);
						 * mcontext.startActivity(EpgWarn);
						 *******************************************************/
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
					dvbBookDataBase.RemoveOneBookInfo(delArray.get(i).bookDay,
							delArray.get(i).bookTimeStart);
				}
			}

			if (mapEpgList.size() > 0) {
				List<Map.Entry<Integer, Long>> infoIds = new ArrayList<Map.Entry<Integer, Long>>(
						mapEpgList.entrySet());

				Collections.sort(infoIds,
						new Comparator<Map.Entry<Integer, Long>>() {
							public int compare(Map.Entry<Integer, Long> o1,
									Map.Entry<Integer, Long> o2) {
								return o2.getValue().compareTo(o1.getValue());
								// return
								// (o1.getKey()).toString().compareTo(o2.getKey());
							}
						});
				new EpgWarn().regOneShotTimer(infoIds.get(0).getValue());
			}
		} else {
			P.e("ChannelBook list is null  !");
		}

		P.d("Book  Thread  finish!");

	}

}