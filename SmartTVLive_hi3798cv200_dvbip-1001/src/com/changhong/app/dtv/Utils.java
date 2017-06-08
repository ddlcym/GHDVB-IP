package com.changhong.app.dtv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.changhong.app.timeshift.common.MyApp;

import android.Manifest.permission;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

	/**
	 * get current time
	 * 
	 * @return current time with format "yyyyMMddHHmmss"
	 */
	public static String getCurTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.CHINESE);
		Date curDate = new Date(System.currentTimeMillis());
		return formatter.format(curDate);
	}

	/**
	 * creat folder if not exists
	 * 
	 * @return is exist or created successfully
	 */
	public static boolean creatFolderIfNotExists(String strFolder) {
		File file = new File(strFolder);
		if (!file.exists()) {
			if (file.mkdirs()) {
				return true;
			} else {
				return false;
			}
		}

		return true;
	}

	public static String getExternalStorageDirectory() {
		String dir = new String();

		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			String line;
			BufferedReader br = new BufferedReader(isr);

			while ((line = br.readLine()) != null) {
				if (line.contains("secure")) {
					continue;
				}

				if (line.contains("asec")) {
					continue;
				}

				if (line.contains("fat")) {
					String columns[] = line.split(" ");

					if (columns != null && columns.length > 1) {
						dir = dir.concat(columns[1] + "/");
					}
				} else if (line.contains("fuse")) {
					String columns[] = line.split(" ");

					if (columns != null && columns.length > 1) {
						dir = dir.concat(columns[1] + "/");
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dir;
	}

	public static boolean setProp(String system_Property, String value) {

		try {
			Runtime.getRuntime().exec(
					"setprop " + system_Property + " " + value);
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
			return false;
		}

		return true;
	}

	public static String getProp(String system_Property) {

		String result;

		Process process = null;

		try {
			process = Runtime.getRuntime().exec("getprop " + system_Property);
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
			return "false";
		}

		InputStreamReader ir = new InputStreamReader(process.getInputStream());

		BufferedReader input = new BufferedReader(ir);

		StringBuilder builder = new StringBuilder();

		String tmpstr;

		try {
			while ((tmpstr = input.readLine()) != null) {

				builder.append(tmpstr);
			}
		} catch (IOException e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		}

		result = builder.toString();

		return result;
	}

	// 将时间字符串 (格式为 yyyy-MM-dd HH:mm:ss )转为时间戳
	public static String coventTimeStringtoTimeStamp(String DateFormat,
			String timeString) {
		String re_time = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
		Date d;
		try {
			d = sdf.parse(timeString);
			long l = d.getTime();
			String str = String.valueOf(l);
			re_time = str.substring(0, 10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return re_time;
	}

	// 将时间戳转为时间字符串 (格式为 yyyy-MM-dd HH:mm:ss )
	public static String coventTimeStamptoTimeString(String DateFormat,
			String timeStamp) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
		long lcc_time = Long.valueOf(timeStamp);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		return re_StrTime;
	}

	// 判断是否有网络连接
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	// 判断WIFI网络是否可用
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	// 判断MOBILE网络是否可用
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	// 获取当前网络连接的类型信息
	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

	 public static int stringNumbers(int numer,String str,String subStr)  
	    {  
	        if (str.indexOf(subStr)==-1)
	        {  
	            return 0;  
	        }  
	        else if(str.indexOf(subStr) != -1)  
	        {  
	        	return stringNumbers(++numer,str.substring(str.indexOf(subStr)+subStr.length()),subStr);  
	        }  
	        return 0;  
	    } 
		public static boolean checkSysSettingMenuOn() {
			String curRunning = getCurRunningPackage(MyApp.getContext());
			if (curRunning.startsWith("com.SysSettings")) {
				P.i("com.SysSettings ON");
				return true;
			}
			P.i("com.SysSettings OFF");
			return false;
		}	
		@SuppressWarnings("deprecation")
		private static String getCurRunningPackage(Context context) {
			
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			
			try {
				RunningTaskInfo runningTask = am.getRunningTasks(1).get(0);
				
				return runningTask.topActivity.getPackageName();
				
			} catch (Exception e) { 
				e.printStackTrace();
				return "";
			}

		}		 
}