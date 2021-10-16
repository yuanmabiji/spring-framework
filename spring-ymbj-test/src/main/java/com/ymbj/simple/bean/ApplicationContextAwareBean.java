package com.ymbj.simple.bean;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

//@Component
public class ApplicationContextAwareBean implements ApplicationContextAware {
	private ApplicationContext ac;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException{
		this.ac =ac;
	}


}
