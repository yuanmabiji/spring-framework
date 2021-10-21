package com.ymbj.simple.lifecycle.destroy;

import org.springframework.stereotype.Component;
import javax.annotation.PreDestroy;

// 总结：当jvm停止即容器销毁时，destroy方法没执行，
// 是因为AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AnnoConfig.class);
// 方式没进行JVM钩子的注册，所以执行不到，但springboot的启动是有jvm钩子的注册的，所以当jvm停止spring销毁时会执行到destroy方法的
@Component
public class CustomPreDestroyBean {

	@PreDestroy
	public void destroy(){
		System.out.println("==============CustomPreDestroyBean.destroy==============");
	}
}
