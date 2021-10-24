package com.ymbj.simple;

import com.ymbj.simple.bean.CglibBean;
import com.ymbj.simple.bean.GenericBean;
import com.ymbj.simple.bean.RootBean;
import com.ymbj.simple.beandefinition.Person;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
// TODO 有空研究下@Lookup注解
public class MainApplication {
	public static void main(String args[]) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AnnoConfig.class);
		GenericBeanDefinition gbd = new GenericBeanDefinition();
		gbd.setBeanClass(Person.class);
		ac.registerBeanDefinition("myPerson", gbd);
		// ac.refresh(); // AnnotationConfigApplicationContext属于GenericApplicationContext，多次刷新会抛出IllegalStateException
		// Person person = (Person)ac.getBean("person");
		// Annimal annimal = (Annimal)ac.getBean("annimal");
		// ac.getBeansOfType(ApplicationContextAware.class);
		/*GenericBean genericBean = ac.getBean(GenericBean.class);
		RootBean rootBean = ac.getBean(RootBean.class);*/
		Object myPerson = ac.getBean("myPerson");
		Object parentBean = ac.getBean("parentBean");
		Object childBean = ac.getBean("childBean"); // 通过bd注册的话，子bean会覆盖父bean的同名属性
		Object parent = ac.getBean("parent");
		Object child = ac.getBean("child"); // 通过@Component注册的父子bean，子bean不会覆盖父bean的同名属性
		CglibBean cglibBeanProxy = (CglibBean)ac.getBean("cglibBean");
		String result = cglibBeanProxy.sayHello();
 		System.out.println();
		ac.stop(); // 这句日志并不能吊起DisposableBean的destroy方法和@PreDestroy注解的方法
	}
}
