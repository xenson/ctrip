package com.ctrip.car.osd.framework.cache.test;

import com.ctrip.car.osd.framework.cache.Cacheable;
import com.ctrip.car.osd.framework.cache.autoconfigure.EnableCaching;
import org.springframework.stereotype.Repository;

@Repository
public class MemCacheMock {

    private int i;

    @Cacheable(name = "MemMockCacheExpiry",key = "arg_{1}",expiryMillis = 500)
    public int getDataExpiry(String arg1){
        return i++;
    }

    @Cacheable(name = "MemMockCacheMetric",key = "arg_{1}",expiryMillis = 5000)
    public int getDataMetric(String arg1){
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return i++;
    }

    @Cacheable(name = "MemMockCacheMaxCount",key = "arg_{1}",maxCount = 2)
    public int getDataMaxCount(String arg1){
        return i++;
    }

    @Cacheable(name = "MemMockCacheLock",key = "arg_{1}",expiryMillis = 50000)
    public int getDataLock(String arg1){
        try {
            i++;
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return i;
    }
}
