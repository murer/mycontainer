package com.googlecode.mycontainer.starter;

public class StarterTypeUrl implements StarterType {

	public void execute(String[] args) {
		BeanshellMyontainerStarter starter = new BeanshellMyontainerStarter();
		starter.setUrl(args[1]);
		starter.execute();
	}

}
