package com.ctrip.car.osd.framework.soa.client.invoker;

import com.ctrip.car.osd.framework.soa.client.ServiceClient;
import com.ctrip.car.osd.framework.soa.client.ServiceClientConfigOptions;
import com.ctrip.soa.caravan.common.delegate.Func;
import com.ctriposs.baiji.rpc.client.ServiceClientBase;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.Future;

public class ClientInvokerDescriptor {

	private String name;
	private Method method;
	private ProxyMethodType type;
	private ClientInvoker clientInvoker;
	private Class<?> clientClass;
	private String root;
	private Class<?> rootClientClass;
	private Class<?> responseClass;
	private ClientConfig clientConfig;

	public ClientInvokerDescriptor(Method method) {
		super();
		this.method = method;
		this.clientConfig = new ClientConfig();
		build(method);
	}

	private void build(Method method) {
		ServiceClient clientAnnotation = null;
		if (method.isAnnotationPresent(ServiceClient.class)) {
			clientAnnotation = method.getAnnotation(ServiceClient.class);
		} else if (method.getDeclaringClass().isAnnotationPresent(ServiceClient.class)) {
			clientAnnotation = method.getDeclaringClass().getAnnotation(ServiceClient.class);
		}
		if (clientAnnotation == null) {
			throw new IllegalArgumentException("Please checked the method add @ServiceClient annotation .");
		}

		this.clientClass = (Class<ServiceClientBase<?>>) clientAnnotation.value();
		if (!clientAnnotation.root().equals(ServiceClient.class)) {
			this.rootClientClass = clientAnnotation.root();
			this.root = clientAnnotation.path();
		}

		this.responseClass = method.getReturnType();
		this.type = ProxyMethodType.of(method);

		if (this.type.isAsync()) {
			this.name = method.getName().substring(0, method.getName().lastIndexOf("Async"));
			this.responseClass = getResponseType(method.getGenericReturnType());
		} else {
			this.name = method.getName();
		}

		this.buildClientConfig(method, clientAnnotation);
	}

	private void buildClientConfig(Method method, ServiceClient serviceClient) {

		ServiceClientConfigOptions configOptions = null;
		if (method.isAnnotationPresent(ServiceClientConfigOptions.class)) {
			configOptions = method.getAnnotation(ServiceClientConfigOptions.class);
		} else if (method.getDeclaringClass().isAnnotationPresent(ServiceClientConfigOptions.class)) {
			configOptions = method.getDeclaringClass().getAnnotation(ServiceClientConfigOptions.class);
		}

		if (configOptions != null) {
			this.clientConfig = new ClientConfig(configOptions.format(), configOptions.timeout(),
					configOptions.socketTimeout(), configOptions.maxConnectionPerRoute(),
					configOptions.connectTimeout(), configOptions.idleTime(),configOptions.logEnable(),
					configOptions.ignoreError());
		} else {
			this.clientConfig = new ClientConfig(serviceClient.format(), serviceClient.timeout(),
					serviceClient.socketTimeout(), serviceClient.maxConnectionPerRoute(),
					serviceClient.connectTimeout(), serviceClient.idleTime(),serviceClient.logEnable(),
					serviceClient.ignoreError());
		}

	}

	public boolean isMultiServiceClient() {
		return this.rootClientClass != null && this.root != null && !this.root.isEmpty();
	}

	public ClientInvoker getClientInvoker() {
		return clientInvoker;
	}

	public void setClientInvoker(ClientInvoker clientInvoker) {
		this.clientInvoker = clientInvoker;
	}

	public String getName() {
		return name;
	}

	public Method getMethod() {
		return method;
	}

	public ProxyMethodType getType() {
		return type;
	}

	public Class<?> getClientClass() {
		return clientClass;
	}

	public String getRoot() {
		return root;
	}

	public Class<?> getRootClientClass() {
		return rootClientClass;
	}

	public Class<?> getResponseClass() {
		return responseClass;
	}

	public ClientConfig getClientConfig() {
		return clientConfig;
	}

	private Class<?> getResponseType(Type returnType) {
		return TypeToken.of(returnType).resolveType(Future.class.getTypeParameters()[0]).getRawType();
	}

	public enum ProxyMethodType {

		Default {
			@Override
			public boolean isDefault() {
				return true;
			}
		},
		async {

			@Override
			public boolean isAsync() {
				return true;
			}
		},
		direct {
			@Override
			public boolean isDirect() {
				return true;
			}
		},
		fallback {
			@Override
			public boolean isFallback() {
				return true;
			}
		};

		public boolean isDefault() {
			return false;
		}

		public boolean isAsync() {
			return false;
		}

		public boolean isDirect() {
			return false;
		}

		public boolean isFallback() {
			return false;
		}

		public static ProxyMethodType of(Method method) {

			if (method.getName().endsWith("Async")) {
				return async;
			} else {
				Class<?>[] types = method.getParameterTypes();
				if (types.length == 2) {
					if (types[1].equals(Func.class)) {
						return fallback;
					} else if (types[1].equals(String.class)) {
						return direct;
					}
				}
			}
			return Default;
		}

	}
}
