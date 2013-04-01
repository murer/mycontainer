package com.googlecode.mycontainer.commons.util;

import java.io.CharArrayReader;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class JsonUtil {

	public static JsonElement parse(char[] content) {
		if (content == null) {
			return null;
		}
		return parse(new CharArrayReader(content));
	}

	public static JsonElement parse(Reader reader) {
		if (reader == null) {
			return null;
		}
		JsonParser parser = new JsonParser();
		JsonElement ret = parser.parse(reader);
		return ret;
	}

	public static JsonElement parse(String json) {
		if (json == null) {
			return null;
		}
		JsonParser parser = new JsonParser();
		JsonElement ret = parser.parse(json);
		return ret;
	}

	public static void format(JsonElement element, Writer writer) {
		getBuilder().create().toJson(element, writer);
	}

	public static GsonBuilder getBuilder() {
		return new GsonBuilder();
	}

	public static Object value(JsonElement value) {
		return getJsonValue(value);
	}

	public static Object getJsonValue(JsonElement value) {
		if (value == null || value.isJsonNull()) {
			return null;
		}
		if (value.isJsonPrimitive()) {
			JsonPrimitive primitive = (JsonPrimitive) value;
			if (primitive.isBoolean()) {
				return primitive.getAsBoolean();
			}
			if (primitive.isNumber()) {
				return primitive.getAsNumber();
			}
			if (primitive.isString()) {
				return primitive.getAsString();
			}
		}
		if (value.isJsonArray()) {
			JsonArray array = (JsonArray) value;
			Iterator<JsonElement> it = array.iterator();
			List<Object> ret = new ArrayList<Object>();
			while (it.hasNext()) {
				JsonElement child = it.next();
				if (child.isJsonNull() || child.isJsonPrimitive()) {
					Object v = getJsonValue(child);
					ret.add(v);
				}
			}
			return ret;
		}
		throw new RuntimeException("unsupported: " + value);
	}

	@SuppressWarnings("unchecked")
	public static JsonElement createBasic(Object value) {
		if (value == null) {
			return new JsonNull();
		}
		if (value instanceof Boolean) {
			return new JsonPrimitive(((Boolean) value));
		}
		if (value instanceof Number) {
			return new JsonPrimitive((Number) value);
		}
		if (value instanceof String) {
			return new JsonPrimitive((String) value);
		}
		if (value instanceof Iterable) {
			Iterable<Object> it = (Iterable<Object>) value;
			JsonArray ret = new JsonArray();
			for (Object object : it) {
				ret.add(createBasic(object));
			}
			return ret;
		}
		throw new UnsupportedOperationException("unsupported type: " + value);
	}

	public static String formatPrimitive(Object value) {
		return createBasic(value).toString();
	}

	public static boolean check(JsonElement element) {
		if (element == null || element.isJsonNull()) {
			return false;
		}
		if (element.isJsonPrimitive()) {
			JsonPrimitive primitive = element.getAsJsonPrimitive();
			if (primitive.isBoolean() && !primitive.getAsBoolean()) {
				return false;
			}
			if (primitive.isNumber()
					&& primitive.getAsNumber().doubleValue() == 0) {
				return false;
			}
			if (primitive.isString() && primitive.getAsString().length() == 0) {
				return false;
			}
		}
		return true;
	}

	public static JsonArray sub(JsonArray array, Integer o, Integer l) {
		if (o == null) {
			o = 0;
		}
		if (l == null) {
			l = array.size() - o;
		}
		JsonArray ret = new JsonArray();
		for (int i = 0; i < l; i++) {
			int idx = o + i;
			if (idx >= array.size()) {
				return ret;
			}
			ret.add(array.get(idx));
		}
		return ret;
	}

	public static boolean f(Object obj) {
		if (obj == null) {
			return true;
		}
		if (obj instanceof JsonElement) {
			JsonElement element = (JsonElement) obj;
			if (element.isJsonNull()) {
				return true;
			}
			if (element.isJsonPrimitive()) {
				JsonPrimitive primitive = element.getAsJsonPrimitive();
				if (primitive.isBoolean()) {
					return !primitive.getAsBoolean();
				}
				if (primitive.isNumber()) {
					return primitive.getAsBigDecimal().compareTo(
							BigDecimal.ZERO) == 0;
				}
				if (primitive.isString()) {
					return primitive.getAsString().length() == 0;
				}
			}
		}
		if (obj instanceof Boolean) {
			return !(Boolean) obj;
		}
		if (obj instanceof Number) {
			return ((Number) obj).doubleValue() == 0d;
		}
		if (obj instanceof String) {
			return ((String) obj).length() == 0;
		}
		return false;
	}

	public static boolean t(Object element) {
		return !f(element);
	}

	public static JsonObject create(Object... values) {
		if (values.length % 2 != 0) {
			throw new RuntimeException("invalid length");
		}
		JsonObject ret = new JsonObject();
		for (int i = 0; i < values.length; i += 2) {
			String name = (String) values[i];
			Object value = values[i + 1];
			if (value == null) {
				value = new JsonNull();
			}
			if (!(value instanceof JsonElement)) {
				value = JsonUtil.createBasic(value);
			}
			ret.add(name, (JsonElement) value);
		}
		return ret;
	}

	public static JsonObject map(Map<String, Object> map) {
		JsonObject ret = new JsonObject();
		Set<Entry<String, Object>> entries = map.entrySet();
		for (Entry<String, Object> entry : entries) {
			String key = entry.getKey();
			Object v = entry.getValue();
			if (v == null) {
				v = new JsonNull();
			}
			if (!(v instanceof JsonElement)) {
				v = JsonUtil.createBasic(v);
			}
			ret.add(key, (JsonElement) v);
		}
		return ret;
	}

	public static JsonArray barray2json(byte[] bytes) {
		JsonArray ret = new JsonArray();
		for (byte b : bytes) {
			ret.add(new JsonPrimitive(b));
		}
		return ret;
	}
}
