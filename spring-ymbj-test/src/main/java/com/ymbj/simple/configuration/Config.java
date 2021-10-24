package com.ymbj.simple.configuration;

import com.ymbj.simple.bean.ConfigurationCustomizer;
import com.ymbj.simple.beandefinition.CustomImportBeanDefinitionRegistrar2;
import com.ymbj.simple.beandefinitionregistrar.CustomImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CustomImportBeanDefinitionRegistrar.class, CustomImportBeanDefinitionRegistrar2.class})
public class Config {
	@Bean
	public ConfigurationCustomizer bConfigurationCustomizer() {
		return configuration -> {};
	}
}
