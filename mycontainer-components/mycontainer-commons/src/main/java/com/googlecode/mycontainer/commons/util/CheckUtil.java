package com.googlecode.mycontainer.commons.util;

import java.util.Arrays;

public class CheckUtil {

	public static <T> T check(T check, T... values) {
		for (T object : values) {
			if (object.equals(check)) {
				return object;
			}
		}
		throw new RuntimeException("expected: " + Arrays.toString(values)
				+ ", but was: " + check);
	}

}
