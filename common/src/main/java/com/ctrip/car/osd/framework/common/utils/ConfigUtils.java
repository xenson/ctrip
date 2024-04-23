package com.ctrip.car.osd.framework.common.utils;

import com.ctrip.car.osd.framework.common.properties.CtripConfigProperties;

public class ConfigUtils {

	public static String getBasePackage(CtripConfigProperties configProperties, String defaultBasePackage) {
		return getBasePackage(configProperties.getBasePackage(), defaultBasePackage);
	}

	public static String getBasePackage(CtripConfigProperties configProperties) {
		return getBasePackage(configProperties.getBasePackage(), null);
	}

	public static String getBasePackage(String basePackage, String defaultBasePackage) {
		if (basePackage == null || basePackage.isEmpty()) {
			basePackage = defaultBasePackage;
		}
		if (basePackage == null || basePackage.isEmpty()) {
			basePackage = "com.ctrip";
		}
		return basePackage;
	}

}
