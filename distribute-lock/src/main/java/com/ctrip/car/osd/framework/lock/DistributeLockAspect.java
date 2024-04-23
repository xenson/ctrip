package com.ctrip.car.osd.framework.lock;

import com.ctrip.arch.distlock.DLock;
import com.ctrip.arch.distlock.DistributedLockService;
import com.ctrip.arch.distlock.exception.DistlockRejectedException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author xh.gao
 * @createTime 2022年08月01日 18:03:00
 */
@Aspect
@Component
public class DistributeLockAspect {

    private static final DefaultParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
    private static final SpelExpressionParser PARSER = new SpelExpressionParser();
    @Autowired
    private DistributedLockService distributedLockService;

    @Pointcut("@annotation(com.ctrip.car.osd.framework.lock.DistributeLock)")
    public void pointCutMethod() {
    }

    @Around("pointCutMethod()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        DistributeLock annotation = AnnotationUtils.findAnnotation(method, DistributeLock.class);
        String preFix = getPreFix(method, annotation.preFix());
        String lockKey = getDistributeLockKey(annotation, method, pjp.getArgs());
        if (StringUtils.isEmpty(lockKey)) {
            return pjp.proceed();
        }
        DLock dLock = distributedLockService.getLock(preFix + ":" + lockKey);
        boolean locked = false;
        try {
            locked = annotation.needBlock() ? dLock.tryLock(annotation.waitTime(), annotation.timeUnit()) : dLock.tryLock();
            if (!locked) {
                throw new DistlockRejectedException();
            }
            return pjp.proceed();
        } catch (DistlockRejectedException e) {
            throw new CarDistlockRejectedException();
        } finally {
            if (locked) {
                dLock.unlock();
            }
        }
    }

    /**
     * 默认使用uid作为lockKey，也支持将spEl表达式解析的结果作为lockKey
     */
    private String getDistributeLockKey(DistributeLock annotation, Method method, Object[] args) {
        if (StringUtils.isEmpty(annotation.lockLey())) {
            return getUid();
        }
        return parse(annotation, method, args);
    }

    private String getUid() {
        try {
            return UidResolver.getUidService().getUid();
        } catch (Exception e) {
            return null;
        }
    }

    private String parse(DistributeLock annotation, Method method, Object[] args) {
        String spEl = annotation.lockLey();
        if (StringUtils.isEmpty(spEl)) {
            return StringUtils.EMPTY;
        }
        String[] parameterNames = NAME_DISCOVERER.getParameterNames(method);
        if (ArrayUtils.isEmpty(parameterNames)) {
            return spEl;
        }
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        return PARSER.parseExpression(spEl).getValue(context, String.class);
    }

    private String getPreFix(Method method, String preFix) {
        if (StringUtils.isEmpty(preFix)) {
            return method.getDeclaringClass().getSimpleName().toLowerCase();
        }
        return preFix;
    }
}
