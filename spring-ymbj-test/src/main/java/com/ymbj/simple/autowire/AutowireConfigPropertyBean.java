package com.ymbj.simple.autowire;

import com.ymbj.simple.bean.ConfigPropertyBean;
import com.ymbj.simple.bean.PropertyBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class AutowireConfigPropertyBean {

	@Autowired // 去掉@Autowired 不会注入ConfigPropertyBean
	private ConfigPropertyBean aBean;
}
