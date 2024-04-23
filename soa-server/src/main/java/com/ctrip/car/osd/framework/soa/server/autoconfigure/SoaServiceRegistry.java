package com.ctrip.car.osd.framework.soa.server.autoconfigure;

import com.ctrip.car.osd.framework.soa.server.exception.ServiceNotFoundException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.common.utils.ConcurrentHashSet;

import java.util.Set;

/**
 * @author xh.gao
 * @date 2023/3/14 21:49
 */
public class SoaServiceRegistry {

    private static final Set<Class<?>> SERVICE_REGISTRY = new ConcurrentHashSet<>();

    public static void registerSoaService(Set<Class<?>> classes) {
        SERVICE_REGISTRY.addAll(classes);
    }

    public static Set<Class<?>> getServiceRegistry() {
        if (CollectionUtils.isEmpty(SERVICE_REGISTRY)) {
            throw new ServiceNotFoundException("Please check the configuration of the file [application.properties]");
        }
        return SERVICE_REGISTRY;
    }
}
