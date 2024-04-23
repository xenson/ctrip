package com.ctrip.car.osd.framework.common.clogging;

import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AspectLogHelper {
    private static Map<String, Logger> logMap = new ConcurrentHashMap<>();

    public static void log(ProceedingJoinPoint point,LogLevel level, String title, String message,Map<String,String> tags) {
        Logger logger = getLogger(point);
        switch (level) {
            case WARN:
                logger.warn(title, message,tags);
                break;
            case ERROR:
                logger.error(title, message,tags);
                break;
            case INFO:
                logger.info(title, message,tags);
                break;
            case DEBUG:
                logger.debug(title, message,tags);
                break;
            case FATAL:
                logger.error(title, message,tags);
                break;
        }
    }

    private static Logger getLogger(ProceedingJoinPoint point) {
        Object target = point.getTarget();
        String className = target.getClass().getName();
        Logger logger = null;
        if (logMap.containsKey(className)) {
            logger = logMap.get(className);
        } else {
            logger = LoggerFactory.getLogger(className);
        }
        return logger;
    }
}
