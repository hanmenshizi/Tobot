package com.tobot.tobot.sqlite.table;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tobot.tobot.db.model.User;
import com.tobot.tobot.sqlite.annotation.Column;
import com.tobot.tobot.sqlite.annotation.Id;
import com.tobot.tobot.sqlite.annotation.Table;
import com.tobot.tobot.utils.TobotUtils;

public class TableUtils {
	public static boolean DEBUG = false;

	public static String buildDropTableStatement(TableInfo tableInfo) {
		StringBuilder sb = new StringBuilder(256);
		sb.append("DROP TABLE ");
		sb.append("IF EXISTS ");
		sb.append(tableInfo.getTableName());
		return sb.toString();
	}

	public static Object getFieldValue(String filedName, Object obj)
			throws IllegalArgumentException, SecurityException, IllegalAccessException,
			NoSuchFieldException {
		Field field = obj.getClass().getDeclaredField(filedName);
		field.setAccessible(true);
		return field.get(obj);
	}

	// Strubg query = "Select *  from tableName where x = xx";
	public static String buildQueryStatements(String tableName, String fieldName,
			String fieldValue) {
		StringBuilder sb = new StringBuilder(256);
		sb.append("SELECT * FROM ");
		sb.append(tableName);
		sb.append(" WHERE ");
		sb.append(fieldName);
		sb.append("=");
		sb.append(fieldValue);
		return sb.toString();
	}

	public static String buildCreateTableStatement(TableInfo tableInfo,
			boolean ifNotExists) {
		// CREATE TABLE IF NOT EXISTS hrw_playlist (id INTEGER PRIMARY KEY,name
		// TEXT CHECK( name != '' ),add_date INTEGER,modified_date INTEGER);
		StringBuilder sb = new StringBuilder(256);
		sb.append("CREATE TABLE ");

		if (ifNotExists) {
			sb.append("IF NOT EXISTS ");
		}

		sb.append(tableInfo.getTableName());
		sb.append(" (");

		Iterator iter = null;
		iter = tableInfo.getColumns().entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry e = (Map.Entry) iter.next();
			if (tableInfo.getColumnsType().get(e.getKey()).equals("Long")||tableInfo.getColumnsType().get(e.getKey()).equals("long")) {
				sb.append(e.getValue() + " INTEGER");
			}  else if (tableInfo.getColumnsType().get(e.getKey()).equals("Integer")||tableInfo.getColumnsType().get(e.getKey()).equals("int")) {
				sb.append(e.getValue() + " INTEGER");
			} else if (tableInfo.getColumnsType().get(e.getKey()).equals("Double")||tableInfo.getColumnsType().get(e.getKey()).equals("double")) {
				sb.append(e.getValue() + " TEXT");
			}else if (tableInfo.getColumnsType().get(e.getKey()).equals("Boolean")||tableInfo.getColumnsType().get(e.getKey()).equals("boolean")) {
				sb.append(e.getValue() + " BOOLEAN ");
			} else
			// if (f.getType().getSimpleName().equals("List")) {
			// sb.append(entry.getKey()+DEFAULT_FOREIGN_KEY_SUFFIX +
			// " INTEGER");
			// }
			if (tableInfo.getColumnsType().get(e.getKey()).equals("String")) {
				sb.append(e.getValue() + " TEXT");
			}
			if(tableInfo.getColumnsDefault().containsKey(e.getKey())){
				sb.append(" DEFAULT " + tableInfo.getColumnsDefault().get(e.getKey()));
			}
			// and primary key here
			if (null != tableInfo.getPrimaryColoum()) {
				if (tableInfo.getPrimaryColoum().equals(e.getKey())) {
					sb.append(" PRIMARY KEY");
				}
			}

			if (iter.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	public static Field extractIdField(Class clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getAnnotations().length != 0) {
				field.setAccessible(true);
				for (Annotation annotation : field.getAnnotations()) {
					Class<?> annotationClass = annotation.annotationType();
					if (annotationClass.getName().equals(Id.class.getName())) {
						return field;
					}
				}
			}
		}
		return null;
	}

	public static int createTable(SQLiteDatabase db, boolean ifNotExists,
			Class... entityClasses) {
		int i = -1;
		for (Class clazz : entityClasses) {
			TableInfo tableInfo = new TableInfo(clazz);
			String sql = buildCreateTableStatement(tableInfo, ifNotExists);
			if (!DEBUG) {
				db.execSQL(sql);
			}
			i = 1;
		}

		return i;
	}

	//查询是否存在表
	public static boolean queryTable(SQLiteDatabase db, Class... entityClasses) {
		boolean b;
		for (Class clazz : entityClasses) {
			TableInfo tableInfo = new TableInfo(clazz);
			String sql = buildQueryStatements(tableInfo.getTableName(), "keyId", "tobot");
			if (!DEBUG) {
				Cursor cursor = db.rawQuery(sql, new String[] { "keyId" });
				if (TobotUtils.isNotEmpty(cursor)){
					return true;
				}
			}
		}
		return false;
	}

	public static int dropTable(SQLiteDatabase db, Class... entityClasses) {
		int i = -1;
		for (Class clazz : entityClasses) {
			TableInfo tableInfo = new TableInfo(clazz);
			String sql = buildDropTableStatement(tableInfo);
			if (!DEBUG) {
				db.execSQL(sql);
			}
			i = 1;
		}
		return i;
	}

	public static int updateTable() {
		int i = -1;

		return i;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getTableName(Class clazz) {
		Table table = (Table) clazz.getAnnotation(Table.class);
		String name = null;
		if (isTableNameEmpty(table)) {
			name = table.name();
		} else {
			// if the name isn't specified, it is the class name lowercased
			name = clazz.getSimpleName().toLowerCase();
		}
		return name;
	}

	public static String getIdName(Class clazz) {
		Annotation[] fieldAnnotations = null;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			fieldAnnotations = field.getAnnotations();
			if (fieldAnnotations.length != 0) {
				for (Annotation annotation : fieldAnnotations) {
					if (annotation instanceof Id) {
						Log.w("Javen","getIdName: " + ((Id) annotation).name());
						return ((Id) annotation).name();
					}
				}
			}
		}
		return null;
	}

	public static boolean isTableNameEmpty(Table table) {
		return table != null && !TobotUtils.isBlank(table.name());
	}

	private static boolean isIdNameEmpty(Id id) {
		return id != null && !TobotUtils.isBlank(id.name());
	}

	public static Map<String, String> getTableColumns(Class clazz) {
		Map<String, String> columns = new TreeMap<String, String>();
		Annotation[] fieldAnnotations = null;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			fieldAnnotations = field.getAnnotations();
			if (fieldAnnotations.length != 0) {
				for (Annotation annotation : fieldAnnotations) {
					String columnName = null;
					if (annotation instanceof Id) {
						columnName = ((Id) annotation).name();
						columns.put(
								field.getName(),
								!TobotUtils.isBlank(columnName) ? columnName : field
										.getName());
					} else if (annotation instanceof Column) {
						columnName = ((Column) annotation).name();
						columns.put(
								field.getName(),
								!TobotUtils.isBlank(columnName) ? columnName : field
										.getName());
					}
				}
			}
		}
		return columns;
	}

	public static List<Object> extratToTableInfo(Class clazz) {
		List<Object> tableInfo = new ArrayList<Object>();
		String tableName = getTableName(clazz);
		String primaryKey = null;
		Map<String, String> columns = new TreeMap<String, String>();
		Map<String, String> columnsType = new TreeMap<String, String>();
		Map<String, String> columnsDefault = new TreeMap<String, String>();
		Annotation[] fieldAnnotations = null;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			fieldAnnotations = field.getAnnotations();
			if (fieldAnnotations.length != 0) {
				for (Annotation annotation : fieldAnnotations) {
					String columnName = null;
					String columnDefault = null;
					if (annotation instanceof Id) {

						primaryKey = field.getName();

						columnName = ((Id) annotation).name();
						columns.put(
								field.getName(),
								!TobotUtils.isBlank(columnName) ? columnName : field
										.getName());
						columnsType.put(field.getName(), field.getType().getSimpleName());
					} else if (annotation instanceof Column) {
						columnName = ((Column) annotation).name();
						columnDefault  = ((Column) annotation).defaultValue();
						columns.put(
								field.getName(),
								!TobotUtils.isBlank(columnName) ? columnName : field
										.getName());
						columnsType.put(field.getName(), field.getType().getSimpleName());
						if(TobotUtils.isNotEmpty(columnDefault)){
							columnsDefault.put(field.getName(),columnDefault);	
						}
					}

				}
			}
		}
		tableInfo.add(tableName);
		tableInfo.add(primaryKey);
		tableInfo.add(columns);
		tableInfo.add(columnsType);
		tableInfo.add(columnsDefault);
		return tableInfo;
	}

	public static Map<String, String> getTableColumnsType(Class clazz) {
		Map<String, String> columnsType = new TreeMap<String, String>();
		Annotation[] fieldAnnotations = null;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			fieldAnnotations = field.getAnnotations();
			if (fieldAnnotations.length != 0) {
				for (Annotation annotation : fieldAnnotations) {
					if (annotation instanceof Id || annotation instanceof Column) {
						columnsType.put(field.getName(), field.getType().getSimpleName());
					}
				}
			}
		}
		return columnsType;
	}

}
