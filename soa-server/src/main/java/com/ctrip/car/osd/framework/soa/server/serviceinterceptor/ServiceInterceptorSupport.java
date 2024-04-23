package com.ctrip.car.osd.framework.soa.server.serviceinterceptor;

import java.util.List;

import com.ctrip.car.osd.framework.common.config.SDFrameworkQConfig;
import com.ctrip.car.osd.framework.soa.server.ExceptionInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;

import com.ctrip.car.osd.framework.soa.server.AfterInterceptor;
import com.ctrip.car.osd.framework.soa.server.ExecutorFactory;
import com.ctrip.car.osd.framework.soa.server.Interceptor;
import com.ctrip.car.osd.framework.soa.server.ExecutorFactory.ExecutorDescriptor;
import com.ctrip.car.osd.framework.soa.server.InterceptorFactory.InterceptorComparator;
import com.google.common.collect.Lists;

@Order
public class ServiceInterceptorSupport
		implements Interceptor, AfterInterceptor, ExceptionInterceptor,ApplicationContextAware, InitializingBean {

	@Autowired
	private ExecutorFactory executorFactory;
	private ApplicationContext applicationContext;
	private List<GlobalServiceInterceptor> filters;
	private SDFrameworkQConfig qconfig;
	
	public ServiceInterceptorSupport(SDFrameworkQConfig qconfig){
		this.qconfig = qconfig;
	}
	

	@Override
	public void afterHandle(Object request, Object response) throws Exception {
		if (isEmptyFilters()) {
			return;
		}
		ExecutorDescriptor descriptor = this.executorFactory.getExecuterDescriptor(request);
		ServiceContext context = setupResponse(request, response);
		if (isServiceFilter(request, descriptor)) {
			ServiceInterceptor filter = (ServiceInterceptor) descriptor.getExecutor();
			filter.after(context);
		}
		
		for (GlobalServiceInterceptor serviceFilter : filters) {
			if (!qconfig.isInterceptorExclude(serviceFilter.getClass().getSimpleName())) {
				serviceFilter.after( buildContext(context, serviceFilter.name()));
			}
		}
	}

	@Override
	public void exceptionHandle(Object request, Object response, Exception exception) {
		if (isEmptyFilters()) {
			return;
		}

		ServiceContext context = setupResponse(request, response);

		for (GlobalServiceInterceptor serviceFilter : filters) {
			if (!qconfig.isInterceptorExclude(serviceFilter.getClass().getSimpleName())) {
				serviceFilter.exception(buildContext(context, serviceFilter.name()), exception);
			}
		}
	}

	private ServiceContext setupResponse(Object request, Object response) {
		ServiceContext requestContext = ServiceContextUtils.getContext();
		ServiceContext context;
		if (requestContext != null) {
			context = requestContext;
			context.setResponse(response);
		} else {
			context = new ServiceContext(request, response);
		}
		context.global();
		return context;
	}

	private ServiceContext buildContext(ServiceContext context , String name){
//		ServiceInterceptorContext filter = new ServiceInterceptorContext(context.getRequest(), context.getResponse() , context.getAttributes(name));
		context.setCurrent(name);
		return context;
	}

	@Override
	public void handle(Object value) throws Exception {
		if (isEmptyFilters()) {
			return;
		}
		ExecutorDescriptor descriptor = this.executorFactory.getExecuterDescriptor(value);
		ServiceContext context = new ServiceContext(value, null);
		ServiceContextUtils.setContext(context);
		
		for (GlobalServiceInterceptor serviceFilter : filters) {
			if (!qconfig.isInterceptorExclude(serviceFilter.getClass().getSimpleName())) {
				serviceFilter.before(buildContext(context, serviceFilter.name()));
			}
		}
		
		context.global();
		
		if (isServiceFilter(value, descriptor)) {
			ServiceInterceptor filter = (ServiceInterceptor) descriptor.getExecutor();
			filter.before(context);
		}
		
	}

	private boolean isServiceFilter(Object request, ExecutorDescriptor descriptor) {
		return ServiceInterceptor.class.isAssignableFrom(descriptor.getExecutor().getClass());
	}

	private boolean isEmptyFilters() {
		return filters == null || filters.isEmpty();
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.filters = Lists.newCopyOnWriteArrayList(applicationContext.getBeansOfType(GlobalServiceInterceptor.class).values());
		this.filters.sort(new InterceptorComparator<>());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
