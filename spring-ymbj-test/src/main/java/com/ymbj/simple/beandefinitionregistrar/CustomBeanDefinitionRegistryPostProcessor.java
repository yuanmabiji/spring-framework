package com.ymbj.simple.beandefinitionregistrar;

import com.ymbj.simple.bean.GenericBean;
import com.ymbj.simple.bean.PostProcessorBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.stereotype.Component;
// 具体可以参考CachingMetadataReaderFactoryPostProcessor是怎么注册bd的，怎么给bd添加属性值的
@Component
public class CustomBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("CustomBeanDefinitionRegistryPostProcessor.postProcessBeanFactory");
		BeanDefinition definition = BeanDefinitionBuilder
				.genericBeanDefinition(PostProcessorBean.class,
						PostProcessorBean::new)
				.getBeanDefinition();
		((DefaultListableBeanFactory)beanFactory).registerBeanDefinition(PostProcessorBean.class.getSimpleName(), definition);

		// 在CustomBeanDefinitionRegistryPostProcessor的postProcessBeanFactory注册一个BeanFactoryPostProcessor类型的bean，
		// 这个bean的postProcessBeanFactory方法还是会被调用，因为BeanDefinitionRegistryPostProcessor的postProcessBeanFactory
		// 方法会比普通的BeanFactoryPostProcessor的postProcessBeanFactory优先调用，调用完再统一从beanFactory中获取所有BeanFactoryPostProcessor类型的
		// bean,再统一调用所有BeanFactoryPostProcessor类型的bean的postProcessBeanFactory方法哈，参考PostProcessorRegistrationDelegate
		// 的invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors)方法
		BeanDefinition definition2 = BeanDefinitionBuilder
				.genericBeanDefinition(RegisterBeanFactoryPostProcessor.class,
						RegisterBeanFactoryPostProcessor::new)
				.getBeanDefinition();
		((DefaultListableBeanFactory)beanFactory).registerBeanDefinition(RegisterBeanFactoryPostProcessor.class.getSimpleName(), definition2);

	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		System.out.println("CustomBeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry");
		GenericBeanDefinition gbd = new GenericBeanDefinition();
		gbd.setBeanClass(GenericBean.class);
		registry.registerBeanDefinition(GenericBean.class.getName(), gbd);

		// 在CustomBeanDefinitionRegistryPostProcessor的postProcessBeanDefinitionRegistry方法中注册一个BeanDefinitionRegistryPostProcessor类型bd，
		// 然后也会递归调用到其postProcessBeanDefinitionRegistry和postProcessBeanFactory两个方法，参考PostProcessorRegistrationDelegate
		// 的invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors)方法
		GenericBeanDefinition gbd2 = new GenericBeanDefinition();
		gbd2.setBeanClass(RegisterBeanDefinitionRegistryPostProcessor.class);
		registry.registerBeanDefinition(RegisterBeanDefinitionRegistryPostProcessor.class.getName(), gbd2);

	}
}
