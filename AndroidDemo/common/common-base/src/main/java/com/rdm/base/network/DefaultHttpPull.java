package com.rdm.base.network;

import android.os.Handler;
import android.os.Looper;

import com.rdm.base.Abandonable;
import com.rdm.base.BaseSession;
import com.rdm.base.BusyFeedback;
import com.rdm.base.ThreadManager;
import com.rdm.common.util.FileUtils;
import com.rdm.common.util.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lokierao on 2015/1/12.
 */
public class DefaultHttpPull implements HttpPull,Abandonable {

    private static final String HEADER_COOKIE = "Cookie";

    private BaseSession mSession;
    private Handler mHandler;
    private final boolean mRunInBackground;
    private boolean isAbandon = false;
    private Map<String,String>  mHeaderMap = new HashMap<String,String>();

    public DefaultHttpPull(BaseSession session, Looper looer, boolean runInBackground){
        this.mSession = session;
        mRunInBackground = runInBackground;
        if(looer != null){
            mHandler = new Handler(looer);
        }
    }

    public Map<String,String> getHeaderMap(){
        return mHeaderMap;
    }


    @Override
    public void setHeader(String header, String content) {
        mHeaderMap.put(header,content);
    }

    @Override
    public void removeHeader(String header) {
        mHeaderMap.remove(header);
    }

    @Override
    public void addCookie(String name, String value) {
        if(name == null || value == null) {
            throw new RuntimeException();
        }
        String cookies = mHeaderMap.get(HEADER_COOKIE);
        String cookie = null;
        try {
            cookie= name + "=" + URLEncoder.encode(value,"utf8");
        } catch (UnsupportedEncodingException e) {
            cookie = name + "=" +URLEncoder.encode(value);
        }

        if (StringUtils.isEmpty(cookie)) {
            throw new RuntimeException("add cookie error");
        }

        if (StringUtils.isEmpty(cookies)) {
            cookies = cookie;
        }else{
            cookies = cookies +  "&" + cookie;
        }
        mHeaderMap.put(HEADER_COOKIE, cookies);
    }

    @Override
    public void removeCookies() {
        mHeaderMap.remove(HEADER_COOKIE);
    }

    @Override
    public String loadText(PullType type, String url, boolean saveCache)throws IOException {
        SyncResultCallback callback = new SyncResultCallback();
        PullTextThread task = new PullTextThread(mSession,null,type, url, mHeaderMap, callback, saveCache);
        execute(task, false);

        String text = callback.result != null?(String)callback.result.getBody():null;
        return text;
    }


    @Override
    public BusyFeedback pullText(String url, ResultCallback<String> callback) {
        return pullText(PullType.CACHE_AND_NETWORK,url,callback,true);
    }

    @Override
    public BusyFeedback pullText(PullType type, String url, ResultCallback<String> callback, boolean applyCache) {
        PullTextThread task = new PullTextThread(mSession,mHandler,type, url, mHeaderMap, callback,  applyCache);
        execute(task,mRunInBackground);
        return task;
    }

    @Override
    public BusyFeedback pullBlob(String url, ResultCallback<byte[]> callback) {
        return pullBlob(PullType.CACHE_AND_NETWORK,url,callback,true);
    }

    @Override
    public BusyFeedback pullBlob(PullType type, String url, ResultCallback<byte[]> callback, boolean saveCache) {
        PullBlobThread task = new PullBlobThread(mSession,mHandler,type, url, mHeaderMap, callback, saveCache);
        execute(task,mRunInBackground);
        return task;
    }

    @Override
    public BusyFeedback pullJSONObject(String url, ResultCallback<JSONObject> callback) {
        return pullJSONObject(PullType.CACHE_AND_NETWORK, url, callback, true);
    }

    @Override
    public BusyFeedback pullJSONObject(PullType type, String url, ResultCallback<JSONObject> callback,  boolean saveCache) {
        PullJSONObjectThread task = new PullJSONObjectThread(mSession,mHandler,type, url, mHeaderMap, callback, saveCache);
        execute(task,mRunInBackground);
        return task;
    }

    @Override
    public BusyFeedback pullJSONArray(String url, ResultCallback<JSONArray> callback) {
        return pullJSONArray(PullType.CACHE_AND_NETWORK, url, callback,  true);
    }

    @Override
    public BusyFeedback pullJSONArray(PullType type, String url, ResultCallback<JSONArray> callback,boolean saveCache) {
        PullJSONArrayThread task = new PullJSONArrayThread(mSession,mHandler,type, url, mHeaderMap, callback, saveCache);
        execute(task,mRunInBackground);
        return task;
    }


    @Override
    public File loadCacheFile(String url) {
        return HttpPullThread.getCacheFile(mSession,url);
    }

    private Object mLock = new Object();

    private void execute(HttpPullThread task,boolean runInBackground){
        synchronized (mLock){
            if(isAbandon){
                throw new IllegalStateException("Loader is abandon.");
            }

        }
        if(runInBackground) {
            ThreadManager.execute(task);
        }else{
            task.run();
        }

    }

    @Override
    public void abandon() {
        synchronized (mLock){
            mSession = null;
            mHandler = null;
            isAbandon = true;
        }

    }

    @Override
    public boolean isAbandon() {
        return isAbandon;
    }


    private static class PullTextThread extends HttpPullThread<String> {

        public PullTextThread(BaseSession session, Handler handler, PullType pullType, String url, Map<String, String> parameters, ResultCallback<String> callback, boolean saveCache) {
            super(session, handler, pullType, url, parameters, callback, saveCache, true);
        }

        @Override
        protected String doHttpContentt(byte[] content, String encode) throws Exception {
            return new String(content,encode);
        }

        @Override
        protected String doCacheData(File file, String defaultEncode) throws Exception {
            byte[] data = FileUtils.readFileToByteArray(file);
            return new String(data,defaultEncode);
        }
    }

    private static class PullJSONObjectThread extends HttpPullThread<JSONObject> {

        public PullJSONObjectThread(BaseSession session, Handler handler, PullType pullType, String url, Map<String, String> parameters, ResultCallback<JSONObject> callback, boolean saveCache) {
            super(session, handler, pullType, url, parameters, callback, saveCache, true);
        }

        @Override
        protected JSONObject doHttpContentt(byte[] content, String encode) throws Exception {
            return new JSONObject(new String(content,encode));
        }

        @Override
        protected JSONObject doCacheData(File file, String defaultEncode) throws Exception {
            byte[] data = FileUtils.readFileToByteArray(file);
            return new JSONObject(new String(data,defaultEncode));
        }
    }

    private static class PullJSONArrayThread extends HttpPullThread<JSONArray> {


        public PullJSONArrayThread(BaseSession session, Handler handler, PullType pullType, String url, Map<String, String> parameters, ResultCallback<JSONArray> callback, boolean saveCache) {
            super(session, handler, pullType, url, parameters, callback, saveCache, true);
        }

        @Override
        protected JSONArray doHttpContentt(byte[] content, String encode) throws Exception {
            return new JSONArray(new String(content,encode));
        }

        @Override
        protected JSONArray doCacheData(File file, String defaultEncode) throws Exception {
            byte[] data = FileUtils.readFileToByteArray(file);
            return new JSONArray(new String(data,defaultEncode));
        }
    }



    private static class PullBlobThread extends HttpPullThread<byte[]> {

        public PullBlobThread(BaseSession session, Handler handler, PullType pullType, String url, Map<String, String> parameters, ResultCallback<byte[]> callback, boolean saveCache) {
            super(session, handler, pullType, url, parameters, callback, saveCache, false);
        }

        @Override
        protected byte[] doHttpContentt(byte[] content, String encode) throws IOException {
            return content;
        }

        @Override
        protected byte[] doCacheData(File file, String defaultEncode) throws Exception {
            byte[] data = FileUtils.readFileToByteArray(file);
            return data;
        }
    }


   private static class SyncResultCallback implements ResultCallback {

        public ResultData result;

       @Override
       public void onResult(boolean isSuccess, ErrorType errorType, ResultData result) {
           this.result = result;
       }

       @Override
       public void onResultFromLocal(ResultData result) {
        this.result = result;
       }
   }


   /* private static class PullFileThread extends HttpPullThread<File> {

        public PullBlobThread(BaseSession session, Handler handler, PullType pullType, String url, Map<String, String> parameters, ResultCallback<byte[]> callback, boolean saveCache) {
            super(PullFileThread, handler, pullType, url, parameters, callback, saveCache, false);
        }

        @Override
        protected File doHttpContentt(byte[] content, String encode) throws IOException {
            return content;
        }

        @Override
        protected File doCacheData(File file, String defaultEncode) throws Exception {
            byte[] data = FileUtils.readFileToByteArray(file);
            return data;
        }
    }*/


}
