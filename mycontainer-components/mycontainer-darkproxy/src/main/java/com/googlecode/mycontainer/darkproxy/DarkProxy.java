package com.googlecode.mycontainer.darkproxy;

import java.io.Closeable;
import java.io.File;
import java.util.SortedMap;
import java.util.TreeMap;

import com.googlecode.mycontainer.util.Util;

public class DarkProxy implements Closeable {

	private String dest = "darkreq";

	private SortedMap<Long, DarkProxyConn> conns = new TreeMap<Long, DarkProxyConn>();

	public String getDest() {
		return dest;
	}

	public DarkProxy setDest(String dest) {
		this.dest = dest;
		return this;
	}

	public void close() {

	}

	public void cleanDest() {
		Util.deleteAll(dest);
		if (!new File(dest).mkdirs()) {
			throw new RuntimeException("we can not create directory: " + dest);
		}
	}

	public void register(DarkProxyRequest req) {
		DarkProxyConn ret = conns.get(req.getId());
		if (ret == null) {
			ret = new DarkProxyConn();
			ret.setId(req.getId());
			conns.put(req.getId(), ret);
		}
		ret.setRequest(req);
	}

	public synchronized void register(DarkProxyResponse resp) {
		DarkProxyConn ret = conns.get(resp.getId());
		ret.setResponse(resp);
	}

	public synchronized void remove(Long id) {
		conns.remove(id);
	}

	public synchronized DarkProxyConn getFirst() {
		if (conns.isEmpty()) {
			return null;
		}
		Long key = conns.firstKey();
		return conns.get(key);
	}

}
