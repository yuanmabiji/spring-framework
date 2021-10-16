package com.ymbj.simple.beandefinitionregistrar;

import com.ymbj.simple.bean.RootBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
// 总结：ImportBeanDefinitionRegistrar是ConfigurationClassPostProcessor这个后置处理器处理调用的，因此比自定义的CustomBeanDefinitionRegistryPostProcessor这个后置处理器之前调用
public class CustomImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		System.out.println("CustomImportBeanDefinitionRegistrar.registerBeanDefinitions");
		RootBeanDefinition rbd = new RootBeanDefinition(RootBean.class);
		registry.registerBeanDefinition(RootBean.class.getSimpleName(), rbd);
	}
}
