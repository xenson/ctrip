package com.ctrip.car.osd.framework.common.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Maps2 {

	/**
	 * Returns the empty map. This map behaves and performs comparably to
	 * {@link Collections#emptyMap}, and is preferable mainly for consistency
	 * and maintainability of your code.
	 */
	public static <K, V> Map<K, V> of() {
		return new HashMap<>();
	}

	/**
	 * Returns an immutable map containing a single entry. This map behaves and
	 * performs comparably to {@link Collections#singletonMap} but will not
	 * accept a null key or value. It is preferable mainly for consistency and
	 * maintainability of your code.
	 */
	public static <K, V> Map<K, V> of(K k1, V v1) {
		return put(of(), k1, v1);
	}

	/**
	 * Returns an immutable map containing the given entries, in order.
	 *
	 * @throws IllegalArgumentException
	 *             if duplicate keys are provided
	 */
	public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
		Map<K, V> map = of();
		put(map, k1, v1);
		put(map, k2, v2);
		return map;
	}

	/**
	 * Returns an immutable map containing the given entries, in order.
	 *
	 * @throws IllegalArgumentException
	 *             if duplicate keys are provided
	 */
	public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
		Map<K, V> map = of();
		put(map, k1, v1);
		put(map, k2, v2);
		put(map, k3, v3);
		return map;
	}

	/**
	 * Returns an immutable map containing the given entries, in order.
	 *
	 * @throws IllegalArgumentException
	 *             if duplicate keys are provided
	 */
	public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
		Map<K, V> map = of();
		put(map, k1, v1);
		put(map, k2, v2);
		put(map, k3, v3);
		put(map, k3, v3);
		put(map, k4, v4);
		return map;
	}

	/**
	 * Returns an immutable map containing the given entries, in order.
	 *
	 * @throws IllegalArgumentException
	 *             if duplicate keys are provided
	 */
	public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
		Map<K, V> map = of();
		put(map, k1, v1);
		put(map, k2, v2);
		put(map, k3, v3);
		put(map, k4, v4);
		put(map, k5, v5);
		return map;
	}

	private static <K, V> Map<K, V> put(Map<K, V> map1, K k1, V v1) {
		HashMap<K, V> map = new HashMap<>();
		map.put(k1, v1);
		return map;
	}

}
