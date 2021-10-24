package com.ymbj.simple.beandefinition;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

// 注册父子bd
public class CustomImportBeanDefinitionRegistrar2 implements ImportBeanDefinitionRegistrar {
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		// 注册parent bd
		GenericBeanDefinition pbd= new GenericBeanDefinition();
		pbd.setBeanClass(Person.class);
		pbd.getPropertyValues().add("name", "parentName");
		pbd.getPropertyValues().add("age", 40);
		pbd.getPropertyValues().add("sex", "male");
		registry.registerBeanDefinition("parentBean",pbd);

		// 注册child bd
		GenericBeanDefinition cbd= new GenericBeanDefinition();
		cbd.setParentName("parentBean");
		cbd.setBeanClass(Person.class);
		cbd.getPropertyValues().add("name", "childName");
		cbd.getPropertyValues().add("age", 20);
		registry.registerBeanDefinition("childBean",cbd);
	}
}
