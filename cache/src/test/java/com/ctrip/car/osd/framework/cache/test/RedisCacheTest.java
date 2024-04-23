package com.ctrip.car.osd.framework.cache.test;

import com.ctrip.car.osd.framework.cache.Cache;
import com.ctrip.car.osd.framework.common.utils.ThreadPoolUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

@RunWith(SpringRunner.class)
@SpringBootApplication
public class RedisCacheTest {

    @Autowired
    private RedisCacheMock redisCacheMock;

    @Test
    public void testExpry(){
        Cache redisCache = redisCacheMock.redisCache;
        redisCache.put("test", 100);
        redisCache.put("test", 200);
        redisCache.put("test1", 300);
        redisCache.clear();
    }

    @Test
    public void testCacheLock(){
        List<Integer> resultList = new CopyOnWriteArrayList<>();
        List<CompletableFuture> taskList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
                Integer value = redisCacheMock.getDataLock("1");
//                System.out.println(value);
                resultList.add(value);
            }, ThreadPoolUtil.callerRunsPool("test-%d",50));
            taskList.add(task);
        }
        CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0])).join();
        Assert.assertEquals(resultList.size(),100);
        Assert.assertTrue(resultList.stream().allMatch(r->r==1));
    }

    @Test
    public void testCacheLockForTimeout(){
        List<CompletableFuture> taskList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
                Integer value = redisCacheMock.getDataLockNull("1");
                System.out.println(value);
            }, ThreadPoolUtil.callerRunsPool("test-%d",50));
            taskList.add(task);
        }
        CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0])).join();
    }

    @Test
    public void testTime(){
        List<CompletableFuture> taskList = new ArrayList<>();
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
                Integer value = redisCacheMock.getDataLockTime("1");
                System.out.println(Thread.currentThread().getId()+":"+(System.currentTimeMillis() - begin));
            }, ThreadPoolUtil.callerRunsPool("test-%d",10));
            taskList.add(task);
        }
        CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0])).join();
    }
}
