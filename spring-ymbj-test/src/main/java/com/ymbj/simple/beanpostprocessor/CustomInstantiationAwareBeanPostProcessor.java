package com.ymbj.simple.beanpostprocessor;

import com.ymbj.simple.bean.CglibBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.stereotype.Component;


@Component
public class CustomInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		if (CglibBean.class == beanClass) {
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(beanClass);
			enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
				System.out.println(">>>>MethodInterceptor start...");
				Object result = proxy.invokeSuper(obj, args);
				System.out.println(">>>>MethodInterceptor ending...");
				return "sayHi";
			});
			return enhancer.create();
		}
		return null;
	}
}
