package com.ctrip.car.osd.framework.aspectj.util;

import com.ctrip.car.osd.framework.aspectj.IgnoreWeaver;
import com.ctrip.car.osd.framework.common.spring.SpringInitializingBean;
import org.springframework.stereotype.Component;

@Component
@IgnoreWeaver
public class SpringBeanUtility extends SpringInitializingBean {
    public static <T> T getBean(Class<T> cls) {
        return getApplicationContext().getBean(cls);
    }
}
