
package com.rdm.base.db.converter;

import android.database.Cursor;

/**
 * Created by hugozhong 2013-11-12
 */
public class SqlDateColumnConverter implements ColumnConverter<java.sql.Date, Long> {

    @Override
    public Long field2Column(java.sql.Date fieldValue) {
        if (fieldValue == null)
            return null;
        return fieldValue.getTime();
    }

    @Override
    public String getColumnDbType() {
        return "INTEGER";
    }

    @Override
    public Long getColumnValue(Cursor cursor, int index) {
        return cursor.getLong(index);
    }

    @Override
    public java.sql.Date column2Filed(Long columnValue, ClassLoader classLoader) {
        return new java.sql.Date(columnValue);
    }
}
