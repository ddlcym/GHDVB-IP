package com.changhong.app.dtv;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.Vector;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageParser.NewPermissionInfo;
import android.text.method.WordIterator;
import android.widget.Toast;

import com.changhong.app.book.BookDataBase;
import com.changhong.app.book.BookInfo;
import com.changhong.app.utils.UserDate;

public class BootCastReceiver extends BroadcastReceiver {
	private static BookDataBase dvbBookDataBase = null;
	//private static Map<Integer, Long> mapEpgList = new HashMap<Integer, Long>();

	private Context mcontext;
	private static bookAddThread bookThread = null;
	private static int threadStatus = 1;

	@Override
	public void onReceive(Context arg0, Intent arg1) {

		mcontext = arg0;
		
		P.d("YBYB","request to check EPG timer !");

		threadStatus = 1;

		if (bookThread == null) {
			P.d("YBYB","create bookThread !");
			if (dvbBookDataBase == null)
				dvbBookDataBase = new BookDataBase(arg0);

			bookThread = new bookAddThread();
			bookThread.start();
		}else{
			P.d("YBYB","bookThread running on !");
		}

		// AlarmReceiver.getInstance(arg0).checkEpgBook();
	}

	private class bookAddThread extends Thread {

		@Override
		public void run() {
			ArrayList<BookInfo> delArray = new ArrayList<BookInfo>();;
			Vector<BookInfo> vector = null;
			P.d("YBYB", "Book   Thread Start  !");
			
			while (threadStatus > 0) {
				if (threadStatus == 1) {
					delArray.clear();
					vector = dvbBookDataBase.GetBookInfo();
					threadStatus = 2;
				}
				if (vector != null && vector.size() > 0) {

					String endTime=null,startTime=null,tmpday=null;					
					UserDate curUserdate = new UserDate();
					//P.i("YBYB", "当前时间>>" + System.currentTimeMillis());
					P.i("YBYB", "当前时间>>" + curUserdate.getMDHM());
					
					for (BookInfo bookInfo : vector) {
						

						P.i("YBYB", bookInfo.bookChannelName + ">>>"
								+ bookInfo.bookEnventName + ": 时间信息>>"
								+ bookInfo.bookDay + " "
								+ bookInfo.bookTimeStart);
						
						String[] mTime1 = null, mTime2 = null, mTime3 = null;
						String[] mTime0 = null;
						boolean bDataErr = false;
						//Calendar cal = Calendar.getInstance();

						if (bookInfo.bookDay == null
								|| bookInfo.bookDay.length() < 5
								|| bookInfo.bookTimeStart == null
								|| bookInfo.bookTimeStart.length() < 11) {
							bDataErr = true;
						} else {

							mTime0 = bookInfo.bookDay.split("-");//date
							mTime1 = bookInfo.bookTimeStart.split("~");
							if (mTime1.length >= 2) {
								mTime2 = mTime1[0].split(":");//start time 
								mTime3 = mTime1[1].split(":");//end time
							}

							/*cal.setTimeInMillis(System.currentTimeMillis());
							cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));*/
							try {
								/*cal.set(Calendar.MONTH, (Integer
										.parseInt(mTime0.substring(0, 1))) - 1);
								cal.set(Calendar.DAY_OF_MONTH, Integer
										.parseInt(mTime0.substring(3, 4)));
								cal.set(Calendar.HOUR_OF_DAY,
										Integer.parseInt(mTime3[0].trim()));
								cal.set(Calendar.MINUTE,
										Integer.parseInt(mTime3[1].trim()));
								cal.set(Calendar.SECOND, 0);
								cal.set(Calendar.MILLISECOND, 0);*/
								if (Integer.parseInt(mTime3[0].trim()) < Integer
										.parseInt(mTime2[0].trim()))// 跨天
								{
									int day = Integer.parseInt(mTime0[1])+1;
									if(day<10)
										tmpday = "0"+day;
									else {
										tmpday = ""+day;
									}
									P.i("YBYB", "+1天");
								}else{
									tmpday = mTime0[1];
								}
								
								endTime = mTime0[0].trim()+tmpday+ mTime3[0].trim()+mTime3[1].trim();
								

							} catch (Exception e) {
								e.printStackTrace();
								bDataErr = true;
							}
						}
						
						P.i("YBYB", "结束时间>>" + endTime + ">>" + bDataErr);
						/*P.i("YBYB", "结束时间>>" + cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.DAY_OF_MONTH)+"  "+
								cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND));*/
						if (!bDataErr
								&& endTime!=null && (curUserdate.getMDHM().compareTo(endTime)<0)) // 结束时间>当前时间
						{
							/*cal.setTimeInMillis(System.currentTimeMillis());
							cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));*/
							try {/*
								cal.set(Calendar.MONTH, (Integer
										.parseInt(mTime0.substring(0, 1))) - 1);
								cal.set(Calendar.DAY_OF_MONTH, Integer
										.parseInt(mTime0.substring(3, 4)));
								cal.set(Calendar.HOUR_OF_DAY,
										Integer.parseInt(mTime2[0].trim()));
								cal.set(Calendar.MINUTE,
										Integer.parseInt(mTime2[1].trim()) - 1); // -1
																					// 提前1分钟
								cal.set(Calendar.SECOND, 0);
								cal.set(Calendar.MILLISECOND, 0);
								*/
								int day = Integer.parseInt(mTime2[1].trim());
								if(day>0)//简单处理:如果是整点就不提前1分钟
									day -=1;
								if(day<10)
									tmpday = "0"+day;
								else {
									tmpday = ""+day;
								}
																
								startTime = mTime0[0].trim() + mTime0[1].trim()+ mTime2[0].trim()+tmpday;
								
							} catch (Exception e) {
								e.printStackTrace();
								bDataErr = true;
							}

							
							P.i("YBYB", "开始时间>>" + startTime + ">>" + bDataErr);
							
							if (!bDataErr
									&& startTime!=null && (curUserdate.getMDHM().compareTo(startTime)>=0)) // 当前时间>开始时间
							{
								P.i("YBYB", "即将开始播放  >>>" + bookInfo.bookEnventName);

								Intent EpgWarn = new Intent(mcontext, 
										EpgWarn.class);
								EpgWarn.putExtra("bookinfo", bookInfo);
								EpgWarn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								mcontext.startActivity(EpgWarn);
								
								P.i("YBYB", "startActivity  >>>" + bookInfo.bookEnventName);
								
								threadStatus = 0;
								break;
							}
						} else {

							bDataErr = true;
						}

						if (bDataErr) {
							P.i("YBYB", "删除过期预约的节目：" + bookInfo.bookEnventName);
							delArray.add(bookInfo);
						}
					}
					if (delArray.size() > 0) {
						for (int i = 0; i < delArray.size(); i++) {
							dvbBookDataBase.RemoveOneBookInfo(
									delArray.get(i).bookDay,
									delArray.get(i).bookTimeStart);
						}
						threadStatus = 1;
					}
				} else {
					P.e("YBYB", "ChannelBook list is null  !");
					threadStatus = 0;
				}
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			P.d("YBYB", "Book  Thread  finish!");
			bookThread = null;

		}

	}

}
