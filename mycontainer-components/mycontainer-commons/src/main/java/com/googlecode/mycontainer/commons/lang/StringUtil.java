package com.googlecode.mycontainer.commons.lang;

public class StringUtil {

	public static char[] toCharArray(CharSequence chars) {
		int length = chars.length();
		char[] ret = new char[length];
		for (int i = 0; i < length; i++) {
			ret[i] = chars.charAt(i);
		}
		return ret;
	}

	public static StringBuilder join(StringBuilder sb, String separator,
			String... values) {
		if (sb == null) {
			sb = new StringBuilder();
		}
		for (int i = 0; i < values.length; i++) {
			Object value = values[i];
			sb.append(value);
			if (i < values.length - 1) {
				sb.append(separator);
			}
		}
		return sb;
	}
}
