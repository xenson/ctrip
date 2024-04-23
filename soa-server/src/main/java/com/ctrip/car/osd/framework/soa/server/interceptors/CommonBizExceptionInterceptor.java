package com.ctrip.car.osd.framework.soa.server.interceptors;

import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.framework.soa.server.ExceptionInterceptor;
import com.ctrip.car.osd.framework.soa.server.exception.CommonBizException;
import com.ctrip.car.osd.framework.soa.server.util.BaseInfoHelper;
import org.springframework.core.annotation.Order;

import java.util.Objects;

@Order(-11)
public class CommonBizExceptionInterceptor implements ExceptionInterceptor {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void exceptionHandle(Object request, Object response, Exception exception) throws Exception {
        if (exception instanceof CommonBizException) {
            CommonBizException commonBizException = (CommonBizException) exception;
            switch (commonBizException.getLevel()) {
                case DEBUG:
                    logger.debug(exception);
                    break;
                case INFO:
                    logger.info(exception);
                    break;
                case WARN:
                    logger.warn(exception);
                    break;
                case ERROR:
                    logger.error(exception);
                    break;
                case FATAL:
                    logger.fatal(exception);
                    break;
            }
            CommonBizException bizException = (CommonBizException) exception;
            String requestId = BaseInfoHelper.getRequestId(request);
            BaseInfoHelper.setBaseResponse(response, System.currentTimeMillis(), false, bizException.getMessage(), requestId);
        }
    }

    @Override
    public boolean canHandle(Exception exception) {
        return Objects.nonNull(exception) && exception instanceof CommonBizException;
    }
}
