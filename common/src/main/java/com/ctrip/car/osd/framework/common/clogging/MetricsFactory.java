package com.ctrip.car.osd.framework.common.clogging;

import com.ctrip.framework.clogging.agent.aggregator.impl.Aggregator;
import com.ctrip.framework.clogging.agent.metrics.IMetric;
import com.ctrip.framework.clogging.agent.metrics.MetricManager;
import com.ctrip.framework.foundation.Foundation;

public class MetricsFactory {

	private static Aggregator aggregator;
	private static IMetric metricer;

	public static void setAggregator(Aggregator aggregator) {
		MetricsFactory.aggregator = aggregator;
	}

	public static void setMetricer(IMetric metricer) {
		MetricsFactory.metricer = metricer;
	}

	private static Aggregator getAggregator() {
		if (aggregator == null) {
			aggregator = Aggregator.getMetricsAggregator(60);
		}
		return aggregator;
	}

	private static IMetric getIMetric() {
		if (metricer == null) {
			metricer = MetricManager.getMetricer();
		}
		return metricer;
	}

	public static Metrics createMetrics(String name) {
		return new Metrics(name, getAggregator(), getIMetric());
	}

	public static Metrics createMetrics(String name, double value) {
		Metrics metrics = createMetrics(name);
		metrics.setValue(value);
		return metrics;
	}

	public static Metrics createMetrics(String name, long value) {
		Metrics metrics = createMetrics(name);
		metrics.setValue(value);
		return metrics;
	}

	public static Metrics createAppMetrics(String name) {
		return createMetrics(Foundation.app().getAppId() + "_" + name);
	}

	public static Metrics createAppMetrics(String name, double value) {
		return createMetrics(Foundation.app().getAppId() + "_" + name, value);
	}

	public static Metrics createAppMetrics(String name, long value) {
		return createMetrics(Foundation.app().getAppId() + "_" + name, value);
	}
}
