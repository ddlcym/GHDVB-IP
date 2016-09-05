package com.changhong.app.timeshift.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeText extends TextView {

	public MarqueeText(Context context) {
		super(context);
	}

	public MarqueeText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MarqueeText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public boolean isFocused() {
		return true;
	}
	
	
}
