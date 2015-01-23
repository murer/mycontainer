/*
 * Copyright 2008 Whohoo Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.googlecode.mycontainer.kernel.reflect;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;

public class ReflectUtil {

	private final Class<?> clazz;

	public ReflectUtil(Class<?> clazz) {
		this.clazz = clazz;
	}

	public List<Field> getFields() {
		Field[] fields = clazz.getDeclaredFields();
		List<Field> ret = new ArrayList<Field>(Arrays.asList(fields));

		Class<?> superclass = clazz.getSuperclass();
		if (superclass != null) {
			ReflectUtil util = new ReflectUtil(superclass);
			ret.addAll(util.getFields());
		}

		return ret;
	}

	public <T extends Annotation> List<Field> getFields(Class<T> type) {
		List<Field> ret = new ArrayList<Field>();

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			T annotation = field.getAnnotation(type);
			if (annotation != null) {
				ret.add(field);
			}
		}

		Class<?> superclass = clazz.getSuperclass();
		if (superclass != null) {
			ReflectUtil util = new ReflectUtil(superclass);
			ret.addAll(util.getFields(type));
		}

		return ret;
	}

	public static void setField(Field field, Object obj, Object value) {
		try {
			field.setAccessible(true);
			field.set(obj, value);
		} catch (IllegalArgumentException e) {
			throw new KernelRuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public <T extends Annotation> List<Method> getMethods(Class<T> type) {
		List<Method> ret = new ArrayList<Method>();

		Method[] methods = clazz.getDeclaredMethods();
		for (Method field : methods) {
			T annotation = field.getAnnotation(type);
			if (annotation != null) {
				ret.add(field);
			}
		}
		Class<?> superclass = clazz.getSuperclass();
		if (superclass != null) {
			ReflectUtil util = new ReflectUtil(superclass);
			ret.addAll(util.getMethods(type));
		}
		return ret;
	}

	public List<Method> getMethods() {
		List<Method> ret = new ArrayList<Method>();
		Method[] methods = clazz.getDeclaredMethods();
		for (Method field : methods) {
			ret.add(field);
		}
		Class<?> superclass = clazz.getSuperclass();
		if (superclass != null) {
			ReflectUtil util = new ReflectUtil(superclass);
			ret.addAll(util.getMethods());
		}
		return ret;
	}

	public static void invokeMethod(Method method, Object obj, Object... args) {
		try {
			boolean flag = method.isAccessible();
			method.setAccessible(true);
			method.invoke(obj, args);
			method.setAccessible(flag);
		} catch (IllegalArgumentException e) {
			throw new KernelRuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new KernelRuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public static String location(Class<?> clazz) {
		ProtectionDomain pd = clazz.getProtectionDomain();
		CodeSource cs = pd.getCodeSource();
		URL ret = cs.getLocation();
		return ret.toString();
	}

	public static List<String> location(String name) {
		return location(ReflectUtil.class.getClassLoader(), name);
	}

	public static List<URL> locationURL(String name) {
		return locationURL(ReflectUtil.class.getClassLoader(), name);
	}

	private static List<URL> locationURL(ClassLoader cl, String name) {
		try {
			Enumeration<URL> resources = cl.getResources(name);
			if (resources == null) {
				return null;
			}
			List<URL> ret = new ArrayList<URL>();
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				ret.add(url);
			}
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<String> location(ClassLoader cl, String name) {
		List<URL> urls = locationURL(cl, name);
		List<String> ret = new ArrayList<String>();
		for (URL url : urls) {
			ret.add(url == null ? null : url.toString());
		}
		return ret;
	}

	public static Object classForName(String listener) {
		if (listener == null) {
			return null;
		}
		try {
			return Class.forName(listener);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T newInstance(Class<T> listener) {
		if (listener == null) {
			return null;
		}
		try {
			return listener.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static Object invoke(Object obj, String methodName, Object... params) {
		if (params.length % 2 != 0) {
			throw new RuntimeException("types and values mismatch");
		}
		try {
			int size = params.length / 2;
			Class<?>[] types = new Class<?>[size];
			Object[] args = new Object[size];
			for (int i = 0; i < types.length; i++) {
				int idx = (i * 2);
				types[i] = (Class<?>) params[idx];
				args[i] = params[idx + 1];
			}
			Method method = obj.getClass().getMethod(methodName, types);
			Object ret = method.invoke(obj, args);
			return ret;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static Object invokeStatic(String className, String method, Object... params) {
		try {
			Class<?> clazz = Class.forName(className);
			return invokeStatic(clazz, method, params);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static Object invokeStatic(Class<?> clazz, String methodName, Object... params) {
		if (params.length % 2 != 0) {
			throw new RuntimeException("types and values mismatch");
		}
		try {
			int size = params.length / 2;
			Class<?>[] types = new Class<?>[size];
			Object[] args = new Object[size];
			for (int i = 0; i < types.length; i++) {
				int idx = (i * 2);
				types[i] = (Class<?>) params[idx];
				args[i] = params[idx + 1];
			}
			Method method = clazz.getMethod(methodName, types);
			Object ret = method.invoke(null, args);
			return ret;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
