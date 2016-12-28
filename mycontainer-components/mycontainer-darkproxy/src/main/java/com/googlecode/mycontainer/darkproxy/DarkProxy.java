package com.googlecode.mycontainer.darkproxy;

import java.io.Closeable;

public class DarkProxy implements Closeable {

	private String dest = "target/requests";

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

}
