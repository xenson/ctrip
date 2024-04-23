package com.ctrip.car.osd.framework.common.clogging;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LogConfiguration.class)
public @interface EnableLog {
}
