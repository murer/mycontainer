package com.googlecode.mycontainer.commons.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InvocationHandlerAdapter implements InvocationHandler {

	protected ObjectReflect objectReflect;
	private Object impl;

	public InvocationHandlerAdapter(Object impl) {
		this.impl = impl;
		objectReflect = new ObjectReflect(impl);
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Method invokeMethod = method;
		if (!method.getDeclaringClass().isInstance(impl)) {
			invokeMethod = objectReflect.getMethod(method.getName(),
					method.getParameterTypes());
		}

		return objectReflect.invoke(invokeMethod, args);
	}

	@SuppressWarnings("unchecked")
	public <T> T createProxy(Class<T> clazz) {
		return (T) createProxy(new Class<?>[] { clazz });
	}

	public Object createProxy(Class<?>[] classes) {
		return Proxy.newProxyInstance(getClass().getClassLoader(), classes,
				this);
	}

}
