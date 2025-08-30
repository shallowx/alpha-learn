package com.alpha.learn.spring6;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.FactoryBean;

import java.util.HashMap;
import java.util.Map;

public class FactoryBeanTests {

    // getBean("&bean")
    @Test
    public void test() throws Exception {
        SimpleFactoryBean factoryBean = new SimpleFactoryBean(TestEnum.DEFAULT);
        TestFactoryBean bean = factoryBean.getObject();
        TestFactoryBean bean1 = factoryBean.getObject();
        Assertions.assertNotNull(bean);
        Assertions.assertNotNull(bean1);
        Assertions.assertNotEquals(bean, bean1);

        SimpleFactoryBean factoryBean1 = new SimpleFactoryBean(TestEnum.SIMPLE);
        TestFactoryBean cacheBean = factoryBean1.getObject();
        TestFactoryBean cacheBean1 = factoryBean1.getObject();
        Assertions.assertNotNull(cacheBean);
        Assertions.assertNotNull(cacheBean1);
        Assertions.assertEquals(cacheBean, cacheBean1);
    }

    static class SimpleFactoryBean implements FactoryBean<TestFactoryBean> {
        private final TestEnum testEnum;
        private Map<TestEnum, TestFactoryBean> map = new HashMap<>();

        public SimpleFactoryBean(TestEnum testEnum) {
            this.testEnum = testEnum;
        }

        @Override
        public TestFactoryBean getObject() throws Exception {
            if (testEnum.equals(TestEnum.SIMPLE)) {
                return map.computeIfAbsent(TestEnum.SIMPLE, v-> new TestFactoryBean());
            }
            return new TestFactoryBean();
        }

        @Override
        public Class<?> getObjectType() {
            return TestFactoryBean.class;
        }
    }

    static class TestFactoryBean {
        public TestFactoryBean() {}
    }

    enum TestEnum {
        SIMPLE, DEFAULT
    }
}
