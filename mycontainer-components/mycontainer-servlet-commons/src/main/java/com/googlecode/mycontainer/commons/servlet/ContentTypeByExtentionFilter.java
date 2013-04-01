package com.googlecode.mycontainer.commons.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.mycontainer.commons.file.ContentTypeUtil;

public class ContentTypeByExtentionFilter implements Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletResponse r = (HttpServletResponse) response;
		EngineResponseWrapper wrapper = new EngineResponseWrapper(r);
		chain.doFilter(request, wrapper);

		HttpServletRequest req = (HttpServletRequest) request;
		String contentType = req.getParameter("contentType");
		if (contentType == null) {
			contentType = wrapper.getContentType();
		}

		if (contentType == null) {
			String uri = req.getRequestURI();
			contentType = ContentTypeUtil.getContentTypeByPath(uri);
		}
		if (contentType != null) {
			response.setContentType(contentType);
		}

		byte[] buffer = wrapper.getBuffer();
		response.getOutputStream().write(buffer);

	}

	public void init(FilterConfig config) throws ServletException {
	}
}
