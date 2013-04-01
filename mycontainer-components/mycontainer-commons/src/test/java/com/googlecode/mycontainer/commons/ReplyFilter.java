package com.googlecode.mycontainer.commons;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.mycontainer.commons.io.IOUtil;

public class ReplyFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		resp.addHeader("X-RP-Method", req.getMethod());
		resp.addHeader("X-RP-QueryString", req.getQueryString());
		Set<Entry<String, String[]>> map = req.getParameterMap().entrySet();
		for (Entry<String, String[]> entry : map) {
			String[] array = entry.getValue();
			for (String value : array) {
				resp.addHeader("X-RP-Param-" + entry.getKey(), value);
			}
		}

		Enumeration<String> headerNames = req.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			Enumeration<String> headers = req.getHeaders(name);
			while (headers.hasMoreElements()) {
				String header = headers.nextElement();
				resp.addHeader("X-RP-Header-" + name, header);
			}
		}

		ServletOutputStream out = resp.getOutputStream();
		ServletInputStream in = req.getInputStream();
		IOUtil.copy(in, out, new byte[5 * 1024]);
	}

	public void destroy() {

	}

}
