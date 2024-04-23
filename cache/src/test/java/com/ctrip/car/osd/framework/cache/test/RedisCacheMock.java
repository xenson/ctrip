package com.ctrip.car.osd.framework.cache.test;

import com.ctrip.car.osd.framework.cache.Cache;
import com.ctrip.car.osd.framework.cache.CacheType;
import com.ctrip.car.osd.framework.cache.Cacheable;
import com.ctrip.car.osd.framework.common.exception.BizException;
import org.springframework.stereotype.Repository;

@Repository
public class RedisCacheMock {

    private int i;

    @Cacheable(name = "RedisMockCacheField",type = CacheType.REDIS,options = {"carosdcommoncache"})
    public Cache redisCache;

    @Cacheable(name = "RedisMockCacheMethod",type = CacheType.REDIS,options = {"carosdcommoncache"},key = "arg_{1}")
    public int getDataExpiry(String arg1){
        return i++;
    }

    @Cacheable(name = "RedisMockCacheLock",key = "arg_{1}",type = CacheType.REDIS,options = {"carosdcommoncache"},expiryMillis = 10000)
    public Integer getDataLock(String arg1){
        try {
            i++;
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return i;
    }

    @Cacheable(name = "RedisMockCacheLockNull",key = "arg_{1}",type = CacheType.REDIS,options = {"carosdcommoncache"},expiryMillis = 10000)
    public Integer getDataLockNull(String arg1){
        try {
            i++;
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Cacheable(name = "RedisMockCacheTime",key = "arg_{1}",type = CacheType.REDIS,options = {"carosdcommoncache"},expiryMillis = 10000)
    public Integer getDataLockTime(String arg1){
        try {
            i++;
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
