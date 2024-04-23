package com.ctrip.car.osd.framework.common.config;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;

import com.ctrip.framework.foundation.Foundation;

public class ConfigSettings {

	private Set<Triple<Env, String, String>> values = new HashSet<>();

	public void set(String prop, Env env, String value) {
		values.add(Triple.of(env, prop, value));
	}

	public String get(String prop, Env env) {
		for (Triple<Env, String, String> triple : values) {
			if (triple.getLeft().equals(env) && triple.getMiddle().equals(prop)) {
				return triple.getRight();
			}
		}
		return null;
	}

	public String get(String prop) {
		Env envType = Env.of(Foundation.server().getEnvType());
		String value = get(prop, envType);
		if (value == null && envType.isTest()) {
			value = get(prop, Env.test);
		}  
		if (value == null && envType.isAll() ) {
			value = get(prop, Env.all);
		}
		return value;
	}

}
