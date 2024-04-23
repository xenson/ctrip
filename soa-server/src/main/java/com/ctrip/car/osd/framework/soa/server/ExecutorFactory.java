package com.ctrip.car.osd.framework.soa.server;

import com.ctrip.car.osd.framework.common.BaseService;
import com.ctrip.car.osd.framework.common.properties.CtripConfigProperties;
import com.ctrip.car.osd.framework.common.utils.TypeUtils;
import com.ctrip.car.osd.framework.soa.server.serviceinterceptor.ServiceInterceptor;
import com.ctrip.car.osd.framework.soa.server.util.AopTargetUtils;
import com.ctrip.car.osd.framework.soa.server.validator.Validator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("rawtypes")
public class ExecutorFactory extends BaseService implements ApplicationContextAware, InitializingBean {

	private ApplicationContext applicationContext;
	private Map<Class<?>, ExecutorDescriptor> executors;
	private CtripConfigProperties configProperties;

	public ExecutorFactory(CtripConfigProperties ctripConfigProperties) {
		super();
		this.executors = new ConcurrentHashMap<>();
		this.configProperties = ctripConfigProperties;
	}

	public Object execute(Object req) throws Exception {
		return this.executors.get(req.getClass()).getExecutor().execute(req);
	}

	public Object makeDefaultResponse(Object req) {
		return BeanUtils.instantiate(getExecuterDescriptor(req).getResponseServiceType());
	}

	public ExecutorDescriptor getExecuterDescriptor(Object req) {
		return this.executors.get(req.getClass());
	}

	public boolean hasExecutor(Object req) {
		return this.executors.containsKey(req.getClass());
	}

	public boolean hasValidator(Object req) {
		return this.executors.containsKey(req.getClass()) && this.executors.get(req.getClass()).isHasValidator();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		registerExecutors();
	}

	/**
	 * 如果应用指定了Executor的目录，则只扫描对应目录下的Executor
	 *
	 * @see CtripConfigProperties#executorPackage
	 */
	private void registerExecutors() throws Exception {
		Collection<Executor> serviceExecutors = applicationContext.getBeansOfType(Executor.class).values();
		String executorPackage = configProperties.getExecutorPackage();
		for (Executor serviceExecutor : serviceExecutors) {
			if (StringUtils.isNotEmpty(executorPackage)) {
				String name = serviceExecutor.getClass().getPackage().getName();
				if (!Objects.equals(name, executorPackage)) {
					continue;
				}
			}
			ExecutorDescriptor descriptor = new ExecutorDescriptor((Executor) AopTargetUtils.getTarget(serviceExecutor));
			this.executors.put(descriptor.getRequestServiceType(), descriptor);
		}
	}

	public class ExecutorDescriptor {

		private Class requestServiceType;
		private Class responseServiceType;
		private Executor executor;
		private boolean hasValidator;
		private boolean hasServiceInterceptor;
		private Class serviceType;
		private String serviceName;
		private String name;

		public ExecutorDescriptor(Executor executor) {
			super();
			this.executor = executor;
			this.requestServiceType = TypeUtils.getGenericType(executor.getClass(), Executor.class);
			this.responseServiceType = TypeUtils.getGenericType(executor.getClass(), Executor.class, 1);
			this.hasValidator = Validator.class.isAssignableFrom(executor.getClass());
			this.hasServiceInterceptor = ServiceInterceptor.class.isAssignableFrom(executor.getClass());
		}

		public Class getRequestServiceType() {
			return requestServiceType;
		}

		public Class getResponseServiceType() {
			return responseServiceType;
		}

		public Executor getExecutor() {
			return executor;
		}

		public boolean isHasValidator() {
			return hasValidator;
		}

		public boolean isHasServiceInterceptor() {
			return hasServiceInterceptor;
		}

		public Class getServiceType() {
			return serviceType;
		}

		public void setServiceType(Class serviceType) {
			if (this.serviceType == null) {
				this.serviceType = serviceType;
			}
		}

		public String getServiceName() {
			return serviceName;
		}

		public void setServiceName(String serviceName) {
			if (this.serviceName == null || this.serviceName.isEmpty()) {
				this.serviceName = serviceName;
			}
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			if (this.name == null || this.name.isEmpty()) {
				this.name = name;
			}
		}

	}

}
