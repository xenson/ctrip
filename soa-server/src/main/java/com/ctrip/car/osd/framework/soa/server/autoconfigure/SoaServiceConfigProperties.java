package com.ctrip.car.osd.framework.soa.server.autoconfigure;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.MapConfig;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@ConfigurationProperties(prefix = "ctrip.soa")
public class SoaServiceConfigProperties {

	private Map<String, String> path;

	public Map<String, String> getPath() {
		return path;
	}

	public void setPath(Map<String, String> path) {
		this.path = path;
	}

	public Map<String, String> getServiceMap() {
		if (path == null) {
			return Maps.newHashMap();
		}
		Map<String, String> services = Maps.newHashMap();
		for (Entry<String, String> entry : this.path.entrySet()) {
			services.put(entry.getValue(), entry.getKey());
		}
		return services;
	}
}
