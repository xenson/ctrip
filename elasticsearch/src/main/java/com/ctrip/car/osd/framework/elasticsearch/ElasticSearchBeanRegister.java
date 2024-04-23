package com.ctrip.car.osd.framework.elasticsearch;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

public class ElasticSearchBeanRegister extends InstantiationAwareBeanPostProcessorAdapter implements BeanFactoryAware {

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
        }
        ConfigurableListableBeanFactory factory = (ConfigurableListableBeanFactory) beanFactory;
        factory.getBean(ElasticSearchInitializer.class);
    }
}
