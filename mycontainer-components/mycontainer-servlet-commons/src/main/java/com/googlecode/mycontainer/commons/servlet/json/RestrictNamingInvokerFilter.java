package com.googlecode.mycontainer.commons.servlet.json;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.googlecode.mycontainer.commons.reflect.BlockingInvocationHandlerAdapter;
import com.googlecode.mycontainer.commons.servlet.json.NamingInvokerFilter;

public class RestrictNamingInvokerFilter extends NamingInvokerFilter {

	private String domain;

	public void init(FilterConfig config) throws ServletException {
		domain = config.getInitParameter("domain");
		if (domain == null) {
			domain = "web";
		}
		domain = domain.trim();

		super.init(config);
	}

	@Override
	protected Object lookup(HttpServletRequest request,
			HttpServletResponse response, String name, String method,
			String[] args) {
		Object impl = super.lookup(request, response, name, method, args);
		BlockingInvocationHandlerAdapter adapter = new BlockingInvocationHandlerAdapter(
				impl, domain);

		Class<?>[] interfaces = impl.getClass().getInterfaces();
		return adapter.createProxy(interfaces);
	}

}
