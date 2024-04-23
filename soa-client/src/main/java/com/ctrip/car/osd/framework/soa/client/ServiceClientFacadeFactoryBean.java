package com.ctrip.car.osd.framework.soa.client;

import com.ctrip.car.osd.framework.common.BaseService;
import com.ctrip.car.osd.framework.common.clogging.CatFactory;
import com.ctrip.car.osd.framework.common.clogging.LogContext;
import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.framework.common.config.SDFrameworkQConfig;
import com.ctrip.car.osd.framework.common.properties.CtripConfigProperties;
import com.ctrip.car.osd.framework.common.utils.ThreadPoolUtil;
import com.ctrip.car.osd.framework.soa.client.invoker.ClientInvokerDescriptor;
import com.ctrip.car.osd.framework.soa.client.invoker.ClientInvokerProxy;
import com.dianping.cat.Cat;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class ServiceClientFacadeFactoryBean<T> extends BaseService implements FactoryBean<T> {

    protected Logger clientLogger = LoggerFactory.getLogger("SOA-Client");
    private Class<T> serviceClientFacadeInterface;
    private static ExecutorService LOG_POOL = ThreadPoolUtil.singleRunPool("LOG-CLIENT-%d");

    @Autowired
    private ClientInvokerProxy invokerProxy;
    @Autowired
    private SDFrameworkQConfig sdFrameworkQConfig;
    @Autowired
    private CtripConfigProperties configProperties;

    public ServiceClientFacadeFactoryBean(Class<T> serviceClientFacadeInterface) {
        super();
        this.serviceClientFacadeInterface = serviceClientFacadeInterface;
    }

    @Override
    public T getObject() throws Exception {
        Enhancer enhancer = new Enhancer();
        enhancer.setInterfaces(new Class[]{serviceClientFacadeInterface});
        enhancer.setCallback(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object result = null;
                long beginTime = System.currentTimeMillis();
                Exception ex = null;
                try {
                    result = proxy().invoke(method, args);
                    return result;
                } catch (Exception e) {
                    ex = e;
                    return null;
                } finally {
                    logging(method, args[0], result, beginTime, ex);
                }
            }
        });
        return (T) enhancer.create();
    }


    private void logging(Method method, Object request, Object response, long beginTime, Exception ex) {
        LOG_POOL.submit(() -> {
            ClientInvokerDescriptor descriptor = proxy().getInvoker(method);
            int disableLog = sdFrameworkQConfig.clientDisableLog(descriptor.getName());
            boolean needCompress = sdFrameworkQConfig.clientNeedCompressMethod(descriptor.getName());
            int compressAlgorithm = sdFrameworkQConfig.compressAlgorithm();

            String title = "Client-" + method.getName();
            Map<String, String> tags = CatFactory.getCommonTags();
            tags.put("begin-time", DateFormatUtils.format(beginTime, "yyyy-MM-dd HH:mm:ss"));
            String cost = Long.toString(System.currentTimeMillis() - beginTime);
            tags.put("cost", cost);
            tags.put("title", title);
            tags.put("log_type", "client");
            if (descriptor.getClientConfig().isLogEnable() && disableLog != 3) {
                LogContext logContext = new LogContext(disableLog, needCompress, compressAlgorithm);
                logContext.setRequest(request);
                logContext.setResponse(response);
                logContext.setException(ex);
                Map<String, String> storeTags = new HashMap<>();
                storeTags.put("request", logContext.getRequestBody());
                tags.put("compressType", compressAlgorithm == 0 ? "GZIP" : "ZSTD");
                tags.put("exception", Objects.isNull(logContext.getException()) ? null : ExceptionUtils.getStackTrace(logContext.getException()));
                CatFactory.appentResponse(storeTags, logContext);
                Cat.logTags(configProperties.getEs(), tags, storeTags);
                clientLogger.info(title, buildLogContent(logContext), tags);
            }
        });
    }

    private String buildLogContent(LogContext logContext) {
        String compressType = "";
        if (logContext.isNeedCompress()) {
            compressType = "压缩算法 ：" + (logContext.getCompressAlgorithm()== 0 ? "GZIP" : "ZSTD");
        }
        return compressType + "\n请求报文 ：\n" +
                logContext.getRequestBody() +
                "\n响应报文 ：\n" +
                logContext.getResponseBody() +
                "\n异常堆栈 ：\n" +
                (Objects.isNull(logContext.getException()) ? null : ExceptionUtils.getStackTrace(logContext.getException()));
    }

    private ClientInvokerProxy proxy() {
        return this.invokerProxy;
    }

    @Override
    public Class<?> getObjectType() {
        return serviceClientFacadeInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
