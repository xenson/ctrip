package com.ctrip.car.osd.framework.dal;

import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;


/**
 * 
 * 单纯查询接口
 * 
 * @author xiayx@Ctrip.com
 *
 */
public interface DalQueryRepository {

	/**
	 * 基于SQL语句查询
	 * 
	 * @param sql
	 * @param parameters
	 * @param hints
	 * @see com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder
	 * @return
	 */
	<T> List<T> query(String sql, StatementParameters parameters, Class<T> entityClass, DalHints hints);

	<T> List<T> query(String sql, StatementParameters parameters, Class<T> entityClass);

	<T> List<T> query(String sql, Map<String, Object> parameters, Class<T> entityClass, DalHints hints);

	<T> List<T> query(String sql, Map<String, Object> parameters, Class<T> entityClass);
	
	<T> List<T> query(String sql, StatementParameters parameters, DalHints hints , DalRowMapper<T> dalRowMapper);
	
	
	<T> T queryObject(String sql, StatementParameters parameters, Class<T> entityClass, DalHints hints);
	
	<T> T queryObject(String sql, StatementParameters parameters, Class<T> entityClass);

	<T> T queryObject(String sql, StatementParameters parameters, DalHints hints , DalRowMapper<T> dalRowMapper);
	
	<T> T queryObject(String sql, Map<String, Object> parameters, Class<T> entityClass, DalHints hints);

	<T> T queryObject(String sql, Map<String, Object> parameters, Class<T> entityClass);

	/**
	 * 分批拉取数据
	 * 
	 * @param sql
	 * @param parameters
	 * @param entityClass
	 * @param hints
	 * @return
	 */
	<T> List<T> fetchQuery(String sql, StatementParameters parameters, Class<T> entityClass, 
			DalHints hints,
			DatabaseCategory category, 
			int batchSize, 
			long intervalMills);
	
	/**
	 * 分批拉取数据
	 * 
	 * @param sql
	 * @param parameters
	 * @param entityClass
	 * @param hints
	 * @return
	 */
	<T> List<T> fetchQuery(String sql, StatementParameters parameters,
			DalHints hints,
			DalRowMapper<T> dalRowMapper ,
			DatabaseCategory category, 
			int batchSize, 
			long intervalMills);
	
	
	
	/**
	 * 分批拉取数据
	 * 
	 * @param sql
	 * @param parameters
	 * @param entityClass
	 * @param hints
	 * @return
	 */
	<T> List<T> fetchQuery(String sql, StatementParameters parameters, Class<T> entityClass, 
			DalHints hints,
			int batchSize, 
			long intervalMills);
	
	
	/**
	 * 分批拉取数据
	 * 
	 * @param sql
	 * @param parameters
	 * @param entityClass
	 * @param hints
	 * @return
	 */
	<T> List<T> fetchQuery(String sql, StatementParameters parameters,
			DalHints hints,
			DalRowMapper<T> dalRowMapper ,
			int batchSize, 
			long intervalMills);
	

	/**
	 * 查询总数
	 * 
	 * @param builder
	 * @param hints
	 * @return
	 */
	int count(SelectSqlBuilder builder, DalHints hints);

	int count(SelectSqlBuilder builder);

	int count(String sql, StatementParameters parameters, DalHints hints);

	int count(String sql, StatementParameters parameters);

	int count(String sql, Map<String, Object> parameters, DalHints hints);

	int count(String sql, Map<String, Object> parameters);
	
	void setDalRowMapperContext( DalRowMapperContext context );
	
	int update(FreeUpdateSqlBuilder builder, DalHints hints);

	int update(FreeUpdateSqlBuilder builder);
	
	int update(String sql, StatementParameters parameters, DalHints hints);
	
	int update(String sql, StatementParameters parameters);
	
	int update(String sql, Map<String, Object> parameters, DalHints hints);

	int update(String sql, Map<String, Object> parameters);

}
