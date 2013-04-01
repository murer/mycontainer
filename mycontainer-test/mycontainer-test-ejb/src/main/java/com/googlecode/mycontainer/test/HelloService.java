package com.googlecode.mycontainer.test;

import javax.ejb.Local;

@Local
public interface HelloService {
	
	public String sayHello();

}
