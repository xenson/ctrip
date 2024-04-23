package com.ctrip.car.osd.framework.aspectj.interceptor;

import com.ctrip.car.osd.framework.aspectj.BaseCTWInterceptor;
import com.ctrip.car.osd.framework.aspectj.OsdLogTypeEnum;
import com.ctrip.car.osd.framework.aspectj.cache.VerifyUtilCache;
import com.ctrip.car.osd.framework.aspectj.proxy.OsdMessageServiceProxy;
import com.ctrip.car.osd.framework.aspectj.util.SpringBeanUtility;
import com.ctrip.car.osd.framework.common.utils.ThreadPoolUtil;
import com.ctrip.car.osd.message.dto.MessageInfo;
import com.ctrip.car.osd.message.dto.MessageRequestType;
import com.ctrip.car.osd.message.enums.MessageInfoTypeEnum;
import com.ctriposs.baiji.rpc.common.types.BaseRequest;
import javassist.NotFoundException;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public abstract class BaseEmptyResponseInterceptor extends BaseCTWInterceptor {
    protected static ExecutorService LOG_POOL = ThreadPoolUtil.singleRunPool("LOG-EMPTY-RESPONSE-%d");

    /**
     * 必须是 com.ctriposs.baiji.rpc.common.HasResponseStatus 的实现类
     */
    @Pointcut("execution(com.ctriposs.baiji.rpc.common.HasResponseStatus+ com.ctrip..*(..))")
    public void isHasResponseStatus() {

    }

    /**
     * 必须是 com.ctrip.car.osd.framework.aspectj.NeedEmptyResponseWeaver
     */
    @Pointcut("@annotation(com.ctrip.car.osd.framework.aspectj.NeedEmptyResponseWeaver)")
    public void needEmptyResponseWeaverAnno() {
    }

    public abstract Object emptyResponseAround(ProceedingJoinPoint joinPoint) throws Throwable;


    /**
     * 额外自定义校验条件
     *
     * @param proceed
     * @return
     */
    protected Pair<Boolean, String> verifyCustom(Object proceed) {

        return Pair.of(true, "");
    }

    /**
     * 公共空结果校验
     *
     * @param proceed
     * @return
     */
    private final Pair<Boolean, String> basicVerify(Object proceed) {
        if (proceed == null) {
            return Pair.of(true, "response is null.");
        }
        Pair<Boolean, String> verifyResult = Pair.of(false, "");
        if (proceed instanceof com.ctriposs.baiji.rpc.common.HasResponseStatus) {
            try {
                Class aClass = proceed.getClass();
                Object verifyBean = VerifyUtilCache.get(aClass);
                Method verifyMethod = VerifyUtilCache.getVerifyMethod(aClass);
                if (verifyMethod != null) {
                    Object invoke = verifyMethod.invoke(verifyBean, proceed);
                    verifyResult = (Pair<Boolean, String>) invoke;
                }

            } catch (Exception e) {
                LOGGER.warn("使用工具验证空结果异常!", e);
            }

        }else if(proceed instanceof Collection) {
            if (((Collection) proceed).isEmpty()) {
                verifyResult = Pair.of(true, "return List is empty!");
            }
        }else if(proceed instanceof Map) {
            if (((Map) proceed).isEmpty()) {
                verifyResult = Pair.of(true, "return Map is empty!");
            }
        }else{
            if (proceed == null){
                verifyResult = Pair.of(true, "return Object is empty!");
            }
        }

        Pair<Boolean, String> verifyCustomPair = verifyCustom(proceed);


        return Pair.of(verifyResult.getLeft() && verifyCustomPair.getLeft(), verifyResult.getRight() + "  " + verifyCustomPair.getRight());
    }


    /**
     * 公共空结果处理
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    public final Object baseLogAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed = null;
        // 定义返回对象、得到方法需要的参数
        Object[] args = joinPoint.getArgs();

        try {
            proceed = joinPoint.proceed(args);

        } finally {
            Object finalProceed = proceed;
            LOG_POOL.execute(() -> {
                Pair<Boolean, String> verifyResult = null;
                try {
                    verifyResult = basicVerify(finalProceed);
                } catch (Exception e) {
                    LOGGER.warn("验证空结果异常!", e);
                }
                // 将空结果记录下来
                if (verifyResult != null && verifyResult.getLeft()) {
                    try {
                        String requestId = "";
                        BaseRequest baseRequest = BASE_REQUEST_HOLDER.get();
                        if (baseRequest != null) {
                            requestId = baseRequest.getRequestId();
                        }
                        StringBuilder methodStr = buildMethodWithArgName(joinPoint);

                        //log打印的日志
                        StringBuilder logStr = new StringBuilder("-[").append(requestId).append("]-");
                        logStr.append(methodStr);
                        logStr.append(" 错误信息：").append(verifyResult.getRight());
                        MessageInfo messageInfoClog = new MessageInfo();
                        messageInfoClog.setIsSuccess(false);
                        messageInfoClog.setType(MessageInfoTypeEnum.CLOG.getType());
                        messageInfoClog.setMessage(logStr.toString());

                        //hive日志
                        Map<String, String> extendInfo = new HashMap<>();
                        extendInfo.put("requestId", requestId);
                        extendInfo.put("logType", OsdLogTypeEnum.EMPTY_RESPONSE.getType());
                        extendInfo.put("title", methodStr.toString());

                        MessageInfo messageInfo = new MessageInfo();
                        messageInfo.setIsSuccess(false);
                        messageInfo.setType(MessageInfoTypeEnum.HIVE.getType());
                        messageInfo.setExtraInfo(extendInfo);

                        MessageRequestType requestType = new MessageRequestType();
                        requestType.setMessageInfos(Arrays.asList(messageInfo,messageInfoClog));
                        SpringBeanUtility.getBean(OsdMessageServiceProxy.class).sendMessage(requestType);
                        //trackPage(false, extendInfo);

                    } catch (NotFoundException e) {
                        LOGGER.warn("记录空结果失败!", e);
                    }

                }
            });
        }
        return proceed;
    }
}
