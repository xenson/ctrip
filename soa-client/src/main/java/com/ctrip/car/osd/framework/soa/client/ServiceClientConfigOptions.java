package com.ctrip.car.osd.framework.soa.client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * ServiceClient 配置信息  ; 定位到方法级别的代理 。
 * 
 * @author xiayx@ctrip.com
 * 
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceClientConfigOptions {


	/**
	 * 设置指定数据序列化格式
	 * 
	 * 支持格式 bjjson , bjbin , xml
	 * 
	 * 默认格式 bjjson
	 * 
	 * @return
	 */
	String format() default "";

	/**
	 * 请求超时时间
	 * 
	 * @return
	 */
	int timeout() default 0;

	int socketTimeout() default 0;

	int maxConnectionPerRoute() default 0;

	int connectTimeout() default 0;

	int idleTime() default 0;

	boolean logEnable() default true;

	boolean ignoreError() default true;
}
