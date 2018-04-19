package com.rdm.base.db.pool;

/**
 * Created on 2015/9/6 by  .
 * 新增优先级，占用ext3字段,废除key_hash,启用key索引
 */
public class PoolTableV2 extends PoolTableV1 {

    public static final String CREATE_INDEX_SQL = "CREATE INDEX pool_key_index on default_pool (key)";

    public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            "(" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT," +
            "key TEXT," +
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

}
