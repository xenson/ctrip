package com.ctrip.car.osd.framework.soa.server;

import com.ctrip.car.osd.framework.common.BaseService;
import com.ctrip.car.osd.framework.common.clogging.CatFactory;
import com.ctrip.car.osd.framework.common.clogging.Metrics;
import com.ctrip.car.osd.framework.common.context.RequestContextFactory;
import com.ctrip.car.osd.framework.common.exception.ServiceRuntimeException;
import com.ctrip.car.osd.framework.soa.server.ExecutorFactory.ExecutorDescriptor;
import com.ctrip.car.osd.framework.soa.server.autoconfigure.SoaConfigService;
import com.ctrip.car.osd.framework.soa.server.autoconfigure.SoaConfigService.ServiceConfig;
import com.ctrip.car.osd.framework.soa.server.serviceinterceptor.ServiceContextUtils;
import com.ctrip.car.osd.framework.soa.server.util.BaseInfoHelper;
import com.ctriposs.baiji.rpc.common.BaijiContract;
import com.ctriposs.baiji.rpc.common.HasResponseStatus;
import com.ctriposs.baiji.rpc.common.types.AckCodeType;
import com.ctriposs.baiji.rpc.common.types.BaseResponse;
import com.ctriposs.baiji.rpc.common.types.ErrorDataType;
import com.ctriposs.baiji.rpc.common.types.ResponseStatusType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings({"unchecked"})
public class ServiceExecutorEngine extends BaseService implements InitializingBean {

    @Autowired
    private ExecutorFactory executorFactory;
    @Autowired
    private InterceptorFactory interceptorFactory;
    @Autowired
    private SoaConfigService configService;

    public ServiceExecutorEngine() {
        super();
    }

    public <Req, Resp> Resp execute(Req req, Object proxy, Method method, Object[] args) {
        if (!executorFactory.hasExecutor(req)) {
            logger.error("The req  executor not impl.");
            interceptorFactory.exceptionNotResponse(req, new ServiceRuntimeException("execute The request error."));
            return null;
        }
        ExecutorDescriptor descriptor = executorFactory.getExecuterDescriptor(req);
        this.initDescriptor(descriptor, proxy, method);
        Metrics metrics = createMetrics("car.osd.dashbord.service." + descriptor.getServiceName());
        metrics.start();
        metrics.addTag("name", descriptor.getName());
        Resp resp = null;
        long beforeTime = System.currentTimeMillis();
        String requestId = BaseInfoHelper.getRequestId(req);
        String successValue = "0";
        try {
            interceptorFactory.before(req);
            resp = (Resp) executorFactory.execute(req);
            BaseInfoHelper.setBaseResponse(resp, beforeTime,true,"success",requestId);
            interceptorFactory.after(req, resp);
            successValue = "1";
        } catch (Exception ex) {
            if (resp == null) {
                resp = (Resp) executorFactory.makeDefaultResponse(req);
            }
            BaseInfoHelper.setBaseResponse(resp, beforeTime,false,ex.getMessage(),requestId);
            this.buildExceptionResponse(req, resp, ex);
        } finally {
            if (resp instanceof BaseResponse) {
                BaseResponse baseResponse = (BaseResponse) resp;
                successValue = (baseResponse.isIsSuccess() != null && baseResponse.isIsSuccess()) ? "1" : "0";
            }
            metrics.addAllTag(CatFactory.getCommonTags());
            metrics.addTag("success", successValue);
            metrics.end();
            metrics.build();
            metrics.log();
            RequestContextFactory.INSTANCE.release();
            ServiceContextUtils.release();
        }
        return resp;
    }

    private <Req, Resp> void buildExceptionResponse(Req req, Resp resp, Exception ex) {
        try {
            interceptorFactory.exception(req, resp, ex);
        } catch (Exception e) {
            ResponseStatusType status = new ResponseStatusType();
            status.setAck(AckCodeType.Failure);
            status.setTimestamp(Calendar.getInstance());
            ErrorDataType errorDataType = new ErrorDataType();
            errorDataType.setMessage(e.getMessage());
            status.setErrors(Arrays.asList(errorDataType));
            HasResponseStatus responseStatus = (HasResponseStatus) resp;
            responseStatus.setResponseStatus(status);
        }
    }

    private void initDescriptor(ExecutorDescriptor descriptor, Object proxy, Method method) {
        if (descriptor != null) {
            descriptor.setServiceType(proxy.getClass());
            descriptor.setName(method.getName());
            if (StringUtils.isEmpty(descriptor.getServiceName())) {
                ServiceConfig serviceConfig = configService.getServiceConfig(proxy.getClass());
                descriptor.setServiceName(serviceConfig.getService().getAnnotation(BaijiContract.class).serviceName());
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        checkNotNull(this.executorFactory);
        checkNotNull(this.interceptorFactory);
    }

}
