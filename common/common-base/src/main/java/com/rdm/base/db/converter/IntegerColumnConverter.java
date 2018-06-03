package com.rdm.base.db.converter;

import android.database.Cursor;

/**
 * Author: hugozhong
 * Date: 13-11-15
 * Time: 下午10:51
 */
public class IntegerColumnConverter implements ColumnConverter<Integer,Integer> {

    @Override
    public Integer field2Column(Integer fieldValue) {
        return fieldValue;
    }

    @Override
    public String getColumnDbType() {
        return "INTEGER";
    }

    @Override
    public Integer getColumnValue(Cursor cursor, int index) {
        return cursor.getInt(index);
    }

    @Override
    public Integer column2Filed(Integer columnValue, ClassLoader classLoader) {
        return columnValue;
    }
}
