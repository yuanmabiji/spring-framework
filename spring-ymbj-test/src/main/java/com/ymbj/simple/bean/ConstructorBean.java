package com.ymbj.simple.bean;

import org.springframework.stereotype.Component;

@Component
public class ConstructorBean {
	public ConstructorBean() {
		System.out.println("==============ConstructorBean Constructor============");
	}
}
