package com.ymbj.simple.configuration;

import com.ymbj.simple.beandefinitionregistrar.CustomImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CustomImportBeanDefinitionRegistrar.class)
public class Config {
}
