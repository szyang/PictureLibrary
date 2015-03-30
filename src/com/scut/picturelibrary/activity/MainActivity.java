package com.scut.picturelibrary.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.scut.picturelibrary.R;
import com.scut.picturelibrary.adapter.GridViewAdapter;

/**
 * 主Activity，显示所有图片文件夹 目前显示所有图片
 * 
 * @author 黄建斌
 */
public class MainActivity extends ActionBarActivity {
	/**
	 * 用于展示照片墙的GridView
	 */
	private GridView mGridView;

	/**
	 * GridView的适配器
	 */
	private GridViewAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGridView = (GridView) findViewById(R.id.grid_main_photowall);
		mAdapter = new GridViewAdapter(this, 0, get(), mGridView);
		mGridView.setAdapter(mAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出程序时结束所有的加载任务
		mAdapter.cancelAllTasks();
	}

	/**
	 * 测试用，获取手机上所有图片
	 */
	public String[] get() {
		List<String> list = new ArrayList<String>();
		ContentResolver contentResolver = getContentResolver();
		String[] projection = new String[] { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.BUCKET_ID, // 直接包含该图片文件的文件夹ID，防止在不同下的文件夹重名
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // 直接包含该图片文件的文件夹名
				MediaStore.Images.Media.DISPLAY_NAME, // 图片文件名
				MediaStore.Images.Media.DATA // 图片绝对路径
		};
		// String selection = " 0==0) group by bucket_display_name --(";
		Cursor cursor = contentResolver.query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
				null, "");
		cursor.moveToFirst();
		int fileNum = cursor.getCount();
		Log.d("PicLib", "total num" + fileNum);
		for (int counter = 0; counter < fileNum; counter++) {
			list.add(cursor.getString(cursor
					.getColumnIndex(MediaStore.Images.Media.DATA)));
			// Log.d("PicLib",
			// "---Directory is:"
			// + cursor.getString(cursor
			// .getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
			// + "\n   filename is "
			// + cursor.getString(cursor
			// .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
			// Log.d("PicLib",
			// "    path is "
			// + cursor.getString(cursor
			// .getColumnIndex(MediaStore.Images.Media.DATA)));
			// 获取略缩图
			// Thumbnails.getThumbnail(contentResolver, cursor.getLong(cursor
			// .getColumnIndex(MediaStore.Images.Media._ID)),
			// Thumbnails.MICRO_KIND, new BitmapFactory.Options());
			cursor.moveToNext();
		}
		cursor.close();
		String[] result = new String[list.size()];
		list.toArray(result);
		return result;
	}
}
