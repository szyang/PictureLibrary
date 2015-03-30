package com.scut.picturelibrary.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.scut.picturelibrary.R;
import com.scut.picturelibrary.adapter.GridViewAdapter;

/**
 * 主Activity，显示所有图片文件夹 目前显示所有图片 使用Loader进行Cursor的异步查询和管理
 * 
 * @author 黄建斌
 */
public class MainActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor> {
	/**
	 * 用于展示照片墙的GridView
	 */
	private GridView mGridView;
	private final int LOAD_ID = 0x20150330;

	/**
	 * GridView的适配器
	 */
	private GridViewAdapter mAdapter;

	private String mSort = MediaStore.MediaColumns.TITLE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGridView = (GridView) findViewById(R.id.grid_main_photowall);
		mAdapter = new GridViewAdapter(this, null, mGridView);

		mGridView.setAdapter(mAdapter);
		getSupportLoaderManager().initLoader(LOAD_ID, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_sort_name:
			return resort(MediaStore.MediaColumns.TITLE);
		case R.id.action_sort_date:
			return resort("-" + MediaStore.MediaColumns.DATE_MODIFIED);
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 对cursor进行重新排序
	 * 
	 * @param sort
	 *            排序规则
	 * @return
	 */
	public boolean resort(String sort) {
		if (!mSort.equals(sort)) {// 转换排序规则
			// 设置当前规则
			mSort = sort;
			// 重定cursor
			getSupportLoaderManager().restartLoader(LOAD_ID, null, this);
			return true;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出程序时结束所有的加载任务
		mAdapter.cancelAllTasks();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
		// 创建目标cursor
		String[] projection = new String[] { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.BUCKET_ID, // 直接包含该图片文件的文件夹ID，防止在不同下的文件夹重名
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // 直接包含该图片文件的文件夹名
				MediaStore.Images.Media.DISPLAY_NAME, // 图片文件名
				MediaStore.Images.Media.DATA // 图片绝对路径
		};
		mAdapter.setFirstEnter(true);
		return new CursorLoader(this,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
				null, mSort);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// 将新的cursor传入
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// 取消cursor
		mAdapter.swapCursor(null);
	}
}
