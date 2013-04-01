package com.googlecode.mycontainer.commons.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.message.BasicNameValuePair;

import com.googlecode.mycontainer.commons.util.ContentUtil;

import flexjson.JSON;

public class NamePairs {

	private final List<NamePair> pairs = new ArrayList<NamePair>();

	public NamePairs() {

	}

	@JSON(include = true)
	public List<NamePair> pairs() {
		return pairs;
	}

	public String encodeParams() {
		Iterator<NamePair> it = pairs.iterator();
		StringBuilder ret = new StringBuilder();
		while (it.hasNext()) {
			NamePair pair = it.next();
			ret.append(pair.encode());
			if (it.hasNext()) {
				ret.append('&');
			}
		}
		return ret.toString();
	}

	@Override
	public String toString() {
		Iterator<NamePair> it = pairs.iterator();
		StringBuilder ret = new StringBuilder();
		while (it.hasNext()) {
			NamePair pair = it.next();
			ret.append(pair);
			if (it.hasNext()) {
				ret.append('&');
			}
		}
		return ret.toString();
	}

	public String first(String name) {
		return first(name, null);
	}

	public void set(String key, Collection<String> values) {
		clear(key);
		for (String v : values) {
			pairs.add(new NamePair(key, v.toString()));
		}
	}

	public void clear(String key) {
		Iterator<NamePair> iterator = pairs.iterator();
		while (iterator.hasNext()) {
			NamePair pair = iterator.next();
			if (pair.name().equals(key)) {
				iterator.remove();
			}
		}
	}

	public NamePairs set(String key, String value) {
		clear(key);
		if (value != null) {
			pairs.add(new NamePair(key, value));
		}
		return this;
	}

	public void pairs(List<NamePair> pairs) {
		this.pairs.clear();
		this.pairs.addAll(pairs);
	}

	public String first(String name, String def) {
		for (NamePair pair : pairs) {
			if (pair.name().equals(name)) {
				return pair.value();
			}
		}
		return def;
	}

	public void add(String name, String value) {
		pairs.add(new NamePair(name, value));
	}

	public List<String> get(String name) {
		List<String> ret = new ArrayList<String>();
		for (NamePair pair : pairs) {
			if (pair.name().equals(name)) {
				ret.add(pair.value());
			}
		}
		return ret;
	}

	public Integer asInteger(String name) {
		String ret = first(name);
		if (ret == null) {
			return null;
		}
		return new Integer(ret);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pairs == null) ? 0 : pairs.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NamePairs other = (NamePairs) obj;
		if (pairs == null) {
			if (other.pairs != null)
				return false;
		} else if (!pairs.equals(other.pairs))
			return false;
		return true;
	}

	public String contentType() {
		String ret = first("Content-Type");
		return ret;
	}

	public String mediaType() {
		String ret = first("Content-Type");
		return ContentUtil.getMediaType(ret);
	}

	public String charset() {
		String ret = first("Content-Type");
		return ContentUtil.getCharset(ret);
	}

	public boolean isBinary() {
		return charset() == null;
	}

	public NamePairs contentType(String mediaType, String charset) {
		if (charset != null) {
			return set("Content-Type", mediaType + "; charset=" + charset);
		}
		return set("Content-Type", mediaType);
	}

	public String all(String name) {
		StringBuilder ret = new StringBuilder();
		Iterator<String> list = get(name).iterator();
		while (list.hasNext()) {
			String s = list.next();
			ret.append(s);
			if (list.hasNext()) {
				ret.append(", ");
			}
		}
		return ret.toString();
	}

	public Map<String, String[]> toMap() {
		Map<String, String[]> ret = new HashMap<String, String[]>();
		Set<String> names = names();
		for (String name : names) {
			ret.put(name, asArray(name));
		}
		return ret;
	}

	public Set<String> names() {
		Set<String> ret = new HashSet<String>();
		for (NamePair pair : pairs) {
			ret.add(pair.name());
		}
		return ret;
	}

	public String[] asArray(String name) {
		List<String> l = get(name);
		return l.toArray(new String[l.size()]);
	}

	public void remove(String name) {
		Iterator<NamePair> it = pairs.iterator();
		while (it.hasNext()) {
			NamePair pair = it.next();
			if (pair.name().equals(name)) {
				it.remove();
			}
		}
	}

	public String asQueryString() {
		List<NameValuePair> pairs = toNamedValues();
		return URLEncodedUtils.format(pairs, "utf-8");
	}

	public List<NameValuePair> toNamedValues() {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>(
				this.pairs.size());
		for (NamePair pair : this.pairs) {
			pairs.add(new BasicNameValuePair(pair.name(), pair.value()));
		}
		return pairs;
	}

	public Date asDate(String name) {
		String str = all(name);
		if (str == null) {
			return null;
		}
		try {
			return DateUtils.parseDate(str);
		} catch (DateParseException e) {
			throw new RuntimeException(e);
		}
	}
}
