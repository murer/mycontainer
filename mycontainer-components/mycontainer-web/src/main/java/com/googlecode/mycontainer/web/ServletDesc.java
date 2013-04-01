package com.googlecode.mycontainer.web;

import java.io.Serializable;

import javax.servlet.http.HttpServlet;

public class ServletDesc implements Serializable {

	private static final long serialVersionUID = 3773188732813131828L;

	private final Object servlet;

	private final String path;

	public ServletDesc(String servlet, String path) {
		this.servlet = servlet;
		this.path = path;
	}

	public ServletDesc(Class<?> clazz, String path) {
		this(clazz.getName(), path);
	}

	public ServletDesc(HttpServlet servlet, String path) {
		this.servlet = servlet;
		this.path = path;
	}

	public ServletDesc(SpecServlet servlet, String path) {
		this(new AdapterHttpServlet(servlet), path);
	}

	public Object getServlet() {
		return servlet;
	}

	public String getPath() {
		return path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((servlet == null) ? 0 : servlet.hashCode());
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
		ServletDesc other = (ServletDesc) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (servlet == null) {
			if (other.servlet != null)
				return false;
		} else if (!servlet.equals(other.servlet))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + servlet + ": " + path + "]";
	}

}
