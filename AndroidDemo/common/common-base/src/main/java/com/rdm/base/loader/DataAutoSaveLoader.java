package com.rdm.base.loader;

import android.text.TextUtils;

import com.rdm.base.BaseSession;
import com.rdm.common.ILog;
import com.rdm.common.util.StringUtils;

/**

 *  含有自动保存的数据加载中心。（基本原理是：保存原始数据）
 *
 * Created by lokierao on 2015/5/19.
 */
public abstract class DataAutoSaveLoader<T> extends DataLoader<T> {

    protected BaseSession mSession;

    public DataAutoSaveLoader(BaseSession session) {
        mSession = session;
    }


    /**
     * 加载原始数据。
     */
    protected abstract byte[] loadOriginData(boolean loadMore);

    protected abstract byte[] loadOriginData(int pageIndex,int pageSize);


    /**
     * 解析原始数据。
     * @return
     */
    protected abstract T parseOriginData(byte[] serialData);

    protected  String getSerializeId(){
        return this.getClass().getName();
    }

    protected String toString(byte[] data){
        if(data == null) {
            return null;
        }

        if(data.length == 0){
            return "";
        }
        try {
            return new String(data,"utf8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将文本压缩序列化。
     * @param text
     * @return
     */
    protected byte[] toByteArrays(String text){
        if(text == null) {
            return null;
        }

        if(text.length() == 0){
            return new byte[0];
        }
        try {
            return text.getBytes("utf8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    protected String getSerializeId(Boolean isMore, Integer pageIndex, Integer pageSize){

        String sessioId = getSessionId();
        String serializeId = getSerializeId();
        if(sessioId == null || TextUtils.isEmpty(serializeId)){
            return null;
        }
        StringBuilder sb = new StringBuilder(sessioId);
        sb.append("-" + sessioId);
        if(isMore!=null){
            sb.append("-" + isMore);
        }

        if(pageIndex!= null){
            sb.append("-" + pageIndex);

        }

        if(pageSize!= null){
            sb.append("-" + pageSize);
        }

        return sb.toString();
    }


    private T loadFromStore(String serailizeId) throws Exception{
        if(mSession== null){
            return null;
        }
        byte[] localData = null;
        if(!StringUtils.isEmpty(serailizeId)) {
            localData = (byte[]) mSession.getPool().get(serailizeId);
        }
        if(localData!= null) {
            return parseOriginData(localData);
        }
        return null;
    }

    private void saveToStore(String serailizeId, byte[] data) throws Exception{
        if(mSession== null||StringUtils.isEmpty(serailizeId)){
            return ;
        }
        if(data == null){
            mSession.getPool().remove(serailizeId);
        }else {
            mSession.getPool().put(serailizeId, data);
        }
       // DataLoadCache kv = DataLoadCache.loadFromDB(mSession,serailizeId,new DataLoadCache());
      /*  DataLoadCache kv = new DataLoadCache();
        kv.setByteArray(data);
        kv.setKey(serailizeId);
        DataLoadCache.saveToDB(mSession, kv);*/
    }


    @Override
    final protected T loadFromStore() throws LoaderException {

        String serializeId = getSerializeId(null,null,null);
        try {
            if (serializeId != null) {
                return loadFromStore(serializeId);
            }
        }catch (Exception ex){
            ILog.w(this.getClass(),ex.getMessage(),ex);
        }

        return null;
    }

    @Override
    final protected T loadMoreFromStore() throws LoaderException {
        //更多数据不允许缓存。

        return null;
    }

    @Override
    final protected T loadPageFromStore(int pageIndex, int pageSize) throws LoaderException {
        String serializeId = getSerializeId(false,pageIndex,pageSize);
        try {
            if (serializeId != null) {
                return loadFromStore(serializeId);
            }
        }catch (Exception ex){
            ILog.w(this.getClass(),ex.getMessage(),ex);
        }

        return null;
    }

    @Override
    final protected void onLoadFromNet(ResultNotifier notifier) throws LoaderException {
        byte[] data =  loadOriginData(false);
        if(data == null){
            throw new NullPointerException("loadOriginData null");
        }
        T result = parseOriginData(data);
        if (result != null) {
            String serializeId = getSerializeId(null,null,null);
            if(serializeId!= null){
                try{
                    saveToStore(serializeId,data);
                }catch (Exception ex){
                }
            }
            notifier.notifyResult(result);
        }else{
            throw new NullPointerException("parseOriginData null");
        }
    }

    @Override
    final protected void onLoadMoreFromNet(ResultNotifier notifier) throws LoaderException {
        byte[] data =  loadOriginData(true);
        if(data == null){
            throw new NullPointerException("loadOriginData null");
        }
        T result = parseOriginData(data);
        if (result != null) {
            String serializeId = getSerializeId(true,null,null);
            if(serializeId!= null){
                try{
                    saveToStore(serializeId,data);
                }catch (Exception ex){
                }
            }
            notifier.notifyResult(result);
        }else{
            throw new NullPointerException("parseOriginData null");
        }
    }

    @Override
    final protected void onLoadPageFromNet(ResultNotifier notifier, int pageIndex, int pageSize) throws LoaderException {
        byte[] data =  loadOriginData(pageIndex,pageSize);
        if(data == null){
            throw new NullPointerException("loadOriginData null");
        }
        T result = parseOriginData(data);
        if (result != null) {
            String serializeId = getSerializeId(false, pageIndex, pageSize);
            if(serializeId!= null){
                try{
                    saveToStore(serializeId,data);
                }catch (Exception ex){
                }
            }
            notifier.notifyResult(result);
        }else{
            throw new NullPointerException("parseOriginData null");
        }
    }

    @Override
    final protected void saveToStore(Data<T> data) throws LoaderException {
        //ignore

    }


}

