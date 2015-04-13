package com.scut.picturelibrary.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.scut.picturelibrary.R;

/**
 * 使用外部ImageLoader库的Adapter 同时显示图片和视频的略缩图
 * 
 * @author 黄建斌
 * 
 */
public class MediaFilesAdapter extends CursorAdapter {

	@SuppressWarnings("unused")
	private static final String TAG = "MediaFilesAdapter";
	private int mImageHoverResource = 0;
	private int mVideoHoverResource = R.drawable.ic_play;

	public MediaFilesAdapter(Context context, Cursor c) {
		super(context, c, true);
	}

	public void setImageHoverResource(int id) {
		mImageHoverResource = id;
	}

	public void setVideoHoverResource(int id) {
		mVideoHoverResource = id;
	}

	@Override
	public void bindView(View v, Context context, Cursor cursor) {
		if (cursor == null)
			return;
		ViewHolder viewHolder = (ViewHolder) v.getTag();
		// 使用外部库ImageLoader进行图片缓存和异步加载显示
		int typeIndex = cursor.getColumnIndex("type");
		String id = cursor.getString(cursor
				.getColumnIndex(MediaStore.Images.Media._ID));
		if (typeIndex >= 0 && cursor.getString(typeIndex).equals("video")) {
			// 视频格式的略缩图
			ImageLoader.getInstance().displayImage(
					"content://media/external/video/media/" + id,
					viewHolder.imgThumbnail);
			viewHolder.imgHover.setImageResource(mVideoHoverResource);
		} else {
			// 图片略缩图
			ImageLoader.getInstance().displayImage(
					"content://media/external/images/media/" + id,
					viewHolder.imgThumbnail);
			viewHolder.imgHover.setImageResource(mImageHoverResource);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.grid_files_item, parent, false);
		ImageView photo = (ImageView) v
				.findViewById(R.id.img_grid_files_item_photo);
		ImageView hover = (ImageView) v
				.findViewById(R.id.img_grid_files_item_hover);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.imgThumbnail = photo;
		viewHolder.imgHover = hover;
		// 设置Tag
		v.setTag(viewHolder);
		return v;
	}

	/**
	 * 获取要显示的图片的路径
	 * 
	 * @param index
	 * @return
	 */
	public String getPath(int index) {
		Cursor c = getCursor();
		c.moveToPosition(index);
		return c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
	}

	public String getType(int index) {
		Cursor c = getCursor();
		c.moveToPosition(index);
		return c.getString(c.getColumnIndex("type"));
	}

	public String getTitle(int index) {
		Cursor c = getCursor();
		c.moveToPosition(index);
		return c.getString(c
				.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
	}

	/**
	 * 静态类 防止内存泄漏
	 * 
	 * @author 黄建斌
	 * 
	 */
	static class ViewHolder {
		ImageView imgThumbnail;
		ImageView imgHover;
	}
}
