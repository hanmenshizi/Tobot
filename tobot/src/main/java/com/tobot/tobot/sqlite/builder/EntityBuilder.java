package com.tobot.tobot.sqlite.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.tobot.tobot.sqlite.annotation.Column;
import com.tobot.tobot.sqlite.annotation.Id;
import com.tobot.tobot.sqlite.annotation.OneToMany;

import android.database.Cursor;


public class EntityBuilder<T> {
	private Class clazz;

	private Cursor cursor;

	public EntityBuilder(Class clazz, Cursor cursor) {
		this.clazz = clazz;
		this.cursor = cursor;
	}

	public List<T> buildQueryList() {
		List<T> queryList = new ArrayList<T>();
		Field[] fields = clazz.getDeclaredFields();
		if (cursor.moveToFirst()) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				try {
					T t = (T) clazz.newInstance();
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
								if (field.getType().getSimpleName().equals("Long") || field.getType().getSimpleName().equals("long")) {
									field.set(t,
											cursor.getLong(cursor
													.getColumnIndexOrThrow((columnName != null && !columnName
															.equals("")) ? columnName : field.getName())));
								} else if (field.getType().getSimpleName().equals("Integer") || field.getType().getSimpleName().equals("int")) {
									field.set(t,
											cursor.getInt(cursor
													.getColumnIndexOrThrow((columnName != null && !columnName
															.equals("")) ? columnName : field.getName())));
								} else if (field.getType().getSimpleName().equals("Double") || field.getType().getSimpleName().equals("double")) {
									field.set(t,
											cursor.getDouble(cursor
													.getColumnIndexOrThrow((columnName != null && !columnName
															.equals("")) ? columnName : field.getName())));
								} else if (field.getType().getSimpleName().equals("boolean") || field.getType().getSimpleName().equals("Boolean")) {
									int b = cursor.getInt(cursor
											.getColumnIndexOrThrow((columnName != null && !columnName
													.equals("")) ? columnName : field.getName()));
									field.set(t, b == 1);
								} else if (field.getType().getSimpleName().equals("String")) {
									field.set(t,
											cursor.getString(cursor
													.getColumnIndexOrThrow((columnName != null && !columnName
															.equals("")) ? columnName : field.getName())));
								}
							}
						}
					}
					queryList.add(t);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return queryList;
	}

}
