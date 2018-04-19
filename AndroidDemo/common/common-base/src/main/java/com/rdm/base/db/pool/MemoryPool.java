package com.rdm.base.db.pool;

import android.support.v4.util.LruCache;


import com.rdm.base.db.Pool;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Author:
 */
public class MemoryPool implements Pool<Object> {

    private static final int maxSize = 1024;

    private Set<String> keySet;

    private LruCache<String, Object> cacheImpl;

    public MemoryPool() {
        keySet = new HashSet<>();

        cacheImpl = new LruCache<>(maxSize);
    }

    @Override
    public void put(String key, Object value) {
        if (key == null) return;

        keySet.add(key);
        cacheImpl.put(key, value);
    }

    @Override
    public void put(String key, Object object, int priority) {
        throw new UnsupportedOperationException("priority !");
    }

    @Override
    public void putAll(Map<String, ?> map, int priority) {
        throw new UnsupportedOperationException("priority !");
    }

    @Override
    public void putAll(Map map) {

    }

    @Override
    public boolean remove(String key) {
        if (key == null) return false;

        keySet.remove(key);
        return cacheImpl.remove(key) != null;
    }

    @Override
    public int remove(Collection keys) {
        return 0;
    }

    @Override
    public Object get(String key) {
        return get(key, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> _class) {
        if (key == null) return null;

        Object obj = cacheImpl.get(key);

        return getWithType(obj, _class);
    }

    @Override
    public <T> T get(String key, Class<T> _class, Map<String, Object> cacheExtra) {
        return null;
    }

    @Override
    public Map<String, Object> get(Set<String> keys) {
        return get(keys, null);
    }

    @Override
    public <T> Map<String, T> get(Set<String> keys, Class<T> _class) {
        return get(keys, _class, null);
    }

    @Override
    public <T> Map<String, T> get(Set<String> keys, Class<T> _class, Map<String, Object> lastModifyData) {
        //todo lastModifyData

        Map<String, T> result = new HashMap<>();
        for (String key : keys) {
            Object obj = get(key);
            T t = getWithType(obj, _class);
            if (t != null) {
                result.put(key, t);
            }
        }
        return result;
    }

    @Override
    public void clear() {
        keySet.clear();
        cacheImpl.evictAll();
    }

    @Override
    public void release() {

    }

    public Set<String> keySet() {
        return keySet;
    }

    @SuppressWarnings("unchecked")
    private <T> T getWithType(Object obj, Class<T> _class) {
        if (obj == null) return null;
        if (_class == null) {
            return (T) obj;
        } else if (obj.getClass().equals(_class)) {
            return (T) obj;
        } else {
            return null;
        }
    }

}
