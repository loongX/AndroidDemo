package com.rdm.base.db.util;

import android.database.Cursor;
import android.text.TextUtils;

import com.rdm.base.app.BaseApp;
import com.rdm.base.thread.ThreadPool;
import com.rdm.common.ILog;
import com.rdm.common.util.IOUtils;
import com.rdm.base.db.EntityManager;
import com.rdm.base.db.EntityManagerFactory;
import com.rdm.base.db.IEncryptSQLiteOpenHelper;
import com.rdm.base.db.ISQLiteDatabase;
import com.rdm.base.db.ISQLiteOpenHelper;
import com.rdm.base.db.ISdcardSQLiteOpenHelper;
import com.rdm.base.db.annotation.Column;
import com.rdm.base.db.annotation.GenerationType;
import com.rdm.base.db.annotation.Id;
import com.rdm.base.db.annotation.Table;
import com.rdm.base.db.entity.TableEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hugozhong on 2015-8-18.
 */
public class DatabaseUtils {

    private static final ArrayList<String> EXPECT_TABLE_NAMES = new ArrayList<String>();

    static {
        EXPECT_TABLE_NAMES.add("sqlite_master");
        EXPECT_TABLE_NAMES.add("sqlite_sequence");
        EXPECT_TABLE_NAMES.add("sqlite_temp_master");
    }

    public static String getDatabaseName(String userAccount) {
        return "db_"+userAccount;
    }

    public static void dropDatabase(ISQLiteDatabase db) {
        dropDatabase(db,true);
    }

    public static void dropDatabase(ISQLiteDatabase db,boolean dropTable) {
        if (db != null) {
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type ='table'", null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        try {
                            String tableName = cursor.getString(0);
                            if (TextUtils.isEmpty(tableName) ||EXPECT_TABLE_NAMES.contains(tableName)) {
                                continue;
                            }
                            if(dropTable) {
                                db.execSQL("DROP TABLE IF EXISTS " + tableName);
                            } else {
                                db.execSQL("DELETE FROM " + tableName);
                            }
                            TableEntity.remove(tableName);
                        } catch (Throwable e) {
                            ILog.e("DatabaseUtils", e.getMessage(), e);
                        }
                    }
                }
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
    }


    private static final int DBRECORD_DB_VERSION = 1;

    @Table(version = 2,name="DataBaseRecord")
    public static class DataBaseRecord {
        @Id(strategy = GenerationType.ASSIGN)
        public String dbName;
        @Column
        public int version;
        @Column
        public String dbPath;
        @Column
        public String openHelperClass;
        @Column(name = "es")
        public String encryptSeeds;
    }

    public static final String DBRECORD_NAME = "dbrecord";

    private static EntityManager<DataBaseRecord> getDBRecordEntityManager() {
        return EntityManagerFactory.getInstance(BaseApp.get(),DBRECORD_DB_VERSION,DBRECORD_NAME,null,null).getEntityManager(DataBaseRecord.class,null);
    }

    public static void recordDatabase(final ISQLiteOpenHelper openHelper,final String dbName,final int version) {
        if (openHelper != null) {
            ThreadPool.getInstance().submit(new ThreadPool.Job<Object>() {
                @Override
                public Object run(ThreadPool.JobContext jc) {
                    DataBaseRecord record = new DataBaseRecord();
                    record.dbName = dbName;
                    record.version = version;
                    record.openHelperClass = openHelper.getClass().getName();
                    if (openHelper instanceof ISdcardSQLiteOpenHelper) {
                        record.dbPath = ((ISdcardSQLiteOpenHelper) openHelper).getDatabaPath();
                    }
                    if (openHelper instanceof IEncryptSQLiteOpenHelper) {
                        record.encryptSeeds = ((IEncryptSQLiteOpenHelper) openHelper).getEncryptSeeds();
                    }
                    getDBRecordEntityManager().saveOrUpdate(record);
                    return null;
                }
            });
        }
    }

    public static List<DataBaseRecord> getAllDatabaseRecords() {
        return getDBRecordEntityManager().findAll();
    }

}
