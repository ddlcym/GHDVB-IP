package com.changhong.app.timeshift.common;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/** 实现无焦点跑马灯效果 */
public class TextMarquee extends TextView {

	public TextMarquee(Context context) {
		super(context);
	}

	public TextMarquee(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TextMarquee(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public boolean isFocused() {
		return true;
	}

	@Override
	protected void onDraw(Canvas arg0) {

		super.onDraw(arg0);
	}

}
