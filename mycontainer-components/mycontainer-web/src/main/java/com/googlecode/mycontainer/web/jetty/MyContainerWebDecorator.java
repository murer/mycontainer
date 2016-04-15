package com.googlecode.mycontainer.web.jetty;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;

import org.eclipse.jetty.servlet.ServletContextHandler.Decorator;

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

	private void inject(Object instance) throws ServletException {
		LOG.info("Decorating " + instance);
		ReflectUtil.invokeStatic("com.googlecode.mycontainer.ejb.InjectionUtil", "inject", Object.class, instance,
				Context.class, ctx);
	}

	public <T> T decorate(T o) {
		try {
			inject(o);
			return o;
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
	}

	public void destroy(Object o) {

	}

}
