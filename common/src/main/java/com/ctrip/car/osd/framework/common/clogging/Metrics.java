package com.ctrip.car.osd.framework.common.clogging;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ctrip.framework.clogging.agent.aggregator.CountableType;
import com.ctrip.framework.clogging.agent.aggregator.impl.Aggregator;
import com.ctrip.framework.clogging.agent.aggregator.impl.CountableString;
import com.ctrip.framework.clogging.agent.metrics.IMetric;

public class Metrics extends CountableString {

	private Aggregator aggregator;
	private IMetric metricer;

	private String name;
	private long count = 1;
	private boolean maxEnable = false;
	private boolean minEnable = false;

	private Long longValue;
	private Float floatValue;
	private long time;

	private Map<String, String> tags;

	public Metrics(String name, Aggregator aggregator, IMetric metricer) {
		super(name);
		this.name = name;
		this.aggregator = aggregator;
		this.metricer = metricer;

	}

	public Metrics addTag(String key, String value) {
		if (this.tags == null) {
			this.tags = new HashMap<>();
		}
		this.tags.put(key, value);
		this.setTags(tags);
		return this;
	}

	public Metrics addAllTag(Map<String, String> extraTags) {
		if (this.tags == null) {
			this.tags = new HashMap<>();
		}
		this.tags.putAll(extraTags);
		this.setTags(tags);
		return this;
	}

	public Metrics setCount(long count) {
		this.count = count;
		return this;
	}

	public Metrics setMinEnable(boolean enable) {
		this.minEnable = enable;
		return this;
	}

	public Metrics setMaxEnable(boolean enable) {
		this.maxEnable = enable;
		return this;
	}

	public Metrics setValue(double value) {
		setVal(value);
		return this;
	}

	public Metrics setValue(long value) {
		this.longValue = value;
		return this;
	}

	public Metrics setValue(float value) {
		this.floatValue = value;
		return this;
	}

	public Metrics setTime(long createTime) {
		setCreateTime(createTime);
		return this;
	}

	public Metrics aggregator() {
		if (this.longValue != null) {
			setVal(longValue.doubleValue());
		} else if (this.floatValue != null) {
			setVal(floatValue.doubleValue());
		}
		aggregator.addCountable(this);
		return this;
	}
	
	public Metrics start(){
		this.time = System.currentTimeMillis();
		return this;
	}
	
	public Metrics end(){
		this.longValue = System.currentTimeMillis() - time;
		return this;
	}

	public void build() {

		this.aggregator();
	}

	public Metrics log() {
		Date time = this.getCreateTime() > 0 ? new Date(this.getCreateTime()) : new Date();
		if (this.longValue != null) {
			metricer.log(this.name, this.longValue, tags, time);
		} else if (this.floatValue != null) {
			metricer.log(this.name, this.floatValue, tags, time);
		} else {
			Double value = getVal();
			metricer.log(this.name, value.floatValue(), tags, time);
		}
		return this;
	}

	@Override
	public Map<CountableType, Double> getValues() {
		Map<CountableType, Double> result = new HashMap<CountableType, Double>();
		result.put(CountableType.COUNT, (double) this.count);
		double val = getVal();
		if (val != Double.MIN_VALUE) {
			result.put(CountableType.SUM, val);
			if (minEnable) {
				result.put(CountableType.MIN, val);
			}
			if (maxEnable) {
				result.put(CountableType.MAX, val);
			}
		}
		return result;
	}

}
