package com.ymbj.simple;

import com.ymbj.simple.bean.Person;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan("com.ymbj.simple")
@Configuration
public class AnnoConfig {

	public AnnoConfig() {
		System.out.println("=========AnnoConfig Constructor=========" );
	}



	@Bean
	public Person person() {
		System.out.println("=========Person Bean=========");
		return new Person();
	}
	// 结论：static关键字无法提升普通bean创建的优先级，只能提升BeanFactoryPostProcessor类的优先级,普通类也改为static修饰也无法提升。
	@Bean
	public static Teacher teacher() {
		System.out.println("=========Teacher Bean=========");
		return new Teacher();
	}
	// 结论：不加static会打印告警日志：因为AnnoConfig会比CustomBeanFactoryPostProcessor先创建，导致AnnoConfig无法被CustomBeanFactoryPostProcessor处理（TODO 即使这样，但是看日志postProcessBeanFactory执行是在AnnoConfig Constructor实例化前执行？）加static会提升BeanFactoryPostProcessor的创建优先级
	@Bean
	public  static BeanFactoryPostProcessor customBeanFactoryPostProcessor() {
		System.out.println("=========CustomBeanFactoryPostProcessor Bean=========");
		return new CustomBeanFactoryPostProcessor();
	}


	static class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

		@Override
		public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
			System.out.println("=========postProcessBeanFactory=========");
		}
	}

	static class Teacher{}
}
