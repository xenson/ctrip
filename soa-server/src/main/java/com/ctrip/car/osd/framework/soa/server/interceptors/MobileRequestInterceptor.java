package com.ctrip.car.osd.framework.soa.server.interceptors;

import com.ctrip.basebiz.accounts.mobile.request.filter.AccountsMobileRequestFilter;
import com.ctrip.basebiz.accounts.mobile.request.filter.WithAccountsMobileRequestFilter;
import com.ctrip.car.osd.framework.soa.server.ExceptionInterceptor;
import com.ctrip.car.osd.framework.soa.server.ExecutorFactory;
import com.ctrip.car.osd.framework.soa.server.ExecutorFactory.ExecutorDescriptor;
import com.ctrip.car.osd.framework.soa.server.Interceptor;
import com.ctrip.car.osd.framework.soa.server.exception.CommonBizException;
import com.ctrip.car.osd.framework.soa.server.exception.ValidationException;
import com.ctriposs.baiji.rpc.server.BaijiServletContext;
import com.ctriposs.baiji.rpc.server.HttpRequestContext;
import com.ctriposs.baiji.rpc.server.HttpResponseWrapper;
import com.ctriposs.baiji.rpc.server.ServiceHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class MobileRequestInterceptor implements Interceptor, ExceptionInterceptor {

	@Autowired
	private ExecutorFactory executorFactory;

	@Override
	public void handle(Object value) throws Exception {
		ExecutorDescriptor descriptor = this.executorFactory.getExecuterDescriptor(value);
		if (descriptor.getExecutor().getClass().isAnnotationPresent(WithAccountsMobileRequestFilter.class)) {
			HttpRequestContext context = HttpRequestContext.getInstance();
			AccountsMobileRequestFilter filter = new AccountsMobileRequestFilter();
			filter.init(descriptor.getExecutor().getClass().getAnnotation(WithAccountsMobileRequestFilter.class));

			HttpResponseWrapper response = context.response();
			filter.apply(getServiceHost(context), context.request(), response);

			if (response.isResponseSent()) {
				throw new IllegalArgumentException("Mobile Request error.");
			}
		}
	}

	private ServiceHost getServiceHost(HttpRequestContext context) {
		return BaijiServletContext.INSTANCE.getServiceHost(context.request().servicePath());
	}

	@Override
	public void exceptionHandle(Object request, Object response, Exception exception) throws Exception {
//		HttpResponseWrapper responseWrapper = HttpRequestContext.getInstance().response();
//		if (responseWrapper.isResponseSent() || exception instanceof ValidationException || exception instanceof CommonBizException) {
//			return;
//		}
//		throw exception;
	}

}
