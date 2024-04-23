package com.ctrip.car.osd.framework.redis;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import credis.java.client.util.HashStrategy;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisProvider {
	
	String value();
	
	Class<? extends HashStrategy> hashStrategy() default HashStrategy.class; 

}
