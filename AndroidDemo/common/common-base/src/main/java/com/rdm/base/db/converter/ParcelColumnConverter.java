
package com.rdm.base.db.converter;

import android.database.Cursor;
import android.os.Parcelable;

import com.rdm.common.util.FileUtils;

/**
 * Created by hugozhong 2013-11-12
 */
public class ParcelColumnConverter implements ColumnConverter<Parcelable, byte[]> {

    @Override
    public byte[] field2Column(Parcelable fieldValue) {
        return FileUtils.ParcelUtil.writeParcelable(fieldValue);
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
    public Parcelable column2Filed(byte[] columnValue, ClassLoader classLoader) {
        return FileUtils.ParcelUtil.readParcelable(columnValue, classLoader);
    }

}
