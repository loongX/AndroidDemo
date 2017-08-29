package com.rdm.base.db.converter;

import android.database.Cursor;

/**
 * Author: hugozhong
 * Date: 13-11-15
 * Time: 下午10:51
 */
public class ByteArrayColumnConverter implements ColumnConverter<byte[],byte[]> {

    @Override
    public byte[] field2Column(byte[] fieldValue) {
        return fieldValue;
    }

    @Override
    public String getColumnDbType() {
        return "BLOB";
    }

    @Override
    public byte[] getColumnValue(Cursor cursor, int index) {
        return cursor.getBlob(index);
    }

    @Override
    public byte[] column2Filed(byte[] columnValue, ClassLoader classLoader) {
        return columnValue;
    }
}
