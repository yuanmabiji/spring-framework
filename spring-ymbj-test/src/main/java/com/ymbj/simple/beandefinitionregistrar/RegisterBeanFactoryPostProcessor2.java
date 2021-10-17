package com.ymbj.simple.beandefinitionregistrar;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class RegisterBeanFactoryPostProcessor2 implements BeanFactoryPostProcessor {
	// 注意：该方法不会被调用哈
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("RegisterBeanFactoryPostProcessor2.postProcessBeanFactory");
	}
}
