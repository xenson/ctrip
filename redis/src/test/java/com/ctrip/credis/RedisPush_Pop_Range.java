package com.ctrip.credis;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by f_xie on 2016/10/16.
 */
public class RedisPush_Pop_Range extends BaseRedis {
    @Test
    public void test_rPush_rPop_lRange(){
        String key=getKey("TestKey");
        Assert.assertFalse(provider.exists(key));

        long len = provider.rpush(key, "a", "b", "c");
        Assert.assertEquals(3, len);

        long len2 = provider.rpush(key,"d","d");
        Assert.assertEquals(5, len2);

        List<String> list = new ArrayList<String>();
        Assert.assertEquals(list, provider.lrange(key, 8, 10));
        list.add("a");list.add("b");list.add("c");list.add("d");list.add("d");
        Assert.assertEquals(list, provider.lrange(key, 0, 4));
        Assert.assertEquals(list, provider.lrange(key, 0, -1));

        Assert.assertEquals("d", provider.rpop(key));
        Assert.assertEquals(null, provider.rpop(getKey("TestKey2")));
        list.remove(4);
        Assert.assertEquals(list, provider.lrange(key, 0, 4));
        Assert.assertEquals(list, provider.lrange(key, 0, -1));


    }

}
