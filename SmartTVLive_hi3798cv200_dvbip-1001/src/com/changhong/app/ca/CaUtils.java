package com.changhong.app.ca;

import com.changhong.app.dtv.SysApplication;
import com.changhong.app.timeshift.common.MyApp;
import com.changhong.dvb.CA;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.SystemProperties;
import android.text.Layout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class CaUtils{
	
	final String  Tag="CaUtils";
	final int FORCE_SHOW_MAX_VALUE=4;

	//定义0=lockserver; 1=SUPFP; 2=SUPOSD;3=CONTINUSE_WATCH
	boolean isForceShow[]=new boolean[FORCE_SHOW_MAX_VALUE];

	public void initForceShow(){
		int size=isForceShow.length;
		for (int i = 0; i < size; i++) {
			isForceShow[i]=false;
		}
	}
		
	private boolean isForceShow(){
		for (int i = 0; i < FORCE_SHOW_MAX_VALUE; i++) {
			if(isForceShow[i])return true;			
		}
		return false;
	}
	
	
	private void setForceShow(int index,boolean value){
		if(index>= 0 && index<FORCE_SHOW_MAX_VALUE){
			isForceShow[index]=value;
		}
	}
	/**
	 * 获取字符串显示宽度
	 * @param view
	 * @param str
	 * @return
	 */
	public int getDesiredWidth(TextView view,String str){
		TextPaint paint = view.getPaint();
		Rect bounds = new Rect();
		paint.getTextBounds(str, 0, str.length(), bounds);
		int width = bounds.width();		
		return width;
	}
	
	
	public H16AndL16 getH16AndL16(int value) {
		H16AndL16 h16AndL16 = new H16AndL16();
		h16AndL16.H16 = (value >> 16) &  0xff;
		h16AndL16.L16 = value & 0xff;
		return h16AndL16;
	}
	
	
	/**
	 * 禁止用户切换台
	 **/
	public boolean setForcedShow(CA thisCa,boolean isForced,int index){
	
		setForceShow(index,isForced);
		// 锁定用户输入
		if(isForced){
			Log.i(Tag, "now start to CaLockService !!!QW");
			SystemProperties.set("sys.stb.key.blocked", "true");
			ActivityManager am = (ActivityManager) MyApp.getContext().getSystemService(Activity.ACTIVITY_SERVICE);
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			if (!cn.getClassName().contains("CaLockService")) {
				Intent intent = new Intent(MyApp.getContext(), CaLockService.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				MyApp.getContext().startActivity(intent);
			}
		}else{
			
			if(!isForceShow()){
				if(0 == thisCa.getSmartCardStatus()){
				    SystemProperties.set("sys.stb.key.blocked", "false");
				}
				SysApplication.getInstance().unForcedShow();
				Log.i(Tag, " Ca set unLockService !!!");
			}

		}
		return true;
	}

	
	
	public void closeActivity(Object className){
		// 锁定用户输入
		SysApplication.getInstance().finishActivity(className);
	}	
}

class H16AndL16 {
	int H16 = 0;
	int L16 = 0;
}