package com.changhong.app.dtv;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

public class EpgGridview extends GridView {
	public EpgGridview(Context context) {
		super(context);
	}

	public EpgGridview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EpgGridview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {

		int lastSelectItem = getSelectedItemPosition();

		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		if (gainFocus) {

			// save index and top position
			int index1 = getFirstVisiblePosition();
			int index2 = lastSelectItem - index1;
			int left = 0;
			View v = null;

			if (index2 < 0)
				index2 = 0;
			v = getChildAt(index2);
			left = (v == null) ? 0 : v.getLeft();

			// restore
			setSelection(lastSelectItem);
			setSelected(true);
	//		setSelectionInt(lastSelectItem);
		//	setSelector(lastSelectItem);
			//setSelectionFromLeft(lastSelectItem, left);
			// setSelector(R.drawable.focustwo);
		} else {

			int index1 = getFirstVisiblePosition();
			int index2 = lastSelectItem - index1;
			int left = 0;
			View v = null;

			if (index2 < 0)
				index2 = 0;
			v = getChildAt(index2);
			left = (v == null) ? 0 : v.getLeft();
			
			setSelection(lastSelectItem);
			setSelected(true);
		//	setSelector(R.drawable.selectorz3);

		}
	}
}
