package com.rdm.base.loader;

import com.rdm.base.ThreadManager;


/**
 *
 * 数据实体对象可能是有多个DataLoader的数据源组合而来的。
 * //TODO,处理Local和Romote的加载方式。
 * Created by lokierao on 2015/11/2.
 */
public abstract class DataCompositLoader<T> extends DataLoader<T> {

    private DataLoader[] getOriginLoader = null;

    abstract protected DataLoader[] createDataLoaders();

    abstract protected T doComposit(Object... data);


    private DataLoader[] getDataLoaderList(){
        if(getOriginLoader == null) {
            getOriginLoader = createDataLoaders();
        }
        return getOriginLoader;
    }

    @Override
    final protected T loadFromStore() throws LoaderException {
        //Object ob = getOriginLoader().loadFromStore();
        DataLoader[] loaders = getDataLoaderList();

        Object[] data = new Object[loaders.length];

        for(int i = 0; i < loaders.length;i++) {
            Object obj = loaders[i].loadFromStore();
            data[i] = obj;
        }

        return doComposit(data);
    }

    @Override
    final protected T loadMoreFromStore() throws LoaderException {
        DataLoader[] loaders = getDataLoaderList();

        Object[] data = new Object[loaders.length];

        for(int i = 0; i < loaders.length;i++) {
            Object obj = loaders[i].loadMoreFromStore();
            data[i] = obj;
        }

        return doComposit(data);
    }

    @Override
    final protected T loadPageFromStore(int pageIndex, int pageSize) throws LoaderException {
        DataLoader[] loaders = getDataLoaderList();

        Object[] data = new Object[loaders.length];

        for(int i = 0; i < loaders.length;i++) {
            Object obj = loaders[i].loadPageFromStore(pageIndex, pageSize);
            data[i] = obj;
        }

        return doComposit(data);
    }

    @Override
    final  protected void onLoadFromNet(final ResultNotifier<T> notifier) throws LoaderException {

        DataLoader[] loaders = getDataLoaderList();
        Object[] data = new Object[loaders.length];

        NotifyHandler handler = new NotifyHandler(data,notifier);
        for(int i = 0; i < loaders.length;i++) {
            final DataLoader loader = loaders[i];
            final MyResultNotifier myNotify = new MyResultNotifier(i,handler);

            if( i == loaders.length - 1) {
                //最后一个使用当前线程
                loader.onLoadFromNet(myNotify);
            }else {
                ThreadManager.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            loader.onLoadFromNet(myNotify);
                        } catch (LoaderException e) {
                            notifier.notifyError(e.getMessage(), e);
                        }
                    }
                });
            }
        }

    }

    @Override
    final  protected void onLoadMoreFromNet(ResultNotifier<T> notifier) throws LoaderException {

    }

    @Override
    final  protected void onLoadPageFromNet(ResultNotifier<T> notifier, int pageIndex, int pageSize) throws LoaderException {

    }

    @Override
    final protected void saveToStore(Data<T> data) throws LoaderException {
        //igonre
    }



    private class NotifyHandler {
        private  ResultNotifier<T> originNotifier;
        private Object[] dataList = null;
        private final Boolean[] hasSetValue;


        NotifyHandler(Object[] data,ResultNotifier<T> notifier){
            originNotifier = notifier;
            dataList = data;
            hasSetValue = new Boolean[dataList.length];
        }

        synchronized  void notifyResult(int index, Object data){

            if(originNotifier == null) {
                return;
            }

            Boolean setValue = hasSetValue[index];
            if(setValue!= null && setValue.booleanValue()){
                return;
            }

            dataList[index] = data;
            hasSetValue[index] = true;

            boolean finiished = true;
            for(Boolean b : hasSetValue){

                if(b == null || !b.booleanValue()){
                    finiished = false;
                    break;
                }
            }

            if(finiished){
                T result =  doComposit(dataList);
                originNotifier.notifyResult(result);
                originNotifier = null;
            }


        }

        synchronized  void notifyError(int index, String message, Exception except){
            if(originNotifier == null) {
                return;
            }
            originNotifier.notifyError(message, except);
            originNotifier = null;

        }


        synchronized  void notifyTimeout(int index){
            if(originNotifier == null) {
                return;
            }

            originNotifier.notifyTimeout();
            originNotifier = null;
        }
    }

    private class MyResultNotifier implements ResultNotifier<Object>{

        //private final ResultNotifier<T> originNotifier;
       // private DataLoader orginLoader;
        private int mIndex;
        private boolean hasFinished = false;
        private NotifyHandler mHandler = null;

        MyResultNotifier(int index,NotifyHandler hander) {
          //  originNotifier = notifier;
          //  this.orginLoader = loader;
            this.mIndex = index;
            mHandler = hander;
        }

        @Override
        public void notifyTemporaryReulst(Object data) {
            //igonre
        }

        @Override
        public  void notifyResult(Object data) {
            //originNotifier.notifyResult(loadData(data));
            mHandler.notifyResult(mIndex,data);
        }

        @Override
        public  void notifyError(String message, Exception except) {

            mHandler.notifyError(mIndex, message,except);
        }

        @Override
        public synchronized void notifyTimeout() {
            mHandler.notifyTimeout(mIndex);
        }

        @Override
        public void notifyProgress(float progress) {
            //originNotifier.notifyProgress(progress);
        }

        @Override
        public boolean isCancel() {
            return false;
        }
    }
}
