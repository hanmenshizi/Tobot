package com.tobot.tobot.sqlite.sql;

import java.util.Map;

import com.tobot.tobot.sqlite.annotation.Column;
import com.tobot.tobot.sqlite.annotation.Id;

public class Select extends Operate {
	String order;
	String limit;
	Object id;
	Object element;
	private Map<String, String> where;

	public Select(Class clazz) {
		super(clazz);
	}

	public Select(Class clazz, Object id, Id I) {
		super(clazz);
		this.id = id;
	}

	//根据元素查询
	public Select(Class clazz, Object element) {
		super(clazz);
		this.element = element;
	}

	//根据元素查询和id查询
	public Select(Class clazz, Object id, Object element) {
		super(clazz);
		this.id = id;
		this.element = element;
	}

	public Select(Class clazz, Map<String, String> where) {
		super(clazz);
		this.where = where;
	}

	public Select(Class clazz, Map<String, String> where, String order, String limit) {
		super(clazz);
		this.where = where;
		this.order = order;
		this.limit = limit;
	}

	public String toStatementString() {
		if (order != null) {
			return buildSelectSql(getTableName(), where, order, limit);
		}
		if (id != null && element != null){//新增加
			return buildSelectSqlLike(getTableName(), id, element);
		}
		if (id != null && element == null) {
			return buildSelectSql(getTableName(), id);
		}
		if (id == null && element != null){//新增加
			return buildSelectSqlLike(getTableName(), element);
		}
		return buildSelectSql(getTableName(), where);
	}

}
