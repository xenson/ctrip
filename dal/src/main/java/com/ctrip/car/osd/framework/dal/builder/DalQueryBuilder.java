package com.ctrip.car.osd.framework.dal.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import com.ctrip.car.osd.framework.dal.DalQueryRepository;
import com.ctrip.car.osd.framework.dal.DalQueryRepositoryImpl;
import com.ctrip.car.osd.framework.dal.builder.SqlBuilder.QuerySQL;
import com.ctrip.car.osd.framework.dal.mapper.ObjectRowMapperContext;
import com.ctrip.car.osd.framework.dal.query.AbstractSQL;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DalQueryBuilder extends AbstractSQL<DalQueryBuilder> {

	private DalQueryRepository dalQueryRepository;
	private SqlBuilder sqlBuilder;
	private int start = -1;
	private int limit = -1;
	private DalHints hints;
	private Class entityClass;
	private DalRowMapper dalRowMapper;
	private DatabaseCategory database;

	public DalQueryBuilder(DalQueryRepository dalQueryRepository) {
		super();
		this.dalQueryRepository = dalQueryRepository;
		this.sqlBuilder = new SqlBuilder();
	}

	public DalQueryBuilder sql(String sql) {
		this.sqlBuilder.sql(sql);
		return getSelf();
	}

	public DalQueryBuilder addParameter(String name, Object value) {
		this.sqlBuilder.addParameter(name, value);
		return getSelf();
	}

	public DalQueryBuilder addParameters(Map<String, Object> values) {
		this.sqlBuilder.addParameters(values);
		return getSelf();
	}

	public DalQueryBuilder setPagination(int start, int limit) {
		this.start = start;
		this.limit = limit;
		return getSelf();
	}

	public DalQueryBuilder setHints(DalHints hints) {
		this.hints = hints;
		return this;
	}

	public DalQueryBuilder setEntityClass(Class entityClass) {
		this.entityClass = entityClass;
		return this;
	}

	public DalQueryBuilder setDatabase(DatabaseCategory database) {
		this.database = database;
		return this;
	}

	public DalQueryBuilder setDalRowMapper(DalRowMapper dalRowMapper) {
		this.dalRowMapper = dalRowMapper;
		return this;
	}

	private String buildSql() {
		String sql = this.sqlBuilder.buildSql();
		if (sql == null || sql.isEmpty()) {
			return super.toString();
		}
		return sql;
	}

	private QuerySQL build() {
		this.sqlBuilder.sql(this.buildSql());
		return this.sqlBuilder.build();
	}

	/**
	 * 普通查询
	 * @return
	 */
	public <T> List<T> query() {
		Assert.isTrue(!(this.entityClass == null && dalRowMapper == null),
				"The entityClass Or rowMapper be required .");
		DalHints hints = this.hints;
		if (hints == null) {
			hints = new DalHints();
		}

		QuerySQL querySQL = this.build();
		if (this.dalRowMapper == null) {
			return this.dalQueryRepository.query(querySQL.getSql(), querySQL.getParameters(), this.entityClass, hints);
		} else {
			return this.dalQueryRepository.query(querySQL.getSql(), querySQL.getParameters(), hints, this.dalRowMapper);
		}
	}

	/**
	 * 分页查询
	 * @return
	 */
	public <T> List<T> paginationQuery() {

		Assert.isTrue(!(this.entityClass == null && dalRowMapper == null),
				"The entityClass Or rowMapper be required .");

		if (this.start < 0 || this.limit <= 0) {
			return new ArrayList<>();
		}

		DalHints hints = this.hints;
		if (hints == null) {
			hints = new DalHints();
		}

		DatabaseCategory database = this.database;
		if (database == null) {
			database = DatabaseCategory.MySql;
		}

		QuerySQL querySQL = this.build();
		SqlPaginationBuilder paginationBuilder = new SqlPaginationBuilder(querySQL.getSql(), querySQL.getParameters(),
				database);

		if (this.dalRowMapper == null) {
			return this.dalQueryRepository.query(paginationBuilder.build(),
					paginationBuilder.buildParameters(start, limit), this.entityClass, hints);
		} else {
			return this.dalQueryRepository.query(paginationBuilder.build(),
					paginationBuilder.buildParameters(start, limit), hints, dalRowMapper);
		}
	}

	/**
	 * 延迟查询
	 * @param batchSize
	 * @param intervalMills
	 * @return
	 */
	public <T> List<T> fetchQuery(int batchSize, long intervalMills) {
		Assert.isTrue(!(this.entityClass == null && dalRowMapper == null),
				"The entityClass Or rowMapper be required .");
		DalHints hints = this.hints;
		if (hints == null) {
			hints = new DalHints();
		}

		DatabaseCategory database = this.database;
		if (database == null) {
			database = DatabaseCategory.MySql;
		}
		
		QuerySQL querySQL = this.build();
		if (this.dalRowMapper == null) {
			return this.dalQueryRepository.fetchQuery(querySQL.getSql(), querySQL.getParameters(), entityClass, hints,
					database, batchSize, intervalMills);
		} else {
			return this.dalQueryRepository.fetchQuery(querySQL.getSql(), querySQL.getParameters(), hints, dalRowMapper,
					database, batchSize, intervalMills);
		}
	}

	/**
	 * 获取总数
	 * @return
	 */
	public int count() {
		DalHints hints = this.hints;
		if (hints == null) {
			hints = new DalHints();
		}
		QuerySQL querySQL = build();
		return this.dalQueryRepository.count(querySQL.getSql(), querySQL.getParameters() , hints);
	}
	
	/**
	 * 获取总数
	 * @return
	 */
	public int update() {
		DalHints hints = this.hints;
		if (hints == null) {
			hints = new DalHints();
		}
		QuerySQL querySQL = build();
		return this.dalQueryRepository.update(querySQL.getSql(), querySQL.getParameters() , hints);
	}

	@Override
	public DalQueryBuilder getSelf() {
		return this;
	}

	public static DalQueryBuilder create(DalQueryRepository dalQueryRepository) {
		return new DalQueryBuilder(dalQueryRepository);
	}
	
	public static DalQueryBuilder newQuery(String databaseName) {
		return new DalQueryBuilder(new DalQueryRepositoryImpl(databaseName, new ObjectRowMapperContext()));
	}
	
	public static DalQueryBuilder newQuery(Class<?> clazz) {
		return new DalQueryBuilder(new DalQueryRepositoryImpl(clazz, new ObjectRowMapperContext()));
	}

}
