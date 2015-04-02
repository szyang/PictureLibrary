package com.scut.picturelibrary.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.scut.picturelibrary.R;
import com.scut.picturelibrary.adapter.PhotoWallAdapter;
import com.scut.picturelibrary.loader.ImageCursorLoader;

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
	private PhotoWallAdapter mAdapter;

	private String mSort = MediaStore.Images.Media.DISPLAY_NAME;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGridView = (GridView) findViewById(R.id.grid_main_photowall);
		// mAdapter = new GridViewAdapter(this, null, mGridView);
		mAdapter = new PhotoWallAdapter(this, null);
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

		mGridView.setAdapter(mAdapter);
		// 进行cursorloader初始化
		getSupportLoaderManager().initLoader(LOAD_ID, null, this);
		// 设置滚动时图片是否暂停加载的监听
		PauseOnScrollListener listener = new PauseOnScrollListener(
				ImageLoader.getInstance(), false, true);
		mGridView.setOnScrollListener(listener);
		// TODO 点击显示图片
		// 目前是调用外部程序
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String path = mAdapter.getPath(position);
				Intent it = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.parse("file:///" + path);

				if (mAdapter.getType(position).equals("video"))
					it.setDataAndType(uri, "video/*");
				else
					it.setDataAndType(uri, "image/*");
				startActivity(it);
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
			return resort(MediaStore.Images.Media.DISPLAY_NAME);
		case R.id.action_sort_date:
			return resort(MediaStore.Images.Media.DATE_MODIFIED);
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
		// mAdapter.cancelAllTasks();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
		// 创建目标cursor
		// mAdapter.setFirstEnter(true);
		return new ImageCursorLoader(this, null, null, mSort);
		// return new ImageVideoCursorLoader(this, projection, null, null,
		// mSort);
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
