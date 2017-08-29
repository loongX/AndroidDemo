package com.rdm.base.db.pool;


import com.rdm.base.db.Pool;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created on 2015/11/2 by donnyliu .
 */
public class WeakRefPool implements Pool<Object> {

    private Map<String, WeakReference<Object>> refs;

    public WeakRefPool() {
        refs = new HashMap<>();
    }

    @Override
    public void put(String key, Object object) {
        refs.put(key, new WeakReference<>(object));
    }

    @Override
    public void put(String key, Object object, int priority) {
        put(key, object);
    }

    @Override
    public void putAll(Map<String, ?> map) {
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void putAll(Map<String, ?> map, int priority) {
        putAll(map);
    }

    @Override
    public boolean remove(String key) {
        WeakReference<Object> ref = refs.remove(key);
        return ref != null;
    }

    @Override
    public int remove(Collection<String> keys) {
        refs.keySet().removeAll(keys);
        return keys.size();
    }

    @Override
    public Object get(String key) {
        WeakReference<Object> ref = refs.get(key);
        return ref == null ? null : ref.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> _class) {
        Object obj = get(key);
        return obj == null ? null : (T) obj;
    }

    @Override
    public <T> T get(String key, Class<T> _class, Map<String, Object> cacheExtra) {
        return get(key, _class);
    }

    @Override
    public Map<String, Object> get(Set<String> keys) {
        Map<String, Object> result = new HashMap<>();
        for (String key : keys) {
            result.put(key, get(key));
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> get(Set<String> keys, Class<T> _class) {
        return (Map<String, T>) get(keys);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> get(Set<String> keys, Class<T> _class, Map<String, Object> cacheExtra) {
        return (Map<String, T>) get(keys);
    }

    @Override
    public void clear() {
        refs.clear();
    }

    @Override
    public void release() {
        clear();
    }

}
