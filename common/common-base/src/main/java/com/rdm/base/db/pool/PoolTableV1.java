package com.rdm.base.db.pool;

/**
 * Created on 2015/6/30 by  .
 */
public class PoolTableV1 {

    public static final String TABLE_NAME = "default_pool";

    public static final String NAME = "name";
    public static final String KEY = "key";
    public static final String RAW_DATA = "raw_data";
    public static final String RAW_DATA_HASH = "raw_data_hash";
    public static final String SIZE = "size";

    public static final String EXT_1 = "ext1";
    public static final String EXT_2 = "ext2";
    public static final String EXT_3 = "ext3";
    public static final String EXT_4 = "ext4";

    public static final String LAST_MODIFY = "last_modify";
    public static final String DELETE_FLAG = "delete_flag";

    public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            "(" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT," +
            "key TEXT," +
            "key_hash INTEGER," +
            "raw_data BLOB," +
            "raw_data_hash INTEGER," +
            "size INTEGER," +

            "ext1 TEXT," +
            "ext2 TEXT," +
            "ext3 INTEGER," +
            "ext4 INTEGER," +

            "last_modify TIMESTAMP," +
            "delete_flag BOOLEAN" +
            ")";

    public static final String DROP_TABLE_SQL = "DROP TABLE " + TABLE_NAME;

}
