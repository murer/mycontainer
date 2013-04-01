package com.googlecode.mycontainer.commons.servlet.json;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.mycontainer.commons.json.JsonHandler;
import com.googlecode.mycontainer.commons.reflect.ObjectReflect;

public class JsonInvoker {

	private static enum InvokeType {
		NORMAL, HARD, HARDEST;
	}

	private final Object obj;

	public JsonInvoker(Object obj) {
		this.obj = obj;
	}

	public void invoke(HttpServletRequest req, HttpServletResponse resp,
			FilterChain chain, String method, String... args) {
		try {
			invokeInternal(req, resp, chain, method, InvokeType.NORMAL, args);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public void hardInvoke(HttpServletRequest req, HttpServletResponse resp,
			FilterChain chain, String method, String... args) throws Throwable {
		invokeInternal(req, resp, chain, method, InvokeType.HARD, args);
	}

	public void hardestInvoke(HttpServletRequest req, HttpServletResponse resp,
			FilterChain chain, String method, String... args)
			throws IllegalAccessException, InvocationTargetException,
			IllegalArgumentException {
		try {
			invokeInternal(req, resp, chain, method, InvokeType.HARDEST, args);
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private void invokeInternal(HttpServletRequest req,
			HttpServletResponse resp, FilterChain chain, String method,
			InvokeType invokeType, String... args) throws Throwable {
		ObjectReflect reflect = new ObjectReflect(obj);
		Method m = reflect.findMethod(method, new Class<?>[]{
				HttpServletRequest.class, HttpServletResponse.class,
				FilterChain.class});
		if (m == null) {
			m = reflect.findMethod(method, new Class<?>[]{
					HttpServletRequest.class, HttpServletResponse.class});
		}
		if (m == null) {
			m = reflect.findMethod(method,
					new Class<?>[]{HttpServletRequest.class});
		}
		Object ret = null;
		if (m != null) {
			Object[] values = new Object[m.getParameterTypes().length];
			values[0] = req;
			if (m.getParameterTypes().length > 1) {
				values[1] = resp;
			}
			if (m.getParameterTypes().length > 2) {
				values[2] = chain;
			}
			ret = reflect.invoke(m, values);
			if (m.getParameterTypes().length > 1) {
				return;
			}
		} else {
			if (args == null) {
				args = new String[0];
			}
			int size = args.length;
			m = reflect.findMethod(method, size);
			if (m == null) {
				throw new RuntimeException("method not found: " + method
						+ " with " + size + " arguments");
			}
			List<Object> params = parseParameters(args, m.getParameterTypes());
			ret = invoke(reflect, m, params, invokeType);
		}
		try {
			PrintWriter writer = resp.getWriter();
			JsonHandler.instance().format(ret, writer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public Object invoke(ObjectReflect reflect, Method m, List<Object> params,
			InvokeType invokeType) throws Throwable {
		Object[] args = params.toArray(new Object[params.size()]);
		switch (invokeType) {
			case NORMAL :
				return reflect.invoke(m, args);
			case HARD :
				return reflect.hardInvoke(m, args);
			case HARDEST :
				return reflect.hardestInvoke(m, args);
		}

		throw new IllegalArgumentException();
	}

	private List<Object> parseParameters(String[] values, Class<?>[] types) {
		List<Object> ret = new ArrayList<Object>();
		for (int i = 0; i < types.length; i++) {
			Class<?> type = types[i];
			String value = values[i];
			Object obj = JsonHandler.instance().parse(value, type);
			ret.add(obj);
		}
		return ret;
	}

}
