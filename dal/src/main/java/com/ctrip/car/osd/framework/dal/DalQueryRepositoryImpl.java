package com.ctrip.car.osd.framework.dal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.lang3.StringUtils;

import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.framework.dal.builder.SqlBuilder;
import com.ctrip.car.osd.framework.dal.builder.SqlBuilder.QuerySQL;
import com.ctrip.car.osd.framework.dal.builder.SqlPaginationBuilder;
import com.ctrip.car.osd.framework.dal.mapper.JpaRowMapperContext;
import com.ctrip.car.osd.framework.dal.mapper.ObjectRowMapperContext;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;

public class DalQueryRepositoryImpl implements DalQueryRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(DalQueryRepositoryImpl.class);
	private DalQueryDao queryDao;
	private DalRowMapperContext context;
	private DatabaseCategory category;

	public DalQueryRepositoryImpl(Class<?> clazz) {
		this(clazz, new ObjectRowMapperContext());
	}

	public DalQueryRepositoryImpl(String databaseName) {
		this(databaseName, new ObjectRowMapperContext());
	}

	public DalQueryRepositoryImpl(Class<?> clazz, DalRowMapperContext context) {
		initialzie(clazz, context);
	}

	public DalQueryRepositoryImpl(String databaseName, DalRowMapperContext context) {
		initialzie(databaseName, context);
	}

	public DalQueryRepositoryImpl(DalQueryDao queryDao, DalRowMapperContext context) {
		super();
		this.queryDao = queryDao;
		this.context = context;
		this.initCategory();
	}

	protected void initialzie(Class<?> clazz, DalRowMapperContext context) {
		if (!clazz.isAnnotationPresent(Database.class)) {
			throw new IllegalArgumentException("Not Found @Database in " + clazz.getName());
		}
		Database database = clazz.getAnnotation(Database.class);
		if (StringUtils.isBlank(database.name())) {
			throw new IllegalArgumentException(clazz.getName() + " @Database name is blank .");
		}
		this.initialzie(database.name(), context);
	}

	protected void initialzie(String databaseName, DalRowMapperContext context) {
		this.queryDao = new DalQueryDao(databaseName);
		this.context = context;
		this.category = DalClientFactory.getDalConfigure().getDatabaseSet(databaseName).getDatabaseCategory();
	}

	private void initCategory() {
		try {
			String dbName = (String)FieldUtils.getField(DalQueryDao.class, "logicDbName",true).get(this.queryDao);
			this.category = DalClientFactory.getDalConfigure().getDatabaseSet(dbName).getDatabaseCategory();
		} catch (Exception ex) {
			LOGGER.error(" Init DB Category Error.", ex);
		}
	}

	@Override
	public int count(String sql, StatementParameters parameters, DalHints hints) {
		return this.executeCount(sql, parameters, hints);
	}

	@Override
	public int count(String sql, StatementParameters parameters) {
		return this.count(sql, parameters, new DalHints());
	}

	@Override
	public <T> List<T> query(String sql, StatementParameters parameters, Class<T> entityClass, DalHints hints) {
		return this.executeQuery(sql, parameters, entityClass, hints);
	}

	@Override
	public <T> List<T> query(String sql, StatementParameters parameters, Class<T> entityClass) {
		return this.executeQuery(sql, parameters, entityClass, new DalHints());
	}

	@Override
	public <T> List<T> query(String sql, Map<String, Object> parameters, Class<T> entityClass, DalHints hints) {
		QuerySQL querySQL = SqlBuilder.of(sql).addParameters(parameters).build();
		return this.executeQuery(querySQL.getSql(), querySQL.getParameters(), entityClass, hints);
	}

	@Override
	public <T> List<T> query(String sql, Map<String, Object> parameters, Class<T> entityClass) {
		return this.query(sql, parameters, entityClass, new DalHints());
	}

	@Override
	public <T> T queryObject(String sql, StatementParameters parameters, Class<T> entityClass, DalHints hints) {
		return this.executeQueryObject(sql, parameters, entityClass, hints);
	}

	@Override
	public <T> T queryObject(String sql, StatementParameters parameters, Class<T> entityClass) {
		return this.executeQueryObject(sql, parameters, entityClass, new DalHints());
	}

	@Override
	public <T> T queryObject(String sql, Map<String, Object> parameters, Class<T> entityClass, DalHints hints) {
		QuerySQL querySQL = SqlBuilder.of(sql).addParameters(parameters).build();
		return this.executeQueryObject(querySQL.getSql(), querySQL.getParameters(), entityClass, hints);
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
		QuerySQL querySQL = SqlBuilder.of(sql).addParameters(parameters).build();
		return this.executeCount(querySQL.getSql(), querySQL.getParameters(), hints);
	}

	@Override
	public int count(String sql, Map<String, Object> parameters) {
		return this.count(sql, parameters, new DalHints());
	}

	private <T> List<T> executeQuery(String sql, StatementParameters parameters, Class<T> entityClass, DalHints hints) {
		return this.executeQuery(sql, parameters, hints, getDalRowMapper(entityClass));
	}

	private <T> List<T> executeQuery(String sql, StatementParameters parameters, DalHints hints,
			DalRowMapper<T> rowMapper) {
		try {
			return this.queryDao.query(sql, parameters, hints, rowMapper);
		} catch (SQLException e) {
			LOGGER.error(String.format("dal query error , sql [%s]", sql), e);
			return new ArrayList<>();
		}
	}

	private int executeUpdate(FreeUpdateSqlBuilder builder, DalHints hints) {
		try {
			LOGGER.info(builder.build());
			LOGGER.info(builder.buildParameters().toLogString());
			return this.queryDao.update(builder, builder.buildParameters(), hints);
		} catch (SQLException e) {
			LOGGER.error(String.format("dal query error , sql [%s]", builder.build()), e);
			return 0;
		}
	}

	private int executeUpdate(String sql, StatementParameters parameters, DalHints hints) {
		try {
			LOGGER.info(sql);
			LOGGER.info(parameters.toLogString());
			return this.queryDao.getClient().update(sql, parameters, hints);
		} catch (SQLException e) {
			LOGGER.error(String.format("dal query error , sql [%s]", sql), e);
			return 0;
		}
	}

	private <T> T executeQueryObject(String sql, StatementParameters parameters, Class<T> entityClass, DalHints hints) {
		return this.executeQueryObject(sql, parameters, hints, getDalRowMapper(entityClass));
	}

	private <T> T executeQueryObject(String sql, StatementParameters parameters, DalHints hints,
			DalRowMapper<T> rowMapper) {
		try {
			return this.queryDao.queryFirst(sql, parameters, hints, rowMapper);
		} catch (SQLException e) {
			LOGGER.error(String.format("dal query object error , sql [%s]", sql), e);
			return null;
		}
	}

	private int executeCount(String sql, StatementParameters parameters, DalHints hints) {
		try {
			if (!isSelectCountSql(sql)) {
				sql = getCountString(sql);
			}
			Long count = this.queryDao.queryFirst(sql, parameters, hints, new DalRowMapper<Long>() {
				@Override
				public Long map(ResultSet rs, int rowNum) throws SQLException {
					return rs.getLong(1);
				}
			});
			return count == null ? 0 : count.intValue();
		} catch (SQLException e) {
			LOGGER.error(String.format("dal query error , sql [%s]", sql), e);
			return 0;
		}
	}

	private String getCountString(String querySqlString) {
		String sql = removeOrders(querySqlString);
		return "select count(1) from (" + sql + ") as tmp_count";
	}

	private static final String ORDER_BY_REGEX = "order\\s*by[\\w|\\W|\\s|\\S]*";
	private static final String SELECT_COUNT_REGEX = "select\\s*count[(][\\w|\\W|\\s|\\S]*[)]";

	private String removeOrders(String sql) {
		Pattern p = Pattern.compile(ORDER_BY_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(sql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private boolean isSelectCountSql(String querySqlString) {
		Pattern p = Pattern.compile(SELECT_COUNT_REGEX, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(querySqlString);
		return m.find();
	}

	@Override
	public void setDalRowMapperContext(DalRowMapperContext context) {
		this.context = context;
	}

	private <T> DalRowMapper<T> getDalRowMapper(Class<T> entityClass) {
		if (context == null) {
			this.context = new JpaRowMapperContext();
		}
		return this.context.getRowMapper(entityClass);
	}

	@Override
	public <T> List<T> query(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> dalRowMapper) {
		return this.executeQuery(sql, parameters, hints, dalRowMapper);
	}

	@Override
	public <T> T queryObject(String sql, StatementParameters parameters, DalHints hints, DalRowMapper<T> dalRowMapper) {
		return this.executeQueryObject(sql, parameters, hints, dalRowMapper);
	}

	@Override
	public <T> List<T> fetchQuery(String sql, StatementParameters parameters, Class<T> entityClass, DalHints hints,
			DatabaseCategory category, int batchSize, long intervalMills) {
		return this.fetchQuery(sql, parameters, hints, getDalRowMapper(entityClass), category, batchSize,
				intervalMills);
	}

	@Override
	public <T> List<T> fetchQuery(String sql, StatementParameters parameters, DalHints hints,
			DalRowMapper<T> dalRowMapper, DatabaseCategory category, int batchSize, long intervalMills) {
		int total = count(sql, parameters, hints);
		int times = total / batchSize + (total % batchSize == 0 ? 0 : 1);

		SqlPaginationBuilder paginationBuilder = new SqlPaginationBuilder(sql, parameters, category);
		String paginationSql = paginationBuilder.build();

		List<T> results = new ArrayList<>();
		for (int i = 0; i < times; i++) {
			int start = i * batchSize;
			List<T> result2 = this.query(paginationSql, paginationBuilder.buildParameters(start, batchSize), hints,
					dalRowMapper);
			results.addAll(result2);
			try {
				Thread.sleep(intervalMills);
			} catch (InterruptedException e) {
				LOGGER.error(e);
			}
		}
		return results;
	}
	
	@Override
	public <T> List<T> fetchQuery(String sql, StatementParameters parameters, Class<T> entityClass, DalHints hints,
			int batchSize, long intervalMills) {
		return this.fetchQuery(sql, parameters, hints, getDalRowMapper(entityClass), category, batchSize,
				intervalMills);
	}

	@Override
	public <T> List<T> fetchQuery(String sql, StatementParameters parameters, DalHints hints,
			DalRowMapper<T> dalRowMapper, int batchSize, long intervalMills) {
		return this.fetchQuery(sql, parameters, hints, dalRowMapper , category, batchSize,
				intervalMills);
	}

	@Override
	public int update(FreeUpdateSqlBuilder builder, DalHints hints) {
		return this.executeUpdate(builder, hints);
	}

	@Override
	public int update(FreeUpdateSqlBuilder builder) {
		return this.executeUpdate(builder, new DalHints());
	}

	@Override
	public int update(String sql, StatementParameters parameters, DalHints hints) {
		return this.executeUpdate(sql, parameters, hints);
	}

	@Override
	public int update(String sql, StatementParameters parameters) {
		return this.executeUpdate(sql, parameters, new DalHints());
	}

	@Override
	public int update(String sql, Map<String, Object> parameters, DalHints hints) {
		QuerySQL querySQL = SqlBuilder.of(sql).addParameters(parameters).build();
		return this.executeUpdate(querySQL.getSql(), querySQL.getParameters(), hints);
	}

	@Override
	public int update(String sql, Map<String, Object> parameters) {
		QuerySQL querySQL = SqlBuilder.of(sql).addParameters(parameters).build();
		return this.executeUpdate(querySQL.getSql(), querySQL.getParameters(), new DalHints());
	}


}
