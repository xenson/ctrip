package com.ctrip.car.osd.framework.soa.server.autoconfigure;

import org.springframework.context.annotation.Import;

/**
 * 启动自动创建服务代理
 * 
 * @author xiayx@ctrip.com
 *
 */
@Import(SoaServiceAutoConfigureRegistrar.class)
public class SoaServiceProxyConfiguration {
	

}
