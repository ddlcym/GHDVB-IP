package com.changhong.app.timeshift.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.changhong.app.dtv.R;

/**
 * @author cym
 * @date 创建时间：2016年8月22日 下午5:47:00
 * @version 1.0
 * @parameter
 */
public class PlayButton extends ImageView {

	public final static int Play = 0;
	public final static int Pause = 1;
	public final static int Forward = 2;
	public final static int Backward = 3;

	private int[] playDrawable;

	public PlayButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public PlayButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public PlayButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public void init() {
		if (null==playDrawable||playDrawable.length != 4) {
			playDrawable = new int[] { R.drawable.play, R.drawable.pause,
					R.drawable.fast_forward, R.drawable.fast_backward };
		}
	}

	public void setMyBG(int type) {
		this.setBackgroundResource(playDrawable[type]);
	}
}
