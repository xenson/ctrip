package com.ctrip.car.osd.framework.dal.support;

import java.util.List;

import com.ctrip.car.osd.framework.common.utils.TypeUtils;
import com.ctrip.car.osd.framework.dal.DalRepository;
import com.ctrip.car.osd.framework.dal.DalRepositoryImpl;
import com.ctrip.car.osd.framework.dal.builder.DalQueryBuilder;
import com.ctrip.car.osd.framework.dal.builder.SqlBuilder.QuerySQL;
import com.ctrip.platform.dal.dao.DalPojo;

/**
 * DalRepository 实现
 * 
 * @author xiayx@Ctrip.com
 *
 * @param <T>
 */
public abstract class RepositorySupport<T extends DalPojo> {

	private DalRepository<T> repository;

	public RepositorySupport() {
		this.initRepository();
	}

	private void initRepository() {
		Class<T> classType = getEntityType();
		this.repository = new DalRepositoryImpl<>(classType);
	}

	protected DalQueryBuilder newBuilder() {
		return new DalQueryBuilder(this.repository);
	}

	protected DalQueryBuilder newBuilder(String sql) {
		return new DalQueryBuilder(this.repository).sql(sql);
	}

	protected DalRepository<T> getRepository() {
		return this.repository;
	}

	protected DalRepository<T> getDao() {
		return this.repository;
	}

	public int insert(T entity) {
		return this.getRepository().insert(entity);
	}

	public int update(T entity) {
		return this.getRepository().update(entity);
	}

	public int delete(T entity) {
		return this.getRepository().delete(entity);
	}

	public List<T> query(QuerySQL querySql) {
		return this.getRepository().query(querySql.getSql(), querySql.getParameters());
	}

	public T queryObject(QuerySQL querySql) {
		return this.getRepository().queryObject(querySql.getSql(), querySql.getParameters());
	}

	@SuppressWarnings("unchecked")
	protected Class<T> getEntityType() {
		return (Class<T>) TypeUtils.getGenericType(this.getClass(), RepositorySupport.class);
	}

}
