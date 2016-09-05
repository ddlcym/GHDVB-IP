package com.changhong.app.timeshift.common;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.changhong.app.dtv.SysApplication;

public class VolleyTool {

	private static VolleyTool volleyTool;
	
	private RequestQueue requestQueue;
	public static VolleyTool getInstance(){
		
		if(null==volleyTool){
			volleyTool=new VolleyTool();
		}
		return volleyTool;
	}
	
	public VolleyTool(){
		if(null==requestQueue){
			requestQueue=Volley.newRequestQueue(MyApp.getContext());
		}
	}
	
	public RequestQueue getRequestQueue(){
		return requestQueue;
	}
	
	public void stop(Object tag){
		requestQueue.cancelAll(tag);
	}
}
