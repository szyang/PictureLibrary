package com.scut.picturelibrary.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/***
 * 不可检测自身滚动的ListView 放在ScrollView下时使用，防止滚动检测冲突
 * 
 * @author 黄建斌
 * 
 */
public class NoScrollListView extends ListView {

	public NoScrollListView(Context context) {
		super(context);
	}

	public NoScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoScrollListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
