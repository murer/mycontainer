package com.googlecode.mycontainer.commons.httpclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.googlecode.mycontainer.commons.io.IOUtil;

public class WebResponse {

	private final WebRequest request;

	private Map<String, List<String>> headers;

	private HttpResponse response;

	public WebResponse(WebRequest request, HttpResponse response) {
		this.request = request;
		this.response = response;
	}

	public WebRequest getRequest() {
		return request;
	}

	public Integer getCode() {
		return response.getStatusLine().getStatusCode();
	}

	public Map<String, List<String>> getHeaders() {
		if (headers == null) {
			Map<String, List<String>> ret = new HashMap<String, List<String>>();
			Header[] h = response.getAllHeaders();
			for (Header header : h) {
				String name = header.getName();
				String value = header.getValue();
				List<String> list = ret.get(name);
				if (list == null) {
					list = new ArrayList<String>();
					ret.put(name, list);
				}
				list.add(value);
			}
			headers = ret;
		}
		return headers;
	}

	public String getContentType() {
		Header header = response.getEntity().getContentType();
		if (header == null) {
			return null;
		}
		return header.getValue();
	}

	@Override
	public String toString() {
		return "" + getCode() + "|" + getContentType();
	}

	public String getContentAsString() {
		try {
			return EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	public void close() {
		IOUtil.close(request);
	}

	public XPathFinder createXPathFinder() {
		XPathFinder finder = new XPathFinder();
		finder.config(this);
		return finder;
	}

	public byte[] getContentByteArray() {
		try {
			return EntityUtils.toByteArray(response.getEntity());
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	public String getHeader(String key) {
		List<String> ret = getHeaders().get(key);
		if (ret == null) {
			return null;
		}
		return ret.get(0);
	}

	public JsonProtocol getJsonProtocol() {
		String content = getContentAsString().trim();
		JsonProtocol ret = new JsonProtocol();
		ret.parse(content);
		return ret;
	}

	public Object deserializeContent() {
		try {
			byte[] array = getContentByteArray();
			ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(array));
			Object ret = in.readObject();
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
