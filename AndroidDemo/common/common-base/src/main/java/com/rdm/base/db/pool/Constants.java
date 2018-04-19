package com.rdm.base.db.pool;

/**
 * Created on 2015/6/30
 */
public interface Constants {

    String DB_NAME = "pool.db";

    String DEBUG_DB_NAME = "pool_debug.db";

    /**
     * Cache key 索引,去除key_hash
     */
    int VERSION_V2 = 2;

    /**
     * Cache key 索引,去除key_hash时存在bug，导致key明明存在还被多次插入，废弃之前数据吧
     */
    int VERSION_V3 = 3;

    int CURRENT_VERSION = VERSION_V3;

}
