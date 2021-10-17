package com.ymbj.simple.beandefinitionregistrar;

import com.ymbj.simple.bean.GenericBean;
import com.ymbj.simple.bean.PostProcessorBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.stereotype.Component;


public class RegisterBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("RegisterBeanDefinitionRegistryPostProcessor.postProcessBeanFactory");
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		System.out.println("RegisterBeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry");
	}
}
