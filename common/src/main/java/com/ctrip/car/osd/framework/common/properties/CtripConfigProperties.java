package com.ctrip.car.osd.framework.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.ctrip.car.osd.framework.common.config.ConfigSettings;
import com.ctrip.car.osd.framework.common.config.DefaultConfigProperties;
import com.ctrip.car.osd.framework.common.config.Env;

@ConfigurationProperties(prefix = "ctrip")
public class CtripConfigProperties extends DefaultConfigProperties {

	/**
	 * soa service的包路径范围，由ctrip.basePackage配置指定
	 */
	private String basePackage;


	private String es;

	/**
	 * soa service的全限定类名，由ctrip.soaTargetService配置指定
	 */
	private String soaTargetService;

	/**
	 * soa executor的包路径，由ctrip.executorPackage配置指定
	 */
	private String executorPackage;

	@Override
	public void initializingConfig(ConfigSettings settings) throws Exception {
		settings.set("basePackage", Env.all, "com.ctrip");
		settings.set("es", Env.all, "car-osd");
		settings.set("soaTargetService", Env.all, "");
		settings.set("executorPackage", Env.all, "");
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public String getEs() {
		return es;
	}

	public void setEs(String es) {
		this.es = es;
	}

	public String getSoaTargetService() {
		return soaTargetService;
	}

	public void setSoaTargetService(String soaTargetService) {
		this.soaTargetService = soaTargetService;
	}

	public String getExecutorPackage() {
		return executorPackage;
	}

	public void setExecutorPackage(String executorPackage) {
		this.executorPackage = executorPackage;
	}
}
