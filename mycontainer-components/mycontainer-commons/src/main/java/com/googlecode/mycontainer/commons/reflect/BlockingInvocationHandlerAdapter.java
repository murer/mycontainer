package com.googlecode.mycontainer.commons.reflect;

import java.lang.reflect.Method;
import java.text.MessageFormat;

import com.googlecode.mycontainer.annotation.Allow;

public class BlockingInvocationHandlerAdapter extends InvocationHandlerAdapter {

	private final String domain;

	public BlockingInvocationHandlerAdapter(Object impl, String domain) {
		super(impl);
		this.domain = domain;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		Allow allow = method.getAnnotation(Allow.class);

		if (allow != null) {
			for (String domainAllowed : allow.value()) {
				if (domainAllowed.equals(domain)) {
					return super.invoke(proxy, method, args);
				}
			}
		}

		throw new RuntimeException(MessageFormat.format(
				"Your domain ({0}) is not allowed to run the method {1}.",
				domain, method.getName()));
	}
}
