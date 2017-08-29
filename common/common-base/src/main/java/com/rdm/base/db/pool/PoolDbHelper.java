package com.rdm.base.db.pool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rdm.common.Debug;
import com.rdm.common.ILog;


/**
 * Created on 2015/6/30 by  .
 */
public class PoolDbHelper extends SQLiteOpenHelper {

    public PoolDbHelper(Context context,String uid) {
        super(context, Debug.isDebug() ? Constants.DEBUG_DB_NAME + "-" + uid : Constants.DB_NAME + "-" + uid, null, Constants.CURRENT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(PoolTableV2.CREATE_TABLE_SQL);
            db.execSQL(PoolTableV2.CREATE_INDEX_SQL);
            db.execSQL(PoolStatTableV1.CREATE_TABLE_SQL_V_1);
        } catch (Exception e) {
            ILog.printStackTrace(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(PoolTableV2.DROP_TABLE_SQL);
            db.execSQL(PoolStatTableV1.DROP_TABLE_SQL);
            onCreate(db);
        } catch (Exception e) {
            ILog.printStackTrace(e);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(PoolTableV2.DROP_TABLE_SQL);
            db.execSQL(PoolStatTableV1.DROP_TABLE_SQL);
            onCreate(db);
        } catch (Exception e) {
            ILog.printStackTrace(e);
        }
    }

}
