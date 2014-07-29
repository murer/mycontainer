package com.googlecode.mycontainer.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReflectionUtil {

	public static void setBeanField(Object entidade, String key, Object value) {
		try {
			String methodName = toMethodName("set", key);
			Method method = null;
			if (value != null) {
				method = ReflectionUtil.getMethod(entidade, methodName, value.getClass());
			}
			if (method == null) {
				Class<?> type = ReflectionUtil.getBeanFieldType(entidade, key);
				method = ReflectionUtil.getMethod(entidade, methodName, type);
			}
			if (method == null && value == null) {
				throw new RuntimeException("impossible to guess type for field: " + key);
			}
			Class<?> paramType = method.getParameterTypes()[0];
			if (value != null && !paramType.isAssignableFrom(value.getClass())) {
				throw new RuntimeException("this method " + method + " can not be invoked with: " + value.getClass().getName());
			}
			method.invoke(entidade, new Object[] { value });
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

	public static String toMethodName(String pre, String key) {
		char c = Character.toUpperCase(key.charAt(0));
		return "" + pre + c + key.substring(1);
	}

	public static List<String> getBeanFields(Object obj) {
		Class<?> clazz = obj.getClass();
		return getBeanFieldsOfClass(clazz);
	}

	public static List<String> getBeanFieldsOfClass(Class<?> clazz) {
		List<String> ret = new ArrayList<String>();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.getParameterTypes().length != 0) {
				continue;
			}
			String name = method.getName();
			if (!name.startsWith("get") || name.equals("getClass")) {
				continue;
			}
			Class<?> type = method.getReturnType();
			String fieldName = toBeanFieldName(name);
			if (getMethodOfClass(clazz, toMethodName("set", fieldName), new Class[] { type }) != null) {
				ret.add(fieldName);
			}
		}
		return ret;
	}

	private static String toBeanFieldName(String name) {
		char c = Character.toLowerCase(name.charAt(3));
		return "" + c + name.substring(4);
	}

	public static Object getBeanField(Object obj, String fieldName) {
		try {
			String methodName = toMethodName("get", fieldName);
			Method method = getMethod(obj, methodName);
			return method.invoke(obj);
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

	public static Method getMethod(Object obj, String methodName, Class<?>... types) {
		Class<?> clazz = obj.getClass();
		return getMethodOfClass(clazz, methodName, types);
	}

	public static Method getMethodOfClass(Class<?> clazz, String methodName, Class<?>... types) {
		try {
			return clazz.getMethod(methodName, types);
		} catch (NoSuchMethodException e) {
			return null;
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isBeanFieldAnnotated(Object obj, String fieldName, Class<? extends Annotation> annotation) {
		Class<?> clazz = obj.getClass();
		return isBeanFieldAnnotated(clazz, fieldName, annotation);
	}

	public static boolean isBeanFieldAnnotated(Class<?> clazz, String fieldName, Class<? extends Annotation> annotation) {
		Method method = getMethodOfClass(clazz, toMethodName("get", fieldName));
		if (method == null) {
			return false;
		}
		return method.isAnnotationPresent(annotation);
	}

	public static Class<?> getBeanFieldType(Object obj, String fieldName) {
		return getBeanFieldTypeOfClass(obj.getClass(), fieldName);
	}

	public static Class<?> getBeanFieldTypeOfClass(Class<?> clazz, String fieldName) {
		Method method = getMethodOfClass(clazz, toMethodName("get", fieldName));
		if (method == null) {
			throw new RuntimeException("Bean field '" + fieldName + "' not found in " + clazz);
		}
		return method.getReturnType();
	}

	public static void checkBeanFieldAnnotated(Class<?> clazz, String fieldName, Class<? extends Annotation> annotation) {
		if (!ReflectionUtil.isBeanFieldAnnotated(clazz, fieldName, annotation)) {
			throw new IllegalStateException("Requires " + annotation + " on " + clazz.getName() + "." + fieldName);
		}
	}

	public static List<Method> getMethods(Class<?> clazz, int modifier) {
		Method[] methods = clazz.getMethods();
		List<Method> ret = new ArrayList<Method>();
		for (Method method : methods) {
			int result = method.getModifiers() & modifier;
			if (result > 0) {
				ret.add(method);
			}
		}
		return ret;
	}

	public static List<Method> getMethods(Class<?> clazz) {
		return getMethods(clazz, 0xFFFFFFFF);
	}

	public static List<Annotation> getAnnotation(Method method) {
		Annotation[] annotations = method.getAnnotations();
		if (annotations == null || annotations.length == 0) {
			return Collections.emptyList();
		}
		return Arrays.asList(annotations);
	}

	public static List<Annotation> getAnnotationField(Field field) {
		Annotation[] annotations = field.getAnnotations();
		if (annotations == null || annotations.length == 0) {
			return Collections.emptyList();
		}
		return Arrays.asList(annotations);
	}

	public static List<Annotation> getAnnotationClass(Class<?> clazz) {
		Annotation[] annotations = clazz.getAnnotations();
		if (annotations == null || annotations.length == 0) {
			return Collections.emptyList();
		}
		return Arrays.asList(annotations);
	}

	public static List<Field> getFields(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		List<Field> ret = new ArrayList<Field>(Arrays.asList(fields));
		Class<?> superclass = clazz.getSuperclass();
		if (superclass != null) {
			ret.addAll(getFields(superclass));
		}
		return ret;
	}

	public static List<Method> getDeclaringMethods(Class<?> clazz) {
		return getDeclaringMethods(clazz, 0xFFFFFFFF);
	}

	public static List<Method> getDeclaringMethods(Class<?> clazz, int modifier) {
		Method[] methods = clazz.getDeclaredMethods();
		List<Method> ret = new ArrayList<Method>();
		for (Method method : methods) {
			int result = method.getModifiers() & modifier;
			if (result > 0) {
				ret.add(method);
			}
		}
		return ret;
	}

	public static Object invoke(Object o, Method m, Object... args) {
		try {
			return m.invoke(o, args);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static Method getMethodByName(Class<?> clazz, String methodName) {
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			boolean equals = method.getName().equals(methodName);
			if (equals) {
				return method;
			}
		}
		Class<Object> superclass = (Class<Object>) clazz.getSuperclass();
		if (superclass == null) {
			return null;
		}
		return getMethodByName(superclass, methodName);
	}

	public static boolean hasBeanField(Object obj, String fieldName) {
		try {
			String methodName = toMethodName("get", fieldName);
			Method method = getMethod(obj, methodName);
			return method != null;
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}

	public static Object newInstance(String className) {
		Class<?> clazz = clazz(className);
		return newInstance(clazz);
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> clazz(String className) {
		try {
			return (Class<T>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static String debug(Class<?> clazz) {
		if (clazz == null) {
			return "null";
		}
		StringBuilder ret = new StringBuilder();
		ret.append("[").append(clazz.getName());
		String url = debugLocation(clazz);
		if (url != null) {
			ret.append(" ").append(url);
		}
		ret.append(']');
		return ret.toString();
	}

	private static String debugLocation(Class<?> clazz) {
		try {
			return clazz.getProtectionDomain().getCodeSource().getLocation().toString();
		} catch (Exception e) {
			return "null";
		}
	}

	public static Object invokeStatic(Class<?> clazz, String methodName, Class<?>[] types, Object[] args) {
		try {
			Method method = ReflectionUtil.getMethodOfClass(clazz, methodName, types);
			return method.invoke(null, args[0]);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T newInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<Annotation> getBeanFieldsAnnotations(Class<?> clazz, String fieldName) {
		Method method = getMethodOfClass(clazz, toMethodName("get", fieldName));
		return getAnnotation(method);
	}

	public static List<Annotation> getBeanFieldsAnnotationsField(Class<?> clazz, String fieldName) {
		try {
			if ("lastModified".equals(fieldName)) {
				return new ArrayList<Annotation>();
			} else {
				Field field = clazz.getDeclaredField(fieldName);
				return getAnnotationField(field);
			}
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}

	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> clazz, Class<?>[] types, Object... values) {
		try {
			Constructor<?> cons = clazz.getConstructor(types);
			T ret = (T) cons.newInstance(values);
			return ret;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> requireClazz(String name) {
		try {
			return (Class<T>) Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
