package com.ctrip.car.osd.framework.aspectj.interceptor;

import com.ctrip.car.osd.framework.aspectj.BaseCTWInterceptor;
import com.ctrip.car.osd.framework.aspectj.OsdLogTypeEnum;
import com.ctrip.car.osd.framework.aspectj.proxy.OsdMessageServiceProxy;
import com.ctrip.car.osd.framework.aspectj.util.SpringBeanUtility;
import com.ctrip.car.osd.framework.common.utils.ThreadPoolUtil;
import com.ctrip.car.osd.message.dto.MessageInfo;
import com.ctrip.car.osd.message.dto.MessageRequestType;
import com.ctrip.car.osd.message.enums.MessageInfoTypeEnum;
import com.ctriposs.baiji.rpc.common.types.BaseRequest;
import javassist.NotFoundException;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

//使用CTW,不能使用动态代理方式
//@Configuration
//@EnableAspectJAutoProxy
public abstract class BaseCostInterceptor extends BaseCTWInterceptor {
    protected static ExecutorService LOG_POOL = ThreadPoolUtil.singleRunPool("LOG-TIME-COST-%d");

    //打印日志的时间阈值,单位毫秒
    public static final long LOG_SWTICH = 500;

    public abstract Object timeAround(ProceedingJoinPoint joinPoint) throws Throwable;

    public Object baseLogAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 定义返回对象、得到方法需要的参数
        Object obj = null;
        boolean eventResult = true;
        Object[] args = joinPoint.getArgs();
        long startTime = System.currentTimeMillis();

        try {
            obj = joinPoint.proceed(args);
        } catch (Throwable e) {
            LOGGER.warn("统计某方法执行耗时环绕通知出错", e);
            eventResult = false;
            throw e;
        } finally {
            // 打印耗时的信息
            long diffTime = System.currentTimeMillis() - startTime;
            boolean eventResultFinal = eventResult;
            LOG_POOL.execute(() -> {
                if (diffTime > LOG_SWTICH) {
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
                        logStr.append(" 耗时：").append(diffTime).append(" ms");
                        //LOGGER.warn(logStr.toString());
                        MessageInfo messageInfoClog = new MessageInfo();
                        messageInfoClog.setIsSuccess(eventResultFinal);
                        messageInfoClog.setType(MessageInfoTypeEnum.CLOG.getType());
                        messageInfoClog.setMessage(logStr.toString());

                        //hive日志
                        String requestIdFinal = requestId;

                        Map<String, String> extendInfo = new HashMap<>();
                        extendInfo.put("requestId", requestIdFinal);
                        extendInfo.put("logType", OsdLogTypeEnum.COST.getType());
                        extendInfo.put("title", methodStr.toString());
                        extendInfo.put("cost", String.valueOf(diffTime));

                        MessageInfo messageInfo = new MessageInfo();
                        messageInfo.setIsSuccess(eventResultFinal);
                        messageInfo.setType(MessageInfoTypeEnum.HIVE.getType());
                        messageInfo.setExtraInfo(extendInfo);

                        MessageRequestType requestType = new MessageRequestType();
                        requestType.setMessageInfos(Arrays.asList(messageInfo,messageInfoClog));
                        SpringBeanUtility.getBean(OsdMessageServiceProxy.class).sendMessage(requestType);
                        //trackPage(eventResultFinal, extendInfo);


                    } catch (NotFoundException e) {
                        LOGGER.warn("记录耗时失败!", e);
                    }

                }
            });
        }
        return obj;
    }

}
