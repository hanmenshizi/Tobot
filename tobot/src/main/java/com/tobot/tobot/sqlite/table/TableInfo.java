package com.tobot.tobot.sqlite.table;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TableInfo {

	private Map<String, String> columns;
	private Map<String, String> columnsType;
	private Map<String, String> columnsDefault;
	private String tableName;

	private String primaryKey;

	public TableInfo(Class clazz) {
		List<Object> tableInfo = TableUtils.extratToTableInfo(clazz);
		this.tableName = (String) tableInfo.get(0);
		this.primaryKey = (String) tableInfo.get(1);
		this.columns = (Map<String, String>) tableInfo.get(2);
		this.columnsType = (Map<String, String>) tableInfo.get(3);
		this.columnsDefault = (Map<String, String>) tableInfo.get(4);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Map<String, String> getColumns() {
		return this.columns;
	}

	public Map<String, String> getColumnsType() {
		return this.columnsType;
	}
	
	public Map<String, String> getColumnsDefault() {
		return columnsDefault;
	}

	public String getPrimaryColoum() {
		return primaryKey;
	}

}
