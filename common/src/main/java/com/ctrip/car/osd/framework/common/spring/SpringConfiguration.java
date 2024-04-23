package com.ctrip.car.osd.framework.common.spring;

import org.springframework.context.annotation.Bean;

public class SpringConfiguration {

    @Bean
    public SpringApplicationContextHolder springApplicationContextHolder() {
        return new SpringApplicationContextHolder();
    }
}
