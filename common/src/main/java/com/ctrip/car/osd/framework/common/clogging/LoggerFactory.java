package com.ctrip.car.osd.framework.common.clogging;

import java.util.Map;

import com.ctrip.framework.clogging.agent.log.ILog;
import com.ctrip.framework.clogging.agent.log.LogManager;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.car.osd.framework.common.config.Env;

/**
 * 日志工厂
 * @author xiayx@Ctrip.com
 *
 */
public class LoggerFactory {

	public static Logger getLogger(String name) {
		Env env = Env.of(Foundation.server().getEnv().getName());
		return new AdapterLogger(new Logger(LogManager.getLogger(name)),
				new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(name), env.isTest()));
	}

	/**
	 * 获取默认日志  ， 在测试环境下 除 uat、prod环境下   slf4j 日志可以打印 
	 * @param type
	 * @return
	 */
	public static Logger getLogger(Class<?> type) {
		Env env = Env.of(Foundation.server().getEnv().getName());
		return new AdapterLogger(new Logger(LogManager.getLogger(type)),
				new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(type), env.isTest()));
	}

	public static Logger getSlf4jLogger(Class<?> type) {
		return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(type), true);
	}

	public static Logger getCloggingLogger(Class<?> type) {
		return new Logger(LogManager.getLogger(type));
	}

	static class AdapterLogger extends Logger implements ILog {
		private ILog logger1;
		private ILog logger2;

		public AdapterLogger(ILog logger1, ILog logger2) {
			super(null);
			this.logger1 = logger1;
			this.logger2 = logger2;
		}

		@Override
		public void debug(String title, String message) {
			logger1.debug(title, message);
			logger2.debug(title, message);
		}

		@Override
		public void debug(String title, Throwable throwable) {
			logger1.debug(title, throwable);
			logger2.debug(title, throwable);
		}

		@Override
		public void debug(String title, String message, Map<String, String> attrs) {
			logger1.debug(title, message, attrs);
			logger2.debug(title, message, attrs);
		}

		@Override
		public void debug(String title, Throwable throwable, Map<String, String> attrs) {
			logger1.debug(title, throwable, attrs);
			logger2.debug(title, throwable, attrs);
		}

		@Override
		public void debug(String message) {
			logger1.debug(message);
			logger2.debug(message);
		}

		@Override
		public void debug(Throwable throwable) {
			logger1.debug(throwable);
			logger2.debug(throwable);
		}

		@Override
		public void debug(String message, Map<String, String> attrs) {
			logger1.debug(message, attrs);
			logger2.debug(message, attrs);
		}

		@Override
		public void debug(Throwable throwable, Map<String, String> attrs) {
			logger1.debug(throwable, attrs);
			logger2.debug(throwable, attrs);
		}

		@Override
		public void error(String title, String message) {
			logger1.error(title, message);
			logger2.error(title, message);
		}

		@Override
		public void error(String title, Throwable throwable) {
			logger1.error(title, throwable);
			logger2.error(title, throwable);
		}

		@Override
		public void error(String title, String message, Map<String, String> attrs) {
			logger1.error(title, message, attrs);
			logger2.error(title, message, attrs);
		}

		@Override
		public void error(String title, Throwable throwable, Map<String, String> attrs) {
			logger1.error(title, throwable, attrs);
			logger2.error(title, throwable, attrs);
		}

		@Override
		public void error(String message) {
			logger1.error(message);
			logger2.error(message);
		}

		@Override
		public void error(Throwable throwable) {
			logger1.error(throwable);
			logger2.error(throwable);
		}

		@Override
		public void error(String message, Map<String, String> attrs) {
			logger1.error(message, attrs);
			logger2.error(message, attrs);
		}

		@Override
		public void error(Throwable throwable, Map<String, String> attrs) {
			logger1.error(throwable, attrs);
			logger2.error(throwable, attrs);
		}

		@Override
		public void fatal(String title, String message) {
			logger1.fatal(title, message);
			logger2.fatal(title, message);
		}

		@Override
		public void fatal(String title, Throwable throwable) {
			logger1.fatal(title, throwable);
			logger2.fatal(title, throwable);
		}

		@Override
		public void fatal(String title, String message, Map<String, String> attrs) {
			logger1.fatal(title, message, attrs);
			logger2.fatal(title, message, attrs);
		}

		@Override
		public void fatal(String title, Throwable throwable, Map<String, String> attrs) {
			logger1.fatal(title, throwable, attrs);
			logger2.fatal(title, throwable, attrs);
		}

		@Override
		public void fatal(String message) {
			logger1.fatal(message);
			logger2.fatal(message);
		}

		@Override
		public void fatal(Throwable throwable) {
			logger1.fatal(throwable);
			logger2.fatal(throwable);
		}

		@Override
		public void fatal(String message, Map<String, String> attrs) {
			logger1.fatal(message, attrs);
			logger2.fatal(message, attrs);
		}

		@Override
		public void fatal(Throwable throwable, Map<String, String> attrs) {
			logger1.fatal(throwable, attrs);
			logger2.fatal(throwable, attrs);
		}

		@Override
		public void info(String title, String message) {
			logger1.info(title, message);
			logger2.info(title, message);
		}

		@Override
		public void info(String title, Throwable throwable) {
			logger1.info(title, throwable);
			logger2.info(title, throwable);
		}

		@Override
		public void info(String title, String message, Map<String, String> attrs) {
			logger1.info(title, message, attrs);
			logger2.info(title, message, attrs);
		}

		@Override
		public void info(String title, Throwable throwable, Map<String, String> attrs) {
			logger1.info(title, throwable, attrs);
			logger2.info(title, throwable, attrs);

		}

		@Override
		public void info(String message) {
			logger1.info(message);
			logger2.info(message);
		}

		@Override
		public void info(Throwable throwable) {
			logger1.info(throwable);
			logger2.info(throwable);
		}

		@Override
		public void info(String message, Map<String, String> attrs) {
			logger1.info(message, attrs);
			logger2.info(message, attrs);
		}

		@Override
		public void info(Throwable throwable, Map<String, String> attrs) {
			logger1.info(throwable, attrs);
			logger2.info(throwable, attrs);
		}

		@Override
		public void warn(String title, String message) {
			logger1.warn(title, message);
			logger2.warn(title, message);
		}

		@Override
		public void warn(String title, Throwable throwable) {
			logger1.warn(title, throwable);
			logger2.warn(title, throwable);
		}

		@Override
		public void warn(String title, String message, Map<String, String> attrs) {
			logger1.warn(title, message, attrs);
			logger2.warn(title, message, attrs);
		}

		@Override
		public void warn(String title, Throwable throwable, Map<String, String> attrs) {
			logger1.warn(title, throwable, attrs);
			logger2.warn(title, throwable, attrs);
		}

		@Override
		public void warn(String message) {
			logger1.warn(message);
			logger2.warn(message);
		}

		@Override
		public void warn(Throwable throwable) {
			logger1.warn(throwable);
			logger2.warn(throwable);
		}

		@Override
		public void warn(String message, Map<String, String> attrs) {
			logger1.warn(message, attrs);
			logger2.warn(message, attrs);
		}

		@Override
		public void warn(Throwable throwable, Map<String, String> attrs) {
			logger1.warn(throwable, attrs);
			logger2.warn(throwable, attrs);
		}

	}

}
