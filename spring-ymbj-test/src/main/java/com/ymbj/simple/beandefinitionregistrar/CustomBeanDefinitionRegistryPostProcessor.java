package com.ymbj.simple.beandefinitionregistrar;

import com.ymbj.simple.bean.GenericBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;
// 具体可以参考CachingMetadataReaderFactoryPostProcessor是怎么注册bd的，怎么给bd添加属性值的
@Component
public class CustomBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("CustomBeanDefinitionRegistryPostProcessor.postProcessBeanFactory");
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		System.out.println("CustomBeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry");
		GenericBeanDefinition gbd = new GenericBeanDefinition();
		gbd.setBeanClass(GenericBean.class);
		registry.registerBeanDefinition(GenericBean.class.getName(), gbd);
	}
}
