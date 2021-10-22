package com.ymbj.simple.autowire;

import com.ymbj.simple.bean.ConfigSetterBean;
import com.ymbj.simple.bean.SetterBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class AutowireConfigSetterBean {

	private ConfigSetterBean setterBean;

	// 因为本身setSetterBean是一个bean，创建setSetterBean这个bean自然会调用其构造函数，调用其构造函数自然会去容器找ConfigSetterBean,不用显式 @Autowired
	@Bean
	public Object setSetterBean(ConfigSetterBean setterBean) {
		return setterBean;
	}
}
