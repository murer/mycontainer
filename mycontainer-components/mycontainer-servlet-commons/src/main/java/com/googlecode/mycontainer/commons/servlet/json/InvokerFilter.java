package com.googlecode.mycontainer.commons.servlet.json;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.mycontainer.commons.file.PathUtil;
import com.googlecode.mycontainer.commons.servlet.ServletUtil;

public abstract class InvokerFilter implements Filter {

	private String ignore;

	public void init(FilterConfig config) throws ServletException {
		ignore = config.getInitParameter("ignore");
		if (ignore == null) {
			ignore = "";
		}
		ignore = ignore.trim();
	}

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String[] args = req.getParameterValues("args");
		String path = ServletUtil.getUserPath(req, ignore);
		List<String> split = PathUtil.split(path);

		String name = split.get(0);
		String method = split.get(1).replaceAll("\\..*", "");
		Object obj = lookup(req, resp, name, method, args);

		invoke(chain, req, resp, args, method, obj);
	}

	protected void invoke(FilterChain chain, HttpServletRequest req,
			HttpServletResponse resp, String[] args, String method, Object obj)
			throws ServletException {
		JsonInvoker invoker = new JsonInvoker(obj);
		try {
			invoker.hardInvoke(req, resp, chain, method, args);
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new ServletException(e);
		}
	}

	protected abstract Object lookup(HttpServletRequest req,
			HttpServletResponse resp, String name, String method, String[] args);
}
