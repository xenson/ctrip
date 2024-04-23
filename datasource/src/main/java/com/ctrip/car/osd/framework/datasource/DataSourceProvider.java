package com.ctrip.car.osd.framework.datasource;

import javax.sql.DataSource;

public interface DataSourceProvider {

	DataSource createDataSource(DataSourceSettings dataSourceProperties) throws Exception;

}
