package com.googlecode.mycontainer.maven.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import org.apache.maven.plugin.logging.Log;

public class PluginUtil {

	public static void configureLogger(ClassLoader classloader, Log log) {
		try {
			Class<?> c = classloader.loadClass("org.slf4j.LoggerFactory");
			Method m = c.getMethod("getILoggerFactory");
			Object ilogger = m.invoke(null);
			StringBuilder str = new StringBuilder();
			str.append("slf4j-api is using: ").append(ilogger);
			if (ilogger != null) {
				str.append(" ").append(ilogger.getClass());
				str.append(" (").append(ilogger.getClass().getProtectionDomain().getCodeSource().getLocation()).append(")");
			}
			log.info(str);

			boolean found = check(classloader, log, "logging.properties");
			found = found | check(classloader, log, "log4j.properties");
			if (!found) {
				log.info("No logging confiration found");
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
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

	private static boolean check(ClassLoader classloader, Log log, String name) {
		URL url = classloader.getResource(name);
		if (url != null) {
			log.info("log confiration found: " + name + ": " + url);
			return true;
		}
		return false;
	}

}
