package com.rdm.base.network;

import android.os.Handler;

import com.rdm.base.BaseSession;
import com.rdm.base.BusyFeedback;
import com.rdm.common.ILog;
import com.rdm.common.util.NetWorkUtils;
import com.rdm.common.util.StringUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by lokierao on 2015/1/12.
 */
public abstract class HttpPullThread<T> implements Runnable,BusyFeedback {

    private static final int DEFUALT_TIMEOUT = 7000;
    private static final String TAG = "HttpPullThread";

    private final String mUrl;
    private final boolean mSaveCache;
    private HttpPull.PullType mPullTye;

    private HttpClient mHttp;
    private Handler mHandler;
    private Map<String,String> mHeaderMap;
    private HttpPull.ResultCallback mResultCallback;

    private boolean isFinished = false;
    private  boolean isCancel = false;
    private BaseSession mSession = null;
    private HttpRequestBase mCurrentHttpGet ;
    private boolean mNeedEncodText = true;
    private boolean callCalback = true;

    /**
     *
     * @param handler 为null，将同步执行。
     * @param pullType
     * @param url
     * @param callback
     * @param applyCache
     */
    public HttpPullThread(BaseSession session, Handler handler, HttpPull.PullType pullType, String url, Map<String, String> headerMap, HttpPull.ResultCallback<T> callback, boolean applyCache, boolean needEncodText){
        mSession = session;
        mUrl = new String(url);
        mPullTye = pullType;
        mSaveCache = applyCache;
        mHandler = handler;
        mHeaderMap = headerMap;
        mResultCallback = callback;
        mNeedEncodText = needEncodText;

    }

    /**
     * 返回处理结构。改结果将作为最终结果回调。
     * @return
     * @throws IOException
     */
    protected abstract  T doHttpContentt(byte[] content, String encode) throws Exception;


    /**
     * 处理从本地缓存的数据。所有的文本数据都是以utf8编码。
     * @return
     * @throws IOException
     */
    protected abstract  T doCacheData(File file, String defaultEncode) throws Exception;

    protected HttpClient createHttpClient(){
        //TODO 统一管理缓存池。
        HttpParams params = new BasicHttpParams();
        ConnManagerParams.setTimeout(params, DEFUALT_TIMEOUT);
        int ConnectionTimeOut = DEFUALT_TIMEOUT;
        HttpConnectionParams.setConnectionTimeout(params, ConnectionTimeOut);
        HttpConnectionParams.setSoTimeout(params, DEFUALT_TIMEOUT);
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
        return new DefaultHttpClient(conMgr,params);
    }

    protected HttpRequestBase createHttpRequest(String url){
        return new HttpGet(mUrl);
    }

    private boolean isEmptyResult(T data){
        if(data instanceof  CharSequence){
            return StringUtils.isEmpty((CharSequence)data);
        }else if(data instanceof  byte[]){
            return data == null ||(( byte[]) data).length == 0;
        }
        return data == null;

    }

    public static File getCacheFile(BaseSession session, String url){
        CacheManager cm = CacheManager.getDefault(session);
        if (cm != null) {
            CacheManager.ExpireData expireData = cm.getExpireData(url);
            if (expireData != null) {
                return prepareCacheFile(null, expireData);
            }
        }
        return null;
    }


    protected void doPullRresult()throws Exception{

        HttpRequestBase requset = createHttpRequest(mUrl);
        mCurrentHttpGet = requset;

        boolean loadCacheFile = mPullTye == HttpPull.PullType.CACHE_AND_NETWORK || mPullTye == HttpPull.PullType.CACHE_ONLY;
        boolean pullNetwork = mPullTye == HttpPull.PullType.CACHE_AND_NETWORK || mPullTye == HttpPull.PullType.NETWORK_ONLY;

        ResultData.DefaultResultData<T> result = null;
        final CacheManager cm = getCacheManager();

        if(loadCacheFile) {
            if (cm != null) {
                File cacheFile = null;
                CacheManager.ExpireData expireData = cm.getExpireData(mUrl);
                if (expireData != null) {
                   // if (mSaveCache) {
                        cacheFile = prepareCacheFile(requset, expireData);
                   // }
                }

                if(cacheFile!= null){

                    T data = doCacheData(cacheFile,"utf8");
                    if(!isEmptyResult(data)){
                        result = new ResultData.DefaultResultData<T>();
                        result.set(data);
                        result.setLastModified(new Date(cacheFile.lastModified()));
                        if(pullNetwork){
                            commitLocalResult(result);
                        }else{
                            commitResult(0, null, result);
                            return;
                        }
                       /* commitResult(false, 0, null, result);
                        if(mPullTye == HttpPull.PullType.CACHE_ONLY){
                            return;
                        }*/
                    }
                }
            }
        }

        if(!pullNetwork) {
            //不需要从网络抓取
            commitResult(0, null, null);
            return;
        }

            if(!NetWorkUtils.isNetworkAvaliable()){
                //网络不可以用
                commitError(HttpPull.ErrorType.NETWORK_NOT_AVALIABLE,null,null);
                return;
            }
            mHttp = createHttpClient();

            //支持压缩功能。
            requset.addHeader("Accept-Encoding", "gzip,deflate");


            if (mHeaderMap != null) {
                for (Map.Entry<String, String> entry : mHeaderMap.entrySet()) {
                    requset.addHeader(entry.getKey(), entry.getValue());
                }
            }

           final HttpResponse httpResponse = mHttp.execute(requset);

            int nStatusCode = httpResponse.getStatusLine().getStatusCode();

            if (nStatusCode == HttpStatus.SC_NOT_MODIFIED)
            {
               //304原则，本地缓存的数据就是最新的。
                return;
            }

           final  byte[] datas = loadAsBytes(httpResponse);

           final  String textEncode;
            if(mNeedEncodText){
                textEncode = getContentCharset(httpResponse, new ByteArrayInputStream(datas));
            }else{
                textEncode = null;
            }
            getContentCharset(httpResponse, new ByteArrayInputStream(datas));

            //TODO bytes[]结果
            final T data = doHttpContentt(datas,textEncode);

            if(data == null){
                commitError(HttpPull.ErrorType.UNKOWN, null,null);
                return;
            }
                Map<String, String> headers  = getHeaders(httpResponse);
                result = new ResultData.DefaultResultData<T>();
                result.set(data);
                result.setLastModified(new Date(System.currentTimeMillis()));
                if(mSaveCache&&result != null) {
                    Runnable saveCacheRunnable = new Runnable() {
                        @Override
                        public void run() {
                            //添加到缓存数据里
                           // CacheManager cm = getCacheManager();
                            if (cm != null) {
                                if(textEncode != null){
                                    String html = null;
                                    try {
                                        html = new String(datas,textEncode);
                                    } catch (UnsupportedEncodingException e) {
                                       // e.printStackTrace();
                                        ILog.e(getClass(), "add to cache error because of UnsupportedEncodingException");
                                        return;
                                    }
                                    try {
                                        cm.addTextToCacheFile(mUrl, httpResponse, html);
                                    }catch (Exception ex){
                                        ILog.e(getClass(), "add to cache error : " + ex.getMessage());
                                    }
                                }else{
                                    try {
                                        cm.addBytesToCacheFile(mUrl, httpResponse,datas);
                                    }catch (Exception ex){
                                        ILog.e(getClass(), "add to cache error : " + ex.getMessage());
                                    }
                                }
                            }
                        }
                    };
                    result.opration = saveCacheRunnable;
                }

                commitResult(nStatusCode,headers,result);



    }


    protected CacheManager getCacheManager(){
        return CacheManager.getDefault(mSession);
    }

    private boolean isAbandon = false;

    @Override
    public void abandon() {
        mSession = null;
        mHandler = null;
        isAbandon = true;
    }

    @Override
    public boolean isAbandon() {
        return isAbandon;
    }

    @Override
    public void run(){
        try{
            callCalback = false;
            doPullRresult();

        } catch (Exception ex){
            //finished
            if(isCancel){
               commitError(HttpPull.ErrorType.CANCELED,null,null);
            }else{
                if(ex instanceof ConnectTimeoutException) {
                    commitError(HttpPull.ErrorType.CONNECT_TIME_OUT,ex.getMessage(),ex);
                }else{
                    commitError(HttpPull.ErrorType.UNKOWN,ex.getMessage(),ex);
                }
            }

        }finally {
            mSession = null;
            isFinished = true;
            mHttp = null;
            mHeaderMap = null;
            mHandler = null;
            mResultCallback = null;
            cancel();
        }
    }

    private void commitLocalResult(final ResultData.DefaultResultData<T> result) {
        final HttpPull.ResultCallback callback = mResultCallback;
        final boolean succes = result!= null;
        if(callback == null){
            return;
        }

        Handler handler = mHandler;
        if(handler == null){
            callback.onResultFromLocal(result);


        }else{
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onResultFromLocal(result);
                }
            });
        }


    }

    private void commitResult(final int status,final Map<String,String> headers,final ResultData.DefaultResultData<T> result){

        final HttpPull.ResultCallback callback = mResultCallback;
        final boolean succes = result!= null;
        final HttpPull.ErrorType errorType = succes ? null: HttpPull.ErrorType.UNKOWN;
        if(callback == null){
            callCalback = true;
            return;
        }
        result.statusCode = status;
        result.headers = headers;

        Handler handler = mHandler;
        if(handler == null){
                callback.onResult(succes,errorType,result);

            if(result.opration != null && result.enableSaveToCache()){
                result.opration.run();
                result.opration = null;
            }
                callCalback = true;

        }else{
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onResult(succes,errorType,result);

                    //TODO：是否放在后台线程比较好呢。
                    if(result.opration != null && result.enableSaveToCache()){
                        result.opration.run();
                        result.opration = null;
                    }
                    callCalback = true;

                }
            });
        }


    }

    private void commitError(final HttpPull.ErrorType type,final String message,final Exception ex){
        final HttpPull.ResultCallback callback = mResultCallback;
        if(callback == null){
            callCalback = true;
            return;
        }

        Handler handler = mHandler;
        if(handler == null){
            callback.onResult(false,type,null);
            callCalback = true;
        }else{
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onResult(false,type,null);
                    callCalback = true;
                }
            });
        }

    }

    @Override
    public boolean isBusy() {
        if(!callCalback){
            //回调方法等待执行完。
            return true;
        }
        return !isFinished;
    }

    @Override
    public float getProgress() {
        return 0f;
    }

/*    @Override
    public boolean isProgressDeterminate() {
        return false;
    }*/

    @Override
    public void cancel() {
        isCancel = true;
        try {
            if (mCurrentHttpGet != null) {
                mCurrentHttpGet.abort();
                mCurrentHttpGet = null;
            }

        } catch (Throwable ex) {

        }

        try {
            if (mCurrentStream != null) {
                mCurrentStream.close();
                mCurrentStream = null;
            }
        } catch (Throwable ex) {

        }


    }

    @Override
    public boolean isCancellable() {
        return true;
    }

    @Override
    public String getDescription() {
        return mUrl;
    }



    private static File prepareCacheFile(HttpRequestBase requset, CacheManager.ExpireData expireData) {
        File cacheFile = null;
        //查看记录保存的是不是指定的filePath.
        if (expireData.filePath != null) {
            cacheFile = new File(expireData.filePath);
            if (cacheFile.exists()) {
                if(requset!= null) {
                    requset.addHeader("Cache-Control", "max-age=0");
                }
                if (!isEmpty(expireData.lastModified)) {
                    if(requset!= null) {
                        requset.addHeader("If-Modified-Since", expireData.lastModified);
                    }
                }
                if (!isEmpty(expireData.etag)) {
                    if(requset!= null) {
                        requset.addHeader("If-None-Match", expireData.etag);
                    }
                }
            }
        }

        return cacheFile;
    }

    private static boolean isEmpty (String text) {
        return text == null || text.length() == 0;
    }

    private Map<String,String> getHeaders(HttpResponse response) {
        Header[] headers = response.getAllHeaders();
        Map<String, String> map = new HashMap<String, String>();

        int size = headers != null ? headers.length : 0;
        List<NameValuePair> list = new ArrayList<NameValuePair>(size);
        if (size > 0) {
            for (Header entry : headers) {
                 map.put(entry.getName(),entry.getValue());
            }
        }
        return map;

    }

    private InputStream mCurrentStream ;

    protected byte[] loadAsBytes (HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            throw new NullPointerException();
        }

        //检查是否有压缩。
        InputStream is = entity.getContent();
        Header cotentEncodeHeader = response.getFirstHeader("Content-Encoding");
        if (cotentEncodeHeader != null && cotentEncodeHeader.getValue() != null) {
            String encode = cotentEncodeHeader.getValue().toLowerCase();
            if (encode.indexOf("gzip") > -1) {
                is = new GZIPInputStream(is);
            }else{
                ILog.w(TAG,"unkonw Content-Encoding:" + encode);
            }

        }
        mCurrentStream = is;
        if (is == null) {
            throw new NullPointerException();
        }
        ByteArrayOutputStream fos = new ByteArrayOutputStream(1024 * 10);
        final byte[] buffer = new byte[5 * 1024];
        try {
            //file.deleteOnExit();
            int nbRead = 0;
            while (-1 != (nbRead = is.read(buffer))) {
                fos.write(buffer, 0, nbRead);
            }

        } finally {
            Utils.closeQuietly(fos);
            Utils.closeQuietly(is);

        }
        return fos.toByteArray();
    }


    public String getContentCharset (HttpResponse response, InputStream in)
    {
        List<NameValuePair> headers = getHeaderList(response);
        String charset = getContentCharsetOrNull(headers, in);
        if (charset != null)
        {
            return charset;
        }

        return "utf8";
    }

    public List<NameValuePair> getHeaderList (HttpResponse response)
    {
        Header[] headers = response.getAllHeaders();

        int size = headers != null ? headers.length : 0;
        List<NameValuePair> list = new ArrayList<NameValuePair>(size);
        if (size > 0)
        {
            for (Header entry : headers)
            {
                list.add(new BasicNameValuePair(entry.getName(), entry.getValue()));
            }
        }
        return list;
    }

    public String getContentCharsetOrNull (List<NameValuePair> headers, InputStream is)
    {
        try
        {
            return EncodingSniffer.sniffEncoding(headers, is);
        }
        catch (final IOException e)
        {
            return null;
        }
        finally
        {
            Utils.closeQuietly(is);
        }
    }


}
