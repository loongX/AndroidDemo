package com.rdm.base.db.converter;

import android.database.Cursor;

/**
 * Author: hugozhong
 * Date: 13-11-15
 * Time: 下午10:51
 */
public class DoubleColumnConverter implements ColumnConverter<Double,Double> {

    @Override
    public Double field2Column(Double fieldValue) {
        return fieldValue;
    }

    @Override
    public String getColumnDbType() {
        return "REAL";
    }

    @Override
    public Double getColumnValue(Cursor cursor, int index) {
        return cursor.getDouble(index);
    }

    @Override
    public Double column2Filed(Double columnValue, ClassLoader classLoader) {
        return columnValue;
    }
}
