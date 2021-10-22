package com.ymbj.simple.autowire;

import com.ymbj.simple.bean.PropertyBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutowirePropertyBean {

// @Autowired // 结论：若注释掉@Autowired，那么setSetterBean方法不会被调用，虽然SetterBean实例已经放入spring容器，不像AutowireConstructorBean是构造器注入，因为本身AutowireConstructorBean是一个bean，创建AutowireConstructorBean这个bean自然会调用其构造函数，调用其构造函数自然会去容器找ConstructorBean
	@Autowired
	private PropertyBean aBean;
}
