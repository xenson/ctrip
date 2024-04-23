package com.ctrip.car.osd.framework.common.config;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import com.ctrip.car.osd.framework.common.clogging.CatFactory;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

public class ConfigStartup implements ApplicationContextAware, InitializingBean {

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, InitializingConfig> configs = this.applicationContext.getBeansOfType(InitializingConfig.class);
		for (Entry<String, InitializingConfig> config : configs.entrySet()) {
			initializing(config.getValue());
		}
	}

	private void initializing(InitializingConfig config) throws Exception {
		ConfigSettings settings = new ConfigSettings();
		config.initializingConfig(settings);

		Field[] fields = FieldUtils.getAllFields(config.getClass());
		for (Field field : fields) {
			ReflectionUtils.makeAccessible(field);
			Object value = field.get(config);
			String name = field.getName();
			if (value == null) {
				field.set(config, settings.get(name));
			} else if ("es".equals(name)) {
				CatFactory.ES_Scenario = value.toString();
			}
		}

		config.afterInitializingConfig();
	}

}
