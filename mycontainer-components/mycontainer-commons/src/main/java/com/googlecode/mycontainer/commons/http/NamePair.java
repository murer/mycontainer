package com.googlecode.mycontainer.commons.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NamePair {

	private String name;

	private String value;

	protected NamePair() {

	}

	public NamePair(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String name() {
		return name;
	}

	public String value() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		NamePair other = (NamePair) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public String encode() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(URLEncoder.encode(name, "utf8"));
			sb.append('=');
			sb.append(URLEncoder.encode(value, "utf8"));
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append('=');
		sb.append(value);
		return sb.toString();
	}

}
