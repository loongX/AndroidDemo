package com.rdm.base.network;

import java.util.Date;
import java.util.Map;

/**
 *
 * 缓存数据。
 * Created by lokierao on 2015/5/19.
 */
public interface ResultData<DATA> {

    /**
     * 返回数据。
     * @return
     */
    DATA getBody();

    Map<String,String> getHeader();

    int getStatusCode();


    /**
     * 返回上次更新时间。
     * @return
     */
    Date getLastModified();

    /**
     *返回其他相关数据。
     * @param name
     * @return
     */
    Object getExtra(String name);

    /**
     *
     *  是否缓存内容，默认为ture
     * @param flag
     */
    void enableSaveToCache(boolean flag);


    public static class DefaultResultData<DATA> implements ResultData<DATA> {

        private DATA data;
        private Date lastModifiedDay = null;
        private Map<String,Object> extra;
        private boolean enableSaveToCache = true;
        /*package*/ Map<String,String> headers = null;
        /*package*/ int statusCode = 0;

        //public Object argus;
        public Runnable opration;


        public void set(DATA data) {
            this.data = data;
        }

        @Override
        public Date getLastModified() {
            return lastModifiedDay;
        }

        public void setLastModified(Date lastModifiedDay) {
            this.lastModifiedDay = lastModifiedDay;
        }

        public Map<String, Object> getExtraMap() {
            return extra;
        }

        public void setExtraMap(Map<String, Object> extra) {
            this.extra = extra;
        }

        @Override
        public DATA getBody() {
            return data;
        }

        @Override
        public Map<String, String> getHeader() {
            return headers;
        }


        @Override
        public int getStatusCode() {
            return statusCode;
        }




        @Override
        public Object getExtra(String name) {
            if(extra==null){
                return null;
            }
            return extra.get(name);
        }

        @Override
        public void enableSaveToCache(boolean flag) {
            enableSaveToCache = flag;
        }

        public boolean enableSaveToCache(){
            return enableSaveToCache;
        }
    }
}
