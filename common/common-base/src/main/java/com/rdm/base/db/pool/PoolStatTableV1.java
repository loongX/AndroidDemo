package com.rdm.base.db.pool;

import android.content.ContentValues;

/**
 * Created on 2015/6/30 by  .
 */
public class PoolStatTableV1 {

    public static final String TABLE_NAME = "pool_stat";

    public static final String TABLE = "_table";
    public static final String KEY = "key";
    public static final String QUERY_COUNT = "query_count";
    public static final String LAST_QUERY = "last_query";

    public static final String CREATE_TABLE_SQL_V_1 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            "(" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "_table TEXT," +
            "key TEXT," +
            "query_count INTEGER," +
            "last_query TIMESTAMP," +

            "ext1 TEXT," +
            "ext2 TEXT," +
            "ext3 INTEGER," +
            "ext4 INTEGER" +
            ")";

    public static final String DROP_TABLE_SQL = "DROP TABLE " + TABLE_NAME;

    public static ContentValues contentValues(String table, String key, int queryCount, long lastQuery) {
        ContentValues values = new ContentValues();
        values.put(TABLE, table);
        values.put(KEY, key);
        values.put(QUERY_COUNT, queryCount);
        values.put(LAST_QUERY, lastQuery);
        return values;
    }

}
