package com.googlecode.mycontainer.jsfprovider;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.googlecode.mycontainer.cpscanner.ClassScannerListener;

public class ClassSetListener extends ClassScannerListener {

	private final Set<String> classes = new HashSet<String>();

	@Override
	public void classFound(URL b, Class<?> clazz) {
		classes.add(clazz.getName());
	}

	public Set<String> getClasses() {
		return classes;
	}

	@Override
	public String toString() {
		return "ClassSetListener [classes=" + classes + "]";
	}

}