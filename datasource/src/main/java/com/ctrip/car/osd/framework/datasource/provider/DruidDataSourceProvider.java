package com.ctrip.car.osd.framework.datasource.provider;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.ctrip.car.osd.framework.datasource.DataSourceProvider;
import com.ctrip.car.osd.framework.datasource.DataSourceSettings;

public class DruidDataSourceProvider implements DataSourceProvider {

	@Override
	public DataSource createDataSource(DataSourceSettings dataSourceProperties) throws Exception{
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(dataSourceProperties.getDriver());
		dataSource.setUrl(dataSourceProperties.getUrl());
		dataSource.setUsername(dataSourceProperties.getUser());
		dataSource.setPassword(dataSourceProperties.getPass());

		int min = dataSourceProperties.getMin() <= 0 ? 2 : dataSourceProperties.getMin();
		int max = dataSourceProperties.getMax() <= 0 ? 10 : dataSourceProperties.getMax();

		dataSource.setInitialSize(min);
		dataSource.setMinIdle(min);
		dataSource.setMaxActive(max);
		dataSource.setTestOnBorrow(true);
		dataSource.setTestWhileIdle(true);
		dataSource.setValidationQuery("SELECT 1");
		return dataSource;
	}

}
