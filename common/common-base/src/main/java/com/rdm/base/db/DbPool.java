package com.rdm.base.db;

import android.database.Cursor;

/**
 * Created on 2015/6/29
 */
public interface DbPool<Type> extends Pool<Type> {

    String getTable();

    Cursor get(boolean distinct, String[] columns,
               String selection, String[] selectionArgs, String groupBy,
               String having, String orderBy, String limit);

    int removeKeyStartWith(String key);

    int queryCount(String keyPattern);

    /**
     * 使过期，但不删除
     */
    boolean expire(String key);

}
