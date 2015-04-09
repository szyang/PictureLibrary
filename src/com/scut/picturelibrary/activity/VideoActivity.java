package com.scut.picturelibrary.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.scut.picturelibrary.R;
import com.scut.picturelibrary.manager.SurfaceViewManager;
/**
 * 视频播放界面
 * @author cyc
 *
 */
public class VideoActivity extends Activity implements OnClickListener,
		OnTouchListener {

	public final static int MEDIA_TYPE_VIDEO = 2;
	// 视频播放进度条
	private SeekBar mVideoSeekBar;
	// 视频播放时间
	private TextView mVideoTime;
	// 上一个视频
	private ImageButton mVideoPre;
	// 播放视频
	private ImageButton mVideoPlay;
	// 下一个视频
	private ImageButton mVideoNext;
	// 视频播放界面
	private LinearLayout mVideoView;

	private LinearLayout llPlayLayout;

	private LinearLayout llSeekBarLayout;

	private SurfaceViewManager mSurfaceViewManager;

	private Display mDisplay;

	private MediaPlayer mediaPlayer;
	// 播放的文件路径
	private String filePath;
	// 窗口进入动画
	private Animation windowInAnimation;
	// 窗口退出动画
	private Animation windowOutAnimation;
	// 点击事件的起点
	private int startX;
	// 点击事件的终点
	private int endX;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);

		Intent intent = getIntent();
		filePath = intent.getStringExtra("filePath");
		
		llPlayLayout = (LinearLayout) findViewById(R.id.ll_video_play);
		llSeekBarLayout = (LinearLayout) findViewById(R.id.ll_video_seekbar);

		mVideoSeekBar = (SeekBar) findViewById(R.id.seb_video);
		mVideoSeekBar.setOnSeekBarChangeListener(change);
		mVideoTime = (TextView) findViewById(R.id.tv_video_time);
		mVideoPre = (ImageButton) findViewById(R.id.ibtn_video_pre);
		mVideoPre.setOnClickListener(this);
		mVideoPlay = (ImageButton) findViewById(R.id.ibtn_video_play);
		mVideoPlay.setOnClickListener(this);
		mVideoNext = (ImageButton) findViewById(R.id.ibtn_video_next);
		mVideoNext.setOnClickListener(this);

		windowInAnimation = AnimationUtils
				.loadAnimation(this, R.anim.window_in);
		windowOutAnimation = AnimationUtils.loadAnimation(this,
				R.anim.window_out);

		// 传入的第二个参数为媒体类型，第三个参数为文件路径
		mSurfaceViewManager = new SurfaceViewManager(this, MEDIA_TYPE_VIDEO,
				filePath);
		mediaPlayer = mSurfaceViewManager.getMyMediaPlayer();

		mVideoView = (LinearLayout) findViewById(R.id.ll_vedio_view);
		mVideoView.setOnTouchListener(this);
		mVideoView.addView(mSurfaceViewManager);

		mSurfaceViewManager.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(final MediaPlayer mp) {
				mp.start();
				mp.seekTo(0);
				mDisplay = getWindowManager().getDefaultDisplay();
				// 获得视频的高度和宽度
				int mVideoWidth = mp.getVideoWidth();
				int mVideoHeight = mp.getVideoHeight();
				// 如果video的宽或者高超出了当前屏幕的大小，则要进行缩放
				if (mVideoWidth > mDisplay.getWidth()
						|| mVideoHeight > mDisplay.getHeight()) {
					float wRatio = (float) mVideoWidth
							/ (float) mDisplay.getWidth();
					float hRatio = (float) mVideoHeight
							/ (float) mDisplay.getHeight();
					// 选择大的一个进行缩放
					float ratio = Math.max(wRatio, hRatio);
					mVideoWidth = (int) Math.ceil((float) mVideoWidth / ratio);
					mVideoHeight = (int) Math
							.ceil((float) mVideoHeight / ratio);
					mSurfaceViewManager
							.setLayoutParams(new LinearLayout.LayoutParams(
									mVideoWidth, mVideoHeight));

				}

				mVideoSeekBar.setMax(mp.getDuration());
				new Thread() {

					@Override
					public void run() {
						try {
							mVideoSeekBar.setProgress(mp.getCurrentPosition());
							sleep(500);
						} catch (Exception e) {

						}
					}

				}.start();
			}
		});

	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int eventaction = event.getAction();
		if (eventaction == MotionEvent.ACTION_DOWN) {
			startX = (int) event.getX();
			return true;
		} else if (eventaction == MotionEvent.ACTION_UP) {
			endX = (int) event.getX();
			if (startX == endX) { //如果只是点击，则控制layout的进出，防止在拖动seekbar的时候layout进出
				if (llPlayLayout.getVisibility() == View.VISIBLE) {
					llPlayLayout.setVisibility(View.GONE);
					llPlayLayout.startAnimation(windowOutAnimation);
				} else {
					llPlayLayout.setVisibility(View.VISIBLE);
					llPlayLayout.startAnimation(windowInAnimation);
				}
				if (llSeekBarLayout.getVisibility() == View.VISIBLE) {
					llSeekBarLayout.setVisibility(View.GONE);
					llSeekBarLayout.startAnimation(windowOutAnimation);
				} else {
					llSeekBarLayout.setVisibility(View.VISIBLE);
					llSeekBarLayout.startAnimation(windowInAnimation);

				}
			}
		}

		return false;
	}

	// 进度条监听
	private OnSeekBarChangeListener change = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			int progress = seekBar.getProgress();
			mediaPlayer.seekTo(progress);
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {

		}

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

		}
	};

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

		case R.id.ibtn_video_pre:

			break;
		case R.id.ibtn_video_play:

			// 如果没有正在播放视频，则播放
			if (!mediaPlayer.isPlaying()) {
				mediaPlayer.start();
				mVideoPlay.setImageDrawable(getResources().getDrawable(
						R.drawable.img_video_play));
			} else if (mediaPlayer.isPlaying()) { // 如果正在播放视频，则暂停
				mediaPlayer.pause();
				mVideoPlay.setImageDrawable(getResources().getDrawable(
						R.drawable.img_video_pause));
			}
			break;
		case R.id.ibtn_video_next:

			break;
		}
	}
}
