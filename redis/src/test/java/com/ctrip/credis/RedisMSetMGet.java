package com.ctrip.credis;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by f_xie on 2016/10/16.
 */
public class RedisMSetMGet extends BaseRedis {

    @Test
    public void testMset() throws Exception {
        for(int i=1; i<=3; i++){
            provider.del(getKey("key"+i));
            Assert.assertFalse(provider.exists(getKey("key"+i)));
        }
        provider.mset(getKey("key1"),"value1",getKey("key2"),"value2",getKey("key3"),"value3");
        for (int i=1; i<=3; i++)
            Assert.assertTrue(provider.exists(getKey("key" + i)));
        for (int i = 0; i < 3; i++) {
            System.out.println(provider.mget(getKey("key1"),getKey("key2"),getKey("key3")).get(i));   //may not in order
        }
        Assert.assertEquals(3, provider.mget(getKey("key1"), getKey("key2"), getKey("key3")).size());
    }

    @Test
    public void testMget1() throws Exception {
        String key=getKey("TestKey");
        provider.del(key);
        Assert.assertFalse(provider.exists(key));
        provider.set(key, "TestValue");
        List<byte[]> result;
        result = provider.mget(key.getBytes());
        Assert.assertEquals(1, result.size());

        String key1=getKey("TestKey1");
        provider.del(key1);
        result = provider.mget(key.getBytes(), "TestKey1".getBytes());
        Assert.assertEquals(null, result.get(1));
        Assert.assertEquals(2, result.size());
        provider.set(key1, "TestValue1");
        result = provider.mget(key.getBytes(), key1.getBytes());
        Assert.assertEquals(2, result.size());

        provider.del(key);
        provider.del(key1);
        result = provider.mget(key.getBytes(), key1.getBytes());
        Assert.assertTrue(result.get(0) == null && result.get(1) == null);
        Assert.assertEquals(2, result.size());
    }

}
