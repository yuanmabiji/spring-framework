package com.ymbj.simple.bean;

public class LifecycleBean {

	public void init() {
		System.out.println("==============LifecycleBean.init============");
	}

	public void destroy() {
		System.out.println("==============LifecycleBean.destroy============");
	}

}
