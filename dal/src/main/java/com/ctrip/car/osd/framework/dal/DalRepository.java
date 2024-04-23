package com.ctrip.car.osd.framework.dal;

import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;

public interface DalRepository<R extends DalPojo> extends DalQueryRepository {

	/**
	 * Insert pojo and get the generated PK back in keyHolder. If the
	 * "set no count on" for MS SqlServer is set(currently set in Ctrip), the
	 * operation may fail. Please don't pass keyholder for MS SqlServer to avoid
	 * the failure.
	 *
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation.
	 * @param daoPojo
	 *            pojo to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	int insert(DalHints hints, R daoPojo);

	int insert(R daoPojo);

	/**
	 * Insert pojos one by one. If you want to inert them in the batch mode,
	 * user batchInsert instead. You can also use the combinedInsert.
	 *
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation. DalHintEnum.continueOnError can be used to
	 *            indicate that the inserting can be go on if there is any
	 *            failure.
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected
	 */
	int[] insert(DalHints hints, List<R> daoPojos);

	int[] insert(List<R> daoPojos);

	/**
	 * Insert pojo and get the generated PK back in keyHolder. If the
	 * "set no count on" for MS SqlServer is set(currently set in Ctrip), the
	 * operation may fail. Please don't pass keyholder for MS SqlServer to avoid
	 * the failure.
	 *
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation.
	 * @param keyHolder
	 *            holder for generated primary keys
	 * @param daoPojo
	 *            pojo to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	int insert(DalHints hints, KeyHolder keyHolder, R daoPojo);

	int insert(KeyHolder keyHolder, R daoPojo);

	/**
	 * Insert pojos and get the generated PK back in keyHolder. If the
	 * "set no count on" for MS SqlServer is set(currently set in Ctrip), the
	 * operation may fail. Please don't pass keyholder for MS SqlServer to avoid
	 * the failure.
	 *
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation. DalHintEnum.continueOnError can be used to
	 *            indicate that the inserting can be go on if there is any
	 *            failure.
	 * @param keyHolder
	 *            holder for generated primary keys
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	int[] insert(DalHints hints, KeyHolder keyHolder, List<R> daoPojos);

	int[] insert(KeyHolder keyHolder, List<R> daoPojos);

	/**
	 * Insert pojos in batch mode. The DalDetailResults will be set in hints to
	 * allow client know how the operation performed in each of the shard.
	 *
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation.
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected for inserting each of the pojo
	 * @throws SQLException
	 */
	int[] batchInsert(DalHints hints, List<R> daoPojos);

	int[] batchInsert(List<R> daoPojos);

	/**
	 * Insert multiple pojos in one INSERT SQL and get the generated PK back in
	 * keyHolder. If the "set no count on" for MS SqlServer is set(currently set
	 * in Ctrip), the operation may fail. Please don't pass keyholder for MS
	 * SqlServer to avoid the failure. The DalDetailResults will be set in hints
	 * to allow client know how the operation performed in each of the shard.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation.
	 * @param keyHolder
	 *            holder for generated primary keys
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsert(DalHints hints, List<R> daoPojos) throws SQLException;

	/**
	 * Insert multiple pojos in one INSERT SQL and get the generated PK back in
	 * keyHolder. If the "set no count on" for MS SqlServer is set(currently set
	 * in Ctrip), the operation may fail. Please don't pass keyholder for MS
	 * SqlServer to avoid the failure. The DalDetailResults will be set in hints
	 * to allow client know how the operation performed in each of the shard.
	 * 
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation.
	 * @param keyHolder
	 *            holder for generated primary keys
	 * @param daoPojos
	 *            list of pojos to be inserted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	public int combinedInsert(DalHints hints, KeyHolder keyHolder, List<R> daoPojos) throws SQLException;

	public int combinedInsert(List<R> daoPojos) throws SQLException;

	/**
	 * Update the given pojo . By default, if a field of pojo is null value,
	 * that field will be ignored, so that it will not be updated. You can
	 * overwrite this by set updateNullField in hints.
	 *
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation. DalHintEnum.updateNullField can be used to
	 *            indicate that the field of pojo is null value will be update.
	 * @param daoPojo
	 *            pojo to be updated
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	int update(DalHints hints, R daoPojo);

	int update(R daoPojo);

	/**
	 * Update the given pojo list one by one. By default, if a field of pojo is
	 * null value, that field will be ignored, so that it will not be updated.
	 * You can overwrite this by set updateNullField in hints.
	 *
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation. DalHintEnum.updateNullField can be used to
	 *            indicate that the field of pojo is null value will be update.
	 * @param daoPojos
	 *            list of pojos to be updated
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	int[] update(DalHints hints, List<R> daoPojos);

	int[] update(List<R> daoPojos);

	/**
	 * Update the given pojo list in batch.
	 *
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	int[] batchUpdate(DalHints hints, List<R> daoPojos);

	int[] batchUpdate(List<R> daoPojos);

	/**
	 * Delete the given pojo.
	 *
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation.
	 * @param daoPojo
	 *            pojo to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	int delete(DalHints hints, R daoPojo);

	int delete(R daoPojo);

	/**
	 * Delete the given pojos list one by one.
	 *
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation.
	 * @param daoPojos
	 *            list of pojos to be deleted
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	int[] delete(DalHints hints, List<R> daoPojos);

	int[] delete(List<R> daoPojos);

	/**
	 * Delete the given pojo list in batch. The DalDetailResults will be set in
	 * hints to allow client know how the operation performed in each of the
	 * shard.
	 *
	 * @param hints
	 *            Additional parameters that instruct how DAL Client perform
	 *            database operation.
	 * @param daoPojos
	 *            list of pojos to be deleted
	 * @return how many rows been affected for deleting each of the pojo
	 * @throws SQLException
	 */
	int[] batchDelete(DalHints hints, List<R> daoPojos);

	int[] batchDelete(List<R> daoPojos);

	/**
	 * Get the all records count
	 */
	int count(DalHints hints);

	int count();

	/**
	 * Query TDaoPojo by the specified ID The ID must be a number
	 **/
	R queryByPk(Number id, DalHints hints);

	R queryByPk(Number id);

	/**
	 * Query TDaoPojo by TDaoPojo instance which the primary key is set
	 **/
	R queryByPk(R pk, DalHints hints);

	R queryByPk(R pk);

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used as search criteria.
	 **/
	List<R> queryLike(R sample, DalHints hints);

	List<R> queryLike(R sample);

	/**
	 * Query TDaoPojo with paging function The pageSize and pageNo must be
	 * greater than zero.
	 */
	List<R> queryAllByPage(int pageNo, int pageSize, DalHints hints);

	List<R> queryAllByPage(int pageNo, int pageSize);

	/**
	 * Get all records from table
	 */
	List<R> queryAll(DalHints hints);

	List<R> queryAll();

	/**
	 * 基于SQL语句查询
	 * 
	 * @param sql
	 * @param parameters
	 * @param hints
	 * @see com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder
	 * @return
	 */
	List<R> query(SelectSqlBuilder builder, DalHints hints);

	List<R> query(SelectSqlBuilder builder);

	/**
	 * 基于SQL语句查询
	 * 
	 * @param sql
	 * @param parameters
	 * @param hints
	 * @see com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder
	 * @return
	 */
	List<R> query(String sql, StatementParameters parameters, DalHints hints);

	List<R> query(String sql, StatementParameters parameters);

	R queryObject(String sql, StatementParameters parameters, DalHints hints);

	R queryObject(String sql, StatementParameters parameters);

	DalTableDao<R> getDao();

	DalQueryDao getQueryDao();

}
