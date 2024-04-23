package com.ctrip.credis;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by f_xie on 2016/10/16.
 */
public class RedisGet extends BaseRedis {

    @Test
    public void testGet1() throws Exception {
        String key=getKey("TestKey");
        provider.del(key);
        Assert.assertFalse(provider.exists(key));
        provider.set(key, "TestValue");
        Assert.assertNotNull(provider.get(key.getBytes()));
    }
}
