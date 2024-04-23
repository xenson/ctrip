package com.ctrip.car.osd.framework.common.utils;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;

public class TypeUtils {

	public static Class<?> getGenericType(Class<?> clazz, Type type) {
		return TypeToken.of(clazz).resolveType(type).getRawType();
	}

	public static Class<?> getGenericType(Class<?> clazz, Class<?> resolveType) {
		return getGenericType(clazz, resolveType, 0);
	}

	public static Class<?> getGenericType(Class<?> clazz, Class<?> resolveType, int index) {
		return TypeToken.of(clazz).resolveType(resolveType.getTypeParameters()[index]).getRawType();
	}

}
