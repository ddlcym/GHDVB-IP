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
			Log.i(TAG, subTAG+">>"+mMessage);
		}
	}
	public static void d(String mMessage) {
		if (b_DebugInfo) {
			Log.d(TAG, mMessage);
		}
	}
	public static void d(String subTAG,String mMessage) {
		if (b_DebugInfo) {
			Log.d(TAG, subTAG+">>"+mMessage);
		}
	}
	public static void e(String mMessage) {
		if (b_ERRInfo) {
			Log.e(TAG, mMessage);
		}
	}
	public static void e(String subTAG,String mMessage) {
		if (b_ERRInfo) {
			Log.e(TAG, subTAG+">>"+mMessage);
		}
	}
	/**
	 * @authorOscarChang
	 * @params to get advertise
	 * @channeId
	 */
	public static JSONObject getParams(String channelId, String positionId, String IpAddress) {
		JSONObject reqParams = new JSONObject();
		try {
			// channelId = "1010";
			// reqParams.put("catalog_id", "catalog_id");//
			// ��VODӦ�û�����������״��ʽ������Ϣ��Ӧ���£���ǰ����ҳ��λ�ñ�ʶ
			reqParams.put("channel_id", channelId);// �ڹ㲥Ӧ���µ�Ƶ����ʶ
			reqParams.put("adposition_id", positionId);// ���λId
			reqParams.put("ip", IpAddress);// IP����Node Group ID�����
			reqParams.put("user_id", Advertise_Constant.CARD_USER_ID);// CA���ܿ�����
			reqParams.put("application_id", Advertise_Constant.APPLICATION_ID);

			// reqParams.put("decision_id", "decision_id");// ����Id
			reqParams.put("device_id", Advertise_Constant.DEVICE_ID);// �豸��ʶ�����
			reqParams.put("device_type", Advertise_Constant.DEVICE_TYPE);// �豸���ͣ��ֻ��������У����
			// reqParams.put("provider_id", "provider_id");
			// reqParams.put("provider_asset_id", "provider_asset_id");
			// reqParams.put("application_path", "application_path");

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return reqParams;
	}

}
