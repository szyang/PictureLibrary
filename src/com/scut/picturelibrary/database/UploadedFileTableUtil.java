package com.scut.picturelibrary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 上传过的文件记录表 上传后的文件信息保存在此 主要通过filename和size判断是否是同一个文件
 * 
 * @author 黄建斌
 * 
 */
public class UploadedFileTableUtil {
	private static final String TABLE_NAME = "uploadedFilesTable";
	private static final String _ID = "id";
	private static final String FILE_NAME = "filename";
	private static final String URL = "url";
	private static final String SIZE = "size";

	private UploadedFileDatabaseHalper dbHelper;
	private SQLiteDatabase db = null;

	public UploadedFileTableUtil(Context context) {
		dbHelper = new UploadedFileDatabaseHalper(context, null, 1);
	}

	public boolean insert(String filename, int size, String url) {
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(FILE_NAME, filename);
		values.put(URL, url);
		values.put(SIZE, size);
		db.insert(TABLE_NAME, null, values);
		db.close();
		return true;
	}

	public String hasUploaded(String filename, int size) {
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[] { _ID, FILE_NAME,
				SIZE, URL }, "filename=? AND size=?", new String[] { filename,
				String.valueOf(size) }, null, null, null);
		String url = null;
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			url = cursor.getString(cursor.getColumnIndex(URL));
		}
		cursor.close();
		return url;
	}
}
