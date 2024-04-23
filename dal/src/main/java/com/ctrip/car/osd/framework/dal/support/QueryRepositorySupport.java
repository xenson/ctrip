package com.ctrip.car.osd.framework.dal.support;

import java.util.List;

import com.ctrip.car.osd.framework.dal.DalQueryRepository;
import com.ctrip.car.osd.framework.dal.DalQueryRepositoryImpl;
import com.ctrip.car.osd.framework.dal.builder.DalQueryBuilder;
import com.ctrip.car.osd.framework.dal.builder.SqlBuilder.QuerySQL;

/**
 * Dal Query Repository 实现
 * 
 * @author xiayx@Ctrip.com
 *
 */
public class QueryRepositorySupport {

	private DalQueryRepository repository;

	public QueryRepositorySupport(Class<?> clazz) {
		this.repository = new DalQueryRepositoryImpl(clazz);
	}

	public QueryRepositorySupport(String databaseName) {
		this.repository = new DalQueryRepositoryImpl(databaseName);
	}

	protected DalQueryBuilder newBuilder() {
		return new DalQueryBuilder(this.repository);
	}

	protected DalQueryBuilder newBuilder(String sql) {
		return new DalQueryBuilder(this.repository).sql(sql);
	}

	protected DalQueryRepository getRepository() {
		return this.repository;
	}

	protected DalQueryRepository getDao() {
		return this.repository;
	}

	public <T> T queryObject(QuerySQL querySql, Class<T> entityClass) {
		return this.getRepository().queryObject(querySql.getSql(), querySql.getParameters(), entityClass);
	}

	public <T> List<T> query(QuerySQL querySql, Class<T> entityClass) {
		return this.getRepository().query(querySql.getSql(), querySql.getParameters(), entityClass);
	}
}
