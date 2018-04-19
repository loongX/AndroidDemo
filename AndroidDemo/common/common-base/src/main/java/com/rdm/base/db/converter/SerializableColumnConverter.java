
package com.rdm.base.db.converter;

import android.database.Cursor;

import com.rdm.common.util.FileUtils;

import java.io.Serializable;

/**
 * Created by hugozhong 2013-11-12
 */
public class SerializableColumnConverter implements ColumnConverter<Serializable, byte[]> {

    @Override
    public byte[] field2Column(Serializable fieldValue) {
        return FileUtils.ParcelUtil.writeSerializable(fieldValue);
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
    public Serializable column2Filed(byte[] columnValue, ClassLoader classLoader) {
        return FileUtils.ParcelUtil.readSerializable(columnValue);
    }

}
