package com.ctrip.car.osd.framework.common;

import com.ctrip.car.osd.framework.common.clogging.*;
import com.ctrip.framework.foundation.Foundation;

import java.util.Map;
import java.util.Map.Entry;

/**
 * 公共的服务信息 、主要公共的日志等信息
 * 
 * @author xiayx@Ctrip.com
 *
 */
public class BaseService extends AssertionConcern {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected Metrics createMetrics(String name) {
		return MetricsFactory.createMetrics(name);
	}

	protected Metrics createMetrics(String name, double value) {
		return MetricsFactory.createMetrics(name, value);
	}

	protected Metrics createMetrics(String name, long value) {
		return MetricsFactory.createMetrics(name, value);
	}
	
	/**
	 * 创建 metrics
	 * @param name
	 * @param tags
	 */
	protected void buildMetrics(String name, Map<String, String> tags) {
		Metrics metrics = MetricsFactory.createMetrics(Foundation.app().getAppId() + "_" + name, 1);
		for (Entry<String, String> entry : tags.entrySet()) {
			metrics.addTag(entry.getKey(), entry.getValue());
		}
		metrics.build();
	}
	
	protected void catLogError(String message, Throwable cause) {
		CatFactory.logError(message, cause);
		logger.error(message, cause,CatFactory.getCommonTags());
	}
}
