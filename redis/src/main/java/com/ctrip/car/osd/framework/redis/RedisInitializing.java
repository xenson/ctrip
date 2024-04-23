package com.ctrip.car.osd.framework.redis;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import com.ctrip.car.osd.framework.common.helper.RedisClusterNameHelper;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.framework.common.properties.CtripConfigProperties;
import com.ctrip.car.osd.framework.common.spring.SpringInitializingBean;
import com.ctrip.car.osd.framework.common.utils.ConfigUtils;

import credis.java.client.util.CacheFactory;
import credis.java.client.util.HashStrategy;

public class RedisInitializing extends SpringInitializingBean {

	private final Logger LOGGER = LoggerFactory.getLogger(RedisInitializing.class);
	@Autowired
	private CtripConfigProperties ctripConfigProperties;

	@Override
	public void afterPropertiesSet() throws Exception {

		String basePackage = ConfigUtils.getBasePackage(this.ctripConfigProperties);
		Reflections scanner = new Reflections(basePackage, new FieldAnnotationsScanner());
		Set<Field> fields = scanner.getFieldsAnnotatedWith(RedisProvider.class);
		for (Field field : fields) {
			Map<String, ?> values = this.applicationContext.getBeansOfType(field.getDeclaringClass());
			if (values == null || values.isEmpty()) {
				continue;
			}
			for (Object target : values.values()) {
				RedisProvider provider = field.getAnnotation(RedisProvider.class);
				ReflectionUtils.makeAccessible(field);
				LOGGER.info(" set " + target.getClass().getName() + " redis field name : " + provider.value());
				Class<? extends HashStrategy> strategyClass = provider.hashStrategy();
				if (!HashStrategy.class.equals(strategyClass)) {
					HashStrategy strategy = BeanUtils.instantiate(strategyClass);
					field.set(target, CacheFactory.getProvider(RedisClusterNameHelper.getClusterName(provider.value()), strategy));
				} else {
					field.set(target, CacheFactory.GetProvider(RedisClusterNameHelper.getClusterName(provider.value())));
				}
			}
		}
	}

}
