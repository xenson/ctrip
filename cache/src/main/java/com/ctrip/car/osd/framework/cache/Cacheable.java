package com.ctrip.car.osd.framework.cache;

import java.lang.annotation.*;

@Target({ ElementType.FIELD , ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Cacheable {
	
	String name() default "";

	String key() default "";
	
	String type() default CacheType.MEM;

	String[] options() default "";

	String[] changeFactors() default "";

	CacheValueType cacheValueType() default CacheValueType.CACHE_EMPTY;

	int expiryMillis() default -1;

	int maxCount() default 0;

	boolean withPrefix() default true;

	Class<? extends IKeyGenerator> generator() default DefaultKeyGenerator.class;

	int waitLockCount() default 20;

}
