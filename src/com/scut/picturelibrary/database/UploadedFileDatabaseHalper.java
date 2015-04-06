package com.scut.picturelibrary.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 文件上传后记录到数据库中，下次不再上传直接获取地址 若文件在服务端被删除，则需要清空应用缓存
 * 
 * @author 黄建斌
 * 
 */
public class UploadedFileDatabaseHalper extends SQLiteOpenHelper {

	public UploadedFileDatabaseHalper(Context context, CursorFactory factory,
			int version) {
		super(context, "uploadedFiles_db", factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table uploadedFilesTable(id integer PRIMARY KEY, filename varchar, url varchar, size integer, uploadedDate varchar)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
