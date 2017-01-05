package com.changhong.app.ca;

import com.changhong.app.dtv.SysApplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.text.Layout;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;


public class CaUtils{
	
	
	
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
	public boolean setForcedShow(Context  context, boolean isForced){
		// 锁定用户输入
		if(isForced){
			ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			if (!cn.getClassName().contains("CaLockService")) {
				Intent intent = new Intent(context, CaLockService.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		}else{
			SysApplication.getInstance().unForcedShow();
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