package com.ctrip.credis;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by f_xie on 2016/10/16.
 */
public class RedisExpire extends BaseRedis {

    @Test //测试key有效期
    public void testExpire1() throws Exception {
        String key=getKey("TestKey");
        if(provider.exists(key))
            Assert.assertTrue(provider.del(key));
        Assert.assertFalse(provider.persist(key.getBytes()));
        provider.set(key,"TestValue");
        Assert.assertTrue(provider.exists(key));
        Assert.assertFalse(provider.persist(key.getBytes()));

        provider.set(key,"TestValue");
        Assert.assertTrue(provider.exists(key));
        Assert.assertTrue(provider.expire(key.getBytes(), 5));
        Thread.sleep(6000);
        Assert.assertFalse(provider.exists(key));

        provider.set(key,"TestValue");
        Assert.assertTrue(provider.exists(key));
        Assert.assertTrue(provider.expire(key.getBytes(), 5));
        provider.persist(key);
        Thread.sleep(5000);
        Assert.assertTrue(provider.exists(key));
    }
}
