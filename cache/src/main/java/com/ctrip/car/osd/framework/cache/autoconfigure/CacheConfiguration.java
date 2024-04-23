package com.ctrip.car.osd.framework.cache.autoconfigure;

import com.ctrip.car.osd.framework.cache.CacheFactory;
import com.ctrip.car.osd.framework.cache.mem.MemeryStaticCacheProvider;
import com.ctrip.car.osd.framework.cache.proxy.CacheAspectProxy;
import com.ctrip.car.osd.framework.cache.redis.RedisCacheProvider;
import com.ctrip.car.osd.framework.common.properties.CtripConfigProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@EnableAspectJAutoProxy
public class CacheConfiguration {

	@Bean
	public CacheFactory cacheFactory(CtripConfigProperties configProperties) {
		return new CacheFactory(configProperties);
	}

	@Bean
	public MemeryStaticCacheProvider memeryStaticCacheProvider() {
		return new MemeryStaticCacheProvider();
	}

	@Bean
	public RedisCacheProvider redisCacheProvider() {
		return new RedisCacheProvider();
	}

	@Bean
	public CacheAspectProxy cacheAspectProxy() {
		return new CacheAspectProxy();
	}

	@Bean
	public ServletRegistrationBean servletRegistrationBean(WebApplicationContext applicationContext) {
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
		servletRegistrationBean.setName("cacheDispatcherServlet");
		servletRegistrationBean.setServlet(new DispatcherServlet(applicationContext));
		servletRegistrationBean.addUrlMappings("/cache/*");
		return servletRegistrationBean;
	}
}
