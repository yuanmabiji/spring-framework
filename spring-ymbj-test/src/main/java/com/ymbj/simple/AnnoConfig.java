package com.ymbj.simple;

import com.ymbj.simple.bean.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan("com.ymbj.simple")
@Configuration
public class AnnoConfig {

	@Bean
	public Person person() {
		System.out.println("=========Person Bean=========");
		return new Person();
	}
}
