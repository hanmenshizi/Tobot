package com.tobot.tobot.sqlite.dao;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.tobot.tobot.sqlite.annotation.Table;
import com.tobot.tobot.sqlite.builder.EntityBuilder;
import com.tobot.tobot.sqlite.sql.Delete;
import com.tobot.tobot.sqlite.sql.Insert;
import com.tobot.tobot.sqlite.sql.Select;
import com.tobot.tobot.sqlite.sql.Update;
import com.tobot.tobot.sqlite.table.TableUtils;
import com.tobot.tobot.utils.TobotUtils;

@SuppressLint("NewApi") 
public class BasehibernateDao<T> {
	private static String EMPTY_SQL = "DELETE FROM ";
	public SQLiteDatabase db;
	Class clazz;
	Type type;
	private String TAG = this.getClass().getSimpleName();

	public BasehibernateDao(Class cls, SQLiteDatabase db) {
		this.db = db;
		clazz = cls;
		TableUtils.createTable(db, true, clazz);
//		Table table = (Table) clazz.getAnnotation(Table.class);
//		if (!TableUtils.queryTable(db,clazz)) {
//			Log.i("Javen","需要创建表");
//			TableUtils.createTable(db, true, clazz);
//		}else{
//			Log.i("Javen","之前已存在表");
//			TableUtils.createTable(db, true, clazz);
//		}
	}


	public long insert(T entity) {
		String sql;
		sql = new Insert(entity).toStatementString();
		Log.e("Javen","sql insert: "+sql);
		SQLiteStatement stmt = null;
		try {
			stmt = db.compileStatement(sql);
			long rowId = stmt.executeInsert();
			return rowId;
		} catch (android.database.SQLException e) {
			return -1;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public void updatePraiseFlag(String sql, String[] params) {
		SQLiteStatement stmt = null;
		stmt = db.compileStatement(sql);
		stmt.executeUpdateDelete();
	}
	
	@SuppressWarnings("rawtypes")
	public List<T> queryList(Map<String, String> where) {
		String sql = new Select(clazz, where).toStatementString();
		Cursor cursor = db.rawQuery(sql, null);
		EntityBuilder<T> builder = new EntityBuilder<T>(clazz, cursor);
		List<T> queryList = builder.buildQueryList();
		cursor.close();
		return queryList;
	}

	@SuppressWarnings("rawtypes")
	public List<T> queryList(String sql, String[] params) {
		Cursor cursor = db.rawQuery(sql, params);
		EntityBuilder<T> builder = new EntityBuilder<T>(clazz, cursor);
		List<T> queryList = builder.buildQueryList();
		cursor.close();
		return queryList;
	}

	@SuppressWarnings("rawtypes")
	public List<T> queryList(Map<String, String> where, String order, String limit) {
		String sql = new Select(clazz, where, order, limit).toStatementString();
		Cursor cursor = db.rawQuery(sql, null);
		EntityBuilder<T> builder = new EntityBuilder<T>(clazz, cursor);
		List<T> queryList = builder.buildQueryList();
		cursor.close();
		return queryList;
	}

	public T get(Object id) {
		String sql = new Select(clazz, id, null).toStatementString();
		Log.e("Javen","get() sql: " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		EntityBuilder<T> builder = new EntityBuilder<T>(clazz, cursor);
		List<T> queryList = builder.buildQueryList();
		cursor.close();
		if (queryList != null && !queryList.isEmpty()) {
			Log.e("Javen","get queryList.get(0): " + queryList.get(0));
			return queryList.get(0);		
		}
		return null;
	}

	//根据元素查询
	public T queryByElement(Object element) throws Exception {
		String sql = new Select(clazz, element).toStatementString();
		Log.e("Javen","queryByElement() sql: " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		EntityBuilder<T> builder = new EntityBuilder<T>(clazz, cursor);
		List<T> queryList = builder.buildQueryList();
		cursor.close();
		if (queryList != null && !queryList.isEmpty()) {
			Log.e("Javen","queryByElement queryList.size(): " + queryList.size());
			return queryList.get(0);
		}
		return null;
	}

	//根据元素和id查询
	public T queryByElement(Object id,Object element) throws Exception {
		String sql = new Select(clazz, id, element).toStatementString();
		Log.e("Javen","queryByElement() sql: " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		EntityBuilder<T> builder = new EntityBuilder<T>(clazz, cursor);
		List<T> queryList = builder.buildQueryList();
		cursor.close();
		if (queryList != null && !queryList.isEmpty()) {
			Log.e("Javen","queryByElement queryList.size(): " + queryList.size());
			return queryList.get(0);
		}
		return null;
	}

	public T getbywhere(Map<String, String> where) {
		String sql = new Select(clazz, where).toStatementString();
		Cursor cursor = db.rawQuery(sql, null);
		EntityBuilder<T> builder = new EntityBuilder<T>(clazz, cursor);
		List<T> queryList = builder.buildQueryList();
		cursor.close();
		if (queryList != null && !queryList.isEmpty()) {
			return queryList.get(0);
		}
		return null;
	}

	public List<T> queryList() {
		String sql = new Select(clazz).toStatementString();
		Cursor cursor = db.rawQuery(sql, null);
		EntityBuilder<T> builder = new EntityBuilder<T>(clazz, cursor);
		List<T> queryList = builder.buildQueryList();
		cursor.close();
		return queryList;
	}

	public List<T> queryListByIds(String sql, String[] params) {
		Cursor cursor = db.rawQuery(sql, params);
		EntityBuilder<T> builder = new EntityBuilder<T>(clazz, cursor);
		List<T> queryList = builder.buildQueryList();
		cursor.close();
		return queryList;
	}

	public void update(T entity, Map<String, String> where) {
		String sql = new Update(entity, where).toStatementString();
		SQLiteStatement stmt = null;
		try {
			stmt = db.compileStatement(sql);
			stmt.execute();
		} catch (android.database.SQLException e) {
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public void execute(String sql, String[] params) {
		try {
			db.execSQL(sql, params);
		} catch (android.database.SQLException e) {
		} finally {
		}
	}

	public int count(String sql, String[] params) {
		int count = 0;
		Cursor c = db.rawQuery(sql, params);
		while (c.moveToNext()) {
			count = c.getInt(0);
			break;
		}
		c.close();
		return count;
	}

	public long getMaxSubmitTime(String sql, String[] params) {
		Cursor c = db.rawQuery(sql, params);
		long submitTime = 0;
		while (c.moveToNext()) {
			submitTime = c.getLong(0);
			break;
		}
		c.close();
		return submitTime;
	}
	
	public void update(T entity) {
		String sql = new Update(entity).toStatementString();
		SQLiteStatement stmt = null;
		try {
			stmt = db.compileStatement(sql);
			stmt.execute();
		} catch (android.database.SQLException e) {

		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public void truncate() {
		String sql = EMPTY_SQL + TableUtils.getTableName(clazz);
		Log.w("Javen","truncate sql: " + sql);
		SQLiteStatement stmt = null;
		try {
			stmt = db.compileStatement(sql);
			stmt.execute();
		} catch (android.database.SQLException e) {
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public void delete(Object id) {
		String sql = null;
		sql = new Delete(clazz, id).toStatementString();
		SQLiteStatement stmt = null;
		try {
			stmt = db.compileStatement(sql);
			stmt.execute();
		} catch (android.database.SQLException e) {
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public void truncate(Map<String, String> where) {

		StringBuffer sql = new StringBuffer(EMPTY_SQL + TableUtils.getTableName(clazz));
		if (where != null) {
			sql.append(" WHERE ");
			Iterator iter = where.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry e = (Map.Entry) iter.next();
				sql.append(e.getKey()).append(" = ").append("'" + e.getValue() + "'");
				if (iter.hasNext()) {
					sql.append(" AND ");
				}
			}
		}
		SQLiteStatement stmt = null;
		try {
			stmt = db.compileStatement(sql.toString());
			stmt.execute();
		} catch (android.database.SQLException e) {
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

	}

	public SQLiteDatabase getSQLiteDatabase() {
		return db;
	}
}
