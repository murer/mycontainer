package com.googlecode.mycontainer.commons.servlet.json;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NamingInvokerFilter extends InvokerFilter {

	@Override
	protected Object lookup(HttpServletRequest req, HttpServletResponse resp,
			String name, String method, String[] args) {
		try {
			name = name.replaceAll("\\.", "/");
			Object obj = new InitialContext().lookup(name);
			return obj;
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

}
