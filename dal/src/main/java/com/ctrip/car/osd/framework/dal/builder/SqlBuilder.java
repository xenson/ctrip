package com.ctrip.car.osd.framework.dal.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ctrip.car.osd.framework.dal.query.AbstractSQL;
import com.ctrip.car.osd.framework.dal.query.DalQueryTypes;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;

public class SqlBuilder extends AbstractSQL<SqlBuilder> {

	private static Pattern pattern = Pattern.compile("(?<=\\{)[^}]*(?=\\})");
	private String sql;
	private Map<String, Object> parameters;

	@Override
	public SqlBuilder getSelf() {
		return this;
	}

	public SqlBuilder() {
		this.parameters = new HashMap<>();
	}

	public static SqlBuilder of() {
		SqlBuilder builder = new SqlBuilder();
		return builder;
	}

	public static SqlBuilder of(String sql) {
		SqlBuilder builder = new SqlBuilder();
		builder.sql(sql);
		return builder;
	}

	public SqlBuilder sql(String sql) {
		this.sql = sql;
		return this;
	}

	public SqlBuilder addParameter(String name, Object value) {
		this.parameters.put(name, value);
		return this;
	}

	public SqlBuilder addParameters(Map<String, Object> values) {
		this.parameters.putAll(values);
		return this;
	}

	public String buildSql() {
		if (this.sql == null || this.sql.isEmpty()) {
			return super.toString();
		}
		return this.sql;
	}

	public QuerySQL build() {
		String originSql = this.buildSql();
		Matcher matcher = pattern.matcher(originSql);
		StatementParameters parameters = new StatementParameters();
		String buildSql = originSql;
		int index = 1;
		while (matcher.find()) {
			String name = matcher.group();
			buildSql = buildSql.replace("{" + name + "}", "?");
			StatementParameter parameter = this.buildStatementParameter(index, name);
			if (parameter != null) {
				parameters.add(parameter);
			}
			index++;
		}
		return new QuerySQL(originSql, buildSql, parameters);
	}

	private StatementParameter buildStatementParameter(int index, String name) {
		if (!this.parameters.containsKey(name)) {
			return null;
		}
		Object value = this.parameters.get(name);
		if (value instanceof List) {
			List arguments = (List) value;
			if (arguments != null && !arguments.isEmpty()) {
				Integer sqlType = DalQueryTypes.geJdbcType(arguments.get(0).getClass());
				return new StatementParameter(index, sqlType, arguments).setName(name).setSensitive(true)
						.setInParam(true);
			}
			return null;
		} else {
			Integer sqlType = DalQueryTypes.geJdbcType(value.getClass());
			return new StatementParameter(index, sqlType, value).setSensitive(true);
		}
	}

	public class QuerySQL {

		private String originSql;
		private String sql;
		private StatementParameters parameters;

		public QuerySQL(String originSql, String sql, StatementParameters parameters) {
			super();
			this.originSql = originSql;
			this.sql = sql;
			this.parameters = parameters;
		}

		public String getOriginSql() {
			return originSql;
		}

		public String getSql() {
			return sql;
		}

		public StatementParameters getParameters() {
			return parameters;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(sql).append("\n\n");

			for (StatementParameter parameter : getParameters().values()) {
				sb.append(parameter.getIndex())
						.append(",")
						.append(parameter.getName())
						.append(",")
						.append(parameter.getValue().toString())
						.append("\n");
			}
			return sb.append("\n\n").toString();
		}
	}

	public static void main(String[] args) {
		// String sql = "select * from order where name = #{name} AND code =
		// #{code} AND alis = #{name}";
		// // Pattern.matches("/\{[^\}]+\\}/", sql);
		// // re.search('^\[[^\]]+]$', '[abcdefg]')
		// Pattern pattern = Pattern.compile("(?<=#\\{)[^}]*(?=\\})");
		// Matcher matcher = pattern.matcher(sql);
		// while (matcher.find()) {
		// System.out.println(matcher.group(0));
		// System.out.println(matcher.group());
		// }

		// String sql2 =
		// SqlBuilder.of().SELECT("*").FROM("tts_orders").WHERE("name =
		// #{name}").toString();

		// System.out.println(sql2);

		QuerySQL querySql = SqlBuilder.of().SELECT("*").FROM("tts_orders").WHERE("name = {name}")
				.WHERE("code = {code}").WHERE("alis = {name}").addParameter("name", "aaa").addParameter("code", "bbb")
				.build();

		System.out.println(querySql);
		
	}
	

}
