package com.ctrip.car.osd.framework.common.clogging;

import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Loggable {
    LogLevel value() default LogLevel.DEBUG;
    LogType type() default LogType.ALL;
}
