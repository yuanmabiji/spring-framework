package com.ymbj.simple.autowire;

import com.ymbj.simple.bean.ConfigConstructorBean;
import com.ymbj.simple.bean.ConstructorBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class AutowireConfigConstructorBean {
	private ConfigConstructorBean constructorBean;
	// // 因为本身AutowireConfigConstructorBean是一个bean，创建AutowireConfigConstructorBean这个bean自然会调用其构造函数，调用其构造函数自然会去容器找ConfigConstructorBean,不用显式 @Autowired
	public AutowireConfigConstructorBean(ConfigConstructorBean constructorBean) {
		this.constructorBean = constructorBean;
	}

}
