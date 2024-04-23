package com.ctriposs.baiji.rpc.server;

import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctrip.car.osd.framework.soa.server.autoconfigure.SoaConfigService;
import com.ctrip.car.osd.framework.soa.server.autoconfigure.SoaServiceConfigProperties;
import com.ctriposs.baiji.rpc.common.BaijiContract;
import com.ctriposs.baiji.rpc.server.vi.component.SOAIgnition;
import org.apache.commons.collections.MapUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 初始化 Baiji 实例信息
 *
 * @author xiayx@Ctrip.com
 */
@WebListener
public class BaijiWebListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaijiWebListener.class);
    private ApplicationContext applicationContext;
    private SoaConfigService configService;

    /**
     * This interfaces could be implemented by users.
     */
    private static final Class<?>[] CUSTOM_SERVER_INTERFACES = {
            CustomServerConfigurer.class,
            CustomServerInitializer.class,
            CustomServerPostInitializer.class
    };

    public BaijiWebListener() {
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        BaijiServiceInitializer.INSTANCE.setServletContext(sc);
        BaijiServiceInitializer.INSTANCE.preInitialize();
        this.preInitialize(sc);
        BaijiServiceInitializer.INSTANCE.initialize();
        this.postInitialize();
        BaijiServiceInitializer.INSTANCE.complete();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        BaijiServiceInitializer.INSTANCE.destroy();
    }

    private synchronized void preInitialize(ServletContext sc) {
        if (BaijiServiceInitializer.INSTANCE.hasError()) {
            return;
        }
        try {
            SOAIgnition.getInstance().setTopLevelLog("Init custom initializers ...");
            prepareContext(sc);
            addServiceInstance();
            addCustomConfigBean();
        } catch (Throwable ex) {
            BaijiServiceInitializer.INSTANCE.setError(ex);
        }
    }

    private void prepareContext(ServletContext sc) {
        this.applicationContext = (ApplicationContext) sc.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        this.configService = applicationContext.getBean(SoaConfigService.class);
        if (MapUtils.isEmpty(this.configService.getQconfig())) {
            LOGGER.warn("QConfig未发现[sd-framework.properties]文件，或者配置列表为空");
        }
    }

    private void addServiceInstance() {
        BaijiServletContext.INSTANCE.setApplicationContext(applicationContext);
        Collection<SoaConfigService.ServiceConfig> registerServices = configService.getRegisterServices();
        for (SoaConfigService.ServiceConfig serviceConfig : registerServices) {
            Object serviceInstance = applicationContext.getBean(serviceConfig.getService().getName());
            BaijiServiceInitializer.INSTANCE.addBean(serviceInstance);
        }
    }

    private void addCustomConfigBean() {
        for (Class<?> clazz : CUSTOM_SERVER_INTERFACES) {
            Map<String, ?> beans = applicationContext.getBeansOfType(clazz);
            if (null != beans && !beans.isEmpty()) {
                for (Object bean : beans.values()) {
                    BaijiServiceInitializer.INSTANCE.addBean(bean);
                }
            }
        }
    }

    private void postInitialize() {
        List<ServiceHost> allServiceHosts = BaijiServletContext.INSTANCE.getAllServiceHosts();
    }

}