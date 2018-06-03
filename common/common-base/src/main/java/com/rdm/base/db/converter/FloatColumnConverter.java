package com.rdm.base.db.converter;

import android.database.Cursor;

/**
 * Author: hugozhong
 * Date: 13-11-15
 * Time: 下午10:51
 */
public class FloatColumnConverter implements ColumnConverter<Float,Float> {

    @Override
    public Float field2Column(Float fieldValue) {
        return fieldValue;
    }

    @Override
    public String getColumnDbType() {
        return "REAL";
    }

    @Override
    public Float getColumnValue(Cursor cursor, int index) {
        return cursor.getFloat(index);
    }

    @Override
    public Float column2Filed(Float columnValue, ClassLoader classLoader) {
        return columnValue;
    }
}
