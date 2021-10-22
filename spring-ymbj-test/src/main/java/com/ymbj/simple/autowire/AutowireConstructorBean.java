package com.ymbj.simple.autowire;

import com.ymbj.simple.bean.ConstructorBean;
import com.ymbj.simple.bean.PropertyBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutowireConstructorBean {
	private ConstructorBean constructorBean;
	// 结论：一般XxxConfiguration的XxxPropertis就是通过构造器参数传入这种方式哈
	// @Autowired // 因为本身AutowireConstructorBean是一个bean，创建AutowireConstructorBean这个bean自然会调用其构造函数，调用其构造函数自然会去容器找ConstructorBean,不用显式 @Autowired
	public AutowireConstructorBean(ConstructorBean constructorBean) {
		this.constructorBean = constructorBean;
	}

}
