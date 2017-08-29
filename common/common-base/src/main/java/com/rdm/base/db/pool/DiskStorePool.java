package com.rdm.base.db.pool;

import android.util.Log;

import com.rdm.common.ILog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author:
 */
public class DiskStorePool extends MemoryPool implements SerializablePool<Object> {

    private static final String TAG = "DiskStoreCache";

    private static final float MB = 1024 * 1024;

    private volatile File cacheFile;

    private boolean invalidated;

    private boolean restored;

    public void setFile(String path) {
        cacheFile = new File(path);
    }

    @Override
    public synchronized void restore() {
        if (!restored) {
            long start = System.currentTimeMillis();

            loadFromDisk();

            long during = System.currentTimeMillis() - start;
            Log.d(TAG, "Load from disk during " + during);
            restored = true;
        }
    }

    @Override
    public void put(String key, Object value) {
        super.put(key, value);
        invalidated = true;
    }

    @Override
    public boolean remove(String key) {
        Object remove = super.remove(key);
        invalidated = invalidated || remove != null;
        return remove != null;
    }

    @Override
    public void clear() {
        invalidated = true;
        super.clear();
    }

    @Override
    public synchronized void serialize() {
        if (!invalidated) {
            ILog.w(TAG, "No change found !");
            return;
        }

        long start = System.currentTimeMillis();

        if (store2Disk()) {
            invalidated = false;
        }

        long during = System.currentTimeMillis() - start;
        Log.d(TAG, "Store 2 disk during " + during + " cache size:" + (cacheFile.length() / MB));
    }

    @Override
    public void setInvalidated(boolean invalidated) {
        this.invalidated = invalidated;
    }

    @SuppressWarnings("unchecked")
    private void loadFromDisk() {
        if (!cacheFile.exists())
            return;

        InputStream is = null;

        try {
            is = new FileInputStream(cacheFile.getAbsolutePath());
            is = new BufferedInputStream(is);
            is = new ObjectInputStream(is);

            Object obj = ((ObjectInputStream) is).readObject();

            Map<String, Object> serializableMap = (Map<String, Object>) obj;

            for (Map.Entry<String, Object> entry : serializableMap.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }

            Log.d(TAG, "loadFromDisk " + serializableMap.size());
        } catch (Exception e) {
            ILog.printStackTrace(e);

            ILog.w(TAG, "Delete Cache Success ?" + cacheFile.delete());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private synchronized boolean store2Disk() {
        OutputStream os = null;
        try {
            File parentFile = cacheFile.getParentFile();

            if (!parentFile.exists() && !parentFile.mkdirs()) {
                return false;
            }

            os = new FileOutputStream(cacheFile);
            os = new BufferedOutputStream(os);
            os = new ObjectOutputStream(os);

            Set<String> keySet = keySet();

            Map<String, Object> serializableMap = new HashMap<>();

            for (String key : keySet) {
                Object value = get(key);

                boolean contentNotSerializable = false;

                if (value != null && value instanceof Serializable) {

                    if (value instanceof Collection) {
                        for (Object o : (Collection) value) {
                            if (!(o instanceof Serializable)) {
                                contentNotSerializable = true;
                                break;
                            }
                        }
                    }

                    if (contentNotSerializable) {
                        continue;
                    }

                    serializableMap.put(key, value);
                }
            }

            ((ObjectOutputStream) os).writeObject(serializableMap);

            //printSerializable(serializableMap);

            return true;
        } catch (Exception e) {
            ILog.printStackTrace(e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    private void printSerializable(Map<String, Object> serializableMap) {
        Set<Map.Entry<String, Object>> entries = serializableMap.entrySet();

        List<Map.Entry<String, Object>> order = new ArrayList<>(entries);

        Collections.sort(order, new Comparator<Map.Entry<String, Object>>() {
            @Override
            public int compare(Map.Entry<String, Object> lhs, Map.Entry<String, Object> rhs) {
                return lhs.getValue().toString().length() - rhs.getValue().toString().length();
            }
        });

        StringBuilder sb = new StringBuilder();
        sb.append("serializable cache map size ").append(order.size()).append(":\n");
        for (Map.Entry<String, Object> entry : order) {
            sb.append(entry.getKey()).append(":").append(entry.getValue()).append('\n');
        }

        ILog.d(TAG, sb.toString());
    }

}
