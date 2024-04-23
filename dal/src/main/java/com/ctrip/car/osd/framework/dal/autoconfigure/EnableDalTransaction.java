package com.ctrip.car.osd.framework.dal.autoconfigure;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 开启 dal事务
 * 
 * @author xiayx@Ctrip.com
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DalTransactionConfiguration.class)
public @interface EnableDalTransaction {

	String value() default "";
}
