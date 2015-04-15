package com.scut.picturelibrary.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.scut.picturelibrary.R;
import com.scut.picturelibrary.animation.MyCameraButtonAnimation;
import com.scut.picturelibrary.manager.SurfaceViewManager;

/**
 * 拍照界面
 * 
 * @author cyc
 * 
 */
public class CameraActivity extends Activity implements OnClickListener {

	public final static int MEDIA_TYPE_CAMERA = 1;

	// 拍照界面的按钮，返回、分割线、相册、拍照、更多
	private ImageButton mCameraBack;
	private ImageButton mCameraWell;
	private ImageButton mCameraAlbum;
	private ImageButton mCameraTake;
	private ImageButton mCameraMore;
	// 拍照预览
	private FrameLayout mCameraPreview;

	private SurfaceViewManager mSurfaceViewManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		mCameraBack = (ImageButton) findViewById(R.id.ibtn_camera_back);
		mCameraBack.setOnClickListener(this);
		mCameraWell = (ImageButton) findViewById(R.id.ibtn_camera_well);
		mCameraWell.setOnClickListener(this);
		mCameraAlbum = (ImageButton) findViewById(R.id.ibtn_camera_album);
		mCameraAlbum.setOnClickListener(this);
		mCameraTake = (ImageButton) findViewById(R.id.ibtn_camera_take);
		mCameraTake.setOnClickListener(this);
		mCameraMore = (ImageButton) findViewById(R.id.ibtn_camera_more);
		mCameraMore.setOnClickListener(this);
		// 传入第第二个参数为媒体类型，第三个参数为控件动画
		mSurfaceViewManager = new SurfaceViewManager(this, MEDIA_TYPE_CAMERA,
				btAnimation);
		mCameraPreview = (FrameLayout) findViewById(R.id.fl_camera_preview);
		mCameraPreview.addView(mSurfaceViewManager);

	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.ibtn_camera_back:
			finish();
			break;
		case R.id.ibtn_camera_well:

			break;
		case R.id.ibtn_camera_album:

			break;
		case R.id.ibtn_camera_take:
			mSurfaceViewManager.takePhoto();
			break;
		case R.id.ibtn_camera_more:

			break;
		}
	}

	// 传感器方向发生改变时，设置控件旋转动画
	private MyCameraButtonAnimation btAnimation = new MyCameraButtonAnimation() {
		@Override
		public void executeAnimation(Animation animation) {
			mCameraBack.startAnimation(animation);
			mCameraWell.startAnimation(animation);
			mCameraAlbum.startAnimation(animation);
			mCameraTake.startAnimation(animation);
			mCameraMore.startAnimation(animation);

		}
	};

}
