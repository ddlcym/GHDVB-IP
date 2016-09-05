package com.changhong.app.timeshift.common;

import java.io.File;

import android.app.Application;
import android.content.Context;


public class MyApp extends Application {
	 private static MyApp instance;  
	 public static File epgDBCachePath;
     
	    public static MyApp getContext(){  
	        return instance;  
	    }  
	  
	    @Override  
	    public void onCreate() {  
	        super.onCreate();  
	        instance=this;  
	    }  
	    
	    
}
