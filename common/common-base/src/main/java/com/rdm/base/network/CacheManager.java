package com.rdm.base.network;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;

import com.rdm.base.BaseSession;
import com.rdm.base.app.BaseApp;
import com.rdm.common.ILog;
import com.rdm.common.util.FileUtils;
import com.rdm.common.util.IOUtils;
import com.rdm.common.util.StringUtils;


/**
 *
 * //TODO 缓存数据库待优化。
 * @author lokierao
 */
public class CacheManager {

    private static final int MAX_FILE_LIMITED = 3000;

    private static final String CACHE_FILE_NAME = "__cache.map";
    private static volatile CacheManager gInstance = null;

    private File mDir = null;

    private LinkedList<String> toSaveList = new LinkedList<String>();

    private Map<String, ExpireData> mCacheMap = new HashMap<String, ExpireData>();

    private AtomicInteger mToken = new AtomicInteger(0);

    private CacheManager(/*Context context, */File direcotry) {
        //  mContext = context;
        mDir = direcotry;
        if (!mDir.exists()) {
            boolean flag = mDir.mkdirs();
            if (!flag) {
                ILog.e("Common", "CacheManager mkdirs fail . path = " + mDir.getAbsolutePath());
            }
        }
        loadCacheMap();
    }

    private static Map<BaseSession,CacheManager> mCacheManagerMap = new HashMap<BaseSession,CacheManager>();

    public synchronized static CacheManager getDefault(BaseSession session) {
        if(session == null){
            return null;
        }
        CacheManager cm =   mCacheManagerMap.get(session);
        if (cm == null) {
            File dir = new File(session.getDirecotry(),"http-caches");
            cm = new CacheManager(dir);
        }
        return cm;
    }


    public synchronized ExpireData getExpireData(String url) {
        return mCacheMap.get(url);
    }

    public boolean hasCache(String url) {
        ExpireData data = getExpireData(url);
        return data != null && !StringUtils.isEmpty(data.filePath);
    }

    private synchronized File createOrGetCacheFile(String url) {

        File file = getFileFromCache(url);
        if (file == null) {
            //限制大小，防止溢出。
            while (toSaveList.size() > MAX_FILE_LIMITED) {
                String remvoveUrl = toSaveList.removeLast();
                ExpireData deleteData = mCacheMap.remove(remvoveUrl);
                if (deleteData != null && !StringUtils.isEmpty(deleteData.filePath)) {
                    File dfile = new File(deleteData.filePath);
                    try {
                        dfile.delete();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            file = new File(mDir, System.currentTimeMillis() + "-" + mToken.incrementAndGet());
        }
        return file;
    }


    public void addCache(String url, ExpireData data) {
        addExpireData(url, data);
        saveCacheMap();
    }

    private synchronized void addExpireData(String url, ExpireData data) {
        //指定url置前
        toSaveList.remove(url);
        toSaveList.addFirst(url);

        mCacheMap.put(url, data);
    }

    public void addTextToCacheFile(String url, HttpResponse response, String text) {
        String lastModified = Utils.getHeaderValue(response, "Last-Modified");
        String etag = Utils.getHeaderValue(response, "Etag");

        File cacheFile = createOrGetCacheFile(url);
        try {
            boolean ok = Utils.saveStringAsFile(cacheFile, text, "utf8");
            if (!ok) {
                return;
            } else {
                ExpireData data = new ExpireData();
                data.lastModified = lastModified;
                data.etag = etag;
                data.filePath = cacheFile.getAbsolutePath();
                addExpireData(url, data);
                saveCacheMap();
            }
           cacheFile.setLastModified(BaseApp.get().getRealTime());

        } catch (Exception e) {
            //ignore
            e.printStackTrace();
        }

    }


    public void addBytesToCacheFile(String url, HttpResponse response, byte[] bytes) {
        String lastModified = Utils.getHeaderValue(response, "Last-Modified");
        String etag = Utils.getHeaderValue(response, "Etag");

        File cacheFile = createOrGetCacheFile(url);
        try {
            boolean ok = Utils.saveBytesAsFile(cacheFile, bytes);
            if (!ok) {
                return;
            } else {
                ExpireData data = new ExpireData();
                data.lastModified = lastModified;
                data.etag = etag;
                data.filePath = cacheFile.getAbsolutePath();
                addExpireData(url, data);
                saveCacheMap();
            }


        } catch (Exception e) {
            //ignore
            e.printStackTrace();
        }

    }

    public void removeExpireData(String mUrl) {
        deleteUrlFromCache(mUrl);
        saveCacheMap();
    }

    private synchronized boolean deleteUrlFromCache(String url) {
        ExpireData data = mCacheMap.remove(url);
        toSaveList.remove(url);
        if (data != null && !StringUtils.isEmpty(data.filePath)) {

            try {
                File file = new File(data.filePath);

                return file.delete();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private synchronized void loadCacheMap() {
        File file = new File(mDir, CACHE_FILE_NAME);
        mCacheMap.clear();
        FileInputStream in = null;//new FileOutputStream(file);
        try {
            in = new FileInputStream(file);

            ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
            Object ob = oin.readObject();
            if (ob instanceof Map) {
                mCacheMap = (Map<String, ExpireData>) ob;
            }
            oin.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
            toSaveList.clear();
            toSaveList.addAll(mCacheMap.keySet());

        }
        //  IOUtils.readBytes(in, len);
    }

    private synchronized void saveCacheMap() {
        File file = new File(mDir, CACHE_FILE_NAME);

        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out = new BufferedOutputStream(out);
            out = new ObjectOutputStream(out);
            ((ObjectOutputStream) out).writeObject(mCacheMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }


    public File getFileFromCache(String url) {
        ExpireData data = getExpireData(url);
        if (data != null && !StringUtils.isEmpty(data.filePath)) {
            return new File(data.filePath);
        }
        return null;
    }

    //清理缓存
    public void clear()throws IOException{
        FileUtils.deleteDirectory(mDir);
        mDir.mkdirs();
    }

    public static class ExpireData implements Serializable {
        private static final long serialVersionUID = 1L;
        public String lastModified;
        public String etag;
        public String filePath;
        public long httpRange = -1;//已下载的offset，用于断点续传。
        //public long lastUseTime = 0l;


    }


}
