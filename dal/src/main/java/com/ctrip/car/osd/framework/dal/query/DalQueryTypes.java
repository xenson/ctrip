package com.ctrip.car.osd.framework.dal.query;

import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DalQueryTypes {

	private static final Map<Class<?>, Integer> javaTypeToJdbcTypes = new HashMap<Class<?>, Integer>();
	private static final Set<Class<?>> primitives = new HashSet<>();

	static {
		javaTypeToJdbcTypes.put(Boolean.class, java.sql.Types.BIT);
		javaTypeToJdbcTypes.put(Byte.class, java.sql.Types.TINYINT);
		javaTypeToJdbcTypes.put(Integer.class, java.sql.Types.INTEGER);
		javaTypeToJdbcTypes.put(Long.class, java.sql.Types.BIGINT);
		javaTypeToJdbcTypes.put(Float.class, java.sql.Types.FLOAT);
		javaTypeToJdbcTypes.put(Double.class, java.sql.Types.DOUBLE);
		javaTypeToJdbcTypes.put(String.class, java.sql.Types.VARCHAR);
		javaTypeToJdbcTypes.put(Date.class, java.sql.Types.DATE);

		primitives.add(Boolean.class);
		primitives.add(Byte.class);
		primitives.add(Character.class);
		primitives.add(Short.class);
		primitives.add(Integer.class);
		primitives.add(Long.class);
		primitives.add(Double.class);
		primitives.add(Float.class);
		primitives.add(String.class);
	}

	public static Integer geJdbcType(Class<?> clazz) {
		if (javaTypeToJdbcTypes.containsKey(clazz)) {
			return javaTypeToJdbcTypes.get(clazz);
		}
		return Types.NULL;
	}

	public static boolean isPrimitiveType(Class<?> type) {
		return primitives.contains(type);
	}

}
