package com.ctrip.car.osd.framework.common.clogging;

import java.util.HashMap;
import java.util.Map;

import com.ctrip.framework.clogging.agent.log.ILog;

public class Logger implements ILog {

	private ILog logger;

	public Logger(ILog logger) {
		super();
		this.logger = logger;
	}

	@Override
	public void debug(String title, String message) {
		logger.debug(title, message);
	}

	@Override
	public void debug(String title, Throwable throwable) {
		logger.debug(title, throwable);
	}

	@Override
	public void debug(String title, String message, Map<String, String> attrs) {
		logger.debug(title, message, attrs);
	}

	@Override
	public void debug(String title, Throwable throwable, Map<String, String> attrs) {
		logger.debug(title, throwable, attrs);
	}

	@Override
	public void debug(String message) {
		logger.debug(message);
	}

	@Override
	public void debug(Throwable throwable) {
		logger.debug(throwable);
	}

	@Override
	public void debug(String message, Map<String, String> attrs) {
		logger.debug(message, attrs);
	}

	@Override
	public void debug(Throwable throwable, Map<String, String> attrs) {
		logger.debug(throwable, attrs);
	}

	@Override
	public void error(String title, String message) {
		logger.error(title, message);

	}

	@Override
	public void error(String title, Throwable throwable) {
		logger.error(title, throwable);
	}

	@Override
	public void error(String title, String message, Map<String, String> attrs) {
		logger.error(title, message, attrs);

	}

	@Override
	public void error(String title, Throwable throwable, Map<String, String> attrs) {
		logger.error(title, throwable, attrs);

	}

	@Override
	public void error(String title, String msg, Throwable throwable, Map<String, String> attrs) {
		logger.error(title, msg, throwable, attrs);
	}

	@Override
	public void error(String message) {
		logger.error(message);

	}

	@Override
	public void error(Throwable throwable) {
		logger.error(throwable);

	}

	@Override
	public void error(String message, Map<String, String> attrs) {
		logger.error(message, attrs);

	}

	@Override
	public void error(Throwable throwable, Map<String, String> attrs) {
		logger.error(throwable, attrs);

	}

	@Override
	public void fatal(String title, String message) {
		logger.fatal(title, message);

	}

	@Override
	public void fatal(String title, Throwable throwable) {
		logger.fatal(title, throwable);

	}

	@Override
	public void fatal(String title, String message, Map<String, String> attrs) {
		logger.fatal(title, message, attrs);

	}

	@Override
	public void fatal(String title, String message, Throwable throwable, Map<String, String> attrs) {
		logger.fatal(title, message, throwable, attrs);
	}

	@Override
	public void fatal(String title, Throwable throwable, Map<String, String> attrs) {
		logger.fatal(title, throwable, attrs);

	}

	@Override
	public void fatal(String message) {
		logger.fatal(message);

	}

	@Override
	public void fatal(Throwable throwable) {
		logger.fatal(throwable);

	}

	@Override
	public void fatal(String message, Map<String, String> attrs) {
		logger.fatal(message, attrs);

	}

	@Override
	public void fatal(Throwable throwable, Map<String, String> attrs) {
		logger.fatal(throwable, attrs);

	}

	@Override
	public void info(String title, String message) {
		logger.info(title, message);

	}

	@Override
	public void info(String title, Throwable throwable) {
		logger.info(title, throwable);

	}

	@Override
	public void info(String title, String message, Map<String, String> attrs) {
		logger.info(title, message, attrs);

	}

	@Override
	public void info(String title, Throwable throwable, Map<String, String> attrs) {
		logger.info(title, throwable, attrs);

	}

	@Override
	public void info(String message) {
		logger.info(message);

	}

	@Override
	public void info(Throwable throwable) {
		logger.info(throwable);

	}

	@Override
	public void info(String message, Map<String, String> attrs) {
		logger.info(message, attrs);
	}

	@Override
	public void info(Throwable throwable, Map<String, String> attrs) {
		logger.info(throwable, attrs);
	}

	@Override
	public void warn(String title, String message) {
		logger.warn(title, message);
	}

	@Override
	public void warn(String title, Throwable throwable) {
		logger.warn(title, throwable);
	}

	@Override
	public void warn(String title, String message, Map<String, String> attrs) {
		logger.warn(title, message, attrs);
	}

	@Override
	public void warn(String title, String message, Throwable throwable, Map<String, String> attrs) {
		logger.warn(title, message, throwable, attrs);
	}

	@Override
	public void warn(String title, Throwable throwable, Map<String, String> attrs) {
		logger.warn(title, throwable, attrs);
	}

	@Override
	public void warn(String message) {
		logger.warn(message);
	}

	@Override
	public void warn(Throwable throwable) {
		logger.warn(throwable);
	}

	@Override
	public void warn(String message, Map<String, String> attrs) {
		logger.warn(message, attrs);
	}

	@Override
	public void warn(Throwable throwable, Map<String, String> attrs) {
		logger.warn(throwable, attrs);

	}

	public LoggerMessage minfo(String message, Object... args) {
		LoggerMessage m = new LoggerMessage(LoggerMessageType.info);
		m.message(message, args);
		return m;
	}

	public LoggerMessage mwarn(String message, Object... args) {
		LoggerMessage m =  new LoggerMessage(LoggerMessageType.warn);
		m.message(message, args);
		return m;
	}

	public LoggerMessage mdebug(String message, Object... args) {
		return new LoggerMessage(LoggerMessageType.debug);
	}

	public LoggerMessage merror(String message, Object... args) {
		return new LoggerMessage(LoggerMessageType.error);
	}

	public LoggerMessage mfatal(String message, Object... args) {
		return new LoggerMessage(LoggerMessageType.fatal);
	}

	public Map<String, String> attr(String key, String value) {
		Map<String, String> attrs = new HashMap<>();
		attrs.put(key, value);
		return attrs;
	}

	public class LoggerMessage {
		private String message;
		private Map<String, String> attrs;
		private Throwable throwable;
		private LoggerMessageType type;

		public LoggerMessage(LoggerMessageType type) {
			super();
			this.type = type;
		}

		public LoggerMessage message(String message, Object... args) {
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					if (args[i] != null) {
						message = message.replace("{" + i + "}", args[i].toString());
					}
				}
			}
			this.message = message;
			return this;
		}

		public LoggerMessage setThrowable(Throwable throwable) {
			this.throwable = throwable;
			return this;
		}

		public LoggerMessage attr(String key, String value) {
			if (this.attrs == null) {
				this.attrs = new HashMap<>();
			}
			if( value != null) {
				this.attrs.put(key, value);
			}
			return this;
		}
		
		public LoggerMessage attr(String key, Object value) {
			if (this.attrs == null) {
				this.attrs = new HashMap<>();
			}
			if( value != null) {
				this.attrs.put(key, value.toString());
			}
			return this;
		}

		public void build() {
			switch (type) {
			case info:
				if (this.throwable == null) {
					info(message, attrs);
				} else {
					info(message, throwable, attrs);
				}
				break;
			case warn:
				if (this.throwable == null) {
					warn(message, attrs);
				} else {
					warn(message, throwable, attrs);
				}
				break;
			case debug:
				if (this.throwable == null) {
					debug(message, attrs);
				} else {
					debug(message, throwable, attrs);
				}
				break;

			case error:
				if (this.throwable == null) {
					error(message, attrs);
				} else {
					error(message, throwable, attrs);
				}
				break;
			case fatal:
				if (this.throwable == null) {
					fatal(message, attrs);
				} else {
					fatal(message, throwable, attrs);
				}
				break;
			}
		}
	}

	enum LoggerMessageType {
		warn, info, debug, error, fatal;
	}
}
