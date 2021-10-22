package com.ymbj.simple.autowire;

import com.ymbj.simple.bean.ConfigurationCustomizer;
import com.ymbj.simple.bean.CustomProperties;
import com.ymbj.simple.bean.Interceptor;
import com.ymbj.simple.bean.SqlSessionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.util.List;
// 参考mybatis plus的MybatisPlusAutoConfiguration
@Configuration
public class AutowireObjectProviderConfig {
	private final CustomProperties properties;

	private final Interceptor[] interceptors;

	private final ResourceLoader resourceLoader;

	private final List<ConfigurationCustomizer> configurationCustomizers;

	private final ApplicationContext applicationContext;

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

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
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
