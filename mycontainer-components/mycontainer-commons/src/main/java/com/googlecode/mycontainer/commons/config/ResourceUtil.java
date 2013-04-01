package com.googlecode.mycontainer.commons.config;

import java.net.URL;

public class ResourceUtil {

	public static URL getResource(Class<?> clazz, String name) {
		URL url = clazz.getClassLoader().getResource(name);
		if (url == null) {
			url = clazz.getResource(name);
		}
		if (url == null) {
			throw new RuntimeException("resource not found: " + clazz.getName()
					+ " " + name);
		}
		return url;
	}

}
