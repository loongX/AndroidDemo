
package com.rdm.base.db.converter;

import android.database.Cursor;

/**
 * Author: hugozhong Date: 13-11-15 Time: 下午8:57
 */
public interface ColumnConverter<FieldType, ColumnType> {

    ColumnType getColumnValue(final Cursor cursor, int index);

    FieldType column2Filed(ColumnType columnValue, ClassLoader classLoader);

    ColumnType field2Column(FieldType fieldValue);

    String getColumnDbType();
}
