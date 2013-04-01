package com.googlecode.mycontainer.commons.regex;

import java.util.Formatter;

import org.slf4j.Logger;

public class LogUtil {

	public static StringBuilder mount(String msg, Object... args) {
		StringBuilder builder = new StringBuilder();
		Formatter formatter = new Formatter(builder);
		formatter.format(msg, args);
		return builder;
	}

	public static void info(Logger log, String msg, Object... args) {
		StringBuilder builder = mount(msg, args);
		log.info(builder.toString());
	}

	public static void debug(Logger log, String msg, Object... args) {
		StringBuilder builder = mount(msg, args);
		log.debug(builder.toString());
	}

}
