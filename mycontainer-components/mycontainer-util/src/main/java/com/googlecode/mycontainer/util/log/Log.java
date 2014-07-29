package com.googlecode.mycontainer.util.log;

import com.googlecode.mycontainer.util.ReflectionUtil;

public abstract class Log {

	public static Log get(Class<?> clazz) {
		return get(clazz.getName());
	}

	public static Log get(String name) {
		Class<Log> clazz = ReflectionUtil.clazz("org.slf4j.LoggerFactory");
		if (clazz != null) {
			Class<Log> c = ReflectionUtil.requireClazz("com.googlecode.mycontainer.util.log.Slf4jLog");
			return ReflectionUtil.newInstance(c, new Class[] { String.class }, name);
		}
		return new JdkLog(name);
	}

	public abstract void error(String msg);

	public abstract void error(String msg, Throwable e);

	public abstract void info(String msg);

	public abstract void info(String msg, Throwable e);

	public abstract void debug(String msg);

	public abstract void debug(String msg, Throwable e);
}
