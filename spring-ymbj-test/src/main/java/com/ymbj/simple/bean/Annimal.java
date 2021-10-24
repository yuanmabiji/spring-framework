package com.ymbj.simple.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;


@Component
public class Annimal {
	@Autowired
	private ApplicationContextAware applicationContextAware;

	public Annimal() {
		System.out.println("=========Annimal Bean=========");
	}
}
