package com.scut.picturelibrary.loader;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

/**
 * 配合PhotoAdapter的图片加载器 自动设置好projection和URI
 * 
 * @author 黄建斌
 * 
 */
public class ImageCursorLoader extends CursorLoader {

	public ImageCursorLoader(Context context, String selection,
			String[] selectionArgs, String sortOrder) {
		this(context, new String[] { "'image' as type",
				MediaStore.Images.Media._ID, 
				MediaStore.Images.Media.BUCKET_ID, // 文件夹ID
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // 直接包含该图片文件的文件夹名
				MediaStore.Images.Media.DATE_MODIFIED,// 修改日期
				MediaStore.Images.Media.DISPLAY_NAME, // 图片文件名
				MediaStore.Images.Media.DATA, // 图片绝对路径
		}, selection, selectionArgs, sortOrder);
	}

	public ImageCursorLoader(Context context, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		super(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				projection, selection, selectionArgs, sortOrder);
	}

}
