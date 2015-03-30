package com.scut.picturelibrary.adapter;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.scut.picturelibrary.R;
import com.scut.picturelibrary.utils.ImageLoader;

/**
 * 异步加载的Adapter 保证在大量图下流畅
 * 
 * @author 黄建斌
 * 
 */
public class GridViewAdapter extends ArrayAdapter<String> implements
		OnScrollListener {

	/**
	 * 记录所有正在加载或等待加载的任务。
	 */
	private Set<BitmapWorkerTask> taskCollection;

	private GridView mGridView;
	/**
	 * 第一张可见图片的下标
	 */
	private int mFirstVisibleItem;

	/**
	 * 一屏有多少张图片可见
	 */
	private int mVisibleItemCount;

	/**
	 * 记录是否刚打开程序，用于解决进入程序不滚动屏幕，不会加载图片的问题。
	 */
	private boolean isFirstEnter = true;

	public GridViewAdapter(Context context, int textViewResourceId,
			String[] objects, GridView grid) {
		super(context, textViewResourceId, objects);
		taskCollection = new HashSet<BitmapWorkerTask>();
		mGridView = grid;
		mGridView.setOnScrollListener(this);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String path = getItem(position);
		if (convertView == null) {
			convertView = View.inflate(getContext(), R.layout.grid_item, null);
		}
		ImageView photo = (ImageView) convertView
				.findViewById(R.id.img_grid_item_photo);
		photo.setTag(path);
		setImageView(path, photo);
		return convertView;
	}

	private void setImageView(String key, ImageView imageView) {
		Bitmap bitmap = ImageLoader.getInstance().getBitmapFromMemoryCache(key);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {// TODO 缓存中找不到，设置默认的空白图片
			imageView.setImageResource(R.drawable.ic_launcher);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 仅当GridView静止时才去加载图片，GridView滑动时取消所有正在加载的任务
		if (scrollState == SCROLL_STATE_IDLE) {
			loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
		} else {
			cancelAllTasks();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		// 加载的任务应该由onScrollStateChanged里调用，但首次进入程序时onScrollStateChanged并不会调用，
		// 因此在这里为首次进入程序开启加载任务。
		if (isFirstEnter && visibleItemCount > 0) {
			loadBitmaps(firstVisibleItem, visibleItemCount);
			isFirstEnter = false;
		}
	}

	/**
	 * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
	 * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去加载图片。
	 * 
	 * @param firstVisibleItem
	 *            第一个可见的ImageView的下标
	 * @param visibleItemCount
	 *            屏幕中总共可见的元素数
	 */
	private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
		try {
			for (int i = firstVisibleItem; i < (firstVisibleItem + visibleItemCount); i++) {
				String path = getItem(i);
				Bitmap bitmap = ImageLoader.getInstance()
						.getBitmapFromMemoryCache(path);
				if (bitmap == null) {// 缓存中不存在该图片，进行异步加载
					BitmapWorkerTask task = new BitmapWorkerTask();
					taskCollection.add(task);
					task.execute(path);
				} else {
					ImageView imageView = (ImageView) mGridView
							.findViewWithTag(path);
					if (imageView != null && bitmap != null) {
						imageView.setImageBitmap(bitmap);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取消所有正在加载或等待加载的任务。
	 */
	public void cancelAllTasks() {
		if (taskCollection != null) {
			for (BitmapWorkerTask task : taskCollection) {
				task.cancel(false);
			}
		}
	}

	/**
	 * 异步加载图片的任务。
	 * 
	 */
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

		/**
		 * 图片的路径
		 */
		private String path;

		@Override
		protected Bitmap doInBackground(String... params) {
			path = params[0];
			// 在后台加载图片
			Bitmap bitmap = getBitmap(params[0]);
			if (bitmap != null) {
				// 图片加载完成后缓存到LrcCache中
				ImageLoader.getInstance().addBitmapToMemoryCache(params[0],
						bitmap);
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			// 根据Tag找到相应的ImageView控件，将加载好的图片显示出来。
			ImageView imageView = (ImageView) mGridView.findViewWithTag(path);
			if (imageView != null && bitmap != null) {
				imageView.setImageBitmap(bitmap);
			}
			taskCollection.remove(this);
		}

		private Bitmap getBitmap(String path) {
			return ImageLoader.decodeNormaledBitmapFromResource(path, 100);
		}

	}
}
