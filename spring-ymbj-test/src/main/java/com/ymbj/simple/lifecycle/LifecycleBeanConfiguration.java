package com.ymbj.simple.lifecycle;

import com.ymbj.simple.bean.LifecycleBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class LifecycleBeanConfiguration {
	// 与@PostConstruct和@PreDestroy类似
	@Bean(initMethod = "init", destroyMethod = "destroy")
	public LifecycleBean lifecycleBean() {
		return new LifecycleBean();
	}
}
