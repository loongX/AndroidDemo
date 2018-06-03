
package com.rdm.base.db.entity;

import android.text.TextUtils;
import com.rdm.base.db.EntityContext;
import com.rdm.base.db.util.TableUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("rawtypes")
public class TableEntity {

    private String tableName;

    private int version;

    private boolean dynamicClass;

    private ArrayList<IdEntity> idList;

    private Class<?> entityType;

    /**
     * key: columnName
     */
    public final ConcurrentHashMap<String, ColumnEntity> columnMap;

    /**
     * key: className
     */
    private static final ConcurrentHashMap<String, TableEntity> tableMap = new ConcurrentHashMap<String, TableEntity>();

    private TableEntity(Class<?> entityType, EntityContext entityContext) {
        this.tableName = TableUtils.getTableName(entityType, entityContext.getTableName());
        this.idList = TableUtils.getIdList(entityType);
        this.version = TableUtils.getVersion(entityType);
        this.dynamicClass = TableUtils.getDynamicClass(entityType);
        this.columnMap = TableUtils.getColumnMap(entityType, entityContext);
        this.entityType = entityType;
    }

    public static synchronized TableEntity get(Class<?> entityType, EntityContext entityContext) {
        String tableName = TableUtils.getTableName(entityType, entityContext.getTableName());
        String key = entityType.getCanonicalName() + "_" + tableName;
        TableEntity table = tableMap.get(key);
        if (table != null) {
            if (table.entityType != entityType) {
               table = null;
            }
        }
        if (table == null) {
            table = new TableEntity(entityType, entityContext);
            tableMap.put(key, table);
        }

        return table;
    }

    public static synchronized void remove(Class<?> entityType) {
        if (entityType != null) {
            tableMap.remove(entityType.getCanonicalName());
        }
    }

    public static synchronized void remove(String tableName) {
        if (tableMap.size() > 0) {
            String key = null;
            for (Map.Entry<String, TableEntity> entry : tableMap.entrySet()) {
                TableEntity table = entry.getValue();
                if (table != null && table.getTableName().equals(tableName)) {
                    key = entry.getKey();
                    break;
                }
            }
            if (!TextUtils.isEmpty(key)) {
                tableMap.remove(key);
            }
        }
    }

    public ColumnEntity getColumnEntity(String columnName) {
        return columnMap.get(columnName);
    }

    public String getTableName() {
        return tableName;
    }

    public ArrayList<IdEntity> getIdList() {
        return idList;
    }

    public int getVersion() {
        return version;
    }

    public boolean isDynamicClass() {
        return dynamicClass;
    }

}
