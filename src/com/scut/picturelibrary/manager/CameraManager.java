package com.scut.picturelibrary.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.scut.picturelibrary.utils.CameraCheck;
import com.scut.picturelibrary.utils.FileUtil;

/**
 * 拍照管理
 * 
 * @author yc
 * 
 */
public class CameraManager {

	public static final int MEDIA_TYPE_IMAGE = 1;

	private Camera mCamera;

	private Context context;

	int setFixPictureWidth = 0;

	int setFixPictureHeight = 0;

	private int maxPictureSize = 5000000;

	public CameraManager(Context context) {
		super();
		this.context = context;
	}

	// 返回相机
	@SuppressLint("NewApi")
	public Camera getMyCamera(Context context) {
		mCamera = CameraCheck.getCameraInstance(context);
		// 设置预览的角度，因为默认照片是倾斜90度
		mCamera.setDisplayOrientation(90);
		return mCamera;

	}

	@SuppressLint("NewApi")
	// 设置预览时的图像和拍照的参数
	public void setCameraParameters(Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
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
			if (parameters.getMaxNumDetectedFaces() > 0) {
				camera.startFaceDetection();
			}
		}

	}

	// 拍照
	public void takePicture() {
		if (mCamera != null) {
			mCamera.takePicture(null, null, picture);
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
						.getOutPutMediaFile(MEDIA_TYPE_IMAGE);
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
				String filePath = pictureFile.getAbsolutePath();
				String fileName = filePath
						.substring(filePath.lastIndexOf("/") + 1);
				try { // 把文件插入系统图库
					MediaStore.Images.Media.insertImage(
							context.getContentResolver(), filePath, fileName,
							null);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				// 通知图库更新
				context.sendBroadcast(new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
								.parse("file://" + filePath)));

			} else {
				Toast.makeText(context, "SD卡不存在", Toast.LENGTH_SHORT).show();
			}
			return null;
		}
	}
}
