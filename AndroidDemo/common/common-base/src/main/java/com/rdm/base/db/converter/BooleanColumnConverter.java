
package com.rdm.base.db.converter;

import android.database.Cursor;

/**
 * Author: hugozhong 
 * Date: 13-11-15 
 * Time: 下午10:51
 */
public class BooleanColumnConverter implements ColumnConverter<Boolean, Integer> {

    @Override
    public Integer getColumnValue(Cursor cursor, int index) {
        return cursor.getInt(index);
    }

    @Override
    public Boolean column2Filed(Integer columnValue, ClassLoader classLoader) {
        return columnValue == 1;
    }

    @Override
    public Integer field2Column(Boolean fieldValue) {
        if (fieldValue == null)
            return null;
        return fieldValue ? 1 : 0;
    }

    @Override
    public String getColumnDbType() {
        return "INTEGER";
    }
}
