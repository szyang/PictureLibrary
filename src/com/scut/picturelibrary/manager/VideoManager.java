package com.scut.picturelibrary.manager;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.SurfaceHolder;

/**
 * 视频播放管理
 * 
 * @author cyc
 * 
 */
public class VideoManager {

	private MediaPlayer mediaPlayer;

	public VideoManager(Context context) {
		super();
		mediaPlayer = new MediaPlayer();
	}

	// 返回MediaPlayer
	public MediaPlayer getMyMediaPlayer() {
		return mediaPlayer;

	}

	// 播放
	public void play(String filePath, SurfaceHolder holder,
			OnPreparedListener mOnPreparedListener) {
		try {
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// 设置播放源
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.setDisplay(holder);
			mediaPlayer.prepareAsync();
			mediaPlayer.setOnPreparedListener(mOnPreparedListener);

			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer arg0) {
					//播放完毕时回调
					}
			});

			mediaPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
					// 发生错误
					return false;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 时间显示函数
	public String ShowTime(int time) {
		time /= 1000;
		int minute = time / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

	// 暂停
	public void pause() {
		mediaPlayer.pause();
	}

}
