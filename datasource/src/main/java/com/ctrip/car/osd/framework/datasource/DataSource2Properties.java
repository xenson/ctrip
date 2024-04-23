package com.ctrip.car.osd.framework.datasource;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.ctrip.car.osd.framework.common.config.DefaultConfigProperties;

@ConfigurationProperties(prefix = "ctrip.ds")
public class DataSource2Properties extends DefaultConfigProperties {

	private String dataSourceProvider;
	private Map<String, DataSourceSettings> db;

	public String getDataSourceProvider() {
		return dataSourceProvider;
	}

	public void setDataSourceProvider(String dataSourceProvider) {
		this.dataSourceProvider = dataSourceProvider;
	}

	public Map<String, DataSourceSettings> getDb() {
		return db;
	}

	public void setDb(Map<String, DataSourceSettings> db) {
		this.db = db;
	}

}
