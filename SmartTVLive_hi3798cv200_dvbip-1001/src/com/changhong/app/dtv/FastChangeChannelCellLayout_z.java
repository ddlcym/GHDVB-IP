package com.changhong.app.dtv;


import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FastChangeChannelCellLayout_z extends LinearLayout {


	public  com.changhong.app.dtv.TextMarquee ksht_number = null;
	public TextView PositionID = null;

	private Context mContext;

	public FastChangeChannelCellLayout_z(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(context).inflate(R.layout.ksht_cell_layout, this, true);
		mContext = context;
		
		ksht_number = (com.changhong.app.dtv.TextMarquee)findViewById(R.id.ksht_number);
		PositionID = (TextView)findViewById(R.id.PositionID);
		
	}

	public TextView getPositionID() {
		// TODO Auto-generated method stub
		return PositionID;
	}
	
	public void setHorizontallyScrolling() {
		
	/*	ksht_number.setFocusable(true);
		ksht_number.setFocusableInTouchMode(true);
		ksht_number.setSingleLine(true);
		ksht_number.setEllipsize(TruncateAt.MARQUEE);

		ksht_number.setHorizontallyScrolling(true);
		ksht_number.setMarqueeRepeatLimit(-1);*/
		
	}

	
	
}