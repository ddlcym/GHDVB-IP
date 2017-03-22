package com.changhong.app.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.R.integer;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class CustomToast extends Activity {
	private Toast toast;
	private Field field;
	private Object obj;
	private Method showMethod, hideMethod;
	private Field mViewFeild;

	public CustomToast(Toast toast){
		this.toast = toast;
		reflectionTN();
	}
	public CustomToast(Toast toast,int Duration){
		this.toast = toast;
		this.toast.setDuration(Duration);
		reflectionTN();
	}
	public CustomToast(Toast toast,int Duration,Gravity gravity){
		this.toast = toast;
		this.toast.setDuration(Duration);
		//this.toast.setGravity(gravity,0,0);
		reflectionTN();
	}	
	
	public void update(View view){
		//Log.i("GHLive", "CustomToast update----->0");
		try {
			this.toast.setView(view);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Log.i("GHLive", "CustomToast update----->1");
	}
	
	public void show(){
		//Log.i("GHLive", "CustomToast show----->0");
		try {
			//android4.0以上就要以下处理  
			/*
		    if(Build.VERSION.SDK_INT >14) {  
		        Field mNextViewField = obj.getClass().getDeclaredField("mNextView");  
		        mNextViewField.setAccessible(true);  
		        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		        View v = mToast.getView();  
		        mNextViewField.set(mTN, v);  
		        Method method = mTN.getClass().getDeclaredMethod("show", null);  
		        method.invoke(mTN, null);  
		    }*/		
			mViewFeild.set(obj, toast.getView());
			showMethod.invoke(obj, null);// 调用TN对象的show()方法，显示toast
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Log.i("GHLive", "CustomToast show----->1");
	}
	
	public void hide(){
		//Log.i("GHLive", "CustomToast hide----->0");
		try {
			hideMethod.invoke(obj, null);// 调用TN对象的hide()方法，关闭toast
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Log.i("GHLive", "CustomToast hide----->1");
	}
	
	private void reflectionTN() {
		//Log.i("GHLive", "reflectionTN ----->0");
		try {
			field = toast.getClass().getDeclaredField("mTN");
			field.setAccessible(true);
			obj = field.get(toast);
			showMethod = obj.getClass().getDeclaredMethod("show");
			hideMethod = obj.getClass().getDeclaredMethod("hide");
			mViewFeild = obj.getClass().getDeclaredField("mNextView");
			mViewFeild.setAccessible(true);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Log.i("GHLive", "reflectionTN ----->1");
	}
}
