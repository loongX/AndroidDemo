package com.rdm.base.db.pool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rdm.base.BaseSession;
import com.rdm.base.db.Pool;
import com.rdm.base.thread.ThreadPool;
import com.rdm.common.ILog;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by donnyliu.
 */
public class PoolManager {

    private String cacheStorageDir;

    private static boolean debug;

    public static void setDebug(boolean debug) {
        PoolManager.debug = debug;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static final String DEFAULT_CACHE = "default";
    private static final int MAX_POOL_SIZE = 5000;

    private static final String TAG = "PoolManager";

    private Context context;

    private volatile boolean dbPoolInited;

    public Map<String, Info> queryStatMap;

    private Map<String, Pool> cacheMap;

    private boolean hasDbPoolStatChange;
    private BaseSession mSeesion;

    /*Package*/ PoolManager(Context context, BaseSession session) {
        this.context = context;
        mSeesion = session;
        cacheMap = new HashMap<>();
        queryStatMap = new ConcurrentHashMap<>();
    }

    public Context getContext() {
        return context;
    }

    public void syncStat2Db() {
        ILog.i(TAG, "syncStat2Db has change ?" + hasDbPoolStatChange);

        if (!hasDbPoolStatChange) return;
        hasDbPoolStatChange = false;

        PoolDbHelper dbHelper = ((SimpleDbPool) mSeesion.getPool()).getPoolDbHelper();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        db.delete(PoolStatTableV1.TABLE_NAME, null, null);
        Collection<Info> infoList = queryStatMap.values();
        int successInsert = 0;
        int total = infoList.size();
        for (Info info : infoList) {
            ContentValues values = PoolStatTableV1.contentValues(info.table, info.key, info.queryCount, info.lastQuery);
            long insert = db.insert(PoolStatTableV1.TABLE_NAME, null, values);
            if (insert != -1) {
                successInsert++;
            }
        }
        if (successInsert == total) {
            db.setTransactionSuccessful();
        }

        db.endTransaction();

        ILog.i(TAG, "synced ! " + successInsert + "/" + total);
    }

    public void onQuery(String table, Set<String> keys, final SQLiteOpenHelper dbHelper) {
        if (!dbPoolInited) {
            dbPoolInited = true;
            ThreadPool.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    loadStatAndKeepCool(dbHelper);
                }
            });
        }

        long now = System.currentTimeMillis();

        for (String key : keys) {
            if (key == null) continue;
            Info info = queryStatMap.get(key);
            if (info == null) {
                info = new Info(table, key, 0, 0);
                queryStatMap.put(key, info);
            }

            info.queryCount++;
            info.lastQuery = now;
        }

        hasDbPoolStatChange = true;
    }

    private void loadStatAndKeepCool(SQLiteOpenHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        loadAllStatInDb(db);

        Map<String, Integer> poolSummary = loadAllPoolInDb(db);
        ILog.i(TAG, "pool size :" + poolSummary.size());

        queryStatMap.keySet().retainAll(poolSummary.keySet());

        int neverQuerySize = 0;
        for (Map.Entry<String, Integer> entry : poolSummary.entrySet()) {
            String key = entry.getKey();
            int priority = entry.getValue();
            if (key == null) {
                continue;
            }
            if (queryStatMap.get(key) == null) {
                queryStatMap.put(key, new Info(PoolTableV2.TABLE_NAME, key, 0, 0));
                neverQuerySize++;
            }

            queryStatMap.get(key).priority = priority;
        }
        ILog.i(TAG, "NeverQuerySize :" + neverQuerySize);

        keepDbPoolCool();
    }

    private Map<String, Integer> loadAllPoolInDb(SQLiteDatabase db) {
        Map<String, Integer> poolSummary = new HashMap<>();
        String[] columns = new String[]{PoolTableV2.KEY, PoolTableV2.EXT_3};//优先级
        Cursor cursor = db.query(PoolTableV2.TABLE_NAME, columns, null, null, null, null, null);

        try {
            while (cursor != null && cursor.moveToNext()) {
                String key = cursor.getString(0);
                int priority = Pool.PRIORITY_NORMAL;
                try {
                    priority = cursor.getInt(1);
                } catch (Exception e) {
                    ILog.d(TAG, e.getMessage());
                }
                poolSummary.put(key, priority);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return poolSummary;
    }

    public void onDel(Collection<String> keys) {
        for (String key : keys) {
            queryStatMap.remove(key);
        }
        hasDbPoolStatChange = true;
    }

    public Map<String, Pool> getCacheMap() {
        return cacheMap;
    }

    private void loadAllStatInDb(SQLiteDatabase db) {
        ILog.i(TAG, "loadAllStatInDb ");
        Cursor cursor = db.query(PoolStatTableV1.TABLE_NAME, null, null, null, null, null, null);

        try {
            while (cursor != null && cursor.moveToNext()) {
                String table = cursor.getString(1);
                String key = cursor.getString(2);
                int queryCount = cursor.getInt(3);
                long lastQuery = cursor.getLong(4);

                queryStatMap.put(key, new Info(table, key, queryCount, lastQuery));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    private void keepDbPoolCool() {
        int total = queryStatMap.size();

        if (total < MAX_POOL_SIZE) return;

        List<Info> infos = new LinkedList<>(queryStatMap.values());

        Collections.sort(infos);

        Set<String> toRemove = new HashSet<>();
        int size = Math.round(MAX_POOL_SIZE * 0.3f);

        for (int i = 0; i < MAX_POOL_SIZE; i++) {
            if (toRemove.size() > size) break;

            Info info = infos.get(i);
            if (info.priority != Pool.PRIORITY_HIGHEST) {
                toRemove.add(info.key);
            }
        }

        queryStatMap.keySet().removeAll(toRemove);
        int remove = mSeesion.getPool().remove(toRemove);

        ILog.i(TAG, "keepPoolCool remove:" + toRemove.size() + " removed:"
                + remove + "/" + total + "->max " + MAX_POOL_SIZE);
    }

    public String getCacheStorageDir() {
        return cacheStorageDir;
    }

    public void setCacheStorageDir(String cacheStorageDir) {
        this.cacheStorageDir = cacheStorageDir;
    }

    private static class Info implements Comparable<Info> {
        final String table;
        final String key;
        int queryCount;
        long lastQuery;
        int priority;

        public Info(String table, String key, int queryCount, long lastQuery) {
            this.table = table;
            this.key = key;
            this.queryCount = queryCount;
            this.lastQuery = lastQuery;
        }

        @Override
        public int compareTo(Info another) {
            int priorityDiff = priority - another.priority;

            if (priorityDiff != 0) {
                return priorityDiff;
            }

            return compare(lastQuery, another.lastQuery);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Info info = (Info) o;

            return !(key != null ? !key.equals(info.key) : info.key != null);

        }

        @Override
        public int hashCode() {
            return key != null ? key.hashCode() : 0;
        }

        int compare(long lhs, long rhs) {
            return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
        }
    }

}
