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

import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DarkProxyFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(DarkProxyFilter.class);

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
		proxy.register(req);
		LOG.info("Request: {} {} {}", new Object[] { Long.toHexString(req.getId()), req.getMethod(), req.getUri() });
		req.waitFor();
		LOG.info("Proxing: {} {} {}", new Object[] { Long.toHexString(req.getId()), req.getMethod(), req.getUri() });
		req.reload(proxy.getDest());
		DarkProxyResponse resp = new DarkProxyResponse();
		resp.setId(req.getId());
		forward(req, proxy.getDest());
		LOG.info("Response: {} {} {}: {}",
				new Object[] { Long.toHexString(req.getId()), req.getMethod(), req.getUri(), resp.getCode() });
		proxy.register(resp);
		resp.waitFor();
		resp.reload(proxy.getDest(), request);
		proxy.remove(req.getId());
		resp.writeTo(proxy.getDest(), response);
	}

	private void forward(DarkProxyRequest req, String dest) {
		DefaultHttpClient client = new DefaultHttpClient();
	}

	public void destroy() {

	}

	public void setProxy(DarkProxy proxy) {
		this.proxy = proxy;
	}

}
