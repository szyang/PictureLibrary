package com.scut.picturelibrary.manager;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;

import com.scut.picturelibrary.utils.CameraCheck;
import com.scut.picturelibrary.utils.FileUtil;

/**
 * 录像管理
 * 
 * @author cyc
 * 
 */
public class MediaRecorderManager {

	public static final int MEDIA_TYPE_RECORDER = 3;

	private Camera mCamera;

	private MediaRecorder mediaRecorder;

	public MediaRecorderManager(Context context) {
		super();
		mediaRecorder = new MediaRecorder();
	}

	public Camera getMyCamera(Context context) {
		mCamera = CameraCheck.getCameraInstance(context);
		mCamera.setDisplayOrientation(90);
		return mCamera;
	}

	public MediaRecorder getMyMediaRecorder() {
		return mediaRecorder;
	}

	@SuppressLint("InlinedApi")
	public void startRecord(Camera camera, SurfaceHolder holder) {
		camera.unlock();
		mediaRecorder.setCamera(camera);
		// 设置录音源
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// 设置视频源
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		// 设置视频和声音的编码
		mediaRecorder.setProfile(CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH));
		mediaRecorder.setOutputFile(FileUtil.getOutPutMediaFile(
				MEDIA_TYPE_RECORDER).toString());
		mediaRecorder.setPreviewDisplay(holder.getSurface());
		// 设置视频最长录制时间为10分钟
		mediaRecorder.setMaxDuration(10 * 60 * 1000);
		try {
			mediaRecorder.prepare();
			mediaRecorder.start();
		} catch (IllegalStateException e) {
			releaseMediaRecorder(camera);
			return;
		} catch (IOException e) {
			releaseMediaRecorder(camera);
			return;
		}
	}

	public void releaseMediaRecorder(Camera camera) {
		if (mediaRecorder != null) {
			mediaRecorder.stop();
			mediaRecorder.reset();
			mediaRecorder.release();
			mediaRecorder = null;
			camera.lock();
		}
	}

}
