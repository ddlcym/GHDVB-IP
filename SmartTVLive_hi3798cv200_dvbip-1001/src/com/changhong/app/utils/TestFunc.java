package com.changhong.app.utils;

import java.util.Locale;

import com.changhong.dvb.DVB;
import com.changhong.dvb.ProtoCaNovel.DVB_CA_NOVEL_SERVICE_INFO;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.StaticIpConfiguration;
import android.util.DisplayMetrics;
import android.util.Log;

public class TestFunc {
	 private static int times=0;
	
	public static void testThirdPackage(){
		/*
		Log.i("HUAN", "GET DEVICE INFO>>> model:" 
					+ org.ngb.system.HardwareInfo.getProperty(org.ngb.system.HardwareInfo.STB_MODEL)
					+",hw:"+org.ngb.system.HardwareInfo.getProperty(org.ngb.system.HardwareInfo.HW_VERSION)
					+",opn:"+org.ngb.system.HardwareInfo.getProperty(org.ngb.system.HardwareInfo.STB_PROVIDER)
					+",opi:"+org.ngb.system.HardwareInfo.getProperty(org.ngb.system.HardwareInfo.STB_PROVIDER_ID)
					+",sn:"+org.ngb.system.HardwareInfo.getProperty(org.ngb.system.HardwareInfo.STB_SERIAL_NUMBER)
					+",sw:"+org.ngb.system.SoftwareInfo.getProperty(org.ngb.system.SoftwareInfo.SW_VERSION)
					+",card:"+ org.bgctv.ca.CAMananger.getInstance().getCardSerialNumber());	
		*/
	}
	
	public static void changeAppLanguage(Context context) {
		String sta = "zh"; // : "en";
		Locale myLocale = new Locale(sta);
		Resources res = context.getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		if ((++times) % 2 != 0) {
			conf.locale = Locale.ENGLISH;
		} else {
			conf.locale = Locale.CHINESE;
		}
		res.updateConfiguration(conf, dm);
		Log.i("LANG", "cur language is " + conf.locale);
	}
	
	public static void tt_caPlay(int idx){
		if(idx==0)
			DVB.getManager().getDefaultLivePlayer().tt_play(698000,6875,4,1021,1020,1020,0x1fff,0x1fff,4,2);
		else if(idx==1) {
			DVB.getManager().getDefaultLivePlayer().tt_play(698000,6875,4,1041,1040,1040,0x0419,0x0419,4,2);	
		}
	}
}
