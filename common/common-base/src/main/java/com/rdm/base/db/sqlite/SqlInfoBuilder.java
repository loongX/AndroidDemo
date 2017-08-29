
package com.rdm.base.db.sqlite;

import com.rdm.base.db.EntityContext;
import com.rdm.base.db.entity.ColumnEntity;
import com.rdm.base.db.entity.IdEntity;
import com.rdm.base.db.entity.TableEntity;
import com.rdm.base.db.exception.DBException;
import com.rdm.base.db.util.KeyValue;

import java.util.*;

/**
 * Build "insert", "replace",，"update", "delete" and "create" sql.
 */
@SuppressWarnings("rawtypes")
public class SqlInfoBuilder {

    private SqlInfoBuilder() {
    }

    // ------------------ insert sql ------------------//

    public static SqlInfo buildInsertSqlInfo(Class<?> entityType, Object entity, EntityContext entityContext) throws DBException {

        List<KeyValue> keyValueList = collectInsertKeyValues(entityType,entity, entityContext);
        if (keyValueList.size() == 0)
            return null;

        SqlInfo result = new SqlInfo();
        StringBuffer sqlBuffer = new StringBuffer();

        sqlBuffer.append("INSERT INTO ");
        sqlBuffer.append(TableEntity.get(entityType, entityContext).getTableName());
        sqlBuffer.append(" (");
        for (KeyValue kv : keyValueList) {
            sqlBuffer.append(kv.getKey()).append(",");
            result.addBindArgWithoutConverter(kv.getValue());
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(") VALUES (");

        int length = keyValueList.size();
        for (int i = 0; i < length; i++) {
            sqlBuffer.append("?,");
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(")");

        result.setSql(sqlBuffer.toString());

        return result;
    }

    // ------------------ replace sql ---------------------//

    public static SqlInfo buildReplaceSqlInfo(Class<?> entityType, Object entity, EntityContext entityContext) throws DBException {

        List<KeyValue> keyValueList = collectReplaceKeyValues(entityType,entity, entityContext);
        if (keyValueList.size() == 0)
            return null;

        SqlInfo result = new SqlInfo();
        StringBuffer sqlBuffer = new StringBuffer();

        sqlBuffer.append("REPLACE INTO ");
        sqlBuffer.append(TableEntity.get(entityType, entityContext).getTableName());
        sqlBuffer.append(" (");
        for (KeyValue kv : keyValueList) {
            sqlBuffer.append(kv.getKey()).append(",");
            result.addBindArgWithoutConverter(kv.getValue());
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(") VALUES (");

        int length = keyValueList.size();
        for (int i = 0; i < length; i++) {
            sqlBuffer.append("?,");
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(")");

        result.setSql(sqlBuffer.toString());

        return result;
    }

    // ------------------------- delete sql ----------------------------//

    private static String buildDeleteSqlByTableName(String tableName) {
        return "DELETE FROM " + tableName;
    }

    public static SqlInfo buildDeleteByObjectSqlInfo(Class<?> entityType,  Object entity, EntityContext entityContext) throws DBException {
        SqlInfo result = new SqlInfo();

        TableEntity table = TableEntity.get(entityType, entityContext);
        StringBuilder sb = new StringBuilder(buildDeleteSqlByTableName(table.getTableName()));

        ArrayList<IdEntity> idEntities = table.getIdList();
        if(idEntities == null || idEntities.size() == 0) {
            throw new DBException("this entity[" + entity.getClass() + "] can't find id field");
        }
        WhereBuilder whereBuilder = null;
        for(IdEntity id : idEntities) {
            Object idValue = id.getColumnValue(entity);
            if (idValue == null) {
                throw new DBException("this entity[" + entity.getClass() + "]'s id value is null");
            }
            if (whereBuilder == null) {
                whereBuilder = WhereBuilder.create(id.getColumnName(), "=", idValue);
            } else {
                whereBuilder.and(id.getColumnName(),"=",idValue);
            }
        }
        if (whereBuilder != null) {
            sb.append(" WHERE ");
            sb.append(whereBuilder);
        }


        result.setSql(sb.toString());

        return result;
    }

    public static SqlInfo buildDeleteSqlInfo(Class<?> entityType, Object idValue, EntityContext entityContext)
            throws DBException {
        SqlInfo result = new SqlInfo();

        TableEntity table = TableEntity.get(entityType, entityContext);

        ArrayList<IdEntity> idEntities = table.getIdList();
        if (idEntities == null || idEntities.isEmpty() || idEntities.size() > 1) {
            throw new DBException("BuildDelteSqlInfo failed(there's more than one idEntity)");
        }

        IdEntity id = idEntities.get(0);

        StringBuilder sb = new StringBuilder(buildDeleteSqlByTableName(table.getTableName()));
        if (null != idValue) {
            sb.append(" WHERE ").append(WhereBuilder.create(id.getColumnName(), "=", idValue));
        }
        result.setSql(sb.toString());

        return result;
    }

    public static SqlInfo buildDeleteSqlInfo(Class<?> entityType, WhereBuilder whereBuilder, EntityContext entityContext)
            throws DBException {
        TableEntity table = TableEntity.get(entityType, entityContext);
        StringBuilder sb = new StringBuilder(buildDeleteSqlByTableName(table.getTableName()));

        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            sb.append(" WHERE ").append(whereBuilder.toString());
        }

        return new SqlInfo(sb.toString());
    }

    // ----------------------------- update sql --------------------------------//

    public static SqlInfo buildUpdateSqlInfo(EntityContext entityContext,Class<?> entityType,  Object entity, String... updateColumnNames)
            throws DBException {

        List<KeyValue> keyValueList = collectUpdateKeyValues(entityType,entity, entityContext);
        if (keyValueList.size() == 0)
            return null;

        HashSet<String> updateColumnNameSet = null;
        if (updateColumnNames != null && updateColumnNames.length > 0) {
            updateColumnNameSet = new HashSet<String>(updateColumnNames.length);
            Collections.addAll(updateColumnNameSet, updateColumnNames);
        }

        TableEntity table = TableEntity.get(entityType, entityContext);

        SqlInfo result = new SqlInfo();
        StringBuffer sqlBuffer = new StringBuffer("UPDATE ");
        sqlBuffer.append(table.getTableName());
        sqlBuffer.append(" SET ");
        for (KeyValue kv : keyValueList) {
            if (updateColumnNameSet == null || updateColumnNameSet.contains(kv.getKey())) {
                sqlBuffer.append(kv.getKey()).append("=?,");
                result.addBindArgWithoutConverter(kv.getValue());
            }
        }

        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);

        ArrayList<IdEntity> idEntities = table.getIdList();
        if(idEntities == null || idEntities.size() == 0) {
            throw new DBException("this entity[" + entity.getClass() + "] can't find id field");
        }

        WhereBuilder whereBuilder = null;
        for(IdEntity id : idEntities) {
            Object idValue = id.getColumnValue(entity);
            if (idValue == null) {
                throw new DBException("this entity[" + entity.getClass() + "]'s id value is null");
            }
            if (whereBuilder == null) {
                whereBuilder = WhereBuilder.create(id.getColumnName(), "=", idValue);
            } else {
                whereBuilder.and(id.getColumnName(),"=",idValue);
            }
        }
        if (whereBuilder != null) {
            sqlBuffer.append(" WHERE ");
            sqlBuffer.append(whereBuilder);
        }

        result.setSql(sqlBuffer.toString());
        return result;
    }

    public static SqlInfo buildUpdateSqlInfo(EntityContext entityContext,Class<?> entityType, Object entity, WhereBuilder whereBuilder,
            String... updateColumnNames) throws DBException {

        List<KeyValue> keyValueList = collectUpdateKeyValues(entityType,entity, entityContext);
        if (keyValueList.size() == 0)
            return null;

        HashSet<String> updateColumnNameSet = null;
        if (updateColumnNames != null && updateColumnNames.length > 0) {
            updateColumnNameSet = new HashSet<String>(updateColumnNames.length);
            Collections.addAll(updateColumnNameSet, updateColumnNames);
        }

        TableEntity table = TableEntity.get(entityType, entityContext);

        SqlInfo result = new SqlInfo();
        StringBuffer sqlBuffer = new StringBuffer("UPDATE ");
        sqlBuffer.append(table.getTableName());
        sqlBuffer.append(" SET ");
        for (KeyValue kv : keyValueList) {
            if (updateColumnNameSet == null || updateColumnNameSet.contains(kv.getKey())) {
                sqlBuffer.append(kv.getKey()).append("=?,");
                result.addBindArgWithoutConverter(kv.getValue());
            }
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            sqlBuffer.append(" WHERE ").append(whereBuilder.toString());
        }

        result.setSql(sqlBuffer.toString());
        return result;
    }

    // --------------------------- others ---------------------------//

    public static SqlInfo buildCreateTableSqlInfo(Class<?> entityType, EntityContext entityContext) throws DBException {
        TableEntity table = TableEntity.get(entityType, entityContext);

        ArrayList<IdEntity> idEntities = table.getIdList();
        if (idEntities == null || idEntities.isEmpty()) {
            return null;
        }

        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("CREATE TABLE IF NOT EXISTS ");
        sqlBuffer.append(entityContext.getTableName());
        sqlBuffer.append(" ( ");


        boolean isMultiId = (idEntities.size() > 1);

        for(IdEntity id : idEntities) {
            if (isMultiId && id.isAutoIncrement()) {//复合组件不能自增长
                throw new DBException("Not support auto increment column when declared composite primary key!");
            }

            if (!isMultiId && id.isAutoIncrement()) {
                sqlBuffer.append("\"").append(id.getColumnName()).append("\"  ")
                        .append("INTEGER PRIMARY KEY AUTOINCREMENT,");
            } else {
                sqlBuffer.append("\"").append(id.getColumnName()).append("\"  ").append(id.getColumnDbType());
                if (!isMultiId) {
                    sqlBuffer.append(" PRIMARY KEY");
                }
                sqlBuffer.append(",");
            }
        }

        Collection<ColumnEntity> columns = table.columnMap.values();
        for (ColumnEntity column : columns) {
            sqlBuffer.append("\"").append(column.getColumnName()).append("\"  ");
            sqlBuffer.append(column.getColumnDbType());
            if (column.isUnique()) {
                sqlBuffer.append(" UNIQUE");
            }
            if (!column.isNullable()) {
                sqlBuffer.append(" NOT NULL");
            }
            sqlBuffer.append(",");
        }

        if (table.isDynamicClass()) {
            sqlBuffer.append("\"").append(ColumnEntity.RESERVED_COLUMN_DYNAMIC_CLASS).append("\"  ");
            sqlBuffer.append(" TEXT,");
        }

        if (isMultiId) {
            sqlBuffer.append(" PRIMARY KEY(");
            for (IdEntity id : idEntities) {
                sqlBuffer.append(id.getColumnName()).append(",");
            }
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        if (isMultiId) {
            //PRIMARY KEY 的右括号
            sqlBuffer.append(" )");
        }
        //CREATE TABLE的右括号
        sqlBuffer.append(" )");
        return new SqlInfo(sqlBuffer.toString());
    }

    @SuppressWarnings("unchecked")
    private static KeyValue column2KeyValue(Object entity, ColumnEntity column) {
        KeyValue kv = null;
        String key = column.getColumnName();
        Object value = column.getColumnValue(entity);
        if (key != null) {
            kv = new KeyValue(key, value);
        }
        return kv;
    }

    public static List<KeyValue> collectReplaceKeyValues(Class<?> entityType, Object entity, EntityContext entityContext) {
        List<KeyValue> keyValueList = new ArrayList<KeyValue>();

        //不能用entity.getClass，因为有dynamicClass的需求，entity.getClass取的可能是子类型
        TableEntity table = TableEntity.get(entityType, entityContext);
        ArrayList<IdEntity> idEntities = table.getIdList();
        if (idEntities == null) {
            return null;
        }
        for(IdEntity id : idEntities) {
            if (!id.isAutoIncrement()) {
                Object idValue;
                if (id.isUUIDGenerationType()) {
                    idValue = UUID.randomUUID().toString();
                } else {
                    idValue = id.getColumnValue(entity);
                }
                KeyValue kv = new KeyValue(id.getColumnName(), idValue);
                keyValueList.add(kv);
            } else { //如果是自增长的，判断主键是否有值，有值则更新
                long idValue = 0;
                try {
                    idValue = ((Number) id.getColumnValue(entity)).longValue();
                } catch (Exception e) {
                }
                if (idValue > 0) {//自增长如果没值会自动生成，不需要填到sql语句中
                    KeyValue kv = new KeyValue(id.getColumnName(), idValue);
                    keyValueList.add(kv);
                }
            }
        }

        if (table.isDynamicClass()) {
            KeyValue kv = new KeyValue(ColumnEntity.RESERVED_COLUMN_DYNAMIC_CLASS, entity.getClass().getName());
            keyValueList.add(kv);
        }

        Collection<ColumnEntity> columns = table.columnMap.values();
        for (ColumnEntity column : columns) {
            KeyValue kv = column2KeyValue(entity, column);
            if (kv != null) {
                keyValueList.add(kv);
            }
        }

        return keyValueList;
    }

    public static List<KeyValue> collectInsertKeyValues(Class<?> entityType,Object entity, EntityContext entityContext) {
        List<KeyValue> keyValueList = new ArrayList<KeyValue>();

        TableEntity table = TableEntity.get(entityType, entityContext);
        ArrayList<IdEntity> idEntities = table.getIdList();
        if (idEntities == null) {
            return null;
        }
        for(IdEntity id : idEntities) {
            if (!id.isAutoIncrement()) {
                Object idValue;
                if (id.isUUIDGenerationType()) {
                    idValue = UUID.randomUUID().toString();
                } else {
                    idValue = id.getColumnValue(entity);
                }
                KeyValue kv = new KeyValue(id.getColumnName(), idValue);
                keyValueList.add(kv);
            }
        }
        if (table.isDynamicClass()) {
            KeyValue kv = new KeyValue(ColumnEntity.RESERVED_COLUMN_DYNAMIC_CLASS, entity.getClass().getName());
            keyValueList.add(kv);
        }

        Collection<ColumnEntity> columns = table.columnMap.values();
        for (ColumnEntity column : columns) {
            KeyValue kv = column2KeyValue(entity, column);
            if (kv != null) {
                keyValueList.add(kv);
            }
        }

        return keyValueList;
    }

    public static List<KeyValue> collectUpdateKeyValues(Class<?> entityType,Object entity, EntityContext entityContext) {

        List<KeyValue> keyValueList = new ArrayList<KeyValue>();

        TableEntity table = TableEntity.get(entityType, entityContext);

        Collection<ColumnEntity> columns = table.columnMap.values();
        for (ColumnEntity column : columns) {
            KeyValue kv = column2KeyValue(entity, column);
            if (kv != null) {
                keyValueList.add(kv);
            }
        }

        return keyValueList;
    }
}
