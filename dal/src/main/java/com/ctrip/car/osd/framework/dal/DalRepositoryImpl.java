package com.ctrip.car.osd.framework.dal;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.framework.dal.builder.SqlBuilder;
import com.ctrip.car.osd.framework.dal.builder.SqlBuilder.QuerySQL;
import com.ctrip.car.osd.framework.dal.mapper.JpaRowMapperContext;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.helper.EntityManager;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;

public class DalRepositoryImpl<R extends DalPojo> implements DalRepository<R> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DalRepositoryImpl.class);
	private static final boolean ASC = true;
	protected DalTableDao<R> client;
	private DalQueryDao queryDao;
	protected String primaryColumName;
	private Class<R> entityType;
	private DalQueryRepository queryRepository;

	public DalRepositoryImpl(Class<R> clazz) {
		try {
			this.entityType = clazz;
			initialzie(clazz);
		} catch (Exception ex) {
			throw new DalRepositoryExcetion(ex);
		}
	}

	protected void initialzie(Class<R> clazz) {
		try {
			DalParser<R> parser = new DalDefaultJpaParser<>(clazz);
			this.client = new DalTableDao<>(parser);
			this.queryDao = new DalQueryDao(parser.getDatabaseName());
			String[] names = parser.getPrimaryKeyNames();
			if (names.length > 0) {
				primaryColumName = names[0];
			}
			this.queryRepository = new DalQueryRepositoryImpl(this.queryDao, new JpaRowMapperContext());
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Query TDaoPojo by the specified ID The ID must be a number
	 **/
	@Override
	public R queryByPk(Number id, DalHints hints) {
		try {
			hints = DalHints.createIfAbsent(hints);
			return client.queryByPk(id, hints);
		} catch (SQLException ex) {
			throw new DalRepositoryExcetion(ex);
		}
	}

	/**
	 * Query TDaoPojo by TDaoPojo instance which the primary key is set
	 **/
	@Override
	public R queryByPk(R pk, DalHints hints) {
		try {
			hints = DalHints.createIfAbsent(hints);
			return client.queryByPk(pk, hints);
		} catch (SQLException ex) {
			throw new DalRepositoryExcetion(ex);
		}
	}

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used as search criteria.
	 **/
	@Override
	public List<R> queryLike(R sample, DalHints hints) {
		try {
			hints = DalHints.createIfAbsent(hints);
			return client.queryLike(sample, hints);
		} catch (SQLException ex) {
			throw new DalRepositoryExcetion(ex);
		}
	}

	/**
	 * Get the all records count
	 */
	@Override
	public int count(DalHints hints) {
		try {
			hints = DalHints.createIfAbsent(hints);
			SelectSqlBuilder builder = new SelectSqlBuilder().selectCount();
			return client.count(builder, hints).intValue();
		} catch (SQLException ex) {
			throw new DalRepositoryExcetion(ex);
		}
	}

	/**
	 * Query TDaoPojo with paging function The pageSize and pageNo must be
	 * greater than zero.
	 */
	@Override
	public List<R> queryAllByPage(int pageNo, int pageSize, DalHints hints) {
		try {
			hints = DalHints.createIfAbsent(hints);

			SelectSqlBuilder builder = new SelectSqlBuilder();
			builder.selectAll().atPage(pageNo, pageSize);
			if (primaryColumName != null) {
				builder.orderBy(primaryColumName, ASC);
			}

			return client.query(builder, hints);
		} catch (SQLException ex) {
			throw new DalRepositoryExcetion(ex);
		}
	}

	/**
	 * Get all records from table
	 */
	@Override
	public List<R> queryAll(DalHints hints) {
		try {
			hints = DalHints.createIfAbsent(hints);

			SelectSqlBuilder builder = new SelectSqlBuilder().selectAll();
			if (primaryColumName != null) {
				builder.orderBy(primaryColumName, ASC);
			}

			return client.query(builder, hints);
		} catch (SQLException ex) {
			throw new DalRepositoryExcetion(ex);
		}
	}

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
	@Override
	public int insert(DalHints hints, R daoPojo) {
		// try {
		// if (null == daoPojo)
		// return 0;
		// hints = DalHints.createIfAbsent(hints);
		// return client.insert(hints, daoPojo);
		// } catch (SQLException ex) {
		// throw new DalRepositoryExcetion(ex);
		// }
		return this.insert(hints, new KeyHolder(), daoPojo);
	}

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
	@Override
	public int[] insert(DalHints hints, List<R> daoPojos) {
		// try {
		// if (null == daoPojos || daoPojos.size() <= 0)
		// return new int[0];
		// hints = DalHints.createIfAbsent(hints);
		// return client.insert(hints, daoPojos);
		// } catch (SQLException ex) {
		// throw new DalRepositoryExcetion(ex);
		// }
		return this.insert(hints, new KeyHolder(), daoPojos);
	}

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
	@Override
	public int insert(DalHints hints, KeyHolder keyHolder, R daoPojo) {
		try {
			if (null == daoPojo)
				return 0;
			hints = DalHints.createIfAbsent(hints);
			int status = client.insert(hints, keyHolder, daoPojo);
			setPk(keyHolder, daoPojo);
			return status;
		} catch (SQLException ex) {
			throw new DalRepositoryExcetion(ex);
		} catch (ReflectiveOperationException ex) {
			LOGGER.error("KeyHolder get Key error.", ex);
			throw new DalRepositoryExcetion(ex);
		}
	}

	@Override
	public int insert(R daoPojo) {
		return this.insert(new DalHints(), daoPojo);
	}

	@Override
	public int[] insert(List<R> daoPojos) {
		return this.insert(new DalHints(), daoPojos);
	}

	@Override
	public int insert(KeyHolder keyHolder, R daoPojo) {
		return this.insert(new DalHints(), keyHolder, daoPojo);
	}

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
	@Override
	public int[] insert(DalHints hints, KeyHolder keyHolder, List<R> daoPojos) {
		try {
			if (null == daoPojos || daoPojos.size() <= 0)
				return new int[0];
			hints = DalHints.createIfAbsent(hints);
			int[] statues = client.insert(hints, keyHolder, daoPojos);
			setPks(keyHolder, daoPojos);
			return statues;
		} catch (SQLException ex) {
			throw new DalRepositoryExcetion(ex);
		} catch (ReflectiveOperationException ex) {
			LOGGER.error("KeyHolder get Key error.", ex);
			throw new DalRepositoryExcetion(ex);
		}
	}

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
	@Override
	public int[] batchInsert(DalHints hints, List<R> daoPojos) {
		try {
			if (null == daoPojos || daoPojos.size() <= 0)
				return new int[0];
			hints = DalHints.createIfAbsent(hints);
			return client.batchInsert(hints, daoPojos);
		} catch (SQLException ex) {
			throw new DalRepositoryExcetion(ex);
		}
	}

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
	@Override
	public int delete(DalHints hints, R daoPojo) {
		try {
			if (null == daoPojo)
				return 0;
			hints = DalHints.createIfAbsent(hints);
			return client.delete(hints, daoPojo);
		} catch (SQLException ex) {
			throw new DalRepositoryExcetion(ex);
		}
	}

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
	@Override
	public int[] delete(DalHints hints, List<R> daoPojos) {
		try {
			if (null == daoPojos || daoPojos.size() <= 0)
				return new int[0];
			hints = DalHints.createIfAbsent(hints);
			return client.delete(hints, daoPojos);

		} catch (SQLException ex) {
			throw new DalRepositoryExcetion(ex);
		}
	}

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
	@Override
	public int[] batchDelete(DalHints hints, List<R> daoPojos) {
		try {
			if (null == daoPojos || daoPojos.size() <= 0)
				return new int[0];
			hints = DalHints.createIfAbsent(hints);
			return client.batchDelete(hints, daoPojos);
		} catch (SQLException ex) {
			throw new DalRepositoryExcetion(ex);
		}
	}

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
	@Override
	public int update(DalHints hints, R daoPojo) {
		try {
			if (null == daoPojo)
				return 0;
			hints = DalHints.createIfAbsent(hints);
			return client.update(hints, daoPojo);
		} catch (SQLException ex) {
			throw new DalRepositoryExcetion(ex);
		}
	}

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
	@Override
	public int[] update(DalHints hints, List<R> daoPojos) {
		try {
			if (null == daoPojos || daoPojos.size() <= 0)
				return new int[0];
			hints = DalHints.createIfAbsent(hints);
			return client.update(hints, daoPojos);
		} catch (SQLException ex) {
			throw new DalRepositoryExcetion(ex);
		}
	}

	/**
	 * Update the given pojo list in batch.
	 *
	 * @return how many rows been affected
	 * @throws SQLException
	 */
	@Override
	public int[] batchUpdate(DalHints hints, List<R> daoPojos) {
		try {
			if (null == daoPojos || daoPojos.size() <= 0)
				return new int[0];
			hints = DalHints.createIfAbsent(hints);
			return client.batchUpdate(hints, daoPojos);
		} catch (SQLException ex) {
			throw new DalRepositoryExcetion(ex);
		}
	}

	@Override
	public DalTableDao<R> getDao() {
		return this.client;
	}

	@Override
	public DalQueryDao getQueryDao() {
		return this.queryDao;
	}

	@Override
	public int[] insert(KeyHolder keyHolder, List<R> daoPojos) {
		return this.insert(new DalHints(), keyHolder, daoPojos);
	}

	@Override
	public int[] batchInsert(List<R> daoPojos) {
		return this.batchInsert(new DalHints(), daoPojos);
	}

	@Override
	public int update(R daoPojo) {
		return this.update(new DalHints(), daoPojo);
	}

	@Override
	public int[] update(List<R> daoPojos) {
		return this.batchUpdate(new DalHints(), daoPojos);
	}

	@Override
	public int[] batchUpdate(List<R> daoPojos) {
		return this.batchUpdate(new DalHints(), daoPojos);
	}

	@Override
	public int delete(R daoPojo) {
		return this.delete(new DalHints(), daoPojo);
	}

	@Override
	public int[] delete(List<R> daoPojos) {
		return this.delete(new DalHints(), daoPojos);
	}

	@Override
	public int[] batchDelete(List<R> daoPojos) {
		return this.batchDelete(new DalHints(), daoPojos);
	}

	@Override
	public int count() {
		return this.count(new DalHints());
	}

	@Override
	public R queryByPk(Number id) {
		return this.queryByPk(id, new DalHints());
	}

	@Override
	public R queryByPk(R pk) {
		return this.queryByPk(pk, new DalHints());
	}

	@Override
	public List<R> queryLike(R sample) {
		return this.queryLike(sample, new DalHints());
	}

	@Override
	public List<R> queryAllByPage(int pageNo, int pageSize) {
		return this.queryAllByPage(pageNo, pageSize, new DalHints());
	}

	@Override
	public List<R> queryAll() {
		return this.queryAll(new DalHints());
	}

	@Override
	public int combinedInsert(DalHints hints, KeyHolder keyHolder, List<R> daoPojos) throws SQLException {
		try {
			hints = DalHints.createIfAbsent(hints);
			int status = this.client.combinedInsert(hints, keyHolder, daoPojos);
			setPks(keyHolder, daoPojos);
			return status;
		} catch (Exception e) {
			LOGGER.error(e);
			throw new DalRepositoryExcetion(e);
		}
	}

	@Override
	public int combinedInsert(DalHints hints, List<R> daoPojos) throws SQLException {
		return this.combinedInsert(hints, new KeyHolder(), daoPojos);
	}

	@Override
	public int combinedInsert(List<R> daoPojos) throws SQLException {
		return this.combinedInsert(new DalHints(), new KeyHolder(), daoPojos);
	}

	private void setPk(KeyHolder keyHolder, R daoPojo) throws ReflectiveOperationException, SQLException {
		EntityManager manager = EntityManager.getEntityManager(daoPojo.getClass());
		if (!manager.isAutoIncrement()) {
			return;
		}
		Field[] identities = manager.getIdentity();
		Field identity = identities != null && identities.length == 1 ? identities[0] : null;
		Number pk = keyHolder.getKey();
		if (identity != null) {
			setValue(identity, daoPojo, pk);
		}
	}

	private void setPks(KeyHolder keyHolder, List<R> results) throws ReflectiveOperationException, SQLException {
		if (results == null || results.size() == 0) {
			return;
		}

		R daoPojo = results.get(0);
		EntityManager manager = EntityManager.getEntityManager(daoPojo.getClass());
		if (!manager.isAutoIncrement()) {
			return;
		}
		Field[] identities = manager.getIdentity();
		Field identity = identities != null && identities.length == 1 ? identities[0] : null;
		if (identity == null) {
			return;
		}
		List<Number> pks = keyHolder.getIdList();
		for (int i = 0; i < pks.size(); i++) {
			setValue(identity, results.get(i), pks.get(i));
		}
	}

	private void setValue(Field field, Object entity, Object val) throws ReflectiveOperationException {
		if (val == null) {
			field.set(entity, val);
			return;
		}
		// The following order is optimized for most cases
		if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
			field.set(entity, ((Number) val).longValue());
			return;
		}
		if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
			field.set(entity, ((Number) val).intValue());
			return;
		}
		if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
			field.set(entity, ((Number) val).doubleValue());
			return;
		}
		if (field.getType().equals(Float.class) || field.getType().equals(float.class)) {
			field.set(entity, ((Number) val).floatValue());
			return;
		}
		if (field.getType().equals(Byte.class) || field.getType().equals(byte.class)) {
			field.set(entity, ((Number) val).byteValue());
			return;
		}
		if (field.getType().equals(Short.class) || field.getType().equals(short.class)) {
			field.set(entity, ((Number) val).shortValue());
			return;
		}
		field.set(entity, val);
	}

	@Override
	public List<R> query(String sql, StatementParameters parameters, DalHints hints) {
		return this.queryRepository.query(sql, parameters, this.entityType, hints);
	}

	@Override
	public int count(String sql, StatementParameters parameters, DalHints hints) {
		return this.queryRepository.count(sql, parameters, hints);
	}

	@Override
	public List<R> query(String sql, StatementParameters parameters) {
		return this.query(sql, parameters, new DalHints());
	}

	@Override
	public int count(String sql, StatementParameters parameters) {
		return this.count(sql, parameters, new DalHints());
	}

	@Override
	public R queryObject(String sql, StatementParameters parameters, DalHints hints) {
		return this.queryObject(sql, parameters, this.entityType, hints);
	}

	@Override
	public R queryObject(String sql, StatementParameters parameters) {
		return this.queryObject(sql, parameters, new DalHints());
	}

	@Override
	public List<R> query(SelectSqlBuilder builder, DalHints hints) {
		return this.query(builder.build(), builder.buildParameters(), hints);
	}

	@Override
	public List<R> query(SelectSqlBuilder builder) {
		return this.query(builder.build(), builder.buildParameters(), new DalHints());
	}

	@Override
	public <T> List<T> query(String sql, StatementParameters parameters, Class<T> entityClass, DalHints hints) {
		return this.queryRepository.query(sql, parameters, entityClass, hints);
	}

	@Override
	public <T> List<T> query(String sql, StatementParameters parameters, Class<T> entityClass) {
		return this.query(sql, parameters, entityClass, new DalHints());
	}

	@Override
	public <T> List<T> query(String sql, Map<String, Object> parameters, Class<T> entityClass, DalHints hints) {
		QuerySQL querySQL = SqlBuilder.of(sql).addParameters(parameters).build();
		return this.query(querySQL.getSql(), querySQL.getParameters(), entityClass, hints);
	}

	@Override
	public <T> List<T> query(String sql, Map<String, Object> parameters, Class<T> entityClass) {
		return this.query(sql, parameters, entityClass, new DalHints());
	}

	@Override
	public <T> T queryObject(String sql, StatementParameters parameters, Class<T> entityClass, DalHints hints) {
		return this.queryRepository.queryObject(sql, parameters, entityClass, hints);
	}

	@Override
	public <T> T queryObject(String sql, StatementParameters parameters, Class<T> entityClass) {
		return this.queryObject(sql, parameters, entityClass, new DalHints());
	}

	@Override
	public <T> T queryObject(String sql, Map<String, Object> parameters, Class<T> entityClass, DalHints hints) {
		return this.queryRepository.queryObject(sql, parameters, entityClass, hints);
	}

	@Override
	public <T> T queryObject(String sql, Map<String, Object> parameters, Class<T> entityClass) {
		return this.queryObject(sql, parameters, entityClass, new DalHints());
	}

	@Override
	public int count(SelectSqlBuilder builder, DalHints hints) {
		return this.count(builder.selectCount().build(), builder.buildParameters(), hints);
	}

	@Override
	public int count(SelectSqlBuilder builder) {
		return this.count(builder, new DalHints());
	}

	@Override
	public int count(String sql, Map<String, Object> parameters, DalHints hints) {
		return this.queryRepository.count(sql, parameters, hints);
	}

	@Override
	public int count(String sql, Map<String, Object> parameters) {
		return this.count(sql, parameters, new DalHints());
	}

	@Override
	public <T> List<T> fetchQuery(String sql, StatementParameters parameters, Class<T> entityClass, DalHints hints,
			DatabaseCategory category, int batchSize, long intervalMills) {
		return this.queryRepository.fetchQuery(sql, parameters, entityClass, hints, category, batchSize, intervalMills);
	}

	@Override
	public void setDalRowMapperContext(DalRowMapperContext context) {
		this.queryRepository.setDalRowMapperContext(context);
	}

	@Override
	public <T> List<T> query(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> dalRowMapper) {
		return this.queryRepository.query(sql, parameters, hints, dalRowMapper);
	}

	@Override
	public <T> T queryObject(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> dalRowMapper) {
		return this.queryRepository.queryObject(sql, parameters, hints, dalRowMapper);
	}

	@Override
	public <T> List<T> fetchQuery(String sql, StatementParameters parameters, DalHints hints,
			DalRowMapper<T> dalRowMapper, DatabaseCategory category, int batchSize, long intervalMills) {
		return this.queryRepository.fetchQuery(sql, parameters, hints, dalRowMapper, category, batchSize,
				intervalMills);
	}

	@Override
	public <T> List<T> fetchQuery(String sql, StatementParameters parameters, Class<T> entityClass, DalHints hints,
			int batchSize, long intervalMills) {
		return this.queryRepository.fetchQuery(sql, parameters, entityClass, hints, batchSize, intervalMills);
	}

	@Override
	public <T> List<T> fetchQuery(String sql, StatementParameters parameters, DalHints hints,
			DalRowMapper<T> dalRowMapper, int batchSize, long intervalMills) {
		return this.queryRepository.fetchQuery(sql, parameters, hints, dalRowMapper, batchSize, intervalMills);
	}

	@Override
	public int update(FreeUpdateSqlBuilder builder, DalHints hints) {
		return this.queryRepository.update(builder, hints);
	}

	@Override
	public int update(FreeUpdateSqlBuilder builder) {
		return this.queryRepository.update(builder);
	}

	@Override
	public int update(String sql, StatementParameters parameters, DalHints hints) {
		return this.queryRepository.update(sql , parameters , hints);
	}

	@Override
	public int update(String sql, StatementParameters parameters) {
		return this.queryRepository.update(sql , parameters);
	}

	@Override
	public int update(String sql, Map<String, Object> parameters, DalHints hints) {
		return this.queryRepository.update(sql , parameters , hints);
	}

	@Override
	public int update(String sql, Map<String, Object> parameters) {
		return this.queryRepository.update(sql , parameters);
	}

}
