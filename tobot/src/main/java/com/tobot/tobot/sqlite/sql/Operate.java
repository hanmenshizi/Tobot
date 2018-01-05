package com.tobot.tobot.sqlite.sql;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.tobot.tobot.sqlite.annotation.Column;
import com.tobot.tobot.sqlite.annotation.Id;
import com.tobot.tobot.sqlite.annotation.OneToMany;
import com.tobot.tobot.sqlite.table.TableInfo;
import com.tobot.tobot.sqlite.table.TableUtils;
import com.tobot.tobot.utils.TobotUtils;



public class Operate {
	Class clazz;

	public Operate(Class clazz) {
		this.clazz = clazz;
		tableName = TableUtils.getTableName(clazz);
		primaryKey = TableUtils.getIdName(clazz);
	}

	private String tableName;

	private String primaryKey;

	public String getTableName() {
		return tableName;
	}

	/*
	 * public static boolean isNumeric(String str) { Pattern pattern =
	 * Pattern.compile("[0-9]*"); Matcher isNum = pattern.matcher(str); if
	 * (!isNum.matches()) { return false; } return false; }
	 */
	public String buildSelectSql(String tableName, Map<String, String> where) {
		StringBuilder sb = new StringBuilder(256);
		sb.append("SELECT * FROM ");
		sb.append(tableName);
		Iterator iter = null;
		if (where != null) {
			sb.append(" WHERE ");
			iter = where.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry e = (Map.Entry) iter.next();
				sb.append(e.getKey()).append(" = ").append("'" + e.getValue() + "'");
				if (iter.hasNext()) {
					sb.append(" AND ");
				}
			}
		}
		return sb.toString();
	}

	public String buildSelectSql(String tableName, Object id) {
		StringBuilder sb = new StringBuilder(256);
		sb.append("SELECT * FROM ");
		sb.append(tableName);
		sb.append(" WHERE ").append(primaryKey).append("=").append("'" + id + "'");

		return sb.toString();
	}

	//新增加模糊查询
	public String  buildSelectSqlLike(String tableName, Object element) {
		Map<String,String> fields = new TableInfo(clazz).getColumns();
		StringBuilder sb = new StringBuilder(256);
		sb.append("SELECT * FROM ");
		sb.append(tableName);
		Iterator iter = null;
		if (fields != null) {
			sb.append(" WHERE ");
			iter = fields.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry e = (Map.Entry) iter.next();
				sb.append(e.getKey()).append(" LIKE ").append("'" + element + "'");// % _ [] [^]
				if (iter.hasNext()) {
					sb.append(" OR ");
				}
			}
		}

		return sb.toString();
	}

	//新增加AND模糊查询
	public String  buildSelectSqlLike(String tableName, Object id, Object element) {
		Map<String,String> fields = new TableInfo(clazz).getColumns();
		StringBuilder sb = new StringBuilder(256);
		sb.append("SELECT * FROM ");
		sb.append(tableName);
		Iterator iter = null;
		if (fields != null) {
			sb.append(" WHERE ").append(primaryKey).append("=").append("'" + id + "'").append(" AND ");
			iter = fields.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry e = (Map.Entry) iter.next();
				sb.append(e.getKey()).append(" LIKE ").append("'" + element + "'");// % _ [] [^]
				if (iter.hasNext()) {
					sb.append(" OR ");
				}
			}
		}

		return sb.toString();
	}

	public String buildDeleteSql(String tableName, Object id) {
		StringBuilder sb = new StringBuilder(256);
		sb.append("delete   FROM ");
		sb.append(tableName);
		sb.append(" WHERE ").append(primaryKey).append("=").append("'" + id + "'");

		return sb.toString();
	}

	public String buildSelectSql(String tableName, Map<String, String> where, String order, String limit) {
		StringBuilder sb = new StringBuilder(256);
		sb.append("SELECT * FROM ");
		sb.append(tableName);
		Iterator iter = null;
		if (where != null) {
			sb.append(" WHERE ");
			iter = where.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry e = (Map.Entry) iter.next();
				sb.append(e.getKey()).append(" = ").append("'" + e.getValue() + "'");
				if (iter.hasNext()) {
					sb.append(" AND ");
				}
			}
		}

		sb.append(" order by ").append(order);
		if (limit != null) {
			sb.append(" ").append(limit);
		}
		return sb.toString();
	}

	public String buildInsertSql(String tableName, Map<String, String> insertColumns) {
		StringBuilder columns = new StringBuilder(256);
		StringBuilder values = new StringBuilder(256);
		columns.append("INSERT INTO ");

		columns.append(tableName).append(" (");
		values.append("(");

		Iterator iter = insertColumns.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry e = (Map.Entry) iter.next();

			if (!TobotUtils.isEmpty(e.getValue())) {
				columns.append(e.getKey());
				/*
				 * values.append(isNumeric(e.getValue() != null ?
				 * e.getValue().toString() : "") ? e .getValue() : "'" +
				 * e.getValue() + "'");
				 */
				if(e.getValue().toString().contains("'")){
					values.append("'" + e.getValue().toString().replaceAll("'", "’") + "'");
				}
				else
				{
					values.append("'" + e.getValue() + "'");
				}
				if (iter.hasNext()) {
					columns.append(",");
					values.append(",");
				}
			}
		}

		if (columns.charAt(columns.length() - 1) == ',') {
			columns.deleteCharAt(columns.length() - 1);
		}
		if (values.charAt(values.length() - 1) == ',') {
			values.deleteCharAt(values.length() - 1);
		}
		columns.append(") values ");
		values.append(")");
		columns.append(values);
		return columns.toString();
	}

	public String buildUpdateSql(String tableName, Map<String, String> needUpdate, Map<String, String> where) {
		StringBuilder sb = new StringBuilder(256);
		sb.append("UPDATE ");
		sb.append(tableName).append(" SET ");

		Iterator iter = needUpdate.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry e = (Map.Entry) iter.next();
			if (e.getValue() != null) {
				
				if(e.getValue().toString().contains("'")){
					sb.append(e.getKey()).append(" = ").append("'" + e.getValue().toString().replaceAll("'", "’") + "'");
				}else{
					sb.append(e.getKey()).append(" = ").append("'" + e.getValue() + "'");
				}
				if (iter.hasNext()) {
					sb.append(",");
				}
			}

		}
		if (sb.charAt(sb.length() - 1) == ',') {
			sb.deleteCharAt(sb.length() - 1);
		}
		if (where != null) {
			sb.append(" where ");
			iter = where.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry e = (Map.Entry) iter.next();
				sb.append(e.getKey()).append(" = ").append("'" + e.getValue() + "'");
				if (iter.hasNext()) {
					sb.append(" and ");
				}
			}
		}
		return sb.toString();
	}

	public String buildDeleteSql(String tableName, Map<String, String> where) {
		StringBuffer buf = new StringBuffer(tableName.length() + 10);
		buf.append("DELETE FROM ").append(tableName);
		if (where != null) {
			buf.append(" WHERE ");
			Iterator iter = where.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry e = (Map.Entry) iter.next();
				buf.append(e.getKey()).append(" = ").append("'" + e.getValue() + "'");
				if (iter.hasNext()) {
					buf.append(" AND ");
				}
			}
		}
		return buf.toString();
	}

	public Map<String, String> buildWhere(Object entity) throws Exception {
		Map<String, String> where = new HashMap<String, String>();
		Class clazz = entity.getClass();
		Field[] fields = clazz.getDeclaredFields();
		Annotation[] fieldAnnotations = null;
		for (Field field : fields) {
			field.setAccessible(true);
			fieldAnnotations = field.getAnnotations();
			if (fieldAnnotations.length != 0) {
				for (Annotation annotation : fieldAnnotations) {
					String columnName = null;
					if (annotation instanceof Id) {
						columnName = ((Id) annotation).name();
					} else if (annotation instanceof Column) {
						columnName = ((Column) annotation).name();
					} else if (annotation instanceof OneToMany) {
						continue;
						// Ignore
					}
					try {
						if (null != field.get(entity) && field.get(entity).toString().length() > 0) {
							where.put((columnName != null && !columnName.equals("")) ? columnName : field.getName(), field.get(entity).toString());
						}
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
		if (where.isEmpty()) {
			throw new Exception("can't delete,entity is illegal");
		}
		return where;
	}

}
