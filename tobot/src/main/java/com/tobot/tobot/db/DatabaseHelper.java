package com.tobot.tobot.db;

import com.tobot.tobot.sqlite.table.TableUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
	Class<?> cls;
	
	public DatabaseHelper(Context context, String databaseName, Class<?> cls) {
		super(context, databaseName, null, 4);
		this.cls = cls;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
//		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (int i = oldVersion + 1; i <= newVersion; i++) {
			switch (i) {
			case 1:
			
				break;
			}
		}
		TableUtils.dropTable(db, cls);
//		onCreate(db);
	}

}
