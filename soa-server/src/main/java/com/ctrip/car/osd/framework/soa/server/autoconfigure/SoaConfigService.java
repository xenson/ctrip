package com.ctrip.car.osd.framework.soa.server.autoconfigure;

import com.ctrip.car.osd.framework.common.config.SDFrameworkQConfig;
import com.ctrip.car.osd.framework.common.properties.CtripConfigProperties;
import com.ctrip.car.osd.framework.common.scan.Scanner;
import com.ctrip.car.osd.framework.soa.server.exception.ServiceNotFoundException;
import com.ctriposs.baiji.rpc.common.BaijiContract;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SoaConfigService {

    @Autowired
    private CtripConfigProperties ctripConfigProperties;
    @Autowired
    private SoaServiceConfigProperties serviceProperties;
    @Autowired
    private SDFrameworkQConfig sdFrameworkQConfig;

    private final Map<Class<?>, ServiceConfig> serviceConfigs;

    public SoaConfigService() {
        this.serviceConfigs = new ConcurrentHashMap<>();
    }

    public Collection<ServiceConfig> getRegisterServices() {
        if (this.serviceConfigs.isEmpty()) {
            Set<Class<?>> classes = SoaServiceRegistry.getServiceRegistry();
            Map<String, String> serviceMap = serviceProperties.getServiceMap();
            for (Class<?> clazz : classes) {
                BaijiContract baijiContract = clazz.getAnnotation(BaijiContract.class);
                if (baijiContract == null) {
                    continue;
                }
                String serviceName = baijiContract.serviceName();
                if (serviceMap != null && serviceMap.containsKey(serviceName)) {
                    serviceConfigs.put(clazz, new ServiceConfig(serviceMap.get(serviceName), clazz));
                } else {
                    serviceConfigs.put(clazz, new ServiceConfig(serviceName, clazz));
                }
            }
        }
        return serviceConfigs.values();
    }

    public ServiceConfig getServiceConfig(Class<?> class1) {
        for (Class<?> clazz : this.serviceConfigs.keySet()) {
            if (clazz.isAssignableFrom(class1)) {
                return this.serviceConfigs.get(clazz);
            }
        }
        return null;

    }

    /**
     * 原来调用这个方法的地方为 {@link #getRegisterServices()}
     */
    @Deprecated
    private Set<Class<?>> getServiceInterfaces() {
        String packageName = ctripConfigProperties.getBasePackage();
        String soaTargetService = ctripConfigProperties.getSoaTargetService();
        Scanner scanner = new Scanner(StringUtils.isEmpty(packageName) ? "com.ctrip" : packageName);
        Set<Class<?>> classes = scanner.scanWithAnnotated(BaijiContract.class);
        classes = Sets.newCopyOnWriteArraySet(classes);
        if (StringUtils.isNotEmpty(soaTargetService)) {
            classes.removeIf(clazz -> !clazz.isInterface() || !Objects.equals(clazz.getName(), soaTargetService));
            if (classes.isEmpty()) {
                throw new ServiceNotFoundException("Please check the configuration of the file[application.properties:ctrip.soaTargetService]");
            }
            return classes;
        }
        classes.removeIf(class1 -> !class1.isInterface());
        return classes;
    }

    public static class ServiceConfig {
        private final boolean root;
        private final String path;
        private final Class<?> service;

        public ServiceConfig(String path, Class<?> service) {
            super();
            this.root = path.equalsIgnoreCase("root") || path.equalsIgnoreCase("default");
            this.path = path;
            this.service = service;
        }

        public boolean isRoot() {
            return root;
        }

        public String getPath() {
            return path;
        }

        public Class<?> getService() {
            return service;
        }

    }

    public Map<String, String> getQconfig() {
        return sdFrameworkQConfig.getQconfig();
    }

}
