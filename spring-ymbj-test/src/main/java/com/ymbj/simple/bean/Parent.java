package com.ymbj.simple.bean;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@DependsOn("child")
@Component
public class Parent {
	protected String name = "parentName";
	protected int age = 40;
}
