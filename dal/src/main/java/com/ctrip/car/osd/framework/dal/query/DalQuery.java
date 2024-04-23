package com.ctrip.car.osd.framework.dal.query;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.framework.dal.builder.SqlBuilder;
import com.ctrip.car.osd.framework.dal.builder.SqlBuilder.QuerySQL;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeUpdateSqlBuilder;
import com.google.common.reflect.TypeToken;

public class DalQuery {

	private static final Logger LOGGER = LoggerFactory.getLogger("DAL SQL");
	private Map<String, DalQueryDao> query;
	private Map<Method, QueryMethodDescriptor> descriptors;

	public DalQuery() {
		super();
		this.query = new ConcurrentHashMap<>();
		this.descriptors = new ConcurrentHashMap<>();
	}

	private DalQueryDao getQueryDao(String dbname) {
		if (!query.containsKey(dbname)) {
			query.put(dbname, new DalQueryDao(dbname));
		}
		return query.get(dbname);
	}

	public Object query(Method method, List<Object> values, DalHints dalHints) throws SQLException {
		dalHints = dalHints == null ? new DalHints() : dalHints;
		QueryMethodDescriptor descriptor = getMethodDescriptor(method);
		String sql = descriptor.isMethodArgSql() ? (String) values.get(descriptor.getSqlIndex()) : descriptor.getSql();
		if( descriptor.isUpdate()) {
			return executeUpdate(descriptor, sql, values, dalHints);
		}
		return executeQuery(descriptor, sql , values, dalHints);
	}


	private Class<?> getListEntityType(Type returnType) {
		return TypeToken.of(returnType).resolveType(List.class.getTypeParameters()[0]).getRawType();
	}	
	
	private <T> T executeQuery(QueryMethodDescriptor descriptor, 
			String sql , 
			List<Object> values, 
			DalHints hints) throws SQLException{
		
		String templateSql;
		StatementParameters parameters ;
		hints = DalHints.createIfAbsent(hints);
		DalQueryDao dao = getQueryDao(descriptor.getDbName());
		
		if( descriptor.hasMapParameter()) {
			QuerySQL querySQL = SqlBuilder.of(sql)
					.addParameters(descriptor.buildMapParameters(values) )
					.build();
			templateSql = querySQL.getSql();
			parameters = querySQL.getParameters(); 
		} else {
			templateSql = sql;
			parameters = this.buildParameters(descriptor, values);
		}
		
		FreeSelectSqlBuilder<T> builder = new FreeSelectSqlBuilder<>(descriptor.getDbType());
		builder.setTemplate(templateSql);
		if (DalQueryTypes.isPrimitiveType(descriptor.getEntityType())) {
			builder.simpleType();
		} else {
			DalRowMapper<?> rowMapper = new DalDefaultJpaMapper<>(descriptor.getEntityType());
			builder.mapWith(rowMapper);
		}
		if( !descriptor.isArray()) {
			builder.requireFirst().nullable();
		}
		
		LOGGER.info(templateSql );
		LOGGER.info(parameters.toLogString());
		
		return  (T) dao.query(builder, parameters, hints);
	}
	
	
	private int executeUpdate(QueryMethodDescriptor descriptor, 
			String sql , 
			List<Object> values, 
			DalHints hints) throws SQLException{
		
		String templateSql;
		StatementParameters parameters ;
		hints = DalHints.createIfAbsent(hints);
		DalQueryDao dao = getQueryDao(descriptor.getDbName());
		
		if( descriptor.hasMapParameter()) {
			QuerySQL querySQL = SqlBuilder.of(sql)
					.addParameters(descriptor.buildMapParameters(values) )
					.build();
			templateSql = querySQL.getSql();
			parameters = querySQL.getParameters(); 
		} else {
			templateSql = sql;
			parameters = this.buildParameters(descriptor, values);
		}
		
		FreeUpdateSqlBuilder builder = new FreeUpdateSqlBuilder(descriptor.getDbType());
		builder.setTemplate(templateSql);
		
		LOGGER.info(templateSql);
		LOGGER.info(parameters.toLogString());
		
		return dao.update(builder, parameters, hints);
	}
	
	
	

	private StatementParameters buildParameters(QueryMethodDescriptor descriptor, List<Object> values) {
		StatementParameters parameters = new StatementParameters();
		int index = 0;
		for (int i = 0; i < values.size(); i++) {
			Object value = values.get(i);
			if (value == null || descriptor.getSqlIndex() == i || descriptor.getHitsIndex() == i) {
				continue;
			}
			index++;
			if (value instanceof List) {
				List arguments = (List) value;
				if (arguments != null && !arguments.isEmpty()) {
					Integer type = DalQueryTypes.geJdbcType(arguments.get(0).getClass());
					parameters.setSensitiveInParameter(index, type, arguments);
				}
			} else {
				Integer type = DalQueryTypes.geJdbcType(value.getClass());
				parameters.setSensitive(index, type, value);
			}
		}
		return parameters;
	}

	private QueryMethodDescriptor getMethodDescriptor(Method method) {
		if (!this.descriptors.containsKey(method)) {
			this.descriptors.put(method, new QueryMethodDescriptor(method));
		}
		return this.descriptors.get(method);
	}

	class QueryMethodDescriptor {
		String sql;
		Method method;
		Class<?> entityType;
		String dbName;
		DatabaseCategory dbType;
		boolean array;
		int hitsIndex = -1;
		int sqlIndex = -1;
		Map<Integer , String> arguments;
		boolean update;

		public QueryMethodDescriptor(Method method) {
			super();
			this.arguments = new HashMap<>();
			this.method = method;
			buildDescriptor(method);
		}

		private void checkArgsIndex(Method method) {
			Annotation[][] parameterAnnotations = method.getParameterAnnotations();
			for (int i = 0; i < parameterAnnotations.length; i++) {
				Annotation[] annotations = parameterAnnotations[i];
				if (annotations.length > 0) {
					if (annotations[0].annotationType().equals(Sql.class)) {
						sqlIndex = i;
					} else if( annotations[0].annotationType().equals(SqlParam.class) ) {
						SqlParam sqlParam = (SqlParam) annotations[0];
						arguments.put(i , sqlParam.value());
					}
				}
			}

			Class<?>[] args = method.getParameterTypes();
			for (int i = 0; i < args.length; i++) {
				if (DalHints.class.equals(args[i])) {
					hitsIndex = i;
				}
			}
		}
		
		
		private void checkMethodUpdate(Method method) {
			update =  method.getName().startsWith("update");
			
			if( method.getName().length() > 6 ) {
				update = "update".equalsIgnoreCase(method.getName().substring(0, 6));
			} 
		}
		
		// next cache the method.
		private void buildDescriptor(Method method) {

			this.checkArgsIndex(method);

			if (sqlIndex == -1 && !method.isAnnotationPresent(Sql.class)) {
				throw new IllegalArgumentException("Please checked the method add @Sql annotation .");
			}

			Dal dal = null;
			if (method.isAnnotationPresent(Dal.class)) {
				dal = method.getAnnotation(Dal.class);
			} else if (method.getDeclaringClass().isAnnotationPresent(Dal.class)) {
				dal = method.getDeclaringClass().getAnnotation(Dal.class);
			}

			if (dal == null) {
				throw new IllegalArgumentException("Please checked the method add @Dal annotation .");
			}
			dbName = dal.value();
			dbType = dal.type();
			if (sqlIndex == -1) {
				sql = method.getAnnotation(Sql.class).value();
			}
			entityType = method.getReturnType();
			if (entityType.equals(List.class)) {
				array = true;
				entityType = getListEntityType(method.getGenericReturnType());
			}
			
			this.checkMethodUpdate(method);
		}

		public String getSql() {
			return sql;
		}

		public boolean isMethodArgSql() {
			return sqlIndex > -1;
		}

		public boolean isMethodArgHits() {
			return hitsIndex > -1;
		}

		public Method getMethod() {
			return method;
		}

		public Class<?> getEntityType() {
			return entityType;
		}

		public boolean isArray() {
			return array;
		}

		public String getDbName() {
			return dbName;
		}

		public DatabaseCategory getDbType() {
			return dbType;
		}

		public int getHitsIndex() {
			return hitsIndex;
		}

		public int getSqlIndex() {
			return sqlIndex;
		}
		
		public boolean isUpdate() {
			return update;
		}

		public boolean hasMapParameter(){
			return this.arguments != null && !this.arguments.isEmpty();
		}
		
		public Map<String, Object> buildMapParameters(List<Object> values){
			Map<String, Object> results = new HashMap<>();
			for(Entry<Integer, String> entry : this.arguments.entrySet()) {
				results.put(entry.getValue(), values.get(entry.getKey()));
			}
			return results;
		}

	}

}
