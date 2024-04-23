package com.ctrip.credis;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by f_xie on 2016/10/16.
 */
public class RedisZadd extends BaseRedis {

    @Test
    public void testZadd() throws Exception {
        String key=getKey("Testkey");
        provider.del(key);
        Assert.assertFalse(provider.exists(key));
        boolean result;

        result = provider.zadd(key,10,"google");
        Assert.assertTrue(result);
        result = provider.zadd(key,8.0,"baiDu");
        Assert.assertTrue(result);
        result = provider.zadd(key,9.0,"qq");
        Assert.assertTrue(result);

        Set<String> set = new HashSet<String>();
        set.add("baiDu");
        Set<String> actual = provider.zrange(key,0,0);
        Assert.assertEquals("testZadd failed.", set, actual);

        result = provider.zadd(key,9.5,"baiDu");
        Assert.assertFalse(result);
        set = new HashSet<String>();
        set.add("qq");
        actual = provider.zrange(key,0,0);
        Assert.assertEquals("testZadd failed 2.", set, actual);
    }


    @Test
    public void testZadd1() throws Exception {
        String key=getKey("Testkey");
        Long result;
        Long length = 3L;
        Map<String, Double> scoreMembers = new HashMap<String, Double>();

        scoreMembers.put("baiDu",8.0);
        scoreMembers.put("google", 10.0);
        scoreMembers.put("qq",9.0);

        provider.del(key);
        Assert.assertFalse(provider.exists(key));
        result = provider.zadd(key,scoreMembers);
        Assert.assertEquals("testZadd1 failed.", length, result);

        Set<String> set = new HashSet<String>();
        set.add("baiDu");
        Set<String> actual = provider.zrange(key,0,0);
        Assert.assertEquals("testZadd1 failed 2.", set, actual);

        provider.zadd(key, 9.5, "baiDu");
        set = new HashSet<String>();
        set.add("qq");
        actual = provider.zrange(key,0,0);
        Assert.assertEquals("testZadd1 failed 3.", set, actual);

    }
}
