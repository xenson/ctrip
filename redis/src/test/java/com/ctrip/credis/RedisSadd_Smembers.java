package com.ctrip.credis;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * Created by f_xie on 2016/10/16.
 */
public class RedisSadd_Smembers extends BaseRedis {

    @Test
    public void testSadd1_Smembers1() throws Exception {
        String key=getKey("TestKey");
        provider.del(key);
        Assert.assertFalse(provider.exists(key));
        long len = provider.sadd(key.getBytes(), "a".getBytes(),
                "b".getBytes(), "c".getBytes());
        Assert.assertEquals(3, len);
        Set<byte[]> set2 = provider.smembers(key.getBytes());
        Assert.assertEquals(3, set2.size());

        long len2 = provider.sadd(key.getBytes(),"b".getBytes(),
                "c".getBytes(),"d".getBytes());
        Assert.assertEquals(1, len2);
        set2 = provider.smembers(key.getBytes());
        Assert.assertEquals(4, set2.size());
    }
}
