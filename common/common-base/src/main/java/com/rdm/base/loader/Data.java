package com.rdm.base.loader;

import java.util.Date;

/**
 * Created by lokierao on 2015/7/17.
 */
public interface Data<RESULT> {

    /**
     * 返回结果
     * @return 可能为null的结果
     */
    RESULT get();

    /**
     * 返回上次更新时间。
     * @return
     */
    Date getLastModifiedTime();

    /**
     * 如果不是页面加载的数据，返回-1；
     * @return
     */
    int getPageIndex();

    /**
     * 如果不是页面加载的数据，返回-1；
     * @return
     */
    int getPageSize();


    /**
     * 是否从本地加载（从硬盘和缓存加载）
     * @return
     */
    boolean isFromLocal();

    /**
     * 是否为临时结果,常用于一边加载一边显示；
     * @return
     */
    boolean isTemporary();

    /**
     * 是否是加载更多
     * @return
     */
    boolean isLoadMore();


    public static class DataImpl<T> implements Data<T>{

        private T mData;
        private Date lastModifiedTime = null;
        private int pageIndex = 0;
        private int pageSize = -1;
        private boolean isFormLocal = false;
        private boolean isTemporary = false;
        private boolean isLoadMore = false;

        public DataImpl(){

        }

        public DataImpl(DataImpl<T> source){
            mData = source.mData;
            lastModifiedTime = source.lastModifiedTime;
            pageIndex = source.pageIndex;
            pageSize = source.pageSize;
            isFormLocal = source.isFormLocal;
            isTemporary = source.isTemporary;
            isLoadMore = source.isLoadMore;
        }

        @Override
        public T get() {
            return mData;
        }

        public void set(T data){
            mData = data;
        }

        @Override
        public Date getLastModifiedTime() {
            return lastModifiedTime;
        }


        @Override
        public int getPageIndex() {
            return pageIndex;
        }

        @Override
        public int getPageSize() {
            return pageSize;
        }

        @Override
        public boolean isFromLocal() {
            return isFormLocal;
        }

        @Override
        public boolean isTemporary() {
            return isTemporary;
        }

        @Override
        public boolean isLoadMore() {
            return isLoadMore;
        }

        public void setLastModifiedTime(Date lastModifiedTime) {
            this.lastModifiedTime = lastModifiedTime;
        }

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }


        public void setIsFromLocal(boolean isFormLocal) {
            this.isFormLocal = isFormLocal;
        }

        public void setIsTemporary(boolean isTemporary) {
            this.isTemporary = isTemporary;
        }

        public void setIsLoadMore(boolean isLoadMore) {
            this.isLoadMore = isLoadMore;
        }
    }
}
