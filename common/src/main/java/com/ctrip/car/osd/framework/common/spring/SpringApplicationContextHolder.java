package com.ctrip.car.osd.framework.common.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpringApplicationContextHolder extends SpringInitializingBean implements ApplicationListener {
    private static AtomicBoolean refreshed = new AtomicBoolean(false);

    public static ApplicationContext getContext(){
        if(!refreshed.get()){
            return null;
        }
        return getApplicationContext();
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> inter){
        return getApplicationContext().getBeansOfType(inter);
    }

    public static <T> T getBean(Class<T> inter){
        return getApplicationContext().getBean(inter);
    }

    public static Object getBean(String beanName){
        return getApplicationContext().getBean(beanName);
    }

    public static <T> T getBean(String beanName,Class<T> inter){
        return getApplicationContext().getBean(beanName,inter);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        refreshed.set(true);
    }
}
