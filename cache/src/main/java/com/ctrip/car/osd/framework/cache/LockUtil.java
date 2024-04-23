package com.ctrip.car.osd.framework.cache;

import com.google.common.cache.CacheBuilder;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author by xiayx on 2019/8/5 20:26
 */
public class LockUtil {
    private static final Object	defaultLockObjForObj	= new Object();

    private static final com.google.common.cache.Cache<String,Serializable> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build();

    public static Serializable getLockObj(String cacheKey, Object myLock) {
        Serializable cacheLock = cache.getIfPresent(cacheKey);
        if (cacheLock == null) {
            Object ml = myLock;
            if (ml == null) {
                ml = defaultLockObjForObj;
            }
            synchronized (ml) {
                cacheLock = cache.getIfPresent(cacheKey);
                if (cacheLock == null) {
                    cacheLock = new Serializable() {
                        private static final long serialVersionUID = 255956860617836425L;
                    };
                    cache.put(cacheKey, cacheLock);
                }
            }
        }
        return cacheLock;
    }
}
