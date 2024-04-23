package com.ctrip.car.osd.framework.cache.vi;

import com.ctrip.car.osd.framework.cache.Cache;
import com.ctrip.framework.vi.cacheRefresh.CacheManager;

public class CacheStats<K, V> {

    private String name;
    private String type;
    private Cache<K, V> cache2;

    public CacheStats(String name, String type, Cache<K, V> cache2) {
        super();
        this.name = name;
        this.type = type;
        this.cache2 = cache2;
        this.init();
    }

    private void init() {
        RefreshAllCacheCell<K, V> cell = new RefreshAllCacheCell<>(this.name, type, cache2);
        CacheManager.add(cell);
    }

    public String getType() {
        return type;
    }

}
