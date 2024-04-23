package com.ctrip.car.osd.framework.elasticsearch;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ElasticSearchBeanRegister.class})
public @interface EnableElasticSearch {

}
