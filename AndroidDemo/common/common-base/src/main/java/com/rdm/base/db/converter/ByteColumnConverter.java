package com.rdm.base.db.converter;

import android.database.Cursor;

/**
 * Author: hugozhong
 * Date: 13-11-15
 * Time: 下午10:51
 */
public class ByteColumnConverter implements ColumnConverter<Byte,Byte> {

    @Override
    public Byte field2Column(Byte fieldValue) {
        return fieldValue;
    }

    @Override
    public String getColumnDbType() {
        return "INTEGER";
    }

    @Override
    public Byte getColumnValue(Cursor cursor, int index) {
        return (byte) cursor.getInt(index);
    }

    @Override
    public Byte column2Filed(Byte columnValue, ClassLoader classLoader) {
        return columnValue;
    }
}
