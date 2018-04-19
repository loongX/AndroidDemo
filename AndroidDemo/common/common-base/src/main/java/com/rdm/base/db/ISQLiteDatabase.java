
package com.rdm.base.db;


import android.content.ContentValues;
import android.database.Cursor;
import com.rdm.base.annotation.PluginApi;

@PluginApi(since = 8)
public interface ISQLiteDatabase {
    /**
     * Begins a transaction in EXCLUSIVE mode.
     * <p>
     * Transactions can be nested. When the outer transaction is ended all of the work done in that
     * transaction and all of the nested transactions will be committed or rolled back. The changes
     * will be rolled back if any transaction is ended without being marked as clean (by calling
     * setTransactionSuccessful). Otherwise they will be committed.
     * </p>
     * <p>
     * Here is the standard idiom for transactions:
     * 
     * <pre>
     *   db.beginTransaction();
     *   try {
     *     ...
     *     db.setTransactionSuccessful();
     *   } finally {
     *     db.endTransaction();
     *   }
     * </pre>
     */
    @PluginApi(since = 8)
    public void beginTransaction();

    /**
     * End a transaction. See beginTransaction for notes about how to use this and when transactions
     * are committed and rolled back.
     */
    @PluginApi(since = 8)
    public void endTransaction();

    /**
     * Marks the current transaction as successful. Do not do any more database work between calling
     * this and calling endTransaction. Do as little non-database work as possible in that situation
     * too. If any errors are encountered between this and endTransaction the transaction will still
     * be committed.
     * 
     * @throws IllegalStateException if the current thread is not in a transaction or the
     *             transaction is already marked as successful.
     */
    @PluginApi(since = 8)
    public void setTransactionSuccessful();

    /**
     * Returns true if the current thread has a transaction pending.
     * 
     * @return True if the current thread is in a transaction.
     */
    @PluginApi(since = 8)
    public boolean inTransaction();

    /**
     * Gets the database version.
     * 
     * @return the database version
     */
    @PluginApi(since = 8)
    public int getVersion();

    /**
     * Sets the database version.
     * 
     * @param version the new database version
     */
    @PluginApi(since = 8)
    public void setVersion(int version);

    /**
     * Query the given URL, returning a {@link android.database.Cursor} over the result set.
     *
     * @param distinct true if you want each row to be unique, false otherwise.
     * @param table The table name to compile the query against.
     * @param columns A list of which columns to return. Passing null will return all columns, which
     *            is discouraged to prevent reading data from storage that isn't going to be used.
     * @param selection A filter declaring which rows to return, formatted as an SQL WHERE clause
     *            (excluding the WHERE itself). Passing null will return all rows for the given
     *            table.
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values
     *            from selectionArgs, in order that they appear in the selection. The values will be
     *            bound as Strings.
     * @param groupBy A filter declaring how to group rows, formatted as an SQL GROUP BY clause
     *            (excluding the GROUP BY itself). Passing null will cause the rows to not be
     *            grouped.
     * @param having A filter declare which row groups to include in the cursor, if row grouping is
     *            being used, formatted as an SQL HAVING clause (excluding the HAVING itself).
     *            Passing null will cause all row groups to be included, and is required when row
     *            grouping is not being used.
     * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause (excluding the
     *            ORDER BY itself). Passing null will use the default sort order, which may be
     *            unordered.
     * @param limit Limits the number of rows returned by the query, formatted as LIMIT clause.
     *            Passing null denotes no LIMIT clause.
     * @return A {@link android.database.Cursor} object, which is positioned before the first entry. Note that
     *         {@link android.database.Cursor}s are not synchronized, see the documentation for more details.
     * @see android.database.Cursor
     */
    @PluginApi(since = 8)
    public Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs,
                        String groupBy, String having, String orderBy, String limit);

    /**
     * Query the given table, returning a {@link android.database.Cursor} over the result set.
     *
     * @param table The table name to compile the query against.
     * @param columns A list of which columns to return. Passing null will return all columns, which
     *            is discouraged to prevent reading data from storage that isn't going to be used.
     * @param selection A filter declaring which rows to return, formatted as an SQL WHERE clause
     *            (excluding the WHERE itself). Passing null will return all rows for the given
     *            table.
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values
     *            from selectionArgs, in order that they appear in the selection. The values will be
     *            bound as Strings.
     * @param groupBy A filter declaring how to group rows, formatted as an SQL GROUP BY clause
     *            (excluding the GROUP BY itself). Passing null will cause the rows to not be
     *            grouped.
     * @param having A filter declare which row groups to include in the cursor, if row grouping is
     *            being used, formatted as an SQL HAVING clause (excluding the HAVING itself).
     *            Passing null will cause all row groups to be included, and is required when row
     *            grouping is not being used.
     * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause (excluding the
     *            ORDER BY itself). Passing null will use the default sort order, which may be
     *            unordered.
     * @return A {@link android.database.Cursor} object, which is positioned before the first entry. Note that
     *         {@link android.database.Cursor}s are not synchronized, see the documentation for more details.
     * @see android.database.Cursor
     */
    @PluginApi(since = 8)
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy);

    /**
     * Query the given table, returning a {@link android.database.Cursor} over the result set.
     *
     * @param table The table name to compile the query against.
     * @param columns A list of which columns to return. Passing null will return all columns, which
     *            is discouraged to prevent reading data from storage that isn't going to be used.
     * @param selection A filter declaring which rows to return, formatted as an SQL WHERE clause
     *            (excluding the WHERE itself). Passing null will return all rows for the given
     *            table.
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values
     *            from selectionArgs, in order that they appear in the selection. The values will be
     *            bound as Strings.
     * @param groupBy A filter declaring how to group rows, formatted as an SQL GROUP BY clause
     *            (excluding the GROUP BY itself). Passing null will cause the rows to not be
     *            grouped.
     * @param having A filter declare which row groups to include in the cursor, if row grouping is
     *            being used, formatted as an SQL HAVING clause (excluding the HAVING itself).
     *            Passing null will cause all row groups to be included, and is required when row
     *            grouping is not being used.
     * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause (excluding the
     *            ORDER BY itself). Passing null will use the default sort order, which may be
     *            unordered.
     * @param limit Limits the number of rows returned by the query, formatted as LIMIT clause.
     *            Passing null denotes no LIMIT clause.
     * @return A {@link android.database.Cursor} object, which is positioned before the first entry. Note that
     *         {@link android.database.Cursor}s are not synchronized, see the documentation for more details.
     * @see android.database.Cursor
     */
    @PluginApi(since = 8)
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy, String limit);

    /**
     * Runs the provided SQL and returns a {@link android.database.Cursor} over the result set.
     *
     * @param sql the SQL query. The SQL string must not be ; terminated
     * @param selectionArgs You may include ?s in where clause in the query, which will be replaced
     *            by the values from selectionArgs. The values will be bound as Strings.
     * @return A {@link android.database.Cursor} object, which is positioned before the first entry. Note that
     *         {@link android.database.Cursor}s are not synchronized, see the documentation for more details.
     */
    @PluginApi(since = 8)
    public Cursor rawQuery(String sql, String[] selectionArgs);

    /**
     * Convenience method for inserting a row into the database.
     *
     * @param table the table to insert the row into
     * @param nullColumnHack optional; may be <code>null</code>. SQL doesn't allow inserting a
     *            completely empty row without naming at least one column name. If your provided
     *            <code>values</code> is empty, no column names are known and an empty row can't be
     *            inserted. If not set to null, the <code>nullColumnHack</code> parameter provides
     *            the name of nullable column name to explicitly insert a NULL into in the case
     *            where your <code>values</code> is empty.
     * @param values this map contains the initial column values for the row. The keys should be the
     *            column names and the values the column values
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    @PluginApi(since = 8)
    public long insert(String table, String nullColumnHack, ContentValues values);

    /**
     * Convenience method for inserting a row into the database.
     *
     * @param table the table to insert the row into
     * @param nullColumnHack optional; may be <code>null</code>. SQL doesn't allow inserting a
     *            completely empty row without naming at least one column name. If your provided
     *            <code>values</code> is empty, no column names are known and an empty row can't be
     *            inserted. If not set to null, the <code>nullColumnHack</code> parameter provides
     *            the name of nullable column name to explicitly insert a NULL into in the case
     *            where your <code>values</code> is empty.
     * @param values this map contains the initial column values for the row. The keys should be the
     *            column names and the values the column values
     * @throws android.database.SQLException
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    @PluginApi(since = 8)
    public long insertOrThrow(String table, String nullColumnHack, ContentValues values);

    /**
     * Convenience method for replacing a row in the database.
     *
     * @param table the table in which to replace the row
     * @param nullColumnHack optional; may be <code>null</code>. SQL doesn't allow inserting a
     *            completely empty row without naming at least one column name. If your provided
     *            <code>initialValues</code> is empty, no column names are known and an empty row
     *            can't be inserted. If not set to null, the <code>nullColumnHack</code> parameter
     *            provides the name of nullable column name to explicitly insert a NULL into in the
     *            case where your <code>initialValues</code> is empty.
     * @param initialValues this map contains the initial column values for the row.
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    @PluginApi(since = 8)
    public long replace(String table, String nullColumnHack, ContentValues initialValues);

    /**
     * Convenience method for replacing a row in the database.
     *
     * @param table the table in which to replace the row
     * @param nullColumnHack optional; may be <code>null</code>. SQL doesn't allow inserting a
     *            completely empty row without naming at least one column name. If your provided
     *            <code>initialValues</code> is empty, no column names are known and an empty row
     *            can't be inserted. If not set to null, the <code>nullColumnHack</code> parameter
     *            provides the name of nullable column name to explicitly insert a NULL into in the
     *            case where your <code>initialValues</code> is empty.
     * @param initialValues this map contains the initial column values for the row. The key
     * @throws android.database.SQLException
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    @PluginApi(since = 8)
    public long replaceOrThrow(String table, String nullColumnHack, ContentValues initialValues);

    /**
     * General method for inserting a row into the database.
     *
     * @param table the table to insert the row into
     * @param nullColumnHack optional; may be <code>null</code>. SQL doesn't allow inserting a
     *            completely empty row without naming at least one column name. If your provided
     *            <code>initialValues</code> is empty, no column names are known and an empty row
     *            can't be inserted. If not set to null, the <code>nullColumnHack</code> parameter
     *            provides the name of nullable column name to explicitly insert a NULL into in the
     *            case where your <code>initialValues</code> is empty.
     * @param initialValues this map contains the initial column values for the row. The keys should
     *            be the column names and the values the column values
     * @param conflictAlgorithm for insert conflict resolver
     */
    @PluginApi(since = 8)
    public long insertWithOnConflict(String table, String nullColumnHack, ContentValues initialValues,
                                     int conflictAlgorithm);

    /**
     * Convenience method for deleting rows in the database.
     *
     * @param table the table to delete from
     * @param whereClause the optional WHERE clause to apply when deleting. Passing null will delete
     *            all rows.
     * @return the number of rows affected if a whereClause is passed in, 0 otherwise. To remove all
     *         rows and get a count pass "1" as the whereClause.
     */
    @PluginApi(since = 8)
    public int delete(String table, String whereClause, String[] whereArgs);

    /**
     * Convenience method for updating rows in the database.
     *
     * @param table the table to update in
     * @param values a map from column names to new column values. null is a valid value that will
     *            be translated to NULL.
     * @param whereClause the optional WHERE clause to apply when updating. Passing null will update
     *            all rows.
     * @return the number of rows affected
     */
    @PluginApi(since = 8)
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs);

    /**
     * Convenience method for updating rows in the database.
     *
     * @param table the table to update in
     * @param values a map from column names to new column values. null is a valid value that will
     *            be translated to NULL.
     * @param whereClause the optional WHERE clause to apply when updating. Passing null will update
     *            all rows.
     * @param conflictAlgorithm for update conflict resolver
     * @return the number of rows affected
     */
    @PluginApi(since = 8)
    public int updateWithOnConflict(String table, ContentValues values, String whereClause, String[] whereArgs,
                                    int conflictAlgorithm);
    @PluginApi(since = 8)
    public void execSQL(String sql);

    @PluginApi(since = 8)
    public void execSQL(String sql, Object[] bindArgs);

    /**
     * Returns true if the database is opened as read only.
     * 
     * @return True if database is opened as read only.
     */
    @PluginApi(since = 8)
    public boolean isReadOnly();

    /**
     * Returns true if the database is currently open.
     * 
     * @return True if the database is currently open (has not been closed).
     */
    @PluginApi(since = 8)
    public boolean isOpen();

    /**
     * Returns true if the new version code is greater than the current database version.
     * 
     * @param newVersion The new version code.
     * @return True if the new version code is greater than the current database version.
     */
    @PluginApi(since = 8)
    public boolean needUpgrade(int newVersion);

    /**
     * Gets the path to the database file.
     * 
     * @return The path to the database file.
     */
    @PluginApi(since = 8)
    public String getPath();
    
    @PluginApi(since = 8)
    public void close();

}
