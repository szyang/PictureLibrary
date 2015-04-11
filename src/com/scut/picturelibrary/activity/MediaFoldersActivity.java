package com.scut.picturelibrary.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.scut.picturelibrary.R;
import com.scut.picturelibrary.adapter.MediaFoldersAdapter;
import com.scut.picturelibrary.loader.MediaFoldersCursorLoader;

/**
 * 入口Activity，显示所有图片文件夹 使用Loader进行Cursor的异步查询和管理
 * 
 * @author 黄建斌
 */
public class MediaFoldersActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor> {
	@SuppressWarnings("unused")
	private static final String TAG = "FoldersActivity";
	/**
	 * 用于展示文件夹的GridView
	 */
	private GridView mGridView;
	private final int LOAD_ID = 0x20150330;

	/**
	 * GridView的适配器
	 */
	private MediaFoldersAdapter mAdapter;
	private final String SORT_BY_NAME = MediaStore.Images.Media.BUCKET_DISPLAY_NAME;
	private final String SORT_BY_DATE = MediaStore.Images.Media.DATE_MODIFIED;

	private String mSort = SORT_BY_NAME;

	private long mKeyTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folders);
		initImageLoader();
		// 进行cursorloader初始化
		getSupportLoaderManager().initLoader(LOAD_ID, null, this);
		// 初始化视图
		initView();
		// 设置监听器
		initListener();
	}

	private void initView() {
		mGridView = (GridView) findViewById(R.id.grid_folders);
		mAdapter = new MediaFoldersAdapter(this, null);
		mGridView.setAdapter(mAdapter);
	}

	private void initImageLoader() {
		// 设置图片显示选项
		DisplayImageOptions displayOp = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.bg_loading)// 图片正在加载时显示的背景
				.cacheInMemory(true)// 缓存在内存中
				.cacheOnDisk(true)// 缓存在磁盘中
				.displayer(new FadeInBitmapDisplayer(400))// 显示渐变动画
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 按ImageView的scaleType进行压缩
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(displayOp).build();
		ImageLoader.getInstance().init(config);
	}

	private void initListener() {
		// 设置滚动时图片是否暂停加载的监听
		PauseOnScrollListener listener = new PauseOnScrollListener(
				ImageLoader.getInstance(), false, true);
		mGridView.setOnScrollListener(listener);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String bucketId = mAdapter.getBucketId(position);
				mAdapter.getCursor().moveToPosition(position);
				Intent intent = new Intent();
				intent.putExtra("bucketId", bucketId);
				intent.setClass(MediaFoldersActivity.this,
						MediaFilesActivity.class);
				MediaFoldersActivity.this.startActivity(intent);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {// 根据选项进行排序
		case R.id.action_sort_name:
			return resort(SORT_BY_NAME);
		case R.id.action_sort_date:
			return resort(SORT_BY_DATE);
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
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
		return new MediaFoldersCursorLoader(this, mSort);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mKeyTime) > 2000) {
				mKeyTime = System.currentTimeMillis();
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_LONG).show();
			} else {
				finish();
			}

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
