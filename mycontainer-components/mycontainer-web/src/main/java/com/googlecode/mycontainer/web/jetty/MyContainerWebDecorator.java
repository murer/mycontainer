package com.googlecode.mycontainer.web.jetty;

import java.util.EventListener;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler.Decorator;
import org.eclipse.jetty.servlet.ServletHolder;

import com.googlecode.mycontainer.kernel.reflect.ReflectUtil;

public class MyContainerWebDecorator implements Decorator {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MyContainerWebDecorator.class);

	private Context ctx;

	public MyContainerWebDecorator() {
		try {
			ctx = new InitialContext();
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

	public <T extends Filter> T decorateFilterInstance(T filter) throws ServletException {
		inject(filter);
		return filter;
	}

	public <T extends Servlet> T decorateServletInstance(T servlet) throws ServletException {
		inject(servlet);
		return servlet;
	}

	public <T extends EventListener> T decorateListenerInstance(T listener) throws ServletException {
		inject(listener);
		return listener;
	}

	private void inject(Object instance) throws ServletException {
		LOG.info("Decorating " + instance);
		ReflectUtil.invokeStatic("com.googlecode.mycontainer.ejb.InjectionUtil", "inject", Object.class, instance, Context.class, ctx);
	}

	public void decorateFilterHolder(FilterHolder filter) throws ServletException {
	}

	public void decorateServletHolder(ServletHolder servlet) throws ServletException {
	}

	public void destroyServletInstance(Servlet s) {
	}

	public void destroyFilterInstance(Filter f) {
	}

	public void destroyListenerInstance(EventListener f) {
	}

}
