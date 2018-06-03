package com.rdm.base.db.entity;

import java.lang.reflect.Field;

import android.database.Cursor;
import android.text.TextUtils;

import com.rdm.common.ILog;
import com.rdm.base.db.annotation.Column;
import com.rdm.base.db.converter.ColumnConverter;
import com.rdm.base.db.converter.ColumnConverterFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ColumnEntity<EntityType> {

	private static final String TAG = "ColumnEntity";
	
	private static final String RESERVED_ID_NAME = "id";
    private static final String FIXED_ID_NAME = "_id";

	public static final String RESERVED_COLUMN_DYNAMIC_CLASS = "_reserved_dynamic_class";

	protected final String columnName;

	protected final Field columnField;
	protected final ColumnConverter columnConverter;
	private final Class<EntityType> entityClass;
	private Column column;

	public ColumnEntity(Class<EntityType> entityType, Field field){
		this.columnField = field;
		this.columnConverter = ColumnConverterFactory.getColumnConverter(field
				.getType());
        this.column = field.getAnnotation(Column.class);
		this.columnName = getColumnNameByField(field);
		this.entityClass = entityType;
	}

	public void setValue2Entity(EntityType entity, Cursor cursor, int index,
			ClassLoader classLoader) {
		Object columnValue = columnConverter.getColumnValue(cursor, index);
		Object value = columnConverter.column2Filed(columnValue,
                classLoader);
		try {
			this.columnField.setAccessible(true);
			this.columnField.set(entity, value);
		} catch (Throwable e) {
			ILog.e(TAG, e.getMessage(), e);
		}
	}

	public Object getColumnValue(EntityType entity) {
		Object fieldValue = getFieldValue(entity);
		Object columnValue = columnConverter.field2Column(fieldValue);
		return columnValue;
	}

	public Object getFieldValue(EntityType entity) {
		Object fieldValue = null;
		if (entity != null) {
			try {
				this.columnField.setAccessible(true);
				fieldValue = this.columnField.get(entity);
			} catch (Throwable e) {
				ILog.e(TAG, e.getMessage(), e);
			}
		}
		return fieldValue;
	}
	
	
	protected String getColumnNameByField(Field field) {
        if (column != null) {
        	String columnName = column.name();
        	if (!TextUtils.isEmpty(columnName)) {
        		return fixedColumnName(columnName);
        	}
        }
        return fixedColumnName(field.getName());
    }
    
    protected static String fixedColumnName(String columnName) {
        if (RESERVED_ID_NAME.equalsIgnoreCase(columnName)) {
            return FIXED_ID_NAME;
        }
        return columnName;
    }

    public static boolean isColumn(Field field) {
        return field.getAnnotation(Column.class) != null;
    }
    
    private boolean hasCachedUniqueValue;
    private boolean isColumnUnique;

    public boolean isUnique() {
    	if(!hasCachedUniqueValue) {
            if (column != null) {
            	isColumnUnique = column.unique();
            	hasCachedUniqueValue = true;
            } else {
            	isColumnUnique = false;
            }
    	}
        return isColumnUnique;
    }
    
    private boolean hasCachedNullableValue;
    private boolean isColumnNullable;

    public boolean isNullable() {
    	if (!hasCachedNullableValue) {
            hasCachedNullableValue = true;
	        if (column != null) {
	        	isColumnNullable = column.nullable();
	        } else {
	        	isColumnNullable = true;
            }
    	}
        return isColumnNullable;
    }

    public static Object convert2DbColumnValueIfNeeded(final Object value) {
        Object result = value;
        if (value != null) {
            Class<?> valueType = value.getClass();
            ColumnConverter converter = ColumnConverterFactory.getColumnConverter(valueType);
            if (converter != null) {
                result = converter.field2Column(value);
            } else {
                result = value;
            }
        }
        return result;
    }
	

	public String getColumnName() {
		return columnName;
	}

	public Field getColumnField() {
		return columnField;
	}

	public String getColumnDbType() {
		if (columnConverter != null) {
			return columnConverter.getColumnDbType();
		}
		return "TEXT";
	}
}
