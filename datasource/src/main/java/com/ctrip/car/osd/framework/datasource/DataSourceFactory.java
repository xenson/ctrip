package com.ctrip.car.osd.framework.datasource;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;

import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;

public class DataSourceFactory implements InitializingBean {

	private Logger logger = LoggerFactory.getLogger(DataSourceFactory.class);
	private DataSource2Properties dataSource2Properties;
	private DataSourceProvider dataSourceProvider;
	private Map<String, DataSource> dataSources;

	public DataSourceFactory(DataSource2Properties dataSource2Properties) {
		super();
		this.dataSources = new ConcurrentHashMap<>();
		this.dataSource2Properties = dataSource2Properties;
	}

	public DataSource getDataSource(String dataSourceKey) {

		if (dataSourceKey != null && !StringUtils.isEmpty(dataSourceKey)
				&& this.dataSources.containsKey(dataSourceKey)) {
			return this.dataSources.get(dataSourceKey);
		}
		if (this.dataSources.containsKey("default")) {
			return this.dataSources.get("default");
		}
		return this.dataSources.values().iterator().next();
	}

	public DataSource getDefaultDataSource() {
		if (this.dataSources.containsKey("default")) {
			return this.dataSources.get("default");
		}
		return this.dataSources.values().iterator().next();
	}

	public Map<Object, Object> getDataSources() {
		Map<Object, Object> dataSources = new HashMap<>();

		for (Entry<String, DataSource> ds : this.dataSources.entrySet()) {
			dataSources.put(ds.getKey(), ds.getValue());
		}
		return dataSources;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.dataSourceProvider == null) {
			try {
				this.dataSourceProvider = (DataSourceProvider) BeanUtils
						.instantiate(Class.forName(this.dataSource2Properties.getDataSourceProvider()));
			} catch (BeanInstantiationException e) {
				logger.error("The DataSource provider init error.", e);
			} catch (ClassNotFoundException e) {
				logger.error("The DataSource provider Class not found error.", e);
			}
		}
		Map<String, DataSourceSettings> dbs = this.dataSource2Properties.getDb();
		for (Entry<String, DataSourceSettings> entry : dbs.entrySet()) {
			try {
				DataSource dataSource = this.dataSourceProvider.createDataSource(entry.getValue());
				this.dataSources.put(entry.getKey(), dataSource);
			} catch (Exception e) {
				logger.error("Create the dataSource error.", e);
			}
		}
	}
}
