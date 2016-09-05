package com.changhong.app.timeshift.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.changhong.app.dtv.R;


/** 
 * @author  cym  
 * @date 创建时间：2016年8月9日 上午9:56:17 
 * @version 1.0 
 * @parameter   
 */
public class MySeekbar extends LinearLayout{
	
	private Context context;
	
	private SeekBar seekBar;
	private TextView curText;
	
	/**
	 * 屏幕宽度
	 */
	private int screenWidth;
	private ViewGroup.LayoutParams layoutParams;
	

	public MySeekbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.context=context;
		initView();
	}

	public MySeekbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context=context;
		initView();
	}

	public MySeekbar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
		initView();
		
	}
	
	
	private void initView(){
		LayoutInflater.from(context).inflate(R.layout.myseekbar, this);
		
		seekBar=(SeekBar)findViewById(R.id.seekbar);
		curText=(TextView)findViewById(R.id.textLayout);
		
//		float moveStep = (float) ((float) seekBar.getProgress() / (float) seekBar.getMax() ) ;
//		int seekwidth=seekBar.getWidth();
//		curText.layout((int) (seekwidth * moveStep), 0, (int)(seekwidth * moveStep)+curText.getWidth(), curText.getHeight());
//		Log.i("mmmm", "myseekbar--maxTimes:" + seekBar.getMax()  + "--seekwidth:" + seekwidth+ "--moveStep:" + moveStep + "--arg1:" + seekBar.getProgress()+"--curText.getWidth:"+curText.getWidth()+"--curText.getHeight:"+curText.getHeight());
	}
	
	public void setMax(int max){
		seekBar.setMax(max);
	}
	
	public void setCurText(String curTime){
		curText.setText(curTime);
	}
	
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener){
		seekBar.setOnSeekBarChangeListener(listener);
	}

	public SeekBar getSeekBar() {
		return seekBar;
	}

	public TextView getCurText() {
		return curText;
	}
	
	
}
