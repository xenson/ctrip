package com.ctrip.car.osd.framework.common.clogging;

import com.ctrip.car.osd.framework.common.utils.JsonUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
public class LogAspectProxy {
    @Around("@annotation(loggable2)")
    public Object cacheProxy(ProceedingJoinPoint point, Loggable loggable2) throws Throwable {
        LogLevel level = loggable2.value();
        LogType logType = loggable2.type();
        Object[] args = point.getArgs();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Map<String,String> tags = new ConcurrentHashMap<>();
        if (logType == LogType.ALL || logType == LogType.ARGUMENTS) {
            AspectLogHelper.log(point,level,method.getName() + "_args", JsonUtil.toJson(args),tags);
        }
        Object result = point.proceed();
        if (logType == LogType.ALL || logType == LogType.RETURN) {
            AspectLogHelper.log(point,level,method.getName() + "_return", JsonUtil.toJson(result),tags);
        }
        return result;
    }
}
