package com.ctrip.credis;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@EnableAutoConfiguration
public class RedisExists extends BaseRedis {

    @Test
    public void testExists1() throws Exception {
        String key=getKey("TestKey");
        provider.del(key);
        Assert.assertFalse(provider.exists(key));
        provider.set(key, "TestValue");
        Assert.assertTrue(provider.exists(key));
    }
}
