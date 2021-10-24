package com.ymbj.simple.bean;

import org.springframework.stereotype.Component;

@Component
public class CglibBean {
	public String sayHello() {
		System.out.println("==========CglibBean.sayHello=============");
		return "sayHello";
	}
}
