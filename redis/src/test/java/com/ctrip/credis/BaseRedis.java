package com.ctrip.credis;

import com.ctrip.framework.foundation.Foundation;
import credis.java.client.CacheProvider;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootApplication
public class BaseRedis {

	@Autowired
	private CacheProviderWrapper cacheProviderWrapper;
	public CacheProvider provider;
	public static String ip = Foundation.net().getHostAddress();

	@Before
	public void setUp() {
		provider = cacheProviderWrapper.getProvider();
	}

	public String getKey(String key) {
		return ip + "_" + key;
	}
}
