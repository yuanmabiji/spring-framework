package com.ymbj.simple.bean;

import org.springframework.stereotype.Component;

@Component
public class ConfigConstructorBean {
	public ConfigConstructorBean() {
		System.out.println("==============ConfigConstructorBean Constructor============");
	}
}
