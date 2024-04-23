package com.ctrip.car.osd.framework.dal.builder;

import com.ctrip.car.osd.framework.dal.query.DalQueryTypes;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;

public class SqlPaginationBuilder {

	private static final String MYSQL_PAGE_SUFFIX_TPL = " limit ?, ?";
	private static final String SQLSVR_PAGE_SUFFIX_TPL = " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
	private String sql;
	private StatementParameters parameters;
	private DatabaseCategory database;

	public SqlPaginationBuilder(String sql, StatementParameters parameters, DatabaseCategory database) {
		super();
		this.sql = sql;
		this.database = database;
		this.parameters = parameters;
	}

	public String build() {
		String suffix = DatabaseCategory.SqlServer == this.database ? SQLSVR_PAGE_SUFFIX_TPL : MYSQL_PAGE_SUFFIX_TPL;
		return this.sql + suffix;
	}

	public StatementParameters buildParameters(int start, int limit) {
		StatementParameters tempParameters = this.parameters.duplicate();
		int index = this.parameters.size();
		tempParameters.add(
				new StatementParameter(index + 1, DalQueryTypes.geJdbcType(Integer.class), start).setSensitive(true));
		tempParameters.add(
				new StatementParameter(index + 2, DalQueryTypes.geJdbcType(Integer.class), limit).setSensitive(true));
		return tempParameters;
	}

}
