package com.ctrip.car.osd.framework.cache.mem;

import com.ctrip.car.osd.framework.cache.Cache;
import com.ctrip.car.osd.framework.cache.CacheType;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class MemeryStaticCache<K, V> implements Cache<K, V> {
	private LoadingCache<K,V> cache;
	private int expiryMillis;
	private int maxCount;
	private Date refreshDate;
	private String name;
	private List<String> changeFactors;
	private AtomicLong hitCount;
	private AtomicLong missCount;

	MemeryStaticCache(String name, int expiryMillis, int maxCount,String[] changeFactors) {
		super();
		this.expiryMillis = expiryMillis;
		this.maxCount = maxCount;
		this.name = name;
		this.changeFactors = Arrays.asList(changeFactors);
		CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder().concurrencyLevel(2);
		if (maxCount > 0) {
			cacheBuilder.maximumSize(maxCount);
		}
		if (expiryMillis > 0) {
			cacheBuilder.expireAfterWrite(expiryMillis, TimeUnit.MILLISECONDS);
		}
		hitCount = new AtomicLong(0);
		missCount = new AtomicLong(0);
		this.refreshDate = new Date();
		this.cache = cacheBuilder.build(new CacheLoader<K, V>() {
			@Override
			public V load(K k) throws Exception
			{
				refreshDate = new Date();
				return null;
			}
		});
	}

	@Override
	public String cacheType() {
		return CacheType.MEM;
	}

	@Override
	public String cacheName() {
		return name;
	}

	@Override
	public V get(K key) {
		return cache.getIfPresent(key);
	}

	@Override
	public Set<K> keys() {
		return cache.asMap().keySet();
	}

	@Override
	public boolean containsKey(K key) {
		V unchecked = this.cache.getIfPresent(key);
        boolean exist = unchecked != null;
        if (exist) {
            hitCount.incrementAndGet();
        } else {
            missCount.incrementAndGet();
        }
        return exist;
	}

	@Override
	public Map<K, V> asMap() {
		return this.cache.asMap();
	}

	@Override
	public List<String> getChangeFactors() {
		return this.changeFactors;
	}

	@Override
	public boolean put(K key, V value) {
		refreshDate = new Date();
		this.cache.put(key, value);
		return true;
	}

	@Override
	public void putAll(Map<K, V> values) {
		refreshDate = new Date();
		this.cache.putAll(values);
	}

	@Override
	public void clear() {
		refreshDate = new Date();
		this.cache.invalidateAll();
	}

	@Override
	public void clear(K key) {
		refreshDate = new Date();
		this.cache.invalidate(key);
	}

	@Override
	public void clearAll(Collection<K> keys) {
		refreshDate = new Date();
		this.cache.invalidateAll(keys);
	}

	@Override
	public Map<K, V> getAll(Collection<K> keys) {
		return this.cache.getAllPresent(keys);
	}

	@Override
	public Date refreshDate() {
		return refreshDate;
	}

	@Override
	public int expiryMillis() {
		return expiryMillis;
	}

	@Override
	public int maxCount() {
		return maxCount;
	}

	@Override
	public double hitRate() {
		long requestCount = this.hitCount() + this.missCount();
		double hitRate = (requestCount == 0) ? 1.0 : (double) hitCount() / requestCount;
		return (double) Math.round(hitRate * 100) / 100;
	}

	@Override
	public long hitCount() {
		return this.hitCount.longValue();
	}

	@Override
	public long missCount() {
		return this.missCount.longValue();
	}
}
