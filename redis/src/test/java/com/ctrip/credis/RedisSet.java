package com.ctrip.credis;

import com.ctrip.framework.foundation.Foundation;
import credis.java.client.CacheProvider;
import credis.java.client.setting.RAppSetting;
import credis.java.client.util.CacheFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by f_xie on 2016/10/16.
 */
public class RedisSet extends BaseRedis {


    @Test
    public void testSet1() throws Exception {
        String key=getKey("TestKey");
        provider.del(key);
        Assert.assertFalse(provider.exists(key));
        Assert.assertFalse(provider.exists(key.getBytes()));
        provider.set(key.getBytes(), "TestValue".getBytes());
        Assert.assertTrue(provider.exists(key));
        Assert.assertTrue(provider.exists(key.getBytes()));
    }


    @Test
    public void testSet2() throws Exception {
        String key=getKey("test1");
        provider.set(key, "test");
        boolean tag = provider.set(key.getBytes("UTF-8"), key.getBytes("UTF-8"), "XX".getBytes("UTF-8"), "EX".getBytes("UTF-8"), 10086L);
        Assert.assertTrue(tag);
        tag = provider.set(key.getBytes("UTF-8"), key.getBytes("UTF-8"), "XX".getBytes("UTF-8"), "PX".getBytes("UTF-8"), 12331212121L);
        Assert.assertTrue(tag);
        provider.del(key);
    }
}
