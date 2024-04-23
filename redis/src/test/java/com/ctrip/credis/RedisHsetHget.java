package com.ctrip.credis;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by f_xie on 2016/10/16.
 */
public class RedisHsetHget extends BaseRedis {

    @Test
    public void testHset1_Hget1() throws Exception {
        String key=getKey("TestKey");
        if(provider.exists(key))
            Assert.assertTrue(provider.del(key));

        Long result = 1L;
        Assert.assertEquals(result, provider.hset(key.getBytes(), "name".getBytes(), "".getBytes()));
        Assert.assertEquals(result, provider.hset(key.getBytes(), "sex".getBytes(), "female".getBytes()));
        result = 0L;
        Assert.assertEquals(result, provider.hset(key.getBytes(), "sex".getBytes(), "male".getBytes()));

        Assert.assertEquals("", new String(provider.hget(key.getBytes(), "name".getBytes())));
        Assert.assertEquals("male", new String(provider.hget(key.getBytes(), "sex".getBytes())));

        String key2="TestKey2";
        provider.del(key2);
        Assert.assertEquals(null, provider.hget(key, "class"));
        Assert.assertEquals(null, provider.hget(key2, "name"));
    }


    @Test
    public void testHexists1_Hdel1() throws Exception {
        String key=getKey("TestKey");
        provider.del(key);
        Assert.assertFalse(provider.exists(key));
        Assert.assertFalse(provider.hexists(key.getBytes(), "name".getBytes()));//key不存在，返回fale
        provider.hset(key, "name", "lemon");
        Assert.assertTrue(provider.exists(key));
        Assert.assertFalse(provider.hexists(key.getBytes(), "age".getBytes()));//key存在,field不存在
        Assert.assertTrue(provider.hexists(key.getBytes(), "name".getBytes()));//key和field都存在


        long re = provider.hdel(key.getBytes(),"age".getBytes());//key存在，field不存在
        Assert.assertEquals(0, re);
        long re2 = provider.hdel(key.getBytes(), "name".getBytes());
        Assert.assertEquals(1, re2);//key和field都存在
        Assert.assertFalse(provider.hexists(key.getBytes(), "name".getBytes()));
        long re3 = provider.hdel(key.getBytes(),"name".getBytes());//key不存在
        Assert.assertEquals(0, re3);
    }
}
