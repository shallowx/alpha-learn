package com.alpha.learn.spring6;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.Ordered;

@Slf4j
public class BeanFactoryTests {

    @Test
    public void test(){
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("beanFactory", new TestBean());

        GenericApplicationContext context = new GenericApplicationContext(beanFactory);
        context.refresh();

        TestBean bean = context.getBean(TestBean.class);
        log.info("bean: {}", bean);
    }

    static class TestBean implements SmartLifecycle,
            ApplicationContextAware, InitializingBean, DisposableBean, BeanNameAware {
        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public boolean isRunning() {
            return false;
        }

        @Override
        public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {

        }

        @Override
        public void afterPropertiesSet() throws Exception {

        }

        @Override
        public void destroy() throws Exception {

        }

        @Override
        public void setBeanName(@NonNull String name) {

        }
    }

    static class TestBeanPostProcessor implements BeanPostProcessor, Ordered {
        @Override
        public int getOrder() {
            return Integer.MAX_VALUE;
        }
    }

    static class TestBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
        @Override
        public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory factory) throws BeansException {}
    }
}
