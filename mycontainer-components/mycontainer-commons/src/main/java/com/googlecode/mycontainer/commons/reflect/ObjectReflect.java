package com.googlecode.mycontainer.commons.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ObjectReflect extends ClassReflect {

	private final Object object;

	public ObjectReflect(Object object) {
		super(object.getClass());
		this.object = object;
	}

	public Object getObject() {
		return object;
	}

	public Object invoke(String name, Class<?>[] types, Object[] args) {
		return super.invoke(object, name, types, args);
	}

	public Object invoke(String name) {
		return invoke(name, null, null);
	}

	public Object invoke(String methodName, List<?> parameters) {
		Method method = findMethod(methodName, parameters.size());
		if (method == null) {
			throw new RuntimeException("method not found: " + methodName
					+ " with " + parameters.size() + " arguments");
		}
		return invoke(method, parameters);
	}

	public Object hardInvoke(Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(object, args);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}

	public Object hardestInvoke(Method method, Object[] args)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		return method.invoke(object, args);
	}

	public Object invoke(Method method, Object[] args) {
		try {
			return method.invoke(object, args);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public Object invoke(Method method, List<?> params) {
		Object[] args = params.toArray(new Object[params.size()]);
		return invoke(method, args);
	}

}
