package com.googlecode.mycontainer.commons.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUtil {

	public static <K, V> V getFirstValue(Map<K, List<V>> map, String name, V def) {
		List<V> list = map.get(name);
		if (list == null || list.isEmpty()) {
			return def;
		}
		V ret = list.get(0);
		if (ret == null) {
			return def;
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, List<V>> populateList(Map<K, List<V>> map,
			Object... array) {
		if (map == null) {
			map = new HashMap<K, List<V>>();
		}
		for (int i = 0; i < array.length; i += 2) {
			K name = (K) array[i];
			List<V> list = map.get(name);
			if (list == null) {
				list = new ArrayList<V>();
				map.put(name, list);
			}
			V value = (V) array[i + 1];
			list.add(value);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> populate(Map<K, V> map, Object... array) {
		if (map == null) {
			map = new HashMap<K, V>();
		}
		for (int i = 0; i < array.length; i += 2) {
			K name = (K) array[i];
			V value = (V) array[i + 1];
			map.put(name, value);
		}
		return map;
	}

}
