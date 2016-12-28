package com.googlecode.mycontainer.darkproxy;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DarkProxyHeaders {

	private Map<String, List<String>> headers = new HashMap<String, List<String>>();

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public DarkProxyHeaders setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
		return this;
	}

	public DarkProxyHeaders addAll(String name, Enumeration<String> values) {
		while (values.hasMoreElements()) {
			String value = values.nextElement();
			add(name, value);
		}
		return this;
	}

	public DarkProxyHeaders add(String name, String value) {
		List<String> list = headers.get(name);
		if (list == null) {
			list = new ArrayList<String>();
			headers.put(name, list);
		}
		list.add(value);
		return this;
	}

	public String first(String name) {
		List<String> ret = headers.get(name);
		if (ret == null || ret.isEmpty()) {
			return null;
		}
		return ret.get(0);
	}

	public DarkProxyHeaders setHeaders(String key, List<String> values) {
		headers.put(key, new ArrayList<String>(values));
		return this;
	}

	public void set(String name, String value) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(value);
		headers.put(name, list);
	}

}
