package com.changhong.app.timeshift.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * @author cym
 * @date 创建时间：2016年8月3日 上午10:28:53
 * @version 1.0
 * @parameter
 */
public class DigitalRoot extends LinearLayout {

	private ImageView digital1, digital2, digital3;
	private ArrayList<Drawable> digitalList;

	private Context context;

	public DigitalRoot(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public DigitalRoot(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView();
		// TODO Auto-generated constructor stub
	}

	public DigitalRoot(Context context) {
		super(context);
		this.context = context;
		initView();
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	protected <T> T findView(int id) {
		return (T) findViewById(id);
	}

	protected void initView() {
//		LayoutInflater.from(context).inflate(R.layout.digital_root, this);
//		digital1 = findView(R.id.number1);
//		digital2 = findView(R.id.number2);
//		digital3 = findView(R.id.number3);
//		digitalList = new ArrayList<Drawable>();
//		digitalList.add(getResources().getDrawable(R.drawable.channelno_0));
//		digitalList.add(getResources().getDrawable(R.drawable.channelno_1));
//		digitalList.add(getResources().getDrawable(R.drawable.channelno_2));
//		digitalList.add(getResources().getDrawable(R.drawable.channelno_3));
//		digitalList.add(getResources().getDrawable(R.drawable.channelno_4));
//		digitalList.add(getResources().getDrawable(R.drawable.channelno_5));
//		digitalList.add(getResources().getDrawable(R.drawable.channelno_6));
//		digitalList.add(getResources().getDrawable(R.drawable.channelno_7));
//		digitalList.add(getResources().getDrawable(R.drawable.channelno_8));
//		digitalList.add(getResources().getDrawable(R.drawable.channelno_9));
	}

	public void setData(int no) {
		if (no < 0&&no>999)
			return;
		if (no < 10) {
			digital1.setVisibility(View.GONE);
			digital2.setVisibility(View.GONE);
			digital3.setVisibility(View.VISIBLE);
			digital3.setBackgroundDrawable(digitalList.get(no));
		} else if (no < 100) {
			digital1.setVisibility(View.GONE);
			digital2.setVisibility(View.VISIBLE);
			digital3.setVisibility(View.VISIBLE);
			digital2.setBackgroundDrawable(digitalList.get(no / 10));
			digital3.setBackgroundDrawable(digitalList.get(no % 10));
		} else if(no<1000){
			digital1.setVisibility(View.VISIBLE);
			digital2.setVisibility(View.VISIBLE);
			digital3.setVisibility(View.VISIBLE);
			digital1.setBackgroundDrawable(digitalList.get(no / 100));
			digital2.setBackgroundDrawable(digitalList.get(no / 10 % 10));
			digital3.setBackgroundDrawable(digitalList.get(no % 10));
		}
	}

}
