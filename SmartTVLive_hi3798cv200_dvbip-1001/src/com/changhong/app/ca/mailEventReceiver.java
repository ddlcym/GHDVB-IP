package com.changhong.app.ca;

import com.changhong.dvb.DVB;
import com.changhong.dvb.ProtoMessage.DVB_CA_TYPE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.Log;

public class mailEventReceiver extends BroadcastReceiver {

	static final String TAG = "mailEvent";

	private Context mContext = null;

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		mContext = context;
		Log.i(TAG, "onReceive newMail msg and action is " + action);

		if (action.equals("chots.action.mailEvent")) {
			int caType = intent.getIntExtra("CaType", 0);
			int eventType = intent.getIntExtra("eventType", -1);
			int eventData = intent.getIntExtra("eventData", -1);
			doWithCaMailEvent(caType, eventType, eventData);
		} else if (action.equals("android.intent.action.BOOT_COMPLETED")) {
			doWithBootBroadcast();
		} else if (action.equals("chots.action.CaCard")) {
			int cardIn = intent.getIntExtra("card_in", 0);
			doWithCardPlug(cardIn);
		}
	}

	private void doWithCaMailEvent(int caType, int eventType, int eventData) {
		Log.i(TAG, "doWithCaMailEvent() ， eventType = " + eventType);
		CaMail objCaMailEvent = new CaMail(mContext);
		objCaMailEvent.ShowIcon(caType, eventType, eventData);
	}

	private void doWithBootBroadcast() {
		CaMail objCaMailEvent = new CaMail(mContext);
		/* check the card status. If no card inserted, not display the icon */
		int cardStatus = DVB.getManager().getCaInstance().getSmartCardStatus();
		if (cardStatus != 0) {
			Log.i(TAG, "No smartcard inserted, return");
			return;
		}
		DVB_CA_TYPE caType = DVB.getManager().getCaInstance().getCurType();
		if (caType == DVB_CA_TYPE.CA_NOVEL) {
			int hasNovelNewMailBootCheck = SystemProperties.getInt(
					"persist.sys.novel.newMail", -1);
			Log.i(TAG, "BootBroadcast neMail hasNovelNewMailBootCheck = "
					+ hasNovelNewMailBootCheck);

			objCaMailEvent.novelShowIcon(hasNovelNewMailBootCheck);
		} else if (caType == DVB_CA_TYPE.CA_SUMA) {
			int hasSumaNewMailBootCheck = SystemProperties.getInt(
					"persist.sys.suma.newMail", -1);
			objCaMailEvent.sumaShowIcon(hasSumaNewMailBootCheck);
		}
		// else if(caType == DVB_CA_TYPE.CA_DVN)
		// {
		// int hasDvnNewMailBootCheck =
		// SystemProperties.getInt("persist.sys.dvn.newMail", -1);
		// objCaMailEvent.sumaShowIcon(hasDvnNewMailBootCheck);
		// }
	}

	private void doWithCardPlug(int cardIn) {
		CaMail objCaMailEvent = new CaMail(mContext);
		if (cardIn == 0)// card out
		{
			objCaMailEvent.hideMailIcon(-1);
		} else if (cardIn == 1)// card in
		{
			DVB_CA_TYPE caType = DVB.getManager().getCaInstance().getCurType();
			if (caType == DVB_CA_TYPE.CA_NOVEL) {
				int hasNovelNewMailBootCheck = SystemProperties.getInt(
						"persist.sys.novel.newMail", -1);
				Log.i(TAG, "11BootBroadcast neMail hasNovelNewMailBootCheck = "
						+ hasNovelNewMailBootCheck);

				objCaMailEvent.novelShowIcon(hasNovelNewMailBootCheck);
			} else if (caType == DVB_CA_TYPE.CA_SUMA) {
				int hasSumaNewMailBootCheck = SystemProperties.getInt(
						"persist.sys.suma.newMail", -1);
				objCaMailEvent.sumaShowIcon(hasSumaNewMailBootCheck);
			}
			// else if(caType == DVB_CA_TYPE.CA_DVN)
			// {
			// int hasDvnNewMailBootCheck =
			// SystemProperties.getInt("persist.sys.dvn.newMail", -1);
			// objCaMailEvent.sumaShowIcon(hasDvnNewMailBootCheck);
			// }
		}
	}
}
