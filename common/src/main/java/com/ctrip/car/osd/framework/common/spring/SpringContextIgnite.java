package com.ctrip.car.osd.framework.common.spring;

import com.ctrip.framework.vi.IgniteManager;
import com.ctrip.framework.vi.annotation.Ignite;
import com.ctrip.framework.vi.ignite.AbstractCtripIgnitePlugin;

import java.util.Map;

@Ignite(id = "car.osd.spring.ignite", type = Ignite.PluginType.Component, after = {"qunarcommon.ignite"}, auto = true)
public class SpringContextIgnite extends AbstractCtripIgnitePlugin {
    @Override
    public String helpUrl() {
        return "https://spring.io/projects/spring-framework";
    }

    @Override
    public boolean warmUP(IgniteManager.SimpleLogger logger) {
        while (SpringApplicationContextHolder.getContext() == null) {
            try {
                logger.info("Spring context ignite is running.");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Spring context ignite failured.", e);
                return false;
            }
        }
        logger.info("Spring context ignite success.");
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
