package com.ctrip.car.osd.framework.soa.server.interceptors;

import com.ctrip.car.osd.framework.soa.server.exception.CommonBizException;
import com.ctrip.car.osd.framework.soa.server.exception.ValidationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.ctrip.car.osd.framework.common.BaseService;
import com.ctrip.car.osd.framework.soa.server.ExceptionInterceptor;
import com.ctriposs.baiji.rpc.server.HttpRequestContext;
import com.ctriposs.baiji.rpc.server.HttpResponseWrapper;

//@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class FrameworkGlobalExceptionInterceptor extends BaseService implements ExceptionInterceptor {

	@Override
	public void exceptionHandle(Object request, Object response, Exception exception) throws Exception {
//		HttpResponseWrapper responseWrapper =  HttpRequestContext.getInstance().response();
//		if( responseWrapper.isResponseSent() || exception instanceof ValidationException || exception instanceof CommonBizException) {
//			return ;
//		}
//		throw exception;
	}

}
