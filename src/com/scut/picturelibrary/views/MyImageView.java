package com.scut.picturelibrary.views;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

/**
 * 显示图片，进行缩放，移动操作
 * 
 * @author 邝岳臻
 * 
 */
public class MyImageView extends ImageView implements OnGlobalLayoutListener,
		OnScaleGestureListener, OnTouchListener {

	private boolean mOnce;

	// 图片的初始、双击放大、最大的缩放值
	private float mInitScale;
	private float mMidScale;
	private float mMaxScale;

	private Matrix mScaleMatrix;

	// 得到手指缩放值
	private ScaleGestureDetector mScaleGestureDetector;
	// 双击事件,单击事件
	private GestureDetector mGestureDetector;

	private boolean scale_ing;

	// 触屏点的数量,移动
	private int mPointerCount;
	private float mX0;
	private float mY0;
	private int mTouchSlop;
	private boolean isCanDrag;

	// 判断方向
	private float last_x;
	private float present_x;
	private int direct;

	// 要否检测上下左右白边情况
	private boolean checkLeftRight;
	private boolean checkTopBottom;

	public MyImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mScaleMatrix = new Matrix();
		setScaleType(ScaleType.MATRIX);

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		direct = 0;
		present_x = 0;
		last_x = 0;

		// 双击事件,单击事件
		mGestureDetector = new GestureDetector(context,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDoubleTap(MotionEvent event) {
						if (scale_ing)
							return true;

						Log.v("Double Tap", "Double Tap");

						float x = event.getX();
						float y = event.getY();

						if (getScale() < mMidScale) {
							// 触发动画
							scale_ing = true;
							postDelayed(new ScaleAnimation(mMidScale, x, y), 10);

						} else {
							// 触发动画
							scale_ing = true;
							postDelayed(new ScaleAnimation(mInitScale, x, y),
									10);
						}

						return true;
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {

						if (scale_ing)
							return true;
						if (mOnSingleTouchListener != null){
							mOnSingleTouchListener.onSingleTouch(
									MyImageView.this, e,singleTap);
							singleTap = !singleTap;
						}
						Log.v("Single Tap", "Single Tap");
						return false;
					}

				});

		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		setOnTouchListener(this);

	}
private boolean singleTap = false;
	public void setOnSingleTouchListener(OnSingleTouchListener l) {
		mOnSingleTouchListener = l;
	}

	OnSingleTouchListener mOnSingleTouchListener;

	public interface OnSingleTouchListener {
		public void onSingleTouch(View v, MotionEvent e,boolean singleTap);
	}

	public MyImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyImageView(Context context) {
		this(context, null);

	}

	// 缩放动画
	private class ScaleAnimation implements Runnable {
		private float targetScale;
		private float x;
		private float y;

		// 每次缩放比例
		private final float larger = 1.1f;
		private final float smaller = 0.9f;

		private float t;

		public ScaleAnimation(float targetScale, float x, float y) {

			this.targetScale = targetScale;
			this.x = x;
			this.y = y;

			if (getScale() < targetScale)
				t = larger;
			if (getScale() > targetScale)
				t = smaller;
		}

		@Override
		public void run() {

			mScaleMatrix.postScale(t, t, x, y);
			checkScale();
			setImageMatrix(mScaleMatrix);

			float currentScale = getScale();
			if ((t > 1.0f && currentScale < targetScale)
					|| (t < 1.0f && currentScale > targetScale)) {
				// 继续缩放过程
				postDelayed(this, 10);
			} else {
				// 缩放临界点操作,刚好超过一点点
				mScaleMatrix.postScale(targetScale / currentScale, targetScale
						/ currentScale, x, y);
				checkScale();
				setImageMatrix(mScaleMatrix);
				scale_ing = false;
			}

		}

	}

	// 注册移除接口
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	/**
	 * 获取imageview加载的图片
	 */

	@Override
	public void onGlobalLayout() {

		if (!mOnce) {
			// 屏幕显示图片区域的宽和高
			int width = getWidth();
			int height = getHeight();

			// 图片宽高
			Drawable d = getDrawable();
			if (d == null)
				return;
			int dw = d.getIntrinsicWidth();
			int dh = d.getIntrinsicHeight();

			float scale = 1.0f;
			Log.v("image dw,dh",
					Integer.toString(dw) + "   " + Integer.toString(dh));
			Log.v("screen width,height", Integer.toString(width) + "   "
					+ Integer.toString(height));
			// 改图片初始显示大小
			if (dw > width && dh <= height) {
				scale = width * 1.0f / dw;
			}

			if (dh > height && dw <= width) {
				scale = height * 1.0f / dh;
			}

			if ((dh > height && dw > width) || (dh <= height && dw <= width)) {
				scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
			}

			Log.v("scale", Float.toString(scale));
			mInitScale = scale;
			mMaxScale = mInitScale * 5;
			mMidScale = mInitScale * 2;

			// 使图片居中显示
			int dx = getWidth() / 2 - dw / 2;
			int dy = getHeight() / 2 - dh / 2;

			mScaleMatrix.postTranslate(dx, dy);
			mScaleMatrix.postScale(mInitScale, mInitScale, width / 2,
					height / 2);
			setImageMatrix(mScaleMatrix);

			mOnce = true;
		}

	}

	// 得到图片缩放值
	public float getScale() {
		float[] values = new float[9];
		mScaleMatrix.getValues(values);
		return values[Matrix.MSCALE_X];
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {

		float currentScale = getScale();
		float scaleFactor = detector.getScaleFactor();

		if (getDrawable() == null)
			return true;

		// 范围内允许缩放
		if ((currentScale < mMaxScale && scaleFactor > 1.0f)
				|| (currentScale > mInitScale && scaleFactor < 1.0f)) {
			// 缩放范围边界控制
			if (currentScale * scaleFactor > mMaxScale) {
				scaleFactor = mMaxScale / currentScale;
			}
			if (currentScale * scaleFactor < mInitScale) {
				scaleFactor = mInitScale / currentScale;
			}

			mScaleMatrix.postScale(scaleFactor, scaleFactor,
					detector.getFocusX(), detector.getFocusY());

			checkScale();

			setImageMatrix(mScaleMatrix);

		}

		return true;
	}

	// 得到图片缩放后宽高，上下左右边界位置
	private RectF getMatrixRectF() {
		Matrix matrix = mScaleMatrix;
		RectF rectF = new RectF();
		Drawable d = getDrawable();

		if (d != null) {
			rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.mapRect(rectF);
		}

		return rectF;
	}

	// 检测缩放时边界、位置
	private void checkScale() {

		RectF rect = getMatrixRectF();
		float move_x = 0;
		float move_y = 0;
		float width = getWidth();
		float height = getHeight();

		// 不能太小, 显示不能留白边

		if (rect.width() >= width) {
			// 左边白边
			if (rect.left > 0)
				move_x = -rect.left;
			// 右边白边
			if (rect.right < width)
				move_x = width - rect.right;
		}
		if (rect.height() >= height) {
			// 上边白边
			if (rect.top > 0)
				move_y = -rect.top;
			// 下边白边
			if (rect.bottom < height)
				move_y = height - rect.bottom;
		}

		// 若宽或高较小，则居中
		if (rect.width() < width) {
			move_x = width / 2 - rect.left - rect.width() / 2;
		}
		if (rect.height() < height) {
			move_y = height / 2 - rect.top - rect.height() / 2;
		}

		mScaleMatrix.postTranslate(move_x, move_y);

	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {

		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		// 双击中跳过移动操作
		if (mGestureDetector.onTouchEvent(event))
			return true;

		mScaleGestureDetector.onTouchEvent(event);

		// 触控中心点
		float x = 0;
		float y = 0;
		int count = event.getPointerCount();
		for (int i = 0; i < count; i++) {
			x = x + event.getX(i);
			y = y + event.getY(i);
		}
		x = x / count;
		y = y / count;

		// 触控点改变
		if (mPointerCount != count) {
			mX0 = x;
			mY0 = y;
			isCanDrag = false;
		}
		mPointerCount = count;

		RectF rectF = getMatrixRectF();

		last_x = present_x;
		present_x = rectF.centerX();
		if (last_x > present_x)
			direct = -1;
		else if (last_x < present_x)
			direct = 1;

		Log.v("last_x", Float.toString(last_x));
		Log.v("present_x", Float.toString(present_x));
		Log.v("direct", Integer.toString(direct));

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:

			// 放大时移动与换图不冲突
			if ((rectF.width() > (getWidth() + 0.01) || rectF.height() > (getHeight() + 0.01))
					&& (rectF.left != 0 && rectF.right != getWidth())) {
				if (getParent() instanceof ViewParent)
					getParent().requestDisallowInterceptTouchEvent(true);
			}

			break;

		case MotionEvent.ACTION_MOVE:

			if ((rectF.width() > (getWidth() + 0.01) || rectF.height() > (getHeight() + 0.01))
					&& !(rectF.left == 0 && direct == 1)
					&& !(rectF.right == getWidth() && direct == -1)) {
				if (getParent() instanceof ViewParent)
					getParent().requestDisallowInterceptTouchEvent(true);
			}

			float dx = x - mX0;
			float dy = y - mY0;

			// 未移动
			if (!isCanDrag) {
				isCanDrag = isMove(dx, dy);
			}
			// 可移动
			if (isCanDrag) {

				if (getDrawable() != null) {
					checkLeftRight = true;
					checkTopBottom = true;
					// 太小无需移动
					if (rectF.width() < getWidth()) {
						dx = 0;
						checkLeftRight = false;
					}
					if (rectF.height() < getHeight()) {
						dy = 0;
						checkTopBottom = false;
					}

					//
					mScaleMatrix.postTranslate(dx, dy);
					checkMove();
					setImageMatrix(mScaleMatrix);
				}
			}
			mX0 = x;
			mY0 = y;

			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			// 无触控
			mPointerCount = 0;
			break;

		default:
			break;
		}

		return true;
	}

	// 检测移动时边界、位置
	private void checkMove() {

		RectF rectF = getMatrixRectF();

		float move_x = 0;
		float move_y = 0;
		float width = getWidth();
		float height = getHeight();

		if (rectF.left > 0 && checkLeftRight) {
			move_x = -rectF.left;
		}
		if (rectF.right < width && checkLeftRight) {
			move_x = width - rectF.right;
		}
		if (rectF.top > 0 && checkTopBottom) {
			move_y = -rectF.top;
		}
		if (rectF.bottom < height && checkTopBottom) {
			move_y = height - rectF.bottom;
		}

		mScaleMatrix.postTranslate(move_x, move_y);

	}

	private boolean isMove(float dx, float dy) {

		if (Math.sqrt(dx * dx + dy * dy) > mTouchSlop)
			return true;
		return false;
	}

}
