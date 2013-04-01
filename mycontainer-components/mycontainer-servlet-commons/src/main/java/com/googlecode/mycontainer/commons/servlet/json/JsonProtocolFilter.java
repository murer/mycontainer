package com.googlecode.mycontainer.commons.servlet.json;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.mycontainer.commons.servlet.EngineResponseWrapper;

public class JsonProtocolFilter implements Filter {

	public void destroy() {

	}

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {

		String callback = req.getParameter("callback");
		if (callback == null) {
			callback = "";
		}
		callback = callback.trim();
		if (callback.length() == 0) {
			chain.doFilter(req, resp);
			return;
		}

		HttpServletResponse r = (HttpServletResponse) resp;
		EngineResponseWrapper wrapper = new EngineResponseWrapper(r);
		chain.doFilter(req, wrapper);

		byte[] buffer = wrapper.getBuffer();
		if (buffer.length > 0) {
			r.setContentType("text/javascript");
			ServletOutputStream out = r.getOutputStream();
			out.print(callback);
			out.print("(");
			out.write(buffer);
			out.print(");");
		}
	}

	public void init(FilterConfig config) throws ServletException {

	}

}
