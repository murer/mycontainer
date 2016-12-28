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

	private String query;

	private String schema;

	private String host;

	private Integer port;

	private DarkProxyHeaders headers = new DarkProxyHeaders();

	public String getQuery() {
		return query;
	}

	public DarkProxyRequest setQuery(String query) {
		this.query = query;
		return this;
	}

	public String getSchema() {
		return schema;
	}

	public DarkProxyRequest setSchema(String schema) {
		this.schema = schema;
		return this;
	}

	public String getHost() {
		return host;
	}

	public DarkProxyRequest setHost(String host) {
		this.host = host;
		return this;
	}

	public Integer getPort() {
		return port;
	}

	public DarkProxyRequest setPort(Integer port) {
		this.port = port;
		return this;
	}

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
			ret.setQuery(request.getQueryString());
			ret.parseHeaders(request);
			ret.writeBody(dest, request.getInputStream());
			ret.writeMeta(dest);
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

	public synchronized void waitFor() {
		try {
			wait();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public String createUrl(DarkProxyRequest req) {
		return String.format("%s://%s:%s%s?%s", req.getSchema(), req.getHost(), req.getPort(), req.getUri(),
				req.getQuery());
	}

}
