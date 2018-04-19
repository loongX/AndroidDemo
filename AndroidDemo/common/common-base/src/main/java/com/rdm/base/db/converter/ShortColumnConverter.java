
package com.rdm.base.db.converter;

import android.database.Cursor;

/**
 * Created by hugozhong 2013-11-12
 */
public class ShortColumnConverter implements ColumnConverter<Short, Short> {

    @Override
    public Short field2Column(Short fieldValue) {
        return fieldValue;
    }

    @Override
    public String getColumnDbType() {
        return "INTEGER";
    }

    @Override
    public Short getColumnValue(Cursor cursor, int index) {
        return cursor.getShort(index);
    }

    @Override
    public Short column2Filed(Short columnValue, ClassLoader classLoader) {
        return columnValue;
    }
}
