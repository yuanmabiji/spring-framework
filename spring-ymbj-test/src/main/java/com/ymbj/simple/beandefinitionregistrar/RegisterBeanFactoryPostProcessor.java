package com.ymbj.simple.beandefinitionregistrar;

import com.ymbj.simple.bean.PostProcessorBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.SocketUtils;

public class RegisterBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("RegisterBeanFactoryPostProcessor.postProcessBeanFactory");
		// 在RegisterBeanFactoryPostProcessor的postProcessBeanFactory注册一个BeanFactoryPostProcessor类型的bean，
		// 这个bean的postProcessBeanFactory方法不会被调用，因为普通BeanFactoryPostProcessor不像BeanDefinitionRegistryPostProcessor，还会从beanFactory中
		// 获取BeanDefinitionRegistryPostProcessor类型的bean从新调用其的两个方法哈
		// 参考PostProcessorRegistrationDelegate
		// 的invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors)方法
		BeanDefinition definition = BeanDefinitionBuilder
				.genericBeanDefinition(RegisterBeanFactoryPostProcessor2.class,
						RegisterBeanFactoryPostProcessor2::new)
				.getBeanDefinition();
		((DefaultListableBeanFactory)beanFactory).registerBeanDefinition(RegisterBeanFactoryPostProcessor2.class.getSimpleName(), definition);
	}
}
