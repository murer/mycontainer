package com.googlecode.mycontainer.darkproxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.mycontainer.util.Util;

public class DarkProxyResponse {

	private Long id;

	private Integer code;

	private String reason;

	private DarkProxyHeaders headers = new DarkProxyHeaders();

	public Long getId() {
		return id;
	}

	public DarkProxyResponse setId(Long id) {
		this.id = id;
		return this;
	}

	public DarkProxyHeaders getHeaders() {
		return headers;
	}

	public DarkProxyResponse setHeaders(DarkProxyHeaders headers) {
		this.headers = headers;
		return this;
	}

	public String getReason() {
		return reason;
	}

	public DarkProxyResponse setReason(String reason) {
		this.reason = reason;
		return this;
	}

	public Integer getCode() {
		return code;
	}

	public DarkProxyResponse setCode(Integer code) {
		this.code = code;
		return this;
	}

	public void writeTo(String dest, HttpServletResponse response) {
		response.setStatus(response.getStatus());
		writeHeaders(response);
		writeBody(dest, response);
	}

	public void writeBody(String dest, HttpServletResponse response) {
		try {
			File file = getBodyFile(dest);
			Util.read(file, response.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeHeaders(HttpServletResponse response) {
		for (Entry<String, List<String>> entry : headers.getHeaders().entrySet()) {
			for (String value : entry.getValue()) {
				response.addHeader(entry.getKey(), value);
			}
		}
	}

	public void writeBody(String dest, InputStream in) {
		File file = getBodyFile(dest);
		DarkProxyFiles.write(file, in);
	}

	public File getBodyFile(String dest) {
		return DarkProxyFiles.getFile(dest, id, "resp.body");
	}

	public void writeMeta(String dest) {
		File file = getMetaFile(dest);
		String json = JSON.stringify(this);
		DarkProxyFiles.write(file, json);
	}

	public File getMetaFile(String dest) {
		return DarkProxyFiles.getFile(dest, id, "resp.json");
	}

	public synchronized void waitFor(long time) {
		try {
			if (time >= 0) {
				wait(time);
			} else {
				wait();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized void proceed() {
		notify();
	}

	public void reload(String dest, HttpServletRequest req) {
		File file = getMetaFile(dest);
		String json = Util.readAll(file, "UTF-8");
		DarkProxyResponse resp = JSON.parse(json, DarkProxyResponse.class);
		setHeaders(resp.getHeaders());
		setCode(resp.getCode());
		setId(resp.getId());
		setReason(resp.getReason());
		if (!"HEAD".equals(req)) {
			long len = getBodyFile(dest).length();
			headers.set("Content-Length", Long.toString(len));
		}
	}

}
