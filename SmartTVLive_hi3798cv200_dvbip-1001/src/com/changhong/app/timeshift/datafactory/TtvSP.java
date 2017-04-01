package com.changhong.app.timeshift.datafactory;

import com.changhong.app.timeshift.common.MyApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/** 
 * @author  cym  
 * @date 创建时间：2017年3月30日 下午3:17:09 
 * @version 1.0 
 * @parameter   
 */
public class TtvSP {
	
	private static TtvSP ttvSp=null;
	private static SharedPreferences sp;
	private static Editor ttvEditor;
	private static String ttvFileName="ttvFile";
	public static String ttvData="ttvData";
	public static String ttvDataVersion="ttvVersion";
	
	public static TtvSP getInstance(){
		if(null==ttvSp){
			ttvSp=new TtvSP();
			sp=MyApp.getContext().getSharedPreferences(ttvFileName, Context.MODE_WORLD_READABLE);
			ttvEditor=sp.edit();
		}
		return ttvSp;
	}
	
	public  void setDataString(String key,String value){
		ttvEditor.putString(key, value);
		ttvEditor.commit();
	}
	
	public  void setDataInt(String key,int value){
		ttvEditor.putInt(key, value);
		ttvEditor.commit();
	}
	
	public static String getString(String key){
		return sp.getString(key, "");
	}
	
	public static int getInt(String key){
		return sp.getInt(key, -1);
	}
}
