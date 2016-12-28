package com.googlecode.mycontainer.darkproxy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DarkProxyRequest {

	private Long id;

	public Long getId() {
		return id;
	}

	public DarkProxyRequest setId(Long id) {
		this.id = id;
		return this;
	}

	public static DarkProxyRequest parse(HttpServletRequest request, HttpServletResponse response) {
		DarkProxyRequest ret = new DarkProxyRequest();
		ret.setId(DarkProxyId.nextId());
		return ret;
	}

	public DarkProxyResponse response() {
		return null;
	}

}
