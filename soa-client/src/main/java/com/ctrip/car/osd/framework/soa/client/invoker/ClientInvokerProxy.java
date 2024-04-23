package com.ctrip.car.osd.framework.soa.client.invoker;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.car.osd.framework.common.config.ContentFormatConfig;
import com.ctrip.car.osd.framework.common.config.SDFrameworkQConfig;
import com.ctrip.car.osd.framework.soa.client.ServiceClient;
import com.ctrip.car.osd.framework.soa.client.autoconfigure.ServiceClientProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.ctriposs.baiji.rpc.client.ServiceClientBase;

public class ClientInvokerProxy implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private SDFrameworkQConfig sdFrameworkQConfig;

    private final Map<Class<?>, ClientInvoker> invokers;
    private final Map<Method, ClientInvokerDescriptor> clients;

    public ClientInvokerProxy() {
        super();
        this.clients = new ConcurrentHashMap<>();
        this.invokers = new ConcurrentHashMap<>();
    }

    public Object invoke(Method method, Object[] args) throws Exception {
        ClientInvokerDescriptor descriptor = getInvoker(method);
        Object request = args[0];
        ClientInvoker invoker = descriptor.getClientInvoker();
        invoker.setRequestConfig(descriptor.getName(), descriptor.getClientConfig());

        switch (descriptor.getType()) {
            case Default:
                return invoker.invoke(descriptor.getName(), request, descriptor.getResponseClass());
            case async:
                return invoker.invokeAsync(descriptor.getName(), request, descriptor.getResponseClass());
            case fallback:
                return invoker.invokeFallback(descriptor.getName(), request, args[1], descriptor.getResponseClass());
            case direct:
                return invoker.invokeDirect(descriptor.getName(), request, (String) args[1], descriptor.getResponseClass());
            default:
                return invoker.invoke(descriptor.getName(), request, descriptor.getResponseClass());
        }
    }

    public ClientInvokerDescriptor getInvoker(Method method) {
        if (!clients.containsKey(method)) {
            ClientInvokerDescriptor descriptor = new ClientInvokerDescriptor(method);
            ClientInvoker invoker = getClientInvoker(descriptor);
            descriptor.setClientInvoker(invoker);
            clients.put(method, descriptor);
        }
        return clients.get(method);
    }

    private ClientInvoker getClientInvoker(ClientInvokerDescriptor descriptor) {
        Class<?> clientClass = descriptor.getClientClass();
        if (!invokers.containsKey(clientClass)) {
            if (descriptor.isMultiServiceClient()) {
                throw new IllegalArgumentException("暂时不支持多个路径");
            } else {
                ServiceClientBase<?> client = (ServiceClientBase<?>) this.applicationContext.getBean(clientClass);
                invokers.put(clientClass, new DefaultClientInvoker(client, descriptor.getClientConfig(), sdFrameworkQConfig.getContentFormatConfig()));
            }
        }
        return invokers.get(descriptor.getClientClass());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
