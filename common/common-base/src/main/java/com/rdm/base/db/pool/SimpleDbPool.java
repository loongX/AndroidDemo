package com.rdm.base.db.pool;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;


import com.rdm.base.BaseSession;
import com.rdm.base.app.BaseApp;
import com.rdm.base.db.DbPool;
import com.rdm.common.ILog;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * 每张表对应一个DbPool
 */
public class SimpleDbPool implements DbPool<Serializable> {

    private static final String TAG = "DbPool";


    private boolean released;

    private final String table;

    private final PoolDbHelper poolDbHelper;
    private PoolManager poolManager;
    private SQLiteDatabase db;

    public SimpleDbPool(String table,BaseSession session) {
        poolDbHelper = new PoolDbHelper(BaseApp.get(),session.getUid());
        poolManager = new PoolManager(BaseApp.get(),session);
        this.table = table;
        log("table:" + table);
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public Cursor get(boolean distinct, String[] columns,
                      String selection, String[] selectionArgs, String groupBy,
                      String having, String orderBy, String limit) {
        if (released) {
            ILog.printStackTrace(new IllegalStateException("Db pool released !"));
            return null;
        }

        return poolDbHelper.getReadableDatabase().query(
                distinct,
                getTable(),
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy,
                limit
        );
    }

    @Override
    public int queryCount(String keyPattern) {
        if (TextUtils.isEmpty(keyPattern)) return 0;

        if (released) {
            ILog.printStackTrace(new IllegalStateException("Db pool released !"));
            return 0;
        }

        Cursor cursor = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE %s LIKE '%s'",
                    getTable(), PoolTableV2.KEY, keyPattern
            );
            cursor = poolDbHelper.getReadableDatabase().rawQuery(sql, null);

            if (cursor != null && cursor.moveToNext()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
            ILog.printStackTrace(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    @Override
    public boolean expire(String key) {
        Serializable obj = get(key);
        if (obj == null) return false;

        ensureDb();

        try {
            ContentValues values = PoolTableV3.contentValues4Expire();
            String where = String.format("%s='%s'", PoolTableV2.KEY, key);
            int updated = db.update(getTable(), values, where, null);
            return updated == 1;
        } catch (Exception e) {
            ILog.printStackTrace(e);
        }

        return false;
    }

    @Override
    public void put(String key, Serializable object) {
        put(key, object, PRIORITY_NORMAL);
    }

    @Override
    public void put(String key, Serializable object, int priority) {
        Map<String, Serializable> map = new HashMap<>();
        map.put(key, object);
        putAll(map, priority);
    }

    @Override
    public void putAll(Map<String, ? extends Serializable> map) {
        putAll(map, PRIORITY_NORMAL);
    }

    @Override
    public void putAll(Map<String, ? extends Serializable> map, int priority) {
        if (released) {
            ILog.printStackTrace(new IllegalStateException("Db pool released !"));
            return;
        }

        if (map == null || map.isEmpty()) return;

        ensureDb();

        Map<String, Serializable> inserts = new HashMap<>();
        Map<String, Serializable> updates = new HashMap<>();

        Map<String, Integer> exist = queryExist(map.keySet());

        for (Map.Entry<String, ? extends Serializable> entry : map.entrySet()) {
            if (exist.containsKey(entry.getKey())) {
                updates.put(entry.getKey(), entry.getValue());
            } else {
                inserts.put(entry.getKey(), entry.getValue());
            }
        }

        insert(inserts, priority);

        update(updates, exist, priority);
    }

    @Override
    public boolean remove(String key) {
        ArrayList<String> keys = new ArrayList<>();
        keys.add(key);
        return remove(keys) == 1;
    }

    @Override
    public int remove(Collection<String> keys) {
        if (released) {
            ILog.printStackTrace(new IllegalStateException("Db pool released !"));
            return 0;
        }

        if (keys == null || keys.isEmpty()) return 0;

        ensureDb();

        poolManager.onDel(keys);

        String where = whereKeyIn(keys);

        int delete = db.delete(getTable(), where, null);
        log("delete:" + delete);
        return delete;
    }

    @Override
    public int removeKeyStartWith(String prefix) {
        if (TextUtils.isEmpty(prefix)) return 0;

        ensureDb();

        String where = whereKeyStartWith(prefix);

        int delete = db.delete(getTable(), where, null);
        log("delete:" + delete);
        return delete;
    }

    @Override
    public Serializable get(String key) {
        return get(key, null);
    }

    @Override
    public <T extends Serializable> T get(String key, Class<T> _class) {
        return get(key, _class, null);
    }

    @Override
    public <T extends Serializable> T get(String key, Class<T> _class, Map<String, Object> cacheExtra) {
        Set<String> keys = new HashSet<>();
        keys.add(key);
        Map<String, T> map = get(keys, _class, cacheExtra);
        Serializable obj = map.get(key);
        return getWithType(obj, _class);
    }

    @Override
    public Map<String, Serializable> get(Set<String> keys) {
        return get(keys, null);
    }

    @Override
    public <T extends Serializable> Map<String, T> get(Set<String> keys, Class<T> _class) {
        return get(keys, _class, null);
    }

    @Override
    public <T extends Serializable> Map<String, T> get(Set<String> keys, Class<T> _class, Map<String, Object> cacheExtra) {
        Map<String, T> result = new HashMap<>();

        if (released) {
            ILog.printStackTrace(new IllegalStateException("Db pool released !"));
            return result;
        }

        if (keys == null || keys.isEmpty()) return result;

        poolManager.onQuery(PoolTableV2.TABLE_NAME, keys, poolDbHelper);

        String where = whereKeyIn(keys);

        String[] columns = cacheExtra == null
                ? new String[]{PoolTableV2.KEY, PoolTableV2.RAW_DATA}
                : new String[]{PoolTableV2.KEY, PoolTableV2.RAW_DATA, PoolTableV2.LAST_MODIFY};

        Cursor cursor = get(true, columns, where, null, null, null, null, null);

        Set<String> badData = new HashSet<>();

        try {
            if (cursor != null) {
                int indexKey = cursor.getColumnIndex(PoolTableV2.KEY);
                int indexRawData = cursor.getColumnIndex(PoolTableV2.RAW_DATA);
                int indexLastModify = cursor.getColumnIndex(PoolTableV2.LAST_MODIFY);

                while (cursor.moveToNext()) {
                    String key = cursor.getString(indexKey);

                    try {
                        result.put(key, null);

                        byte[] bytes = cursor.getBlob(indexRawData);
                        if (bytes != null) {
                            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                            ObjectInputStream is = new ObjectInputStream(bis);
                            Object obj = is.readObject();
                            T t = getWithType(obj, _class);
                            if (t != null) {
                                result.put(key, t);
                                if (cacheExtra != null) {
                                    cacheExtra.put(key, cursor.getLong(indexLastModify));
                                }
                            }
                        }
                    } catch (Exception e) {
                        ILog.printStackTrace(e);

                        badData.add(key);
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (!badData.isEmpty()) {
                remove(badData);
            }
        }

        return result;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Are you kidding me !");
    }

    @Override
    public void release() {
        released = true;

        if (db != null) {
            db.close();
            db = null;
        }
    }

    public PoolDbHelper getPoolDbHelper() {
        return poolDbHelper;
    }

    @SuppressWarnings("unchecked")
    private <T extends Serializable> T getWithType(Object obj, Class<T> _class) {
        return obj == null ? null : (T) obj;
    }

    private void log(String msg) {
        if (PoolManager.isDebug()) {
            ILog.d(TAG, msg);
        }
    }

    private void ensureDb() {
        if (db == null) {
            db = poolDbHelper.getWritableDatabase();
        }
    }

    private void insert(Map<String, Serializable> inserts, int priority) {
        try {
            db.beginTransaction();
            for (Map.Entry<String, Serializable> entry : inserts.entrySet()) {
                try {
                    Serializable value = entry.getValue();
                    ContentValues values = PoolTableV3.contentValues(entry.getKey(), value, priority);
                    long insert = db.insert(getTable(), null, values);

                    log("insert :" + insert);
                } catch (Exception e) {
                    ILog.printStackTrace(e);

                    if (PoolManager.isDebug()) {
                        throw new RuntimeException(e);
                    }
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    private void update(Map<String, Serializable> updates, Map<String, Integer> exist, int priority) {
        for (Map.Entry<String, Serializable> entry : updates.entrySet()) {
            try {
                Serializable value = entry.getValue();
                ContentValues values = PoolTableV3.contentValues(entry.getKey(), value, priority);
                if (values.getAsInteger(PoolTableV1.RAW_DATA_HASH).equals(exist.get(entry.getKey()))) {
                    values.remove(PoolTableV1.RAW_DATA);
                    values.remove(PoolTableV1.RAW_DATA_HASH);
                    values.remove(PoolTableV1.SIZE);
                    ILog.d(TAG, "Non changed update !" + value);
                    // continue;
                }
                String where = String.format("%s='%s'", PoolTableV2.KEY, entry.getKey());
                db.update(getTable(), values, where, null);
            } catch (Exception e) {
                ILog.printStackTrace(e);
            }
        }
    }

    private Map<String, Integer> queryExist(Set<String> keys) {
        Map<String, Integer> exist = new HashMap<>();
        String where = whereKeyIn(keys);

        String[] columns = {PoolTableV2.KEY, PoolTableV2.RAW_DATA_HASH};

        Cursor cursor = get(true, columns, where, null, null, null, null, null);

        try {
            while (cursor != null && cursor.moveToNext()) {
                exist.put(cursor.getString(0), cursor.getInt(1));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return exist;
    }

    private String whereKeyIn(Collection<String> keys) {
        StringBuilder where = new StringBuilder();
        where.append(PoolTableV2.KEY + " IN (");
        if (keys != null) {
            for (String key : keys) {
                where.append('\'').append(key).append("\',");
            }
            where.setLength(where.length() - 1);
        }
        where.append(')');
        return where.toString();
    }

    private String whereKeyStartWith(String prefix) {
        return String.format("%s LIKE '%s%%'", PoolTableV2.KEY, prefix);
    }

}
