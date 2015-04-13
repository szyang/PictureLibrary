package com.scut.picturelibrary.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.OrientationEventListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.scut.picturelibrary.animation.MyCameraButtonAnimation;
import com.scut.picturelibrary.utils.CameraCheck;
import com.scut.picturelibrary.utils.FileUtil;

/**
 * 拍照管理
 * @author yc
 * 
 */
public class CameraManager {

	public static final int MEDIA_TYPE_CAMERA = 1;

	private Camera camera;

	private Context context;

	int setFixPictureWidth = 0;

	int setFixPictureHeight = 0;

	private int maxPictureSize = 5000000;

	// 横竖屏切换时，控件跟着切换的动画
	private MyCameraButtonAnimation buttonAnimation;

	private MyOrientationEventListener orientationEventListener;
	// 最终方向
	private int lastBtOrientation = 0;

	public CameraManager(Context context, MyCameraButtonAnimation btAnimation) {
		super();
		this.context = context;
		this.orientationEventListener = new MyOrientationEventListener(context);
		this.buttonAnimation = btAnimation;

	}
    //返回相机
	public Camera getMyCamera(Context context) {
		camera = CameraCheck.getCameraInstance(context);
		Camera.Parameters parameters = camera.getParameters();
		// 设置预览时的图像和拍照的参数
		parameters.setRotation(90);
		List<Camera.Size> mSupportedsizeList = parameters
				.getSupportedPictureSizes();
		if (mSupportedsizeList.size() > 1) {
			Iterator<Camera.Size> itos = mSupportedsizeList.iterator();
			while (itos.hasNext()) {
				Camera.Size curSize = itos.next();
				int curSupporSize = curSize.width * curSize.height;
				int fixPictrueSize = setFixPictureWidth * setFixPictureHeight;
				if (curSupporSize > fixPictrueSize
						&& curSupporSize <= maxPictureSize) {
					setFixPictureWidth = curSize.width;
					setFixPictureHeight = curSize.height;
				}
			}
		}
		if (setFixPictureWidth != 0 && setFixPictureHeight != 0) {
			parameters.setPictureSize(setFixPictureWidth, setFixPictureHeight);
			parameters.setJpegQuality(100);
			camera.setParameters(parameters);
			// 设置预览的角度，因为默认照片是倾斜90度
			camera.setDisplayOrientation(90);
			if (parameters.getMaxNumDetectedFaces() > 0) {
				camera.startFaceDetection();
			}
		}
		return camera;

	}
    //拍照
	public void takePicture() {
		if (camera != null) {
			camera.takePicture(null, null, picture);
		}
	}

	// 回调方法，接受jpeg格式的图像
	public PictureCallback picture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			new savePictureTask().execute(data);
		}
	};

	// 异步保存照片文件
	public class savePictureTask extends AsyncTask<byte[], String, String> {

		@Override
		protected String doInBackground(byte[]... params) {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File pictureFile = FileUtil
						.getOutPutMediaFile(MEDIA_TYPE_CAMERA);
				if (pictureFile == null) {
					return null;
				}

				try {
					FileOutputStream fos = new FileOutputStream(pictureFile);
					fos.write(params[0]);
					fos.close();
				} catch (FileNotFoundException e) {

				} catch (IOException e) {

				}

			} else {
				Toast.makeText(context, "SD卡不存在", Toast.LENGTH_SHORT).show();
			}
			return null;
		}
	}

	// 横竖屏切换使，监听传感器方向的改变
	public class MyOrientationEventListener extends OrientationEventListener {

		public MyOrientationEventListener(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(int orientation) {
			if (orientation == ORIENTATION_UNKNOWN)
				return;

			int phoneRotation = 0;
			if (orientation > 315 && orientation <= 45) {
				phoneRotation = 0;
			} else if (orientation > 45 && orientation <= 135) {
				phoneRotation = 90;
			} else if (orientation > 135 && orientation <= 225) {
				phoneRotation = 180;
			} else if (orientation > 225 && orientation <= 315) {
				phoneRotation = 270;
			}

			if (phoneRotation == 0 && lastBtOrientation == 360) {
				lastBtOrientation = 0;
			}

			if ((phoneRotation == 0 || lastBtOrientation == 0)
					&& (Math.abs(phoneRotation - lastBtOrientation) > 180)) {
				phoneRotation = phoneRotation == 0 ? 360 : phoneRotation;
				lastBtOrientation = lastBtOrientation == 0 ? 360
						: lastBtOrientation;
			}

			if (phoneRotation != lastBtOrientation) {
				int fromDegress = 360 - lastBtOrientation;
				int toDegrees = 360 - phoneRotation;

				RotateAnimation animation = new RotateAnimation(fromDegress,
						toDegrees, Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				animation.setDuration(1000);
				animation.setFillAfter(true);
				buttonAnimation.executeAnimation(animation);
				lastBtOrientation = phoneRotation;

			}
		}
	}

}
