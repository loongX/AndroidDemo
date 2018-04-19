
package com.rdm.base.db;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.*;
import android.net.Uri;
import android.os.Bundle;
import com.rdm.base.db.exception.DbCacheExceptionHandler;

/**
 * Author: hugozhong Date: 2013-11-14
 */
public class SafeCursorWrapper extends CursorWrapper {
    private boolean mClosed;

    private SafeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public static SafeCursorWrapper create(Cursor cursor) {
        if (cursor != null) {
            return new SafeCursorWrapper(cursor);
        }
        return null;
    }

    @Override
    public void close() {
        try {
            super.close();
            mClosed = true;
        } catch (Throwable e) {
            handleException(e);
        }
    }

    @Override
    public boolean isClosed() {
        try {
            return super.isClosed();
        } catch (Throwable e) {
            handleException(e);
        }
        return mClosed;
    }

    @Override
    public int getCount() {
        try {
            return super.getCount();
        } catch (Throwable e) {
            handleException(e);
        }
        return 0;
    }

    @Override
    public void deactivate() {
        try {
            super.deactivate();
        } catch (Throwable e) {
            handleException(e);
        }
    }

    @Override
    public boolean moveToFirst() {
        try {
            return super.moveToFirst();
        } catch (Throwable e) {
            handleException(e);
        }
        return false;
    }

    @Override
    public int getColumnCount() {
        try {
            return super.getColumnCount();
        } catch (Throwable e) {
            handleException(e);
        }
        return 0;
    }

    public int getColumnIndex(String columnName) {
        try {
            return super.getColumnIndex(columnName);
        } catch (Throwable e) {
            handleException(e);
        }
        return -1;
    }

    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        try {
            return super.getColumnIndexOrThrow(columnName);
        } catch (Throwable e) {
            handleException(e);
        }
        return -1;
    }

    public String getColumnName(int columnIndex) {
        try {
            return super.getColumnName(columnIndex);
        } catch (Throwable e) {
            handleException(e);
        }
        return null;
    }

    @Override
    public String[] getColumnNames() {
        try {
            return super.getColumnNames();
        } catch (Throwable e) {
            handleException(e);
        }
        return null;
    }

    @Override
    public double getDouble(int columnIndex) {
        try {
            double value = super.getDouble(columnIndex);
            return processGet(value, columnIndex);
        } catch (Throwable e) {
            handleException(e);
        }
        return 0;
    }

    @Override
    public Bundle getExtras() {
        try {
            return super.getExtras();
        } catch (Throwable e) {
            handleException(e);
        }
        return null;
    }

    @Override
    public float getFloat(int columnIndex) {
        try {
            float value = super.getFloat(columnIndex);
            return processGet(value, columnIndex);
        } catch (Throwable e) {
            handleException(e);
        }
        return 0;
    }

    @Override
    public int getInt(int columnIndex) {
        try {
            int value = super.getInt(columnIndex);
            return processGet(value, columnIndex);
        } catch (Throwable e) {
            handleException(e);
        }
        return 0;
    }

    @Override
    public long getLong(int columnIndex) {
        try {
            long value = super.getLong(columnIndex);
            return processGet(value, columnIndex);
        } catch (Throwable e) {
            handleException(e);
        }
        return 0;
    }

    @Override
    public short getShort(int columnIndex) {
        try {
            short value = super.getShort(columnIndex);
            return processGet(value, columnIndex);
        } catch (Throwable e) {
            handleException(e);
        }
        return 0;
    }

    @Override
    public String getString(int columnIndex) {
        try {
            String value = super.getString(columnIndex);
            return processGet(value, columnIndex);
        } catch (Throwable e) {
            handleException(e);
        }
        return null;
    }

    @Override
    public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
        try {
            super.copyStringToBuffer(columnIndex, buffer);
        } catch (Throwable e) {
            handleException(e);
        }
    }

    @Override
    public byte[] getBlob(int columnIndex) {
        try {
            byte[] value = super.getBlob(columnIndex);
            return processGet(value, columnIndex);
        } catch (Throwable e) {
            handleException(e);
        }
        return null;
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        try {
            return super.getWantsAllOnMoveCalls();
        } catch (Throwable e) {
            handleException(e);
        }
        return false;
    }

    @Override
    public boolean isAfterLast() {
        try {
            return super.isAfterLast();
        } catch (Throwable e) {
            handleException(e);
        }
        return false;
    }

    @Override
    public boolean isBeforeFirst() {
        try {
            return super.isBeforeFirst();
        } catch (Throwable e) {
            handleException(e);
        }
        return false;
    }

    @Override
    public boolean isFirst() {
        try {
            return super.isFirst();
        } catch (Throwable e) {
            handleException(e);
        }
        return false;
    }

    @Override
    public boolean isLast() {
        try {
            return super.isLast();
        } catch (Throwable e) {
            handleException(e);
        }
        return false;
    }

    @Override
    @SuppressLint("NewApi")
    public int getType(int columnIndex) {
        try {
            return super.getType(columnIndex);
        } catch (Throwable e) {
            handleException(e);
        }
        return FIELD_TYPE_NULL;
    }

    @Override
    public boolean isNull(int columnIndex) {
        try {
            return super.isNull(columnIndex);
        } catch (Throwable e) {
            handleException(e);
        }
        return true;
    }

    @Override
    public boolean moveToLast() {
        try {
            return super.moveToLast();
        } catch (Throwable e) {
            handleException(e);
        }
        return false;
    }

    @Override
    public boolean move(int offset) {
        try {
            return super.move(offset);
        } catch (Throwable e) {
            handleException(e);
        }
        return false;
    }

    @Override
    public boolean moveToPosition(int position) {
        try {
            return super.moveToPosition(position);
        } catch (Throwable e) {
            handleException(e);
        }
        return false;
    }

    @Override
    public boolean moveToNext() {
        try {
            return super.moveToNext();
        } catch (Throwable e) {
            handleException(e);
        }
        return false;
    }

    @Override
    public int getPosition() {
        try {
            return super.getPosition();
        } catch (Throwable e) {
            handleException(e);
        }
        return -1;
    }

    @Override
    public boolean moveToPrevious() {
        try {
            return super.moveToPrevious();
        } catch (Throwable e) {
            handleException(e);
        }
        return false;
    }

    @Override
    public void registerContentObserver(ContentObserver observer) {
        try {
            super.registerContentObserver(observer);
        } catch (Throwable e) {
            handleException(e);
        }
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        try {
            super.registerDataSetObserver(observer);
        } catch (Throwable e) {
            handleException(e);
        }
    }

    @Override
    public boolean requery() {
        try {
            return super.requery();
        } catch (Throwable e) {
            handleException(e);
        }
        return false;
    }

    @Override
    public Bundle respond(Bundle extras) {
        try {
            return super.respond(extras);
        } catch (Throwable e) {
            handleException(e);
        }
        return null;
    }

    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri) {
        try {
            super.setNotificationUri(cr, uri);
        } catch (Throwable e) {
            handleException(e);
        }
    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {
        try {
            super.unregisterContentObserver(observer);
        } catch (Throwable e) {
            handleException(e);
        }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        try {
            super.unregisterDataSetObserver(observer);
        } catch (Throwable e) {
            handleException(e);
        }
    }

    private <T> T processGet(T value, int columnIndex) {
        // return mColumnValueProcessor.processGet(value, mEntityClass, getColumnName(columnIndex));
        return value;
    }

    private void handleException(Throwable e) {
        DbCacheExceptionHandler.getInstance().handleException(e);
    }
}
