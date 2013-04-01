package com.googlecode.mycontainer.commons.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

public class ClassReflect {

	private final Class<Object> clazz;

	@SuppressWarnings("unchecked")
	public ClassReflect(Class<?> clazz) {
		this.clazz = (Class<Object>) clazz;
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> getClazz() {
		return (Class<T>) clazz;
	}

	@SuppressWarnings("unchecked")
	public <T> T invoke(Object object, String name, Class<?>[] types,
			Object[] args) {
		try {
			if (types == null) {
				types = new Class<?>[0];
			}
			if (args == null) {
				args = new Class<?>[0];
			}
			Method method = getMethod(name, types);
			Object ret = method.invoke(object, args);
			return (T) ret;
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T[] createNewArray(int length) {
		try {
			return (T[]) Array.newInstance(clazz, length);
		} catch (NegativeArraySizeException e) {
			throw new RuntimeException(e);
		}
	}

	public Method getMethod(String name, Class<?>[] types) {
		try {
			return clazz.getMethod(name, types);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public Method findMethod(String name, Class<?>[] types) {
		try {
			return clazz.getMethod(name, types);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static ClassReflect create(String className) {
		try {
			return new ClassReflect((Class<Object>) Class.forName(className));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public Object newInstance() {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public URL getResource(String pre, String suf) {
		String name = mountResourceName(pre, suf);
		URL ret = getClass().getClassLoader().getResource("." + name);
		return ret;
	}

	public URL getResourceMandatory(String pre, String suf) {
		URL ret = getResource(pre, suf);
		if (ret == null) {
			String name = mountResourceName(pre, suf);
			throw new RuntimeException("resource not found: " + name);
		}
		return ret;
	}

	private String mountResourceName(String pre, String suf) {
		return "/" + clazz.getPackage().getName().replaceAll("\\.", "/") + "/"
				+ pre + clazz.getSimpleName() + suf;
	}

	public URL getResourceMandatory(String name) {
		URL ret = clazz.getResource(name);
		if (ret == null) {
			throw new RuntimeException("resource not found: " + name + " ("
					+ clazz.getName() + ")");
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public Method findMethod(String methodName, int numParams) {
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)
					&& method.getParameterTypes().length == numParams) {
				return method;
			}
		}
		Class<Object> superclass = (Class<Object>) clazz.getSuperclass();
		if (superclass == null) {
			return null;
		}
		Method ret = new ClassReflect(superclass).findMethod(methodName,
				numParams);
		return ret;
	}

}
