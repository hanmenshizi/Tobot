package com.tobot.tobot.db;

import com.tobot.tobot.base.TobotApplication;
import com.tobot.tobot.db.bean.UserDBManager;
import com.tobot.tobot.sqlite.dao.BasehibernateDao;
import com.tobot.tobot.sqlite.table.TableUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public abstract class BaseDBManager<T> {

	Context _context;
	public DatabaseHelper mDatabaseHelper;
	public SQLiteDatabase mSQLiteDatabase;
	public BasehibernateDao<T> mBeanDao;

	public BaseDBManager(Class<?> cls) {
		_context = TobotApplication.getInstance();
		Log.e("Javen","BaseDBManager:"+TableUtils.getTableName(cls));
		mDatabaseHelper = new DatabaseHelper(_context, getDatabaseName(), cls);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();		
		mBeanDao = new BasehibernateDao<T>(cls, mSQLiteDatabase);
	}

	public String getDatabaseName() {
//		if (null != UserDBManager.getManager().getCurrentUser()) {
//			return UserDBManager.getManager().getCurrentUser().keyId + ".db";
//		} else {
//			return TobotApplication.getInstance().getCurrentUser().keyId + ".db";
//		}
		return "tobot.db";
	}

	public void close() {
		_context = null;
		mDatabaseHelper = null;
		mSQLiteDatabase.close();
		mSQLiteDatabase = null;
		mBeanDao = null;
	}


}
