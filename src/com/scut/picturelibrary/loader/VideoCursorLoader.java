package com.scut.picturelibrary.loader;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

/**
 * 配合PhotoAdapter的视频加载器 自动设置好projection和URI
 * 
 * @author 黄建斌
 * 
 */
public class VideoCursorLoader extends CursorLoader {
	public VideoCursorLoader(Context context, String selection,
			String[] selectionArgs, String sortOrder) {
		this(context, new String[] { "'video' as type",
				MediaStore.Video.Media._ID, MediaStore.Video.Media.BUCKET_ID, // 文件夹ID
				MediaStore.Video.Media.BUCKET_DISPLAY_NAME, // 直接包含该视频文件的文件夹名
				MediaStore.Video.Media.DATE_MODIFIED,// 修改日期
				MediaStore.Video.Media.DISPLAY_NAME, // 视频文件名
				MediaStore.Video.Media.DATA, // 视频绝对路径
		}, selection, selectionArgs, sortOrder);
	}

	public VideoCursorLoader(Context context, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		super(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
				selection, selectionArgs, sortOrder);
	}

}
