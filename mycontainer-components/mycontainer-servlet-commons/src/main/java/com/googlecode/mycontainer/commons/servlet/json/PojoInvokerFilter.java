package com.googlecode.mycontainer.commons.servlet.json;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PojoInvokerFilter extends InvokerFilter {

	private String root;

	public void init(FilterConfig config) throws ServletException {
		this.root = config.getInitParameter("root");
		if (root == null) {
			root = "";
		}
		root = root.trim();
		if (root.length() > 0 && !root.endsWith(".")) {
			root += ".";
		}
		super.init(config);
	}

	@Override
	protected Object lookup(HttpServletRequest req, HttpServletResponse resp,
			String name, String method, String[] args) {
		try {
			Class<?> clazz = Class.forName(root + name);
			Object obj = clazz.newInstance();
			return obj;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
