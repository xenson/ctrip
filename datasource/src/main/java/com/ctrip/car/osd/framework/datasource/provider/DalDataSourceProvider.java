package com.ctrip.car.osd.framework.datasource.provider;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.car.osd.framework.datasource.DataSourceProvider;
import com.ctrip.car.osd.framework.datasource.DataSourceSettings;

public class DalDataSourceProvider implements DataSourceProvider {

	@Override
	public DataSource createDataSource(DataSourceSettings dataSourceProperties) throws Exception {

		if (StringUtils.isEmpty(dataSourceProperties.getAppId())) {
			return new DalDataSourceFactory().createDataSource(dataSourceProperties.getAllInOneKey(),
					dataSourceProperties.getSvcUrl());
		} else {
			return new DalDataSourceFactory().createDataSource(dataSourceProperties.getAllInOneKey(),
					dataSourceProperties.getSvcUrl(), dataSourceProperties.getAppId());
		}
	}

}
