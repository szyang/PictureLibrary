package com.scut.picturelibrary.activity;

import android.content.DialogInterface;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.scut.picturelibrary.R;
import com.scut.picturelibrary.adapter.MediaFilesAdapter;
import com.scut.picturelibrary.loader.MediaFilesCursorLoader;
import com.scut.picturelibrary.views.DialogManager;

/**
 * 显示某文件夹下所有图片视频文件
 * 
 * @author 黄建斌
 * 
 */
public class MediaFilesActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor> {
	/**
	 * 用于展示文件夹的GridView
	 */
	private GridView mGridView;
	private final int LOAD_ID = 0x20150405;

	/**
	 * GridView的适配器
	 */
	private MediaFilesAdapter mAdapter;

	private final String SORT_BY_NAME = MediaStore.Images.Media.DISPLAY_NAME;
	private final String SORT_BY_DATE = MediaStore.Images.Media.DATE_MODIFIED;

	private String mSort = SORT_BY_NAME;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_files);
		// 进行cursorloader初始化
		getSupportLoaderManager().initLoader(LOAD_ID, null, this);
		// 初始化视图
		initView();
		// 设置监听器
		initListener();
	}

	private void initView() {
		mGridView = (GridView) findViewById(R.id.grid_files);
		mAdapter = new MediaFilesAdapter(this, null);
		mGridView.setAdapter(mAdapter);
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
				// TODO 点击显示完整图片or播放视频
				// 目前是调用外部程序
				String path = mAdapter.getPath(position);
				Intent it = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.parse("file:///" + path);

				if (mAdapter.getType(position).equals("video")) {// 视频
					Intent intent = new Intent();
					intent.setClass(MediaFilesActivity.this,
							VideoActivity.class);
					intent.putExtra("filePath", path);
					startActivity(intent);
				} else { // 图片
					it.setDataAndType(uri, "image/*");
					startActivity(it);
				}

			}
		});
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mAdapter.getType(position).equals("video")) {// 视频
				} else { // 图片
					final String path = mAdapter.getPath(position);
					final String filename = mAdapter.getTitle(position);
					DialogManager.showImageItemMenuDialog(
							MediaFilesActivity.this, filename,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										Intent intent = new Intent();
										intent.setClass(
												MediaFilesActivity.this,
												RecognizeImageActivity.class);
										intent.putExtra("path", path);
										intent.putExtra("filename", filename);
										MediaFilesActivity.this
												.startActivity(intent);
										break;

									default:
										break;
									}

								}
							});
				}
				return false;
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
		Intent intentMedia = new Intent();
		switch (id) {// 根据选项进行排序
		case R.id.action_sort_name:
			return resort(SORT_BY_NAME);
		case R.id.action_sort_date:
			return resort(SORT_BY_DATE);
			// 开始拍照或录像
		case R.id.action_activity_camera:
			intentMedia.setClass(MediaFilesActivity.this,
					CameraActivity.class);
			startActivity(intentMedia);
			break;
		case R.id.action_activity_recorder:
			intentMedia.setClass(MediaFilesActivity.this,
					MediaRecorderActivity.class);
			startActivity(intentMedia);
			break;
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
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
		String bucketId = getIntent().getStringExtra("bucketId");
		// 创建目标cursor，显示文件夹下图片视频文件
		return new MediaFilesCursorLoader(this, new String[] {
				MediaStore.Images.Media._ID,// 文件ID
				MediaStore.Images.Media.BUCKET_ID, // 文件夹ID
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // 直接包含该图片文件的文件夹名
				MediaStore.Images.Media.DATE_MODIFIED,// 修改日期
				MediaStore.Images.Media.DISPLAY_NAME, // 图片文件名
				MediaStore.Images.Media.DATA // 图片绝对路径
				}, "bucket_id = " + bucketId, null, mSort);
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
