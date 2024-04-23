package com.ctrip.car.osd.framework.common.config;

/**
 * 初始化配置信息
 * @author xiayx@Ctrip.com
 *
 */
public interface InitializingConfig {

	void initializingConfig(ConfigSettings settings) throws Exception;

	void afterInitializingConfig() throws Exception;

}
