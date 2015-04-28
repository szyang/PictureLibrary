package com.scut.picturelibrary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.scut.picturelibrary.R;
import com.scut.picturelibrary.fragment.ImagePagerFragment;

/**
 * 显示大图Activity Fragment
 * 
 * @author 黄建斌
 */
public class SimpleImageActivity extends ActionBarActivity implements OnGestureListener{
	
	private boolean actionbar_appear = true;
	private GestureDetector detector;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(" ");
		String tag = ImagePagerFragment.class.getSimpleName();
		Fragment fr = getSupportFragmentManager().findFragmentByTag(tag);
		if (fr == null) {
			fr = new ImagePagerFragment();
			fr.setArguments(getIntent().getExtras());
		}
		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, fr, tag).commit();
		
		detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
			
			@Override
			public boolean onSingleTapUp(MotionEvent e) {

				actionbar_appear = !actionbar_appear;
				if (actionbar_appear)
					getActionBar().show();
				else {
					getActionBar().hide();
				}
				return true;
			}
			
			@Override
			public boolean onDown(MotionEvent e) {
				
				return true;
			}
			
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		Intent intentMedia = new Intent();
		switch (id) {// 根据选项进行排序
			// 开始拍照或录像
		case R.id.action_activity_camera:
			intentMedia.setClass(SimpleImageActivity.this, CameraActivity.class);
			startActivity(intentMedia);
			break;
		case R.id.action_activity_recorder:
			intentMedia.setClass(SimpleImageActivity.this,
					MediaRecorderActivity.class);
			startActivity(intentMedia);
			break;
		case R.id.action_search:
			Intent intent = new Intent();
			intent.setClass(SimpleImageActivity.this, SearchImageActivity.class);
			SimpleImageActivity.this.startActivity(intent);
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return detector.onTouchEvent(event);
	}
	
	@Override
	public boolean onDown(MotionEvent arg0) {

		return true;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {

		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {

		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		
		actionbar_appear = !actionbar_appear;
		Log.v("actionbar_appear",Boolean.toString(actionbar_appear));
		if (actionbar_appear)
			getActionBar().show();
		else {
			getActionBar().hide();
		}
		return true;
	}
}