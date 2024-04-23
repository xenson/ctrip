package com.ctrip.car.osd.framework.cache.ignite;

import com.ctrip.car.osd.framework.common.spring.SpringApplicationContextHolder;
import com.ctrip.framework.vi.IgniteManager;
import com.ctrip.framework.vi.annotation.Ignite;
import com.ctrip.framework.vi.ignite.AbstractCtripIgnitePlugin;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

@Ignite(id = "car.osd.cache.ignite", type = Ignite.PluginType.Component, after = {"car.osd.spring.ignite"}, auto = true)
public class CacheIgnite extends AbstractCtripIgnitePlugin {
    @Override
    public String helpUrl() {
        return "http://conf.ctripcorp.com/pages/viewpage.action?pageId=162139775";
    }

    @Override
    public boolean warmUP(IgniteManager.SimpleLogger logger) {
        logger.info("start car-osd cache WARM UP.");
        Map<String, CacheInit> cacheInitMap = SpringApplicationContextHolder.getBeansOfType(CacheInit.class);
        if (MapUtils.isNotEmpty(cacheInitMap)) {
            cacheInitMap.values().forEach(ci->{
                String name = ci.name();
                logger.info("start init "+ name);
                ci.init(logger);
                logger.info("end init "+ name);
            });
        }
        logger.info("end car-osd WARM UP.");
        return true;
    }

    @Override
    public Map<String, String> coreConfigs() {
        return null;
    }

    @Override
    public boolean selfCheck(IgniteManager.SimpleLogger simpleLogger) {
        return true;
    }
}
