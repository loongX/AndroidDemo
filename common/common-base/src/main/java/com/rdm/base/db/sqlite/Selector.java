
package com.rdm.base.db.sqlite;

import com.rdm.base.annotation.PluginApi;
import com.rdm.base.db.EntityContext;
import com.rdm.base.db.entity.TableEntity;

import java.util.ArrayList;
import java.util.List;

@PluginApi(since = 4)
public class Selector {

    protected WhereBuilder whereBuilder;
    protected List<OrderBy> orderByList;
    protected int limit = 0;
    protected int offset = 0;

    private Selector() {
    }

    @PluginApi(since = 4)
    public static Selector create() {
        return new Selector();
    }

    @PluginApi(since = 4)
    public Selector where(WhereBuilder whereBuilder) {
        this.whereBuilder = whereBuilder;
        return this;
    }
    @PluginApi(since = 4)
    public Selector where(String columnName, String op, Object value) {
        this.whereBuilder = WhereBuilder.create(columnName, op, value);
        return this;
    }
    @PluginApi(since = 4)
    public Selector and(String columnName, String op, Object value) {
        this.whereBuilder.and(columnName, op, value);
        return this;
    }
    @PluginApi(since = 4)
    public Selector and(WhereBuilder where) {
        this.whereBuilder.expr("AND (" + where.toString() + ")");
        return this;
    }
    @PluginApi(since = 4)
    public Selector or(String columnName, String op, Object value) {
        this.whereBuilder.or(columnName, op, value);
        return this;
    }
    @PluginApi(since = 4)
    public Selector or(WhereBuilder where) {
        this.whereBuilder.expr("OR (" + where.toString() + ")");
        return this;
    }
    @PluginApi(since = 4)
    public Selector expr(String expr) {
        this.whereBuilder.expr(expr);
        return this;
    }
    @PluginApi(since = 4)
    public Selector expr(String columnName, String op, Object value) {
        this.whereBuilder.expr(columnName, op, value);
        return this;
    }
    @PluginApi(since = 4)
    public Selector orderBy(String columnName) {
        if (orderByList == null) {
            orderByList = new ArrayList<OrderBy>(2);
        }
        orderByList.add(new OrderBy(columnName));
        return this;
    }
    @PluginApi(since = 4)
    public Selector orderBy(String columnName, boolean desc) {
        if (orderByList == null) {
            orderByList = new ArrayList<OrderBy>(2);
        }
        orderByList.add(new OrderBy(columnName, desc));
        return this;
    }

    public boolean hasOrder() {
        return orderByList != null && orderByList.size() > 0;
    }

    @PluginApi(since = 4)
    public Selector limit(int limit) {
        this.limit = limit;
        return this;
    }
    @PluginApi(since = 4)
    public Selector offset(int offset) {
        this.offset = offset;
        return this;
    }

    public String buildSql(Class<?> entityType,EntityContext entityContext) {
        String tableName = TableEntity.get(entityType,entityContext).getTableName();
        StringBuilder result = new StringBuilder();
        result.append("SELECT ");
        result.append("*");
        result.append(" FROM ").append(tableName);
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            result.append(" WHERE ").append(whereBuilder.toString());
        }
        if (orderByList != null) {
            for (int i = 0; i < orderByList.size(); i++) {
                if(i>0){
                    result.append(" ,").append(orderByList.get(i).toString());
                }else{
                    result.append(" ORDER BY ").append(orderByList.get(i).toString());
                }

            }
        }
        if (limit > 0) {
            result.append(" LIMIT ").append(limit);
            result.append(" OFFSET ").append(offset);
        }
        return result.toString();
    }

    protected class OrderBy {
        private String columnName;
        private boolean desc;

        public OrderBy(String columnName) {
            this.columnName = columnName;
        }

        public OrderBy(String columnName, boolean desc) {
            this.columnName = columnName;
            this.desc = desc;
        }

        @Override
        public String toString() {
            return columnName + (desc ? " DESC" : " ASC");
        }
    }
}
