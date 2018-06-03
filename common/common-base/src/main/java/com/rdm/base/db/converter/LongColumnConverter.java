package com.rdm.base.db.converter;

import android.database.Cursor;

/**
 * Created by hugozhong 2013-11-12
 */
public class LongColumnConverter implements ColumnConverter<Long,Long> {

    @Override
    public Long field2Column(Long fieldValue) {
        return fieldValue;
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
    public Long column2Filed(Long columnValue, ClassLoader classLoader) {
        return columnValue;
    }
}
