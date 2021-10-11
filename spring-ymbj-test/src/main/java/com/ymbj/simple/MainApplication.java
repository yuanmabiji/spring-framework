package com.ymbj.simple;

import com.ymbj.simple.bean.Annimal;
import com.ymbj.simple.bean.Person;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainApplication {
	public static void main(String args[]) {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AnnoConfig.class);
		Person person = (Person)ac.getBean("person");
		Annimal annimal = (Annimal)ac.getBean("annimal");
		System.out.println();
	}
}
