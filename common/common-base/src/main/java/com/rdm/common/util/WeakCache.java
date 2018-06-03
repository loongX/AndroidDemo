package com.rdm.common.util;


import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * 虚拟应用的cache。
 *
 * @author Rao
 */
public class WeakCache<K, V> {

    private static final int CHECK_MAX_COUNT = 300;

    private Map<K, WeakReference<V>> cache = Collections.synchronizedMap(new HashMap<K, WeakReference<V>>());

    private int oprationCount = 0;

    public WeakCache() {
    }

    public void put(K cacheKey, V value) {
        cache.put(cacheKey, new WeakReference<V>(value));
        oprationCount++;

        //不定时检查并删除null值的key，以防内存溢出。
        check();
    }

    public V get(K cacheKey) {
        // WeakReference<T>
        WeakReference<V> ref = cache.get(cacheKey);
        return ref != null ? ref.get() : null;
    }

    public void clear(String cacheKey) {
        cache.remove(cacheKey);
    }

    private synchronized void check() {
        if (oprationCount < CHECK_MAX_COUNT) {
            return;
        }
        Iterator<Entry<K, WeakReference<V>>> iter = cache.entrySet().iterator();
        Vector toDeleteKeys = new Vector();
        while (iter.hasNext()) {
            Entry<K, WeakReference<V>> entry = iter.next();
            if (entry.getValue().get() == null) {
                toDeleteKeys.add(entry.getKey());
            }
        }

        for (final Object key : toDeleteKeys) {
            cache.remove(key);
        }
        oprationCount = 0;
        // Object[] objs = cache.keySet().toArray();

    }

    public void clear() {
        cache.clear();
        oprationCount = 0;
    }

    public int size() {
        return cache.size();
    }

    public static void main(String[] args) {


        //使用普通的map作为cache，会报内存溢出。
        //  HashMap<String, Object> map = new HashMap<String, Object>();

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < 50 * 1024; i++) {
            sb.append("xxxxxxxxxxxxxxxxxxxxx");
        }

        //使用该cache不会报内存溢出。
        WeakCache<String, byte[]> map = new WeakCache<String, byte[]>();
        for (int i = 0; i < 1000; i++) {
            System.out.println("time : " + i);
            byte[] bigData = new byte[10 * 1024 * 1024];
            map.put(sb.toString() + i, bigData);
        }

    }

}
