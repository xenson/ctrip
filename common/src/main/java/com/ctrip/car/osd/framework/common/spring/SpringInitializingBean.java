package com.ctrip.car.osd.framework.common.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class SpringInitializingBean implements ApplicationContextAware, InitializingBean {

	private static class ApplicationContextInstance {
		private static ApplicationContext context;

		private static void setApplicationContext(ApplicationContext applicationContext) {
			context = applicationContext;
		}

		private static ApplicationContext getApplicationContext() {
			return context;
		}
	}

	protected ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		ApplicationContextInstance.setApplicationContext(applicationContext);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	public static ApplicationContext getApplicationContext() {
		return ApplicationContextInstance.context;
	}
}
