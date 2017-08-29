package com.rdm.base.network;

import android.os.Looper;

import com.rdm.base.Abandonable;
import com.rdm.base.BaseSession;
import com.rdm.base.BusyFeedback;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by lokierao on 2015/1/12.
 * //TODO pullXML ,pullBytes两个方法。
 */
public interface HttpPull extends Abandonable {

    public static class Factory{


        public static HttpPull create(BaseSession session, boolean runInBackground, boolean callbackInUIThread){

            Looper looper = null;
            if(callbackInUIThread){
                looper = Looper.getMainLooper();
            }

            if(runInBackground){
                return new DefaultHttpPull(session, looper,true);
            }else{
                return new DefaultHttpPull(session, looper,false);
            }
        }

        public static HttpPull create(BaseSession session, boolean runInBackground){
           return create(session,runInBackground,true);
        }

        public static HttpPull create(BaseSession session){
            return create(session,true);
        }
    }

    public static enum PullType{
        /**
         *拉取本地缓存获取和网络数据。
         * 如果本地有缓存数据，会先回调本地数据。
         * 如果网络有数据，也会回调网络数据。
         * 所以，有可能会产生两个回调。
         */
        CACHE_AND_NETWORK,
        /**
         * 只从本地缓存获取。
         */
        CACHE_ONLY,

        /**
         * 只从网络获取。
         */
        NETWORK_ONLY
    }

    public static enum ErrorType {

        /**
         * 连接超时
         */
        CONNECT_TIME_OUT,

        /**
         * 网络不可用。
         */
        NETWORK_NOT_AVALIABLE,

        /**
         * 取消执行
         */
        CANCELED,

        /**
         * 未知错误，一般因为有异常发生。
         */
        UNKOWN,

    }

    public static interface ResultCallback<T>{

        /**
         * HTTP拉取回调结果，会在UI线程上面处理。
         * @param isSuccess  从网络获取的结果返回true，为false是从本地缓存获取的结果。
         * @param errorType isSuccess为false时才会有值。
         * @param result 返回拉取结果
         * @return true  是否缓存数据
         */
        void onResult(boolean isSuccess, ErrorType errorType,ResultData<T> result) ;

        /**
         * 返回本地缓存数据，有数据时才会回调；
         * @param result
         */
        void onResultFromLocal(ResultData<T> result);
    }


    /**
     * 设置header头。如果要添加cookie，可以使用该方法。
     */
    void setHeader(String header,String content);

    void removeHeader(String header);

    /**
     * 添加Cookie内容
     * @param name
     * @param value
     * @throws UnsupportedEncodingException
     */
    void addCookie(String name, String value) throws UnsupportedEncodingException;

    void removeCookies();

    /**
     * 加载文本内容，是一个耗时操作，不推荐在UI线程里面使用，UI线程应该使用pullText()方法。
     * @param type
     * @param url
     * @param  saveCache 是否保存缓存，保存缓存之后，下次可以直接从缓存里面拉取，在断网情况下依旧可以有内容。
     * @return
     */
    String loadText(PullType type,String url,boolean saveCache) throws IOException;

    /**
     * 从http拉取文本。先从本地缓存拉取，再从服务器里拉取，并将拉取结果缓存到本地数据上面。
     * @param url  url链接地址。
     * @param callback  只要有结果，就回调拉取结果回；如果拉取失败，将会从errorCallback回调。
     * @return 忙碌状态反馈对象。
     */
    BusyFeedback pullText(String url,ResultCallback<String> callback);


    /**
     * 从http拉取文本。
     * @param type  拉取方式
     * @param url  url链接地址。
     * @param callback  只要有结果，就回调拉取结果回；如果拉取失败，将会从errorCallback回调。
     * @param  saveCache 是否保存缓存，保存缓存之后，下次可以直接从缓存里面拉取，在断网情况下依旧可以有内容。
     * @return 忙碌状态反馈对象。
     */
    BusyFeedback pullText(PullType type,String url,ResultCallback<String> callback,  boolean saveCache);


    /**
     * 从http拉取数据块。先从本地缓存拉取，再从服务器里拉取，并将拉取结果缓存到本地数据上面。
     * @param url  url链接地址。
     * @param callback  只要有结果，就回调拉取结果回；如果拉取失败，将会从errorCallback回调。
     * @return 忙碌状态反馈对象。
     */
    BusyFeedback pullBlob(String url,ResultCallback<byte[]> callback);

    /**
     * 从http拉取数据块。
     * @param type  拉取方式
     * @param url  url链接地址。
     * @param callback  只要有结果，就回调拉取结果回；如果拉取失败，将会从errorCallback回调。
     * @param  saveCache 是否保存缓存，保存缓存之后，下次可以直接从缓存里面拉取，不需要从网络拉取。
     * @return 忙碌状态反馈对象。
     */
    BusyFeedback pullBlob(PullType type,String url,ResultCallback<byte[]> callback,  boolean saveCache);


    /**
     * 从http拉取JsonOjbect。
     * @param url  url链接地址。
     * @param callback  只要有结果，就回调拉取结果回；如果拉取失败，将会从errorCallback回调。
     * @return 忙碌状态反馈对象。
     */
    BusyFeedback pullJSONObject(String url,ResultCallback<JSONObject> callback);


    /**
     * 从http拉取JsonOjbect。
     * @param type  拉取方式
     * @param url  url链接地址。
     * @param callback  只要有结果，就回调拉取结果回；如果拉取失败，将会从errorCallback回调。
     * @param  saveCache 是否保存缓存，保存缓存之后，下次可以直接从缓存里面拉取，在断网情况下依旧可以有内容。
     * @return 忙碌状态反馈对象。
     */
    BusyFeedback pullJSONObject(PullType type,String url,ResultCallback<JSONObject> callback,  boolean saveCache);


    /**
     * 从http拉取JSONArray。
     * @param url  url链接地址。
     * @param callback  只要有结果，就回调拉取结果回；如果拉取失败，将会从errorCallback回调。
     * @return 忙碌状态反馈对象。
     */
    BusyFeedback pullJSONArray(String url,ResultCallback<JSONArray> callback);

    /**
     * 从http拉取JSONArray。
     * @param type  拉取方式
     * @param url  url链接地址。
     * @param callback  只要有结果，就回调拉取结果回；如果拉取失败，将会从errorCallback回调。
     * @param  saveCache 是否保存缓存，保存缓存之后，下次可以直接从缓存里面拉取，在断网情况下依旧可以有内容。
     * @return 忙碌状态反馈对象。
     */
    BusyFeedback pullJSONArray(PullType type,String url,ResultCallback<JSONArray> callback,  boolean saveCache);


/*    *//**
     * 从http拉取文件。
     * @param type  拉取方式
     * @param url  url链接地址。
     * @param callback  只要有结果，就回调拉取结果回；如果拉取失败，将会从errorCallback回调。
     * @param  saveCache 是否保存缓存，保存缓存之后，下次可以直接从缓存里面拉取，在断网情况下依旧可以有内容。
     * @return 忙碌状态反馈对象。
     *//*
    BusyFeedback pullFile(PullType type, String url, ResultCallback<File> callback, boolean saveCache);*/


    /**
     * 拉取缓存文件。
     * @return
     */
    File loadCacheFile(String url);

}
