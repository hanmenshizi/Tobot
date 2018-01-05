package com.tobot.tobot.sqlite.sql;

import java.util.Map;

public class Delete extends Operate {
	private Object id;

	private Map<String, String> where;

	/**
	 * delete entity by id.
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public Delete(Class cls, Object id) {
		super(cls);
		this.id = id;
	}

	/**
	 * delete entity by where,if where is null it will delete all records.
	 * 
	 * @param entity
	 * @param where
	 */
	@SuppressWarnings("rawtypes")
	public Delete(Class clazz, Map<String, String> where) {
		super(clazz);
		this.where = where;
	}

	public String toStatementString() {
		if (id != null) {
			return buildDeleteSql(getTableName(), id);
		} else {
			return buildDeleteSql(getTableName(), where);
		}
	}
}
