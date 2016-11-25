package com.changhong.app.timeshift.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class CommonMethod {

	public static String cmdFreeze = "setprop sys.media.hiplayer.freezemode " + "\"" + "freeze" + "\"";
	public static String cmdBlack = "setprop sys.media.hiplayer.freezemode " + "\"" + "black" + "\"";

	public static void startSettingPage(Context outterContext) {
		Intent mIntent = new Intent();
		mIntent.setComponent(new ComponentName(
				"com.SysSettings.main",
				"com.SysSettings.main.MainActivity"));
		try { 
			outterContext.startActivity(mIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void excuteCmd(String cmd) throws Exception {
		Process process = Runtime.getRuntime().exec(new String[] { "/system/bin/sh", "-c", cmd });
		process.waitFor();

		InputStream is = process.getInputStream();
		InputStreamReader ir = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(ir);

		String line = "";
		StringBuilder sb = new StringBuilder(line);
		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append('\n');
		}
		String result = sb.toString();
	}

	/* save mute state */
	public static void saveMutesState(String muteState, Context mContext) {
		if (mContext != null) {
			SharedPreferences preferences = mContext.getSharedPreferences("mute_setting", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();

			editor.putString("UPDATE_MUTE", muteState);
			editor.commit();
		}
	}

	/* get mute state */
	public static String getMuteState(Context mContext) {
//		SharedPreferences preferences = mContext.getSharedPreferences("mute_setting", Context.MODE_PRIVATE);
//		return preferences.getString("UPDATE_MUTE", "");
		return "";
	}

	/* get mute state */
	public static int getChannelLastTime(Context mContext) {
		SharedPreferences preferences = mContext.getSharedPreferences("lastchannel_wathing", Context.MODE_PRIVATE);
		return preferences.getInt("CHANNEL_LAST_TIME", Class_Constant.DEFAULT_CHANNEL_NUMBER);
	}
	
	/* save channel number last watched */
	public static void saveChannelLastTime(int channelLastTime, Context mContext) {
		if (mContext != null) {
			SharedPreferences preferences = mContext.getSharedPreferences("lastchannel_wathing", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			
			editor.putInt("CHANNEL_LAST_TIME", channelLastTime);
			editor.commit();
		}
	}
}
