package com.rdm.base.loader;

import android.support.v4.util.LruCache;

import java.util.HashMap;

public class MemoryCache
{
	private static MemoryCache instance;
	
	private HashMap<Class<? extends Object>, LruCache<String, ? extends Object>> caches;
	
	private MemoryCache(){}
	public static synchronized MemoryCache getInstance()
	{
		if(instance == null)
		{
			instance = new MemoryCache();
		}
		return instance;
	}
	
	private synchronized HashMap<Class<? extends Object>, LruCache<String, ? extends Object>> getCaches()
	{
		if(caches == null)
		{
			caches = new HashMap<Class<? extends Object>, LruCache<String,? extends Object>>();
		}
		return caches;
	}
	
	@SuppressWarnings("unchecked")
	public Object getFromCache(Class clazz, String key)
	{
		LruCache<String, Object> lruCache = (LruCache<String, Object>) getCaches().get(clazz);
		if(lruCache != null)
		{
			return lruCache.get(key);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public  void putCache(Class clazz, String key, Object value)
	{
		LruCache<String, Object> lruCache = (LruCache<String, Object>) getCaches().get(clazz);
		if(lruCache == null)
		{
			lruCache = new LruCache<String, Object>(1000);
			caches.put(clazz, lruCache);
		}
		lruCache.put(key, value);
	}

	public void clearAllMemoryCache()
	{
		getCaches().clear();
	}
	
    public void clearMemoryCache(Class<? extends Object> clazz) {
		getCaches().remove(clazz);
	}

    public <T extends Object> void clearMemoryCache(Class<T> clazz, String key) {
        LruCache<String, T> lruCache = (LruCache<String, T>) getCaches().get(clazz);
        if (lruCache != null) {
            lruCache.remove(key);
        }
    }
}
