package com.googlecode.mycontainer.starter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class StarterTypeFile implements StarterType {

	public void execute(String[] args) {
		try {
			BeanshellMyontainerStarter starter = new BeanshellMyontainerStarter();
			String path = args[1];
			URL url = new File(path).toURI().toURL();
			starter.setUrl(url.toString());
			starter.execute();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

}
