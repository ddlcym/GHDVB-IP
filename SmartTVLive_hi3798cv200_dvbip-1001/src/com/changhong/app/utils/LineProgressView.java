package com.changhong.app.utils;


import com.changhong.app.dtv.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;

/**
 * 
 * @author yangtong
 *
 */
public class LineProgressView extends View {

	public int progress = 0;
	public int curProgress = 0;
	public static final int PROGRESS_MAX = 100;
	public static final String TAG = "LineProgressView";

	private Paint progressPaint, bkgPaint;
	private int progressColor = 0x70FFFFFF; // 黑色
	private int bkgColor = 0x87CEEB; // 背景色：灰色
	
	private float lineWidth = 10.0f;

	public LineProgressView(Context context, int progress) {
		super(context);
		init(progress);
	}

	public LineProgressView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.LineProgress, defStyleAttr,0);
		int count = a.getIndexCount();
		for(int i=0;i<count;i++){
			int attr = a.getIndex(i);
			switch (attr) {
//			case R.styleable.LineProgress_bkg_color:
//				bkgColor = a.getColor(attr, bkgColor);
//				break;
			case R.styleable.LineProgress_progress_color:
				progressColor = a.getColor(attr, progressColor);
				break;
			case R.styleable.LineProgress_progress:
				progress = a.getInteger(attr, 0);
				break;
			default:
				break;
			}
		}
		init(progress);
	}

	public LineProgressView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LineProgressView(Context context) {
		super(context);
		init(progress);
	}

	public void init(int progress) {
		this.progress = progress;

		progressPaint = new Paint();
		progressPaint.setAntiAlias(true);
		progressPaint.setColor(progressColor);
		progressPaint.setStrokeWidth(lineWidth);

		bkgPaint = new Paint();
		bkgPaint.setAntiAlias(true);
		bkgPaint.setColor(Color.WHITE);
		bkgPaint.setStrokeWidth(lineWidth);

//		this.getViewTreeObserver().addOnPreDrawListener(
//				new OnPreDrawListener() {
//
//					@Override
//					public boolean onPreDraw() {
//						// TODO Auto-generated method stub
//						MyThread myThread = new MyThread();
//						myThread.start();
//						getViewTreeObserver().removeOnPreDrawListener(this);
//						return false;
//					}
//				});
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawLine(0, 0, canvas.getWidth(), 0, bkgPaint);
//		Log.i("yangtong","bkg"+canvas.getWidth());
//		Log.i("yangtong","prgs  "+progress * canvas.getWidth() / PROGRESS_MAX);
		canvas.drawLine(0, 0, progress * canvas.getWidth() / PROGRESS_MAX,
				0, progressPaint);
	}

	public void setProgress(int progress) {
		if (progress >= 0 && progress <= 100) {
			this.progress = progress;
		} else if (progress < 0) {
			this.progress = 0;
		} else if (progress > PROGRESS_MAX) {
			this.progress = PROGRESS_MAX;
		}

		invalidate();
	}

	
	public int getProgress(){
		return this.progress;
	}
	
	
	/**
	 * 标记当前是否需要执行动画
	 */
	public boolean sleep = false;

	/**
	 * 动画效果的实现
	 *
	 */
	class MyThread implements Runnable {

		private Thread thread;

		public void start() {
			thread = new Thread(this);
			thread.start();
		}

		public void run() {
			while (true) {
				if (sleep) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					if (curProgress < progress) {
						curProgress++;
						postInvalidate();
					} else if (curProgress > progress) {
						curProgress--;
						postInvalidate();
					} else if (curProgress == progress) {
						sleep = true;
					}
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
