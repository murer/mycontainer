package com.googlecode.mycontainer.darkproxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

	private void writeBody(String dest, HttpServletResponse response) {
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

	public DarkProxyResponse forward(DarkProxyRequest req, String dest) {
		String strurl = req.createUrl(req);
		HttpURLConnection conn = null;
		OutputStream out = null;
		try {
			URL url = new URL(strurl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(req.getMethod());
			for (Entry<String, List<String>> entry : req.getHeaders().getHeaders().entrySet()) {
				for (String value : entry.getValue()) {
					conn.addRequestProperty(entry.getKey(), value);
				}
			}
			conn.setDoOutput(true);
			out = conn.getOutputStream();
			File bodyFile = req.getBodyFile(dest);
			Util.read(bodyFile, out);
			out.close();
			out = null;

			this.setCode(conn.getResponseCode());
			this.setReason(conn.getResponseMessage());
			this.parseHeaders(conn);
			this.writeBody(dest, conn.getInputStream());
			this.writeMeta(dest);
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Util.close(out);
			Util.close(conn);
		}
	}

	private void writeBody(String dest, InputStream in) {
		File file = getBodyFile(dest);
		DarkProxyFiles.write(file, in);
	}

	public File getBodyFile(String dest) {
		return DarkProxyFiles.getFile(dest, id, "resp.body");
	}

	private void writeMeta(String dest) {
		File file = getMetaFile(dest);
		String json = JSON.stringify(this);
		DarkProxyFiles.write(file, json);
	}

	public File getMetaFile(String dest) {
		return DarkProxyFiles.getFile(dest, id, "resp.json");
	}

	private void parseHeaders(HttpURLConnection conn) {
		Map<String, List<String>> headers = conn.getHeaderFields();
		for (Entry<String, List<String>> entry : headers.entrySet()) {
			if (entry.getKey() != null) {
				this.headers.setHeaders(entry.getKey(), entry.getValue());
			}
		}
	}

	public synchronized void waitFor() {
		try {
			wait(7000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized void proceed() {
		notify();
	}

	public void reload(String dest) {
		File file = getMetaFile(dest);
		String json  = Util.readAll(file, "UTF-8");
		DarkProxyResponse resp = JSON.parse(json, DarkProxyResponse.class);
		setHeaders(resp.getHeaders());
		setCode(resp.getCode());
		setId(resp.getId());
		setReason(resp.getReason());
	}

}
