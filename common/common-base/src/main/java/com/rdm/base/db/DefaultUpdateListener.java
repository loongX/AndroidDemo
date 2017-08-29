package com.rdm.base.db;

import android.database.Cursor;
import android.text.TextUtils;

import com.rdm.common.ILog;
import com.rdm.common.util.IOUtils;
import com.rdm.base.db.EntityManager.UpdateListener;
import com.rdm.base.db.entity.TableEntity;

/**
 * 默认的数据库升降级处理
 * 
 * @author hugozhong
 *
 */
public class DefaultUpdateListener implements UpdateListener {

    private static final String TAG = "DefaultUpdateListener";
    
    @Override
    public void onDatabaseUpgrade(ISQLiteDatabase db, int oldVersion, int newVersion) {
        ILog.i(TAG, "onDatabaseUpgrade(" + oldVersion + " --> " + newVersion + ")");
        dropDatabase(db);
    }

    @Override
    public void onDatabaseDowngrade(ISQLiteDatabase db, int oldVersion, int newVersion) {
        ILog.i(TAG, "onDatabaseDowngrade(" + oldVersion + " --> " + newVersion + ")");
        dropDatabase(db);
    }

    @Override
    public void onTableUpgrade(ISQLiteDatabase db, String tableName, int oldVersion, int newVersion) {
        ILog.i(TAG, "onTableUpgrade(" + oldVersion + " --> " + newVersion + ",tableName:" + tableName + ")");
        dropTable(db, tableName);
    }

    @Override
    public void onTableDowngrade(ISQLiteDatabase db, String tableName, int oldVersion, int newVersion) {
        ILog.i(TAG, "onTableDowngrade(" + oldVersion + " --> " + newVersion + ",tableName:" + tableName + ")");
        dropTable(db, tableName);
    }
    
    private static void dropTable(ISQLiteDatabase db, String tableName) {
        if (db != null && !TextUtils.isEmpty(tableName)) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }
    }

    private static void dropDatabase(ISQLiteDatabase db) {
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
                            ILog.e(TAG, e.getMessage(), e);
                        }
                    }
                }
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
    }

}
