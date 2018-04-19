
package com.rdm.base.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DefaultSQLiteDatabase implements ISQLiteDatabase {
    private SQLiteDatabase mDatabase;

    public DefaultSQLiteDatabase(SQLiteDatabase db) {
        if (db == null) {
            throw new IllegalArgumentException("SQLiteDatabase cannot be null!");
        }
        mDatabase = db;
    }

    @Override
    public void beginTransaction() {
        mDatabase.beginTransaction();
    }

    @Override
    public void endTransaction() {
        mDatabase.endTransaction();
    }

    @Override
    public void setTransactionSuccessful() {
        mDatabase.setTransactionSuccessful();
    }

    @Override
    public boolean inTransaction() {
        return mDatabase.inTransaction();
    }

    @Override
    public int getVersion() {
        return mDatabase.getVersion();
    }

    @Override
    public void setVersion(int version) {
        mDatabase.setVersion(version);
    }

    @Override
    public Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs,
            String groupBy, String having, String orderBy, String limit) {
        return mDatabase.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    @Override
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
            String having, String orderBy) {
        return mDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    @Override
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
            String having, String orderBy, String limit) {
        return mDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    @Override
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return mDatabase.rawQuery(sql, selectionArgs);
    }

    @Override
    public long insert(String table, String nullColumnHack, ContentValues values) {
        return mDatabase.insert(table, nullColumnHack, values);
    }

    @Override
    public long insertOrThrow(String table, String nullColumnHack, ContentValues values) {
        return mDatabase.insertOrThrow(table, nullColumnHack, values);
    }

    @Override
    public long replace(String table, String nullColumnHack, ContentValues initialValues) {
        return mDatabase.replace(table, nullColumnHack, initialValues);
    }

    @Override
    public long replaceOrThrow(String table, String nullColumnHack, ContentValues initialValues) {
        return mDatabase.replaceOrThrow(table, nullColumnHack, initialValues);
    }

    @Override
    public long insertWithOnConflict(String table, String nullColumnHack, ContentValues initialValues,
            int conflictAlgorithm) {
        return mDatabase.insertWithOnConflict(table, nullColumnHack, initialValues, conflictAlgorithm);
    }

    @Override
    public int delete(String table, String whereClause, String[] whereArgs) {
        return mDatabase.delete(table, whereClause, whereArgs);
    }

    @Override
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return mDatabase.update(table, values, whereClause, whereArgs);
    }

    @Override
    public int updateWithOnConflict(String table, ContentValues values, String whereClause, String[] whereArgs,
            int conflictAlgorithm) {
        return mDatabase.updateWithOnConflict(table, values, whereClause, whereArgs, conflictAlgorithm);
    }

    @Override
    public void execSQL(String sql) {
        mDatabase.execSQL(sql);
    }

    @Override
    public void execSQL(String sql, Object[] bindArgs) {
        mDatabase.execSQL(sql, bindArgs);
    }

    @Override
    public boolean isReadOnly() {
        return mDatabase.isReadOnly();
    }

    @Override
    public boolean isOpen() {
        return mDatabase.isOpen();
    }

    @Override
    public boolean needUpgrade(int newVersion) {
        return mDatabase.needUpgrade(newVersion);
    }

    @Override
    public String getPath() {
        return mDatabase.getPath();
    }

    @Override
    public void close() {
        mDatabase.close();
    }

}
