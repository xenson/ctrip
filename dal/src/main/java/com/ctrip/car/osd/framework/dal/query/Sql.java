package com.ctrip.car.osd.framework.dal.query;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * SQL 注释  , 作用给标识 SQL语句
 * 
 * @author xiayx@Ctrip.com
 *
 */
@Target({ ElementType.METHOD , ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sql {
	
	String value() default "";
	
}
