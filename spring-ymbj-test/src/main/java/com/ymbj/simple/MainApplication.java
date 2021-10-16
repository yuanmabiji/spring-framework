package com.ymbj.simple;

import com.ymbj.simple.bean.Annimal;
import com.ymbj.simple.bean.GenericBean;
import com.ymbj.simple.bean.Person;
import com.ymbj.simple.bean.RootBean;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainApplication {
	public static void main(String args[]) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AnnoConfig.class);
		// ac.refresh(); // AnnotationConfigApplicationContext属于GenericApplicationContext，多次刷新会抛出IllegalStateException
		// Person person = (Person)ac.getBean("person");
		// Annimal annimal = (Annimal)ac.getBean("annimal");
		// ac.getBeansOfType(ApplicationContextAware.class);
		GenericBean genericBean = ac.getBean(GenericBean.class);
		RootBean rootBean = ac.getBean(RootBean.class);
		System.out.println();
	}
}
