package com.ymbj.simple.autowire;

import com.ymbj.simple.bean.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.util.List;
// 参考mybatis plus的MybatisPlusAutoConfiguration
@Configuration
public class AutowireObjectProviderConfig {
	private  CustomProperties properties;

	private  Interceptor[] interceptors;// 可以加上final修饰

	private  ResourceLoader resourceLoader;

	private  List<ConfigurationCustomizer> configurationCustomizers;

	private  ApplicationContext applicationContext;

	// 结论1：有多个有参构造函数且都没有加@Autowired(required = true)注解或加了@Autowired(required = false)的情况下，且显式指定无参构造函数，此时会默认调用无参构造函数；若某个有参构造函数加上了(required = true)注解则调用这个有参构造函数，无参构造函数不会调用
	public AutowireObjectProviderConfig() {
		System.out.println();
	}
	// 结论2：有多个有参构造函数且都没有加@Autowired注解的情况下，且没有显式指定无参构造函数，此时会报NoSuchMethodException；这种情况下需要为其中一个构造函数加上@Autowired注解
	@Autowired
	public AutowireObjectProviderConfig(CustomProperties properties,
										ObjectProvider<Interceptor[]> interceptorsProvider,
										ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
										ResourceLoader resourceLoader
										/*, NoBean noBean*/) { // 结论5：@Autowired的情况下，如果方法（包括@Bean方法）参数注入中的某个参数从容器中找不到相应bean，此时会报NoSuchBeanDefinition错
		this.properties = properties;
		this.interceptors = interceptorsProvider.getIfAvailable();
		this.resourceLoader = resourceLoader;
		this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
	}
	// 结论3：仅有一个有参的构造函数时，可以不用@Autowired注解，也可以完成构造参数（可以多个构造参数）注入
	public AutowireObjectProviderConfig(CustomProperties properties,
										ObjectProvider<Interceptor[]> interceptorsProvider,
										ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
										ResourceLoader resourceLoader,
										ApplicationContext applicationContext) {
		this.properties = properties;
		this.interceptors = interceptorsProvider.getIfAvailable();
		this.resourceLoader = resourceLoader;
		this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
		this.applicationContext = applicationContext;
	}
	// 结论4：有@Bean的方法注入，可以完成多个参数bean的注入(包括@Value属性注入)
	@Bean
	public SqlSessionFactory sqlSessionFactory(ApplicationContext applicationContext, CustomProperties properties/*, NoBean noBean*/ /*@Value("${server.port}")*/) throws Exception {
		com.ymbj.simple.bean.Configuration configuration = new com.ymbj.simple.bean.Configuration();
		for (ConfigurationCustomizer customizer : configurationCustomizers) {
			customizer.customize(configuration);
		}

		return new SqlSessionFactory();
	}

	// 注意：如果配置类构造函数想通过configurationCustomizersProvider.getIfAvailable注入本类的@Bean方法的bean是注入不了的
	/*@Bean
	public ConfigurationCustomizer bConfigurationCustomizer() {
		return configuration -> {};
	}*/
}
