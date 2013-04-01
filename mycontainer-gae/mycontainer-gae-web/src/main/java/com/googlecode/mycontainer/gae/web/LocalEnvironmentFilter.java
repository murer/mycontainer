package com.googlecode.mycontainer.gae.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;

public class LocalEnvironmentFilter implements Filter {

	private Environment environment;

	public LocalEnvironmentFilter() {

	}

	public LocalEnvironmentFilter(Environment environment) {
		this.environment = environment;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public void init(FilterConfig config) throws ServletException {
		if (environment == null) {
			environment = ApiProxy.getCurrentEnvironment();
		}
		config.getServletContext().setAttribute("com.google.appengine.devappserver.ApiProxyLocal",
				ApiProxy.getDelegate());
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		ApiProxy.setEnvironmentForCurrentThread(getEnvironment());
		chain.doFilter(request, response);
	}

	public void destroy() {

	}

}
