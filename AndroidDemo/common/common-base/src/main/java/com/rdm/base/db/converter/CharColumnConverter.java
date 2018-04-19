
package com.rdm.base.db.converter;

import android.database.Cursor;

/**
 * Author: hugozhong 
 * Date: 13-11-15 
 * Time: 下午10:51
 */
public class CharColumnConverter implements ColumnConverter<Character, Integer> {

    @Override
    public Integer field2Column(Character fieldValue) {
        if (fieldValue == null)
            return null;
        return (int) fieldValue;
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
    public Character column2Filed(Integer columnValue, ClassLoader classLoader) {
        return (char) columnValue.intValue();
    }
}
