package com.ctrip.car.osd.framework.cache.test;

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
public class MemCacheTest {

    @Autowired
    private MemCacheMock memCacheMock;

    @Test
    public void testExpiry(){
        int data = memCacheMock.getDataExpiry("1");
        Assert.assertEquals(data,0);
        data = memCacheMock.getDataExpiry("1");
        Assert.assertEquals(data,0);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        data = memCacheMock.getDataExpiry("1");
        Assert.assertEquals(data,1);
    }

    @Test
    public void testMetric(){
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            memCacheMock.getDataMetric("1");
        }
        System.out.println(System.currentTimeMillis() - begin);
    }

    @Test
    public void testCacheLock(){
        List<Integer> resultList = new CopyOnWriteArrayList<>();
        List<CompletableFuture> taskList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
                int value = memCacheMock.getDataLock("1");
                resultList.add(value);
            });
            taskList.add(task);
        }
        CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0])).join();
        Assert.assertEquals(resultList.size(),100);
        Assert.assertTrue(resultList.stream().allMatch(r->r==1));
    }

    @Test
    public void testMaxCount(){
        int data = memCacheMock.getDataMaxCount("0");
        Assert.assertEquals(data,0);
        data = memCacheMock.getDataMaxCount("1");
        Assert.assertEquals(data,1);
        data = memCacheMock.getDataMaxCount("2");
        Assert.assertEquals(data,2);
        data = memCacheMock.getDataMaxCount("3");
        Assert.assertEquals(data,3);
        data = memCacheMock.getDataMaxCount("4");
        Assert.assertEquals(data,4);
        data = memCacheMock.getDataMaxCount("5");
        Assert.assertEquals(data,5);
    }
}
