package com.googlecode.mycontainer.darkproxy;

import java.io.Closeable;
import java.io.File;

import com.googlecode.mycontainer.util.Util;

public class DarkProxy implements Closeable {

	private String dest = "darkreq";

	public String getDest() {
		return dest;
	}

	public DarkProxy setDest(String dest) {
		this.dest = dest;
		return this;
	}

	public void close() {

	}

	public void proxy(DarkProxyRequest req) {

	}

	public void cleanDest() {
		Util.deleteAll(dest);
		if (!new File(dest).mkdirs()) {
			throw new RuntimeException("we can not create directory: " + dest);
		}
	}

}
