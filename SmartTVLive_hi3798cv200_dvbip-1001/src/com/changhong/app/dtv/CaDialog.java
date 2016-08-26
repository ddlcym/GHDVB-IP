package com.changhong.app.dtv;


import android.content.Context;
import android.view.View;
import android.view.WindowManager;

public class CaDialog extends View {  
	 private float mTouchStartX;  
	    private float mTouchStartY;  
	    private float x;  
	    private float y;  
	       
	    private WindowManager wm=(WindowManager)getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);  
	    private WindowManager.LayoutParams wmParams = ((SysApplication)getContext().getApplicationContext()).getMywmParams();  
	   
	 public CaDialog(Context context) {  
	  super(context);    
	 
	 }  
	    
	 
	   
	}
