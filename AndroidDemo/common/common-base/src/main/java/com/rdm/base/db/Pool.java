package com.rdm.base.db;

import android.content.Context;

import com.rdm.base.db.pool.DiskStorePool;
import com.rdm.base.db.pool.PoolTableV2;
import com.rdm.base.db.pool.SimpleDbPool;
import com.rdm.base.db.pool.WeakRefPool;
import com.rdm.common.ILog;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * User: jason
 * Date: 12-7-22
 */
public interface Pool<Type> {

    int PRIORITY_NORMAL = 0;

    int PRIORITY_HIGHEST = Integer.MAX_VALUE;

    /*abstract class Factory {

        private static DbPool<Serializable> dbPool;
        private static Pool<Object> weakRefPool;

        public static void init(Context context) {

            dbPool = new SimpleDbPool(PoolTableV2.TABLE_NAME);

            weakRefPool = new WeakRefPool();
        }

        @Deprecated
        //should use db pool
        public static Pool filePool() {
            return filePool(PoolManager.getInstance().getContext(), PoolManager.DEFAULT_CACHE);
        }

        @Deprecated
        //should use db pool
        public static synchronized Pool filePool(Context context, String key) {
            PoolManager poolManager = PoolManager.getInstance();
            Map<String, Pool> cacheMap = poolManager.getCacheMap();
            String cacheDir = poolManager.getCacheStorageDir();

            Pool pool = cacheMap.get(key);
            if (pool == null) {
                DiskStorePool diskStoreCache = new DiskStorePool();
                String cacheOutputFile = null;
                if (PoolManager.DEFAULT_CACHE.equals(key)) {
                    //老的存储位置不要轻易动
                    cacheOutputFile = cacheDir + File.separator + "cache_values";
                } else {
                    File file = new File(context.getCacheDir(), key);

                    try {
                        File parentFile = file.getParentFile();
                        if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs()) {
                            file = null;
                        }

                        if (file != null && !file.exists() && !file.createNewFile()) {
                            file = null;
                        }
                    } catch (Exception e) {
                        ILog.printStackTrace(e);
                    }

                    if (file != null) {
                        cacheOutputFile = file.getAbsolutePath();
                    }
                }

                if (cacheOutputFile == null) {
                    cacheOutputFile = cacheDir + File.separator + key;
                }

                diskStoreCache.setFile(cacheOutputFile);
                pool = diskStoreCache;
                cacheMap.put(key, pool);
            }
            return pool;
        }

        public static DbPool<Serializable> dbPool() {
            return dbPool;
        }

        public static Pool<Object> weakRefPool() {
            return weakRefPool;
        }

    }*/

    void put(String key, Type object);

    void put(String key, Type object, int priority);

    void putAll(Map<String, ? extends Type> map);

    void putAll(Map<String, ? extends Type> map, int priority);

    boolean remove(String key);

    int remove(Collection<String> keys);

    Type get(String key);

    <T extends Type> T get(String key, Class<T> _class);

    <T extends Type> T get(String key, Class<T> _class, Map<String, Object> cacheExtra);

    Map<String, Type> get(Set<String> keys);

    <T extends Type> Map<String, T> get(Set<String> keys, Class<T> _class);

    <T extends Type> Map<String, T> get(Set<String> keys, Class<T> _class, Map<String, Object> cacheExtra);

    void clear();

    void release();

}
