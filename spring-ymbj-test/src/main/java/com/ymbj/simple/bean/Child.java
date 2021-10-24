package com.ymbj.simple.bean;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

// @DependsOn("parent") // 如果父子互相depend on会抛出异常
@Component
public class Child extends Parent{
	int age = 20;
}
