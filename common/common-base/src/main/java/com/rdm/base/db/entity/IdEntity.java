package com.rdm.base.db.entity;

import java.lang.reflect.Field;

import android.text.TextUtils;

import com.rdm.common.ILog;
import com.rdm.base.db.annotation.GenerationType;
import com.rdm.base.db.annotation.Id;

@SuppressWarnings("rawtypes")
public class IdEntity<T> extends ColumnEntity {

	private static final String TAG = "IdEntity";
	private volatile boolean hasCacheAutoIncrement;
	private volatile boolean cachedAutoIncrementValue;

	private volatile boolean hasCacheUUIDGenerationType;
	private volatile boolean cachedUUIDGenerationTypeValue;
	
	private Id id;

	@SuppressWarnings("unchecked")
	public IdEntity(Class<T> entityType, Field field) {
		super(entityType, field);
		if (id == null) {
			id = field.getAnnotation(Id.class);
		}
	}
	
	@Override
	protected String getColumnNameByField(Field field) {
		if (id == null) {
			id = field.getAnnotation(Id.class);
		}
    	String columnName = id.name();
    	if (!TextUtils.isEmpty(columnName)) {
    		return fixedColumnName(columnName);
    	}
        return fixedColumnName(field.getName());
	}

	public boolean isAutoIncrement() {
		if (!hasCacheAutoIncrement) {
			cachedAutoIncrementValue = isAutoIncrementInner();
			hasCacheAutoIncrement = true;
		}
		return cachedAutoIncrementValue;
	}

	private boolean isAutoIncrementInner() {
		if (id == null || id.strategy() != GenerationType.AUTO_INCREMENT) {
			return false;
		}
		Class<?> idType = this.getColumnField().getType();
		return idType.equals(int.class) || idType.equals(Integer.class)
				|| idType.equals(long.class) || idType.equals(Long.class);
	}

	public boolean isUUIDGenerationType() {
		if (!hasCacheUUIDGenerationType) {
			cachedUUIDGenerationTypeValue = isUUIDGenerationTypeInner();
			hasCacheUUIDGenerationType = true;
		}
		return cachedUUIDGenerationTypeValue;
	}

	private boolean isUUIDGenerationTypeInner() {
		if (id == null || id.strategy() != GenerationType.UUID) {
			return false;
		}
		Class<?> idType = this.getColumnField().getType();
		return idType.equals(String.class);
	}

	public void setAutoIncrementId(Object entity, long value) {
		Object idValue = value;
		Class<?> columnFieldType = columnField.getType();
		if (columnFieldType.equals(int.class)
				|| columnFieldType.equals(Integer.class)) {
			idValue = (int) value;
		}

		setIdValue(entity, idValue);
	}

	public void setIdValue(Object entity, Object idValue) {
		try {
			this.columnField.setAccessible(true);
			this.columnField.set(entity, idValue);
		} catch (Throwable e) {
			ILog.e(TAG, e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getColumnValue(Object entity) {
		Object idValue = super.getColumnValue(entity);
		if (idValue != null) {
			if (this.isAutoIncrement()
					&& (idValue.equals(0) || idValue.equals(0L))) {
				return null;
			} else if (isUUIDGenerationType() && idValue.equals("")) {
				return null;
			} else {
				return idValue;
			}
		} else {
			return null;
		}
	}
}
