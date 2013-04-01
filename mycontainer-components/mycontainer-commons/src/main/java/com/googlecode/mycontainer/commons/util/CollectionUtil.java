package com.googlecode.mycontainer.commons.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CollectionUtil {

	public static <X, Y> Map<X, Y> map(List<X> keys, Iterable<Y> values) {
		Map<X, Y> ret = new HashMap<X, Y>();
		Iterator<X> ks = keys.iterator();
		Iterator<Y> vs = values.iterator();
		while (ks.hasNext()) {
			ret.put(ks.next(), vs.next());
		}
		if (vs.hasNext()) {
			throw new RuntimeException("values is greater than keys");
		}
		return ret;
	}

	public static byte[] strToBytes(String str, String charset) {
		try {
			return str.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<Byte> toList(byte[] array) {
		if (array == null) {
			return null;
		}
		List<Byte> ret = new ArrayList<Byte>();
		for (byte b : array) {
			ret.add(b);
		}
		return ret;
	}

	public static byte[] toByteArray(List<? extends Number> list) {
		if (list == null) {
			return null;
		}
		byte[] ret = new byte[list.size()];
		int i = 0;
		for (Number b : list) {
			ret[i++] = b.byteValue();
		}
		return ret;
	}

	public static String join(Iterable<?> pairs, String delimiter) {
		StringBuilder ret = new StringBuilder();
		Iterator<?> it = pairs.iterator();
		while (it.hasNext()) {
			ret.append(it.next());
			if (it.hasNext()) {
				ret.append(delimiter);
			}
		}
		return ret.toString();
	}

}
