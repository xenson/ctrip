package com.ctrip.car.osd.framework.common.clogging;

import java.util.HashMap;
import java.util.Map;

import com.ctrip.framework.clogging.agent.trace.ISpan;
import com.ctrip.framework.clogging.agent.trace.ITrace;
import com.ctrip.framework.clogging.agent.trace.TraceManager;
import com.ctrip.framework.clogging.domain.thrift.LogLevel;
import com.ctrip.framework.clogging.domain.thrift.LogType;
import com.ctrip.framework.clogging.domain.thrift.SpanType;
import com.google.common.base.Preconditions;

/**
 * 对 Itrace 进行简化封装
 * 
 * @author xiayx@ctrip.com
 *
 */
public class Trace {

	private ITrace tracer;

	public Trace(Class<?> entityClass) {
		tracer = TraceManager.getTracer(entityClass);
	}

	public Trace(ITrace tracer) {
		this.tracer = tracer;
	}

	public TraceSpan span() {
		return new TraceSpan();
	}

	public TraceSpan continueSpan() {
		return new TraceContinueSpan();
	}

	public TraceLog log() {
		return new TraceLog();
	}

	public TraceLog log(LogType logType) {
		TraceLog l = log();
		return l.setType(logType);
	}

	TraceSpan createChildSpan(ISpan span) {
		return new TraceChildSpan(span);
	}

	private Trace getThis() {
		return this;
	}

	public class TraceSpan {

		private ISpan span;
		private String spanName;
		private String serviceName;
		private SpanType spanType;

		public TraceSpan setSpanName(String spanName) {
			this.spanName = spanName;
			return this;
		}

		public TraceSpan setServiceName(String serviceName) {
			this.serviceName = serviceName;
			return this;
		}

		public TraceSpan setSpanType(SpanType spanType) {
			this.spanType = spanType;
			return this;
		}

		public TraceSpan sql() {
			return this.setSpanType(SpanType.SQL);
		}

		public TraceSpan url() {
			return this.setSpanType(SpanType.URL);
		}

		public TraceSpan webService() {
			return this.setSpanType(SpanType.WEB_SERVICE);
		}

		public TraceSpan memcached() {
			return this.setSpanType(SpanType.MEMCACHED);
		}

		public TraceSpan other() {
			return this.setSpanType(SpanType.OTHER);
		}

		public TraceSpan start(String spanName, String serviceName) {
			this.setServiceName(serviceName);
			this.setSpanName(spanName);
			this.start();
			return this;
		}

		public TraceSpan start() {
			Preconditions.checkNotNull(spanName);
			Preconditions.checkNotNull(serviceName);
			Preconditions.checkNotNull(spanType);
			this.span = createSpan(spanName, serviceName, spanType);
			return this;
		}

		public TraceSpan createChild() {
			return createChildSpan(span);
		}

		public ISpan getSpan() {
			return this.span;
		}

		public Trace end() {
			Preconditions.checkNotNull(this.span, "The Span is Null.");
			this.span.stop();
			return getThis();
		}

		protected ISpan createSpan(String spanName, String serviceName, SpanType spanType) {
			return tracer.startSpan(spanName, serviceName, spanType);
		}
	}

	public class TraceContinueSpan extends TraceSpan {
		private long traceId;
		private long parentId;

		public TraceContinueSpan() {
			super();
		}

		public TraceContinueSpan setTraceId(long traceId) {
			this.traceId = traceId;
			return this;
		}

		public TraceContinueSpan setParentId(long parentId) {
			this.parentId = parentId;
			return this;
		}

		public TraceContinueSpan continueT(long parentId, long traceId) {
			this.traceId = traceId;
			return this;
		}

		@Override
		protected ISpan createSpan(String spanName, String serviceName, SpanType spanType) {
			Preconditions.checkNotNull(traceId);
			Preconditions.checkNotNull(parentId);
			return tracer.continueSpan(spanName, serviceName, traceId, parentId, spanType);
		}
	}

	public class TraceChildSpan extends TraceSpan {

		protected ISpan parentSpan;

		public TraceChildSpan(ISpan span) {
			super();
			this.parentSpan = span;
		}

		@Override
		protected ISpan createSpan(String spanName, String serviceName, SpanType spanType) {
			Preconditions.checkNotNull(this.parentSpan, "The Parent span is Null.");
			return this.parentSpan.createChild(spanName, serviceName, spanType, tracer);
		}
	}

	public class TraceLog {
		private LogType type;
		private LogLevel level;
		private String title;
		private String message;
		private Throwable throwable;
		private Map<String, String> attrs;

		public TraceLog setType(LogType type) {
			this.type = type;
			return this;
		}

		public TraceLog setLevel(LogLevel level) {
			this.level = level;
			return this;
		}

		public TraceLog setTitle(String title) {
			this.title = title;
			return this;
		}

		public TraceLog setThrowable(Throwable throwable) {
			this.throwable = throwable;
			return this;
		}

		public TraceLog addAttrs(Map<String, String> attrs) {
			if (this.attrs == null) {
				this.attrs = new HashMap<>();
			}
			this.attrs.putAll(attrs);
			return this;
		}

		public TraceLog addAttr(String key, String value) {
			if (this.attrs == null) {
				this.attrs = new HashMap<>();
			}
			this.attrs.put(key, value);
			return this;
		}

		public TraceLog debug() {
			this.setLevel(LogLevel.DEBUG);
			return this;
		}

		public TraceLog info() {
			this.setLevel(LogLevel.INFO);
			return this;
		}

		public TraceLog error() {
			this.setLevel(LogLevel.ERROR);
			return this;
		}

		public Trace build() {
			Preconditions.checkNotNull(type);
			Preconditions.checkNotNull(level);
			if (this.throwable == null) {
				tracer.log(type, level, title, message, attrs);
			} else {
				tracer.log(type, level, title, throwable, attrs);
			}
			return getThis();
		}
	}

}
