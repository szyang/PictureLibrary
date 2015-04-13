package com.scut.picturelibrary.manager;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.scut.picturelibrary.animation.MyCameraButtonAnimation;

/**
 * SurfaceView管理
 * 
 * @author cyc
 * 
 */
public class SurfaceViewManager extends SurfaceView implements
		SurfaceHolder.Callback {
	// 媒体类型，相机或者视频播放器
	private int mediaType;

	private SurfaceHolder mHolder;

	private Context context;

	private Camera mCamera;

	public MediaPlayer mediaPlayer;

	// 播放的文件路径
	private String strFilePath;
	// 视频流的最大播放时长
	public int max;

	private CameraManager mCameraManager;

	private VideoManager mVideoManager;

	private OnPreparedListener mOnPreparedListener;

	// 相机的构造函数
	public SurfaceViewManager(Context context, int type,
			MyCameraButtonAnimation btAnimation) {
		super(context);
		this.context = context;
		this.mediaType = type;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mCameraManager = new CameraManager(context, btAnimation);
	}

	// 视频播放器的构造函数
	public SurfaceViewManager(Context context, int type, String filePath) {
		super(context);
		this.context = context;
		this.mediaType = type;
		this.strFilePath = filePath;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mVideoManager = new VideoManager(context);
		mediaPlayer = mVideoManager.getMyMediaPlayer();
	}

	// mediaType为1时设置相机，为2时设置视频播放器
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int width,
			int height) {
		switch (mediaType) {
		case 1:
			mCamera = mCameraManager.getMyCamera(context);
			if (mHolder.getSurface() == null) {
				return;
			}
			try {
				mCamera.stopPreview();
			} catch (Exception e) {

			}

			try {
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();
			} catch (IOException e) {

			}
			break;

		case 2:

			break;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		switch (mediaType) {
		case 1:
			if (mCamera == null) {
				mCamera = mCameraManager.getMyCamera(context);
			}
			try {
				if (mCamera != null) {
					mCamera.setPreviewDisplay(mHolder);
					mCamera.startPreview();
				}
			} catch (IOException e) {
				if (null != mCamera) {
					mCamera.release();
					mCamera = null;
				}
				e.printStackTrace();
			}

			break;

		case 2:
			mVideoManager.play(strFilePath, mHolder, mOnPreparedListener);
			break;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		switch (mediaType) {
		case 1:
			mCamera.release();
			break;

		case 2:
			
			break;
		}
	}

	public void takePhoto() {
		mCameraManager.takePicture();
	}

	public void setOnPreparedListener(OnPreparedListener listener) {
		mOnPreparedListener = listener;
	}

	public MediaPlayer getMyMediaPlayer() {
		return mediaPlayer;
	}

	public String ShowTime(int currentTime) {
		return mVideoManager.ShowTime(currentTime);
	}
}
