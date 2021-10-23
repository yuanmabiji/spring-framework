package com.ymbj.simple.configuration;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// TODO QUESTION:如果在@Bean非静态方法创建beanFactoryPostProcessor或BeanDefinitionRegistryPostProcessor也会打印告警日志，这个问题更严重好像会导致@Bean方法代理失败，待分析
@Configuration
public class NonStaticBeanPostProcessorConfig {

	public NonStaticBeanPostProcessorConfig() {
		System.out.println("===========NonStaticBeanPostProcessorConfig Constructor==================");
	}
	// 下面的static修饰的话，则不会打印这个警告日志
	// 十月 23, 2021 11:14:12 下午 org.springframework.context.support.PostProcessorRegistrationDelegate$BeanPostProcessorChecker postProcessAfterInitialization
	//信息: Bean 'nonStaticBeanPostProcessorConfig' of type [com.ymbj.simple.configuration.NonStaticBeanPostProcessorConfig$$EnhancerBySpringCGLIB$$982c7a5e] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
	@Bean
	public /*static*/CustomBeanPostProcessor customBeanPostProcessor() {
		System.out.println("===========NonStaticBeanPostProcessorConfig.customBeanPostProcessor==================");
		return new CustomBeanPostProcessor();
	}

}

class CustomBeanPostProcessor implements BeanPostProcessor {

}
