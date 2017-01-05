package com.changhong.app.dtv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

/** ʵ���޽��������Ч�� */
public class CAMarquee extends TextView {
	private float textLength = 0f;// �ı�����
	private int viewWidth = 1800;
	private float step = 0f;// ���ֵĺ����
	private float y = 0f;// ���ֵ������
	private float temp_view_plus_text_length = 0.0f;// ���ڼ������ʱ����
	private float temp_view_plus_two_text_length = 0.0f;// ���ڼ������ʱ����
	public boolean isStarting = false;// �Ƿ�ʼ����
	private static Paint paint = null;// ��ͼ��ʽ
	private String text = "";// �ı�����

	public CAMarquee(Context context) {
		super(context);
	}

	public CAMarquee(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CAMarquee(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public boolean isFocused() {
		return true;
	}

	public void init(WindowManager windowManager) {
		paint = getPaint();
		if (paint == null) {
			Log.e("TEST", "============NULL");
			return;
		}
		text = getText().toString();
		textLength = paint.measureText(text);
		viewWidth -= 30;
		setWidth(viewWidth);
		if (viewWidth <= 0) {
			if (windowManager != null) {
				Display display = windowManager.getDefaultDisplay();
				viewWidth = display.getWidth();
			}
		}
		step = textLength;
		temp_view_plus_text_length = viewWidth + textLength;
		temp_view_plus_two_text_length = viewWidth + textLength * 2;
		y = getTextSize() + getPaddingTop();

	}

	public void onDraw(Canvas canvas) {
		if (paint == null) {
			Log.e("TEST", "============NULL");
			return;
		}

		canvas.drawText(text, temp_view_plus_text_length - step, y, paint);
		if (!isStarting) {
			return;
		}
		step += 5;// 1Ϊ���ֹ����ٶȡ�

		if (step > temp_view_plus_two_text_length + viewWidth / 20) {
			Log.i("TEST", "============= loop end ===========");
//			step = textLength;
//			stopScroll(false);
			step=0;
		}
		invalidate();
		setText("");
		super.onDraw(canvas);
	}

	public void startScroll() {
		isStarting = true;

	}

	public void stopScroll(boolean setInvisible) {
		isStarting = false;
	}

	public boolean isScrolling() {
		return isStarting;
	}
}
