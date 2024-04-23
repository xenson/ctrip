package com.ctrip.car.osd.framework.soa.server.autoconfigure;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;


/**
 * 启动自动创建服务代理
 * 
 * @author xiayx@ctrip.com
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SoaServiceProxyConfiguration.class)
public @interface EnableSoa2ServiceProxy {
	
	String value() default "";

}
