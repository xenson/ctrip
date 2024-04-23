package com.ctrip.car.osd.framework.elasticsearch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableElasticSearch
public class ElasticSearchAutoConfiguration {

    @Bean
    public ElasticSearchInitializer elasticSearchInitializer() {
        return new ElasticSearchInitializer();
    }

}
