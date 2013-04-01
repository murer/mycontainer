package com.googlecode.mycontainer.web;

import java.io.Serializable;

import javax.servlet.Filter;

public class FilterDesc implements Serializable {

	private static final long serialVersionUID = 3773188732813131828L;

	private final Object filter;

	private final String path;
	
	public FilterDesc(String className, String path) {
		this.filter = className;
		this.path = path;
	}

	public FilterDesc(Class<?> clazz, String path) {
		this(clazz.getName(), path);
	}

	public FilterDesc(Filter filter, String path) {
		this.filter = filter;
		this.path = path;
	}

	public Object getFilter() {
		return filter;
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return "FilterDesc [filter=" + filter + ", path=" + path + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		FilterDesc other = (FilterDesc) obj;
		if (filter == null) {
			if (other.filter != null)
				return false;
		} else if (!filter.equals(other.filter))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

}
