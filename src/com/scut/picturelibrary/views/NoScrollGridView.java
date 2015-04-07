package com.scut.picturelibrary.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
/**
 * 不可检测自身滚动的GridView 放在ScrollView下时使用，防止滚动检测冲突
 * 
 * @author 黄建斌
 */

public class NoScrollGridView extends GridView {

	public NoScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, mExpandSpec);
	}
}
