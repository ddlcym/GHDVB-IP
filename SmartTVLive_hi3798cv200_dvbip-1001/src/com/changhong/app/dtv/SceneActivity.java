package com.changhong.app.dtv;

import com.iflytek.xiri.Feedback;
import com.iflytek.xiri.scene.ISceneListener;
import com.iflytek.xiri.scene.Scene;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SceneActivity extends Activity implements ISceneListener{

	private Context context;
	private String sceneJson;
	
	private Scene scene;
	private Feedback feedback;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context=SceneActivity.this;
		scene=new Scene(context);
		feedback=new Feedback(context);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		scene.init(this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		scene.release();
	}

	@Override
	public void onExecute(Intent intent) {
		//解析场景

		// TODO 添加直播中进入时移和退出直播到主页的功能
		feedback.begin(intent);
		if(intent.hasExtra("_scene")
				&&intent.getStringExtra("_scene").equals("Com.smarttv_doggle_newui:TVroot"))
		{
			if(intent.hasExtra("_command"))
			{
				 String command = intent.getStringExtra("_command");
				 if("key1".equals(command))
				 {
					 feedback.feedback("下一个频道", Feedback.SILENCE);
					 //频道切换处理
				 }
				 if("key2".equals(command))
				 {
					 feedback.feedback("上一个频道", Feedback.SILENCE);
					 //频道切换处理
				 }
			}
		}
	
	}

	@Override
	public String onQuery() {
		// 提交场景
		sceneJson="{" + "\"_scene\": \"Com.smarttv_doggle_newui:TVroot\","
                + "\"_commands\": {" + "\"key1\": [ \"下一个频道\", \"频道加\" ],"
                + "\"key2\": [ \"上一个频道\", \"频道减\" ]"+"}" + "}";
		Log.i("TVroot",sceneJson);
		return sceneJson;
	}

}
