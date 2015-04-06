package com.scut.picturelibrary.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

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
