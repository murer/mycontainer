package com.googlecode.mycontainer.darkproxy;

import java.io.Closeable;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.mycontainer.util.Util;

public class DarkProxy implements Closeable {

	private String dest = "darkreq";

	private Map<Long, DarkProxyRequest> requests = new HashMap<Long, DarkProxyRequest>();

	public String getDest() {
		return dest;
	}

	public DarkProxy setDest(String dest) {
		this.dest = dest;
		return this;
	}

	public void close() {

	}

	public synchronized void register(DarkProxyRequest req) {
		requests.put(req.getId(), req);
	}

	public void cleanDest() {
		Util.deleteAll(dest);
		if (!new File(dest).mkdirs()) {
			throw new RuntimeException("we can not create directory: " + dest);
		}
	}

}
