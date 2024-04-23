package com.ctrip.car.osd.framework.cache.autoconfigure;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;


/**
 * 
 * 开启缓存管理
 * 
 * @author xiayx@ctrip.com
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CacheConfiguration.class)
public @interface EnableCaching {
	
	String value() default "";

}
