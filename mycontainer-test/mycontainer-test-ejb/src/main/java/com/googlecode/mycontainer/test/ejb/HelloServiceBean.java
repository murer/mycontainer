package com.googlecode.mycontainer.test.ejb;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

import com.googlecode.mycontainer.test.HelloService;

@Stateless
public class HelloServiceBean implements HelloService {
	
	private String hello;
	
	
	@PostConstruct
	public void ejbCreate() {
		hello = "Hello World";
	}
	
	
	public String sayHello() {
		return hello;
	}
	

}
