package com.ctrip.car.osd.framework.common.clogging;

import com.ctrip.framework.clogging.agent.trace.TraceManager;

public class TraceFactory {

	public static Trace getTrace(Class<?> type) {
		return new Trace(TraceManager.getTracer(type));
	}

	public static Trace getTrace(String name) {
		return new Trace(TraceManager.getTracer(name));
	}

}
