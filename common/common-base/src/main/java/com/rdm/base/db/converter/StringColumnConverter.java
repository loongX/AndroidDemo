
package com.rdm.base.db.converter;

import android.database.Cursor;

/**
 * Created by hugozhong 2013-11-12
 */
public class StringColumnConverter implements ColumnConverter<String, String> {

    @Override
    public String field2Column(String fieldValue) {
        return fieldValue;
    }

    @Override
    public String getColumnDbType() {
        return "TEXT";
    }

    @Override
    public String getColumnValue(Cursor cursor, int index) {
        return cursor.getString(index);
    }

    @Override
    public String column2Filed(String columnValue, ClassLoader classLoader) {
        return columnValue;
    }
}
