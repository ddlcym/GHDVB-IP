package com.changhong.app.dtv;

import org.json.JSONException;
import org.json.JSONObject;

import com.changhong.app.constant.Advertise_Constant;

import android.util.Log;

public class P {
	private static final String TAG = "GHLive";
	private static final boolean b_NomalInfo = true;
	private static final boolean b_DebugInfo = true;
	private static final boolean b_ERRInfo = true;

	public static void i(String mMessage) {
		if (b_NomalInfo) {
			Log.i(TAG, mMessage);
		}
	}
	public static void i(String subTAG,String mMessage) {
		if (b_NomalInfo) {
			Log.i(subTAG, mMessage);
		}
	}
	public static void d(String mMessage) {
		if (b_DebugInfo) {
			Log.d(TAG, mMessage);
		}
	}
	public static void d(String subTAG,String mMessage) {
		if (b_DebugInfo) {
			Log.d(subTAG, mMessage);
		}
	}
	public static void e(String mMessage) {
		if (b_ERRInfo) {
			Log.e(TAG, mMessage);
		}
	}
	public static void e(String subTAG,String mMessage) {
		if (b_ERRInfo) {
			Log.e(subTAG, mMessage);
		}
	}
}
