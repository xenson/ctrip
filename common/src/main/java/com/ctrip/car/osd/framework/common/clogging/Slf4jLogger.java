package com.ctrip.car.osd.framework.common.clogging;

import java.util.Map;

import org.slf4j.Logger;

import com.ctrip.framework.clogging.agent.log.ILog;

public class Slf4jLogger  extends com.ctrip.car.osd.framework.common.clogging.Logger implements ILog {

	private Logger logger;
	private boolean enable;

	public Slf4jLogger(Logger logger, boolean enable) {
		super(null);
		this.logger = logger;
		this.enable = enable;
	}

	@Override
	public void debug(String arg0) {
		this.logger(Slf4jLogLevel.DEBUG, arg0);
	}

	@Override
	public void debug(Throwable arg0) {
		this.logger(Slf4jLogLevel.DEBUG, arg0);
	}

	@Override
	public void debug(String arg0, String arg1) {
		this.logger(Slf4jLogLevel.DEBUG, arg0, arg1);
	}

	@Override
	public void debug(String arg0, Throwable arg1) {
		this.logger(Slf4jLogLevel.DEBUG, arg0, arg1);
	}

	@Override
	public void debug(String arg0, Map<String, String> arg1) {
		this.logger(Slf4jLogLevel.DEBUG, arg0, arg1);
	}

	@Override
	public void debug(Throwable arg0, Map<String, String> arg1) {
		this.logger(Slf4jLogLevel.DEBUG, arg0, arg1);
	}

	@Override
	public void debug(String arg0, String arg1, Map<String, String> arg2) {
		this.logger(Slf4jLogLevel.DEBUG, arg0, arg1, arg2);
	}

	@Override
	public void debug(String arg0, Throwable arg1, Map<String, String> arg2) {
		this.logger(Slf4jLogLevel.DEBUG, arg0, arg1, arg2);
	}

	@Override
	public void error(String arg0) {
		this.logger(Slf4jLogLevel.ERROR, arg0);
	}

	@Override
	public void error(Throwable arg0) {
		this.logger(Slf4jLogLevel.ERROR, arg0);
	}

	@Override
	public void error(String arg0, String arg1) {
		this.logger(Slf4jLogLevel.ERROR, arg0, arg1);
	}

	@Override
	public void error(String arg0, Throwable arg1) {
		this.logger(Slf4jLogLevel.ERROR, arg0, arg1);
	}

	@Override
	public void error(String arg0, Map<String, String> arg1) {
		this.logger(Slf4jLogLevel.ERROR, arg0, arg1);
	}

	@Override
	public void error(Throwable arg0, Map<String, String> arg1) {
		this.logger(Slf4jLogLevel.ERROR, arg0, arg1);
	}

	@Override
	public void error(String arg0, String arg1, Map<String, String> arg2) {
		this.logger(Slf4jLogLevel.ERROR, arg0, arg1, arg2);
	}

	@Override
	public void error(String arg0, Throwable arg1, Map<String, String> arg2) {
		this.logger(Slf4jLogLevel.ERROR, arg0, arg1, arg2);
	}

	@Override
	public void fatal(String arg0) {
		this.logger(Slf4jLogLevel.FATAL, arg0);
	}

	@Override
	public void fatal(Throwable arg0) {
		this.logger(Slf4jLogLevel.FATAL, arg0);
	}

	@Override
	public void fatal(String arg0, String arg1) {
		this.logger(Slf4jLogLevel.FATAL, arg0, arg1);
	}

	@Override
	public void fatal(String arg0, Throwable arg1) {
		this.logger(Slf4jLogLevel.FATAL, arg0, arg1);
	}

	@Override
	public void fatal(String arg0, Map<String, String> arg1) {
		this.logger(Slf4jLogLevel.FATAL, arg0, arg1);
	}

	@Override
	public void fatal(Throwable arg0, Map<String, String> arg1) {
		this.logger(Slf4jLogLevel.FATAL, arg0, arg1);
	}

	@Override
	public void fatal(String arg0, String arg1, Map<String, String> arg2) {
		this.logger(Slf4jLogLevel.FATAL, arg0, arg1, arg2);
	}

	@Override
	public void fatal(String arg0, Throwable arg1, Map<String, String> arg2) {
		this.logger(Slf4jLogLevel.FATAL, arg0, arg1, arg2);
	}

	@Override
	public void info(String arg0) {
		this.logger(Slf4jLogLevel.INFO, arg0);
	}

	@Override
	public void info(Throwable arg0) {
		this.logger(Slf4jLogLevel.INFO, arg0);
	}

	@Override
	public void info(String arg0, String arg1) {
		this.logger(Slf4jLogLevel.INFO, arg0, arg1);
	}

	@Override
	public void info(String arg0, Throwable arg1) {
		this.logger(Slf4jLogLevel.INFO, arg0, arg1);
	}

	@Override
	public void info(String arg0, Map<String, String> arg1) {
		this.logger(Slf4jLogLevel.INFO, arg0, arg1);
	}

	@Override
	public void info(Throwable arg0, Map<String, String> arg1) {
		this.logger(Slf4jLogLevel.INFO, arg0, arg1);
	}

	@Override
	public void info(String arg0, String arg1, Map<String, String> arg2) {
		this.logger(Slf4jLogLevel.INFO, arg0, arg1, arg2);
	}

	@Override
	public void info(String arg0, Throwable arg1, Map<String, String> arg2) {
		this.logger(Slf4jLogLevel.INFO, arg0, arg1, arg2);
	}

	@Override
	public void warn(String arg0) {
		this.logger(Slf4jLogLevel.WARN, arg0);
	}

	@Override
	public void warn(Throwable arg0) {
		this.logger(Slf4jLogLevel.WARN, arg0);
	}

	@Override
	public void warn(String arg0, String arg1) {
		this.logger(Slf4jLogLevel.WARN, arg0, arg1);
	}

	@Override
	public void warn(String arg0, Throwable arg1) {
		this.logger(Slf4jLogLevel.WARN, arg0, arg1);
	}

	@Override
	public void warn(String arg0, Map<String, String> arg1) {
		this.logger(Slf4jLogLevel.WARN, arg0, arg1);
	}

	@Override
	public void warn(Throwable arg0, Map<String, String> arg1) {
		this.logger(Slf4jLogLevel.WARN, arg0, arg1);
	}

	@Override
	public void warn(String arg0, String arg1, Map<String, String> arg2) {
		this.logger(Slf4jLogLevel.WARN, arg0, arg1, arg2);
	}

	@Override
	public void warn(String arg0, Throwable arg1, Map<String, String> arg2) {
		this.logger(Slf4jLogLevel.WARN, arg0, arg1, arg2);
	}

	private void logger(Slf4jLogLevel level, String arg0) {
		if (this.enable) {
			switch (level) {
			case WARN:
				this.logger.warn(arg0);
				break;
			case ERROR:
				this.logger.error(arg0);
				break;
			case INFO:
				this.logger.info(arg0);
				break;
			case DEBUG:
				this.logger.debug(arg0);
				break;
			case FATAL:
				this.logger.error(arg0);
				break;
			}
		}
	}

	private void logger(Slf4jLogLevel level, Throwable arg0) {
		if (this.enable) {
			switch (level) {
			case WARN:
				this.logger.warn(arg0.getMessage(), arg0);
				break;
			case ERROR:
				this.logger.error(arg0.getMessage(), arg0);
				break;
			case INFO:
				this.logger.info(arg0.getMessage(), arg0);
				break;
			case DEBUG:
				this.logger.debug(arg0.getMessage(), arg0);
				break;
			case FATAL:
				this.logger.error(arg0.getMessage(), arg0);
				break;
			}
		}
	}

	private void logger(Slf4jLogLevel level, String arg0, String arg1) {
		if (this.enable) {
			switch (level) {
			case WARN:
				this.logger.warn("{} , {}",arg0, arg1);
				break;
			case ERROR:
				this.logger.error("{} , {}",arg0, arg1);
				break;
			case INFO:
				this.logger.info("{} , {}",arg0, arg1);
				break;
			case DEBUG:
				this.logger.debug("{} , {}",arg0, arg1);
				break;
			case FATAL:
				this.logger.error("{} , {}",arg0, arg1);
				break;
			}
		}
	}

	private void logger(Slf4jLogLevel level, String arg0, Throwable arg1) {
		if (this.enable) {
			switch (level) {
			case WARN:
				this.logger.warn(arg0, arg1);
				break;
			case ERROR:
				this.logger.error(arg0, arg1);
				break;
			case INFO:
				this.logger.info(arg0, arg1);
				break;
			case DEBUG:
				this.logger.debug(arg0, arg1);
				break;
			case FATAL:
				this.logger.error(arg0, arg1);
				break;
			}
		}
	}

	private void logger(Slf4jLogLevel level, String arg0, Map<String, String> arg1) {
		if (this.enable) {
			switch (level) {
			case WARN:
				this.logger.warn(arg0 + " tags : {}", arg1.toString());
				break;
			case ERROR:
				this.logger.error(arg0 + " tags : {}", arg1.toString());
				break;
			case INFO:
				this.logger.info(arg0 + " tags : {}", arg1.toString());
				break;
			case DEBUG:
				this.logger.debug(arg0 + " tags : {}", arg1.toString());
				break;
			case FATAL:
				this.logger.error(arg0 + " tags : {}", arg1.toString());
				break;
			}
		}
	}

	private void logger(Slf4jLogLevel level, Throwable arg0, Map<String, String> arg1) {
		if (this.enable) {
			switch (level) {
			case WARN:
				this.logger.warn(arg0.getMessage() + " tags : " + arg1.toString(), arg0);
				break;
			case ERROR:
				this.logger.error(arg0.getMessage() + " tags : " + arg1.toString(), arg0);
				break;
			case INFO:
				this.logger.info(arg0.getMessage() + " tags : " + arg1.toString(), arg0);
				break;
			case DEBUG:
				this.logger.debug(arg0.getMessage() + " tags : " + arg1.toString(), arg0);
				break;
			case FATAL:
				this.logger.error(arg0.getMessage() + " tags : " + arg1.toString(), arg0);
				break;
			}
		}
	}

	private void logger(Slf4jLogLevel level, String arg0, String arg1, Map<String, String> arg2) {
		if (this.enable) {
			switch (level) {
			case WARN:
				this.logger.warn("{} , {}  , tags : {}", arg0, arg1, arg2.toString());
				break;
			case ERROR:
				this.logger.error("{} , {}  , tags : {}", arg0, arg1, arg2.toString());
				break;
			case INFO:
				this.logger.info("{} , {}  , tags : {}", arg0, arg1, arg2.toString());
				break;
			case DEBUG:
				this.logger.debug("{} , {}  , tags : {}", arg0, arg1, arg2.toString());
				break;
			case FATAL:
				this.logger.error("{} , {}  , tags : {}", arg0, arg1, arg2.toString());
				break;
			}
		}
	}

	private void logger(Slf4jLogLevel level, String arg0, Throwable arg1, Map<String, String> arg2) {
		if (this.enable) {
			switch (level) {
			case WARN:
				this.logger.warn(arg0 + ", error message : " + arg1.getMessage() + "  , tags : " + arg2.toString(),
						arg1);
				break;
			case ERROR:
				this.logger.error(arg0 + ", error message : " + arg1.getMessage() + "  , tags : " + arg2.toString(),
						arg1);
				break;
			case INFO:
				this.logger.info(arg0 + ", error message : " + arg1.getMessage() + "  , tags : " + arg2.toString(),
						arg1);
				break;
			case DEBUG:
				this.logger.debug(arg0 + ", error message : " + arg1.getMessage() + "  , tags : " + arg2.toString(),
						arg1);
				break;
			case FATAL:
				this.logger.error(arg0 + ", error message : " + arg1.getMessage() + "  , tags : " + arg2.toString(),
						arg1);
				break;
			}
		}
	}
	
	 enum Slf4jLogLevel {
		DEBUG(0), INFO(1), WARN(2), ERROR(3), FATAL(4);

		private final int value;

		private Slf4jLogLevel(int value) {
			this.value = value;
		}
		
		/**
		 * Get the integer value of this enum value, as defined in the Thrift IDL.
		 */
		public int getValue() {
			return value;
		}

	}

}
