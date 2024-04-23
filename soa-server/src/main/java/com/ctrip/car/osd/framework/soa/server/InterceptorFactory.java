package com.ctrip.car.osd.framework.soa.server;

import com.ctrip.car.osd.framework.common.BaseService;
import com.ctrip.car.osd.framework.soa.server.exception.SOAServiceException;
import com.ctrip.car.osd.framework.soa.server.exception.ServiceNotFoundException;
import com.google.common.collect.Lists;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Comparator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class InterceptorFactory extends BaseService implements ApplicationContextAware, InitializingBean {

	private ApplicationContext applicationContext;
	private List<Interceptor> interceptors;
	private List<AfterInterceptor> afterInterceptors;
	private List<ExceptionInterceptor> exceptionsInterceptors;

	public InterceptorFactory() {
		super();
	}

	public void before(Object req) throws Exception {
		for (Interceptor interceptor : interceptors) {
			interceptor.handle(req);
		}
	}

	public void after(Object request, Object response) throws Exception {
		for (AfterInterceptor interceptor : afterInterceptors) {
			try {
				interceptor.afterHandle(request, response);
			} catch (Exception e) {
				logger.debug("execute interceptor error.", e);
				catLogError("execute interceptor error.", e);
				throw new SOAServiceException(e);
			}
		}

	}

	public void exception(Object req, Object resp, Exception ex) {
		boolean flag = false;
		for (ExceptionInterceptor interceptor : exceptionsInterceptors) {
			if (interceptor.canHandle(ex)) {
				flag = true;
			}
			try {
				interceptor.exceptionHandle(req, resp, ex);
			} catch (Exception e) {
				logger.debug("execute interceptor error.", e);
				catLogError("execute interceptor error.", e);
				throw new SOAServiceException(e);
			}
		}
		if (!flag) {
			logger.error("The service executor error.", ex);
			catLogError("Execute the executor error.", ex);
		}
	}

	public void exceptionNotResponse(Object req, Exception ex) {
		for (ExceptionInterceptor interceptor : exceptionsInterceptors) {
			try {
				interceptor.exceptionHandle(req, null, ex);
			} catch (Exception e) {
				logger.debug("execute interceptor error.", e);
				catLogError("execute interceptor error.", e);
			}
		}
		throw new ServiceNotFoundException("The " + req.getClass() + " service executor not found.", ex);
	}

	private <T> void registerInterceptors() {
		this.interceptors = Lists
				.newCopyOnWriteArrayList(applicationContext.getBeansOfType(Interceptor.class).values());
		this.afterInterceptors = Lists
				.newCopyOnWriteArrayList(applicationContext.getBeansOfType(AfterInterceptor.class).values());
		this.exceptionsInterceptors = Lists
				.newCopyOnWriteArrayList(applicationContext.getBeansOfType(ExceptionInterceptor.class).values());

		this.interceptors.sort(new InterceptorComparator<>());
		this.afterInterceptors.sort(new InterceptorComparator<>());
		this.exceptionsInterceptors.sort(new InterceptorComparator<>());
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		this.registerInterceptors();

		checkNotNull(this.interceptors);
		checkNotNull(this.afterInterceptors);
		checkNotNull(this.exceptionsInterceptors);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public static class InterceptorComparator<T> implements Comparator<T> {

		@Override
		public int compare(T o1, T o2) {
			if (o1 == null) {
				return 1;
			}
			if (o2 == null) {
				return -1;
			}

			int order1 = Ordered.LOWEST_PRECEDENCE, order2 = Ordered.LOWEST_PRECEDENCE;

			if (o1.getClass().isAnnotationPresent(Order.class)) {
				order1 = o1.getClass().getAnnotation(Order.class).value();
			}
			if (o2.getClass().isAnnotationPresent(Order.class)) {
				order2 = o2.getClass().getAnnotation(Order.class).value();
			}

			return Integer.compare(order1, order2);
		}

	}

}
