
package com.rdm.base.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.rdm.base.db.EntityManager.UpdateListener;
import com.rdm.base.db.entity.TableEntity;
import com.rdm.base.db.exception.DbCacheExceptionHandler;
import com.rdm.common.ILog;
import com.rdm.common.util.IOUtils;

import java.util.concurrent.ConcurrentHashMap;

public class DefaultSQLiteOpenHelper implements ISQLiteOpenHelper {

    private DBHelper mOpenHelper;

    @Override
    public void init(Context context, String name, int version) {
        init(context, name, version, null);
    }

    @Override
    public void init(Context context, String name, int version, UpdateListener updateListener) {
        if (mOpenHelper == null) {
            mOpenHelper = new DBHelper(context, name, null, version, updateListener);
        }
    }

    @Override
    public ISQLiteDatabase getWritableDatabase() {
        return new DefaultSQLiteDatabase(mOpenHelper.getWritableDatabase());
    }

    @Override
    public ISQLiteDatabase getReadableDatabase() {
        return new DefaultSQLiteDatabase(mOpenHelper.getReadableDatabase());
    }

    private static ConcurrentHashMap<String,DefaultSQLiteOpenHelper> sInstanceMap = new ConcurrentHashMap<String, DefaultSQLiteOpenHelper>();

    public static DefaultSQLiteOpenHelper getInstance(String dbName) {
        DefaultSQLiteOpenHelper openHelper = sInstanceMap.get(dbName);
        if (openHelper == null) {
            synchronized (DefaultSQLiteOpenHelper.class) {
                openHelper = sInstanceMap.get(dbName);
                if (openHelper == null) {
                   openHelper = new DefaultSQLiteOpenHelper();
                }
            }
        }
        return openHelper;
    }

    private DefaultSQLiteOpenHelper() {}

    public static class DBHelper extends SQLiteOpenHelper {
        private final String mDataName;

        private final Context mContext;

        /**
         * 数据库更新监听器
         */
        private UpdateListener mUpdateListener;

        /**
         * 构造函数
         * 
         * @param context 上下文
         * @param name 数据库名字
         * @param factory 可选的数据库游标工厂类，当查询(query)被提交时，该对象会被调用来实例化一个游标
         * @param version 数据库版本
         * @param updateListener 数据库更新监听器
         */
        public DBHelper(Context context, String name, CursorFactory factory, int version, UpdateListener updateListener) {
            super(context, name, factory, version);
            mDataName = name;
            mContext = context;
            mUpdateListener = updateListener;
        }

        public void onCreate(SQLiteDatabase db) {

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (mUpdateListener != null) {
                mUpdateListener.onDatabaseUpgrade(new DefaultSQLiteDatabase(db), oldVersion, newVersion);
            } else {
                dropDatabase(db);
            }
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (mUpdateListener != null) {
                mUpdateListener.onDatabaseDowngrade(new DefaultSQLiteDatabase(db), oldVersion, newVersion);
            } else {
                dropDatabase(db);
            }
        }

        private static void dropDatabase(SQLiteDatabase db) {
            if (db != null) {
                Cursor cursor = null;
                try {
                    cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type ='table'", null);
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            try {
                                String tableName = cursor.getString(0);
                                db.execSQL("DROP TABLE IF EXISTS " + tableName);
                                TableEntity.remove(tableName);
                            } catch (Throwable e) {
                                ILog.e("DBHelper", e.getMessage(), e);
                            }
                        }
                    }
                } finally {
                    IOUtils.closeQuietly(cursor);
                }
            }
        }

        @Override
        public SQLiteDatabase getWritableDatabase() {
            synchronized (this) {
                SQLiteDatabase db = null;
                try {
                    db = super.getWritableDatabase();
                } catch (Throwable e) {

                    // delete database if error occurs.
                    deleteDatabase();
                    // try to re-query (fore to create new database file) and handle exception
                    // occurs.
                    try {
                        db = super.getWritableDatabase();
                    } catch (Throwable t) {
                        handleException(t);
                    }
                }
                return db;
            }
        }

        /**
         * Delete this database.
         */
        public void deleteDatabase() {
            mContext.deleteDatabase(mDataName);
        }

        private static void handleException(Throwable e) {
            try {
                // handle exception safely.
                DbCacheExceptionHandler.getInstance().handleException(e);
            } catch (Throwable t) {
                // empty.
            }
        }

    }

}
