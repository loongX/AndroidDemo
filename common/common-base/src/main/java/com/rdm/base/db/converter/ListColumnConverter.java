
package com.rdm.base.db.converter;

import android.database.Cursor;

import com.rdm.common.util.FileUtils;

import java.util.List;

/**
 * Created by hugozhong 2013-12-21
 */
@SuppressWarnings("rawtypes")
public class ListColumnConverter implements ColumnConverter<List, byte[]> {

    @Override
    public byte[] getColumnValue(Cursor cursor, int index) {
        return cursor.getBlob(index);
    }

    @Override
    public List column2Filed(byte[] columnValue, ClassLoader classLoader) {
        return FileUtils.ParcelUtil.readList(columnValue, classLoader);
    }

    @Override
    public byte[] field2Column(List fieldValue) {
        return FileUtils.ParcelUtil.writeList(fieldValue);
    }

    @Override
    public String getColumnDbType() {
        return "BLOB";
    }

}
