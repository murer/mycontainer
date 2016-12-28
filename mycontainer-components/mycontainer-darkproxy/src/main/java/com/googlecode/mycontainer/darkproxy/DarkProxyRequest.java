package com.googlecode.mycontainer.darkproxy;

import java.io.File;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class DarkProxyRequest {

	private Long id;

	private String uri;

	private DarkProxyHeaders headers = new DarkProxyHeaders();

	public String getUri() {
		return uri;
	}

	public DarkProxyHeaders getHeaders() {
		return headers;
	}

	public DarkProxyRequest setHeaders(DarkProxyHeaders headers) {
		this.headers = headers;
		return this;
	}

	public DarkProxyRequest setUri(String uri) {
		this.uri = uri;
		return this;
	}

	public Long getId() {
		return id;
	}

	public DarkProxyRequest setId(Long id) {
		this.id = id;
		return this;
	}

	public static DarkProxyRequest parse(HttpServletRequest request, String dest) {
		DarkProxyRequest ret = new DarkProxyRequest();
		ret.setId(DarkProxyId.nextId());
		ret.setUri(request.getRequestURI());
		ret.parseHeaders(request);
		ret.writeMeta(dest);
		return ret;
	}

	private void writeMeta(String dest) {
		File file = DarkProxyFiles.getFile(dest, id, "req.json");
		System.out.println(file);
	}

	private void parseHeaders(HttpServletRequest request) {
		headers = new DarkProxyHeaders();
		Enumeration<String> names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			Enumeration<String> values = request.getHeaders(name);
			headers.addAll(name, values);
		}
	}

	public DarkProxyResponse response() {
		return null;
	}

}
