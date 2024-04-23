package com.ctrip.car.osd.framework.common.clogging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
public class LogConfiguration {
    @Bean
    public LogAspectProxy logAspectProxy() {
        return new LogAspectProxy();
    }
}
