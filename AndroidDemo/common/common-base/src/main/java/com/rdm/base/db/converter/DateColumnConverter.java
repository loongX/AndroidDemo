package com.rdm.base.db.converter;

import android.database.Cursor;

import java.util.Date;

/**
 * Author: hugozhong
 * Date: 13-11-15
 * Time: 下午10:51
 */
public class DateColumnConverter implements ColumnConverter<Date,Long> {

    @Override
    public Long field2Column(Date fieldValue) {
        if (fieldValue == null) return null;
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
    public Date column2Filed(Long columnValue, ClassLoader classLoader) {
        return new Date(columnValue);
    }
}
