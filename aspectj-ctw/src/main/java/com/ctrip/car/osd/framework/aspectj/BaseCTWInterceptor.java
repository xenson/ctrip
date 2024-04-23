package com.ctrip.car.osd.framework.aspectj;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.ctrip.car.osd.framework.aspectj.proxy.OsdMessageServiceProxy;
import com.ctrip.car.osd.framework.aspectj.util.SpringBeanUtility;
import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.message.dto.MessageInfo;
import com.ctrip.car.osd.message.dto.MessageRequestType;
import com.ctrip.car.osd.message.enums.MessageInfoTypeEnum;
import com.ctriposs.baiji.rpc.common.types.BaseRequest;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@IgnoreWeaver
public abstract class BaseCTWInterceptor {
    protected Logger LOGGER = LoggerFactory.getLogger(getClass());

    public static final TransmittableThreadLocal<BaseRequest> BASE_REQUEST_HOLDER = new TransmittableThreadLocal();


    protected static final ClassPool CLASS_POOL = ClassPool.getDefault();


    @Pointcut("execution (* com.ctrip.car.osd..application..*Application.service(..))")
    public void applicationService() {
    }

    @Pointcut("!@within(com.ctrip.car.osd.framework.aspectj.IgnoreWeaver)")
    public void ignoreWeaverAnno() {
    }

    @Pointcut("!execution(* com.ctrip..entity..*.*(..))")
    public void ignoreEntity() {
    }

    @Pointcut("!execution(* com.ctrip..lambda$*(..))")
    public void ignoreLambad() {

    }

    @Pointcut("!execution(* com.ctrip..mapper..*(..))")
    public void ignoreMapper() {

    }

    protected static final CtClass getCtClass(Class<?> clazz) throws NotFoundException {
        if (clazz == null) {
            return null;
        }
        try {
            //底层已经有缓存
            return CLASS_POOL.get(clazz.getName());
        } catch (NotFoundException e) {
            ClassClassPath classPath = new ClassClassPath(clazz);
            CLASS_POOL.insertClassPath(classPath);
            return CLASS_POOL.get(clazz.getName());
        }
    }

    protected StringBuilder buildMethodWithArgName(ProceedingJoinPoint joinPoint) throws NotFoundException {
        // 获取执行的方法名
        SourceLocation sourceLocation = joinPoint.getSourceLocation();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        int line = sourceLocation.getLine();

        Class<?> clazz = signature.getDeclaringType();
        String clazzName = clazz.getName();
        //hive里的title属性
        StringBuilder methodStr = new StringBuilder(clazzName).append(".").append(methodName);

        CtClass ctClass = getCtClass(clazz);
        String ctMethodStr = "";
        if (ctClass != null) {
            try {
                CtMethod ctMethod = null;
                Class[] parameterTypes = signature.getParameterTypes();
                if (ArrayUtils.isNotEmpty(parameterTypes)) {
                    CtClass[] ctParameterTypes = new CtClass[parameterTypes.length];
                    for (int i = 0; i < parameterTypes.length; i++) {
                        ctParameterTypes[i] = getCtClass(parameterTypes[i]);
                    }
                    ctMethod = ctClass.getDeclaredMethod(methodName, ctParameterTypes);
                } else {
                    ctMethod = ctClass.getDeclaredMethod(methodName);
                }
                StringBuilder ctMethodSB = new StringBuilder();
                MethodInfo methodInfo = ctMethod.getMethodInfo();
                CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
                int modifiers = ctMethod.getModifiers();
                ctMethodSB.append("(");
                int pos = Modifier.isStatic(modifiers) ? 0 : 1;
                String paramStr = Stream.iterate(0, i -> ++i).limit(parameterTypes.length).map(i -> parameterTypes[i].getSimpleName() + " " + attr.variableName(i + pos)).collect(Collectors.joining(","));
                ctMethodSB.append(paramStr);
                ctMethodSB.append(")");
                ctMethodStr = ctMethodSB.toString();
            } catch (NotFoundException e) {
                LOGGER.error("通过javassist获取参数名异常!", e);
            }
        }

        if (StringUtils.isNotBlank(ctMethodStr)) {
            methodStr.append(ctMethodStr);
        }
        methodStr.append(":").append(line);
        return methodStr;
    }

    protected void trackPage(boolean eventResultFinal, Map<String, String> extendInfo) {
        try {
            /*TrackPageRequestType request = new TrackPageRequestType();
            request.setEventResult(eventResultFinal);
            request.setExtendInfo(extendInfo);
            SpringBeanUtility.getBean(OsdCommonServiceProxy.class).trackPage(request);*/
            MessageRequestType requestType = new MessageRequestType();
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setExtraInfo(extendInfo);
            messageInfo.setIsSuccess(eventResultFinal);
            messageInfo.setType(MessageInfoTypeEnum.HIVE.getType());

            requestType.setMessageInfos(Arrays.asList(messageInfo));
            SpringBeanUtility.getBean(OsdMessageServiceProxy.class).sendMessage(requestType);
        } catch (Exception e) {
            LOGGER.warn("trackPage error!", e);
        }
    }


}
