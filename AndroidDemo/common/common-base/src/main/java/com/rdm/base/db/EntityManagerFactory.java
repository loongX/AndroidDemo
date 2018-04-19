package com.rdm.base.db;

import android.content.Context;
import android.text.TextUtils;

import com.rdm.base.app.BaseApp;
import com.rdm.common.ILog;
import com.rdm.base.db.EntityManager.UpdateListener;
import com.rdm.base.db.exception.DBException;
import com.rdm.base.db.util.DatabaseUtils;
import com.rdm.base.db.util.TableUtils;
import com.rdm.base.thread.ThreadPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Author: hugozhong Date: 2013-11-7
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class EntityManagerFactory {

    private static final String TAG = "EntityManagerFactory";

    private static final String EMPTY_USER_ACCOUNT = String.valueOf(Integer.MIN_VALUE);

    private final Context mContext;

    private final HashMap<String, EntityManagerRecord> mActiveRecords = new HashMap<String, EntityManagerRecord>();

    private final HashMap<EntityManager<?>, String> mKeysMap = new HashMap<EntityManager<?>, String>();

    private String mUserAccount;

    private String mDatabaseName;

    private int mDBVersion;

    private ISQLiteOpenHelper mOpenHelper;

    private final HashSet<UpdateListener> mUpdateListenerSet = new HashSet<UpdateListener>();

    private final DefaultUpdateListener mDefaultUpdateListener = new DefaultUpdateListener();

    private EntityManagerFactory(Context context, int dbVersion, String userAccount, ISQLiteOpenHelper openHelper) {
        mContext = context.getApplicationContext();
        mUserAccount = userAccount;
        mDatabaseName = DatabaseUtils.getDatabaseName(userAccount);
        mDBVersion = dbVersion;
        if (openHelper == null) {
            openHelper = DefaultSQLiteOpenHelper.getInstance(userAccount);
        }
        openHelper.init(mContext, mDatabaseName, mDBVersion, mUpdateListenerProxy);
        mOpenHelper = openHelper;

        DatabaseUtils.recordDatabase(openHelper, mDatabaseName, mDBVersion);
    }

    public void addUpdateListener(UpdateListener updateListener) {
        if (updateListener != null) {
            mUpdateListenerSet.add(updateListener);
        } else {
            mUpdateListenerSet.add(mDefaultUpdateListener);
        }
    }

    public <T> EntityManager<T> getEntityManager(Class<T> clazz) {
        return getEntityManager(clazz, null, null, false);
    }

    public <T> EntityManager<T> getEntityManager(Class<T> clazz, String table) {
        return getEntityManager(clazz, table, null, false);
    }

    /**
     * Get the corresponding entity manager.
     *
     * @param clazz Class of corresponding.
     * @param table name.
     * @return corresponding entity manager.
     */
    public <T> EntityManager<T> getEntityManager(Class<T> clazz, String table, ClassLoader classLoader, boolean persist) {
        if (clazz == null) {
            throw new RuntimeException("invalid Entity class: " + "null");
        }
        table = TableUtils.getTableName(clazz, table);
        if (TextUtils.isEmpty(table)) {
            throw new RuntimeException("invalid table name: " + table);
        }
        synchronized (mActiveRecords) {
            String key = uniqueKey(mUserAccount, table, persist);
            EntityManagerRecord record = mActiveRecords.get(key);
            if (record != null && record.db != null) {
                Class<T> oldEntityClass = record.db.getEntityClass();
                if (oldEntityClass != clazz) {
                    record = null;
                }
            }
            if (record == null || record.db == null || record.db.isClosed()) {
                if (classLoader == null) {
                    classLoader = getClass().getClassLoader();
                }
                EntityManager<T> cacheManager = new EntityManager<T>(mContext, clazz, mUpdateListenerProxy, mDatabaseName, table, classLoader, mOpenHelper,mUserAccount);
                cacheManager.setOnCloseListener(mCacheCloseListener);
                record = new EntityManagerRecord(cacheManager, mUserAccount, persist);
                mActiveRecords.put(key, record);
                mKeysMap.put(cacheManager, key);
            }
            return record.db;
        }
    }

    private UpdateListener mUpdateListenerProxy = new UpdateListener() {

        @Override
        public void onDatabaseUpgrade(ISQLiteDatabase db, int oldVersion, int newVersion) {
            HashSet<UpdateListener> listeners = new HashSet<UpdateListener>(mUpdateListenerSet);
            for (UpdateListener listener : listeners) {
                if (listener != null) {
                    listener.onDatabaseUpgrade(db, oldVersion, newVersion);
                }
            }
        }

        @Override
        public void onDatabaseDowngrade(ISQLiteDatabase db, int oldVersion, int newVersion) {
            HashSet<UpdateListener> listeners = new HashSet<UpdateListener>(mUpdateListenerSet);
            for (UpdateListener listener : listeners) {
                if (listener != null) {
                    listener.onDatabaseDowngrade(db, oldVersion, newVersion);
                }
            }
        }

        @Override
        public void onTableUpgrade(ISQLiteDatabase db, String tableName, int oldVersion, int newVersion) {
            HashSet<UpdateListener> listeners = new HashSet<UpdateListener>(mUpdateListenerSet);
            for (UpdateListener listener : listeners) {
                if (listener != null) {
                    listener.onTableUpgrade(db, tableName, oldVersion, newVersion);
                }
            }
        }

        @Override
        public void onTableDowngrade(ISQLiteDatabase db, String tableName, int oldVersion, int newVersion) {
            HashSet<UpdateListener> listeners = new HashSet<UpdateListener>(mUpdateListenerSet);
            for (UpdateListener listener : listeners) {
                if (listener != null) {
                    listener.onTableDowngrade(db, tableName, oldVersion, newVersion);
                }
            }
        }

    };

    public void close() {
        close(EMPTY_USER_ACCOUNT);
    }

    /**
     * Close all the cache manager retrieved by this service.
     */
    public void close(String userAccount) {
        synchronized (mActiveRecords) {
            Iterator<EntityManagerRecord> iterator = mActiveRecords.values().iterator();
            while (iterator.hasNext()) {
                EntityManagerRecord record = iterator.next();
                if (record == null) {
                    // remove null record.
                    iterator.remove();
                    continue;
                }
                if (EMPTY_USER_ACCOUNT.equals(userAccount) || record.userAccount == userAccount) {
                    // remove the OnCloseListener to avoid map modification
                    // during iteration.
                    record.db.setOnCloseListener(null);
                    record.db.close();

                    iterator.remove();
                    mKeysMap.remove(record.db);
                }
            }
        }
    }

    public void clearMemory() {
        synchronized (mActiveRecords) {
            mActiveRecords.clear();
        }
    }

    /**
     * Clear all the data of cache manager retrieved by this service, exclude
     * persist ones.
     */
    public void clear() {
        clear(EMPTY_USER_ACCOUNT);
    }

    /**
     * Clear the corresponding user's cache data of cache manager retrieved by
     * this service, exclude persist ones.
     */
    public void clear(String userAccount) {
        synchronized (mActiveRecords) {
            for (EntityManagerRecord record : mActiveRecords.values()) {
                if (record == null)
                    continue;
                if (!record.persist && (EMPTY_USER_ACCOUNT.equals(userAccount) || record.userAccount == userAccount))
                    try {
                        record.db.delete(null);
                    } catch (DBException e) {
                        ILog.e(TAG, e.getMessage(), e);
                    }
            }
        }
    }

    private EntityManager.OnCloseListener mCacheCloseListener = new EntityManager.OnCloseListener() {
        @Override
        public void onClosed(EntityManager cacheManager) {
            synchronized (mActiveRecords) {
                String key = mKeysMap.remove(cacheManager);
                mActiveRecords.remove(key);
            }
        }
    };

    private static String uniqueKey(String userAccount, String table, boolean persist) {
        return userAccount + "_" + table + "_" + persist;
    }

    /**
     * Db cache manager record.
     */
    final static class EntityManagerRecord {

        public final EntityManager db;

        public final String userAccount;

        public final boolean persist;

        public EntityManagerRecord(EntityManager db, String userAccount, boolean persist) {
           // AssertUtil.assertTrue(db != null);
            this.db = db;
            this.userAccount = userAccount;
            this.persist = persist;
        }
    }

    // --------- singleton -------------
    private volatile static HashMap<String, EntityManagerFactory> sFactoryMap = new HashMap<String, EntityManagerFactory>();

    public static EntityManagerFactory getInstance(Context context, int dbVersion, String userAccount, ISQLiteOpenHelper openHelper, UpdateListener updateListener) {
        if (TextUtils.isEmpty(userAccount)) {
            userAccount = EMPTY_USER_ACCOUNT;
        }
        EntityManagerFactory entityManagerFactory = sFactoryMap.get(userAccount);
        if (entityManagerFactory == null) {
            synchronized (sFactoryMap) {
                entityManagerFactory = sFactoryMap.get(userAccount);
                if (entityManagerFactory == null) {
                    entityManagerFactory = new EntityManagerFactory(context, dbVersion, userAccount, openHelper);
                    sFactoryMap.put(userAccount, entityManagerFactory);
                }
            }
        }
        entityManagerFactory.addUpdateListener(updateListener);

        return entityManagerFactory;
    }

    public static void clearFactories() {
        synchronized (sFactoryMap) {
            for (Map.Entry<String, EntityManagerFactory> entry : sFactoryMap.entrySet()) {
                entry.getValue().clearMemory();
            }
            sFactoryMap.clear();
        }
    }

    public static interface ClearDataHandler {
        void onClearSysDB(DatabaseUtils.DataBaseRecord record);

        void onClearCustomDB(DatabaseUtils.DataBaseRecord record);
    }

    public static class DefaultClearDataHandler implements ClearDataHandler {

        @Override
        public void onClearSysDB(DatabaseUtils.DataBaseRecord record) {
            try {
                ISQLiteOpenHelper helper = null;
                Class clazz = Class.forName(record.openHelperClass);
                if (clazz.equals(DefaultSQLiteOpenHelper.class)) {
                    helper = DefaultSQLiteOpenHelper.getInstance(record.dbName);
                } else {
                    helper = SdcardSQLiteOpenHelper.getInstance(record.dbName, record.dbPath);
                }
                if (helper != null) {
                    helper.init(BaseApp.get(), record.dbName, record.version);
                    DatabaseUtils.dropDatabase(helper.getWritableDatabase(), false);
                }
            } catch (ClassNotFoundException e) {
                ILog.e(TAG, e.getMessage(), e);
            }
        }

        @Override
        public void onClearCustomDB(DatabaseUtils.DataBaseRecord record) {

        }
    }


    /**
     * 如果whiteList是空则默认在白名单以内
     */
    private static boolean isInWhiteList(List<String> whiteList, DatabaseUtils.DataBaseRecord record) {
        return whiteList == null || whiteList.size() == 0 || whiteList.contains(record.dbName);
    }

    private static boolean isInBlackList(List<String> blackList, DatabaseUtils.DataBaseRecord record) {
        String dbName = record.dbName;
        if (DatabaseUtils.getDatabaseName(DatabaseUtils.DBRECORD_NAME).equals(dbName)) {
            return true;
        }
        return (blackList != null && blackList.contains(dbName));
    }

    private static List<String> getTranslateWhiteList(List<String> whiteList) {
        if (whiteList != null) {
            ArrayList<String> translateWhiteList = new ArrayList<>();
            for(String white : whiteList) {
                translateWhiteList.add(DatabaseUtils.getDatabaseName(white));
            }
            return translateWhiteList;
        }
        return null;
    }

    private static List<String> getTranslateBlackList(List<String> blackList) {
        if (blackList != null) {
            ArrayList<String> translateBlackList = new ArrayList<>();
            for (String white : blackList) {
                translateBlackList.add(DatabaseUtils.getDatabaseName(white));
            }
            return translateBlackList;
        }
        return null;
    }

    public static void clear(ClearDataHandler handler, final List<String> whiteList, final List<String> blackList) {
        if (handler == null) {
            handler = new DefaultClearDataHandler();
        }
        final ClearDataHandler dataHandler = handler;
        ThreadPool.getInstance().submit(new ThreadPool.Job<Object>() {
            @Override
            public Object run(ThreadPool.JobContext jc) {
                List<DatabaseUtils.DataBaseRecord> records = DatabaseUtils.getAllDatabaseRecords();
                if (records != null) {
                    List<String> translateWhiteList = getTranslateWhiteList(whiteList);
                    List<String> translateBlackList = getTranslateBlackList(blackList);
                    for (DatabaseUtils.DataBaseRecord record : records) {
                        if (!TextUtils.isEmpty(record.openHelperClass)) {
                            if (isInWhiteList(translateWhiteList,record) && !isInBlackList(translateBlackList,record)) {//如果在白名单里（或者白名单为空）并且不在黑名单里则进行删除
                                try {
                                    Class clazz = Class.forName(record.openHelperClass);
                                    if (clazz != null) {
                                        if (clazz.equals(DefaultSQLiteOpenHelper.class) || clazz.equals(SdcardSQLiteOpenHelper.class)) {
                                            ILog.i(TAG,"clear system db "+record.dbName);
                                            dataHandler.onClearSysDB(record);
                                        } else {
                                            ILog.i(TAG,"clear custom db "+record.dbName);
                                            dataHandler.onClearCustomDB(record);
                                        }
                                    }
                                } catch (ClassNotFoundException e) {
                                    ILog.e(TAG, e.getMessage(), e);
                                }
                            }
                        }
                    }
                }
                return null;
            }
        });
    }
}
