package com.googlecode.mycontainer.starter;

import java.net.URL;

public class StarterTypeResource implements StarterType {

	public void execute(String[] args) {
		BeanshellMyontainerStarter starter = new BeanshellMyontainerStarter();
		String path = args[1];
		URL url = getClass().getClassLoader().getResource(path);
		starter.setUrl(url.toString());
		starter.execute();
	}

}
