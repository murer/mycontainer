package com.googlecode.mycontainer.darkproxy;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DarkProxyFilter implements Filter {

	private DarkProxy proxy;

	public void init(FilterConfig cfg) throws ServletException {
		if (proxy == null) {
			throw new RuntimeException("wrong");
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		filter((HttpServletRequest) request, (HttpServletResponse) response);
	}

	public void filter(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String uri = DarkProxyMeta.uri(request);
		if (uri.startsWith("/_darkproxy/")) {
			DarkProxyMeta.filter(proxy, request, response);
			return;
		}

		DarkProxyRequest req = DarkProxyRequest.parse(request, proxy.getDest());
		req.setHost("repoz.dextra.com.br");
		req.setPort(80);
		req.setSchema("http");
		proxy.register(req);
		req.waitFor();
		DarkProxyResponse resp = new DarkProxyResponse();
		resp.setId(req.getId());
		resp.forward(req, proxy.getDest());
		proxy.register(resp);
		resp.waitFor();
		proxy.remove(req.getId());
		resp.writeTo(proxy.getDest(), response);
	}

	public void destroy() {

	}

	public void setProxy(DarkProxy proxy) {
		this.proxy = proxy;
	}

}
