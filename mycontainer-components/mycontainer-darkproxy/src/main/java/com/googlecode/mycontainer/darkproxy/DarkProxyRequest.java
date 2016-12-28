package com.googlecode.mycontainer.darkproxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class DarkProxyRequest {

	private Long id;

	private String method;

	private String uri;

	private DarkProxyHeaders headers = new DarkProxyHeaders();

	public String getMethod() {
		return method;
	}

	public DarkProxyRequest setMethod(String method) {
		this.method = method;
		return this;
	}

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
		try {
			DarkProxyRequest ret = new DarkProxyRequest();
			ret.setId(DarkProxyId.nextId());
			ret.setMethod(request.getMethod().toUpperCase());
			ret.setUri(request.getRequestURI());
			ret.parseHeaders(request);
			ret.writeMeta(dest);
			ret.writeBody(dest, request.getInputStream());
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeBody(String dest, InputStream in) {
		File file = DarkProxyFiles.getFile(dest, id, "req.body");
		DarkProxyFiles.write(file, in);
	}

	private void writeMeta(String dest) {
		File file = DarkProxyFiles.getFile(dest, id, "req.json");
		String json = JSON.stringify(this);
		DarkProxyFiles.write(file, json);
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

	public synchronized void waitFor() {
		try {
			wait();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
