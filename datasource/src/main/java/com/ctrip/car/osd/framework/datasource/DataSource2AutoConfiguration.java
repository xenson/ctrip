package com.ctrip.car.osd.framework.datasource;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties( DataSource2Properties.class)
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class DataSource2AutoConfiguration {
	
	@Bean
	public DataSourceFactory dataSourceFactory(DataSource2Properties dataSource2Properties){
		return new DataSourceFactory(dataSource2Properties);
	}
	
	@Bean
	@ConditionalOnMissingBean(DataSource.class)
	public DataSource dataSource(DataSourceFactory dataSourceFactory) {
		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		dynamicDataSource.setTargetDataSources(dataSourceFactory.getDataSources());
		dynamicDataSource.setDefaultTargetDataSource(dataSourceFactory.getDefaultDataSource());
		return dynamicDataSource;
	}

}
