package com.andlib.lp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 所有item都显示在屏幕上，无法滚动的gridview，可以嵌套在listview里
 */
public class NoScrollGridView extends GridView {

	public NoScrollGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public NoScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	public NoScrollGridView(Context context) {
		super(context);
		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
