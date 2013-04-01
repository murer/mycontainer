package com.googlecode.mycontainer.commons.httpclient;

import com.googlecode.mycontainer.commons.json.JsonHandler;

public class JsonWebRequest {

	private final WebRequest request;

	public JsonWebRequest(WebRequest request) {
		this.request = request;
	}

	public WebRequest getRequest() {
		return request;
	}

	public void addParameter(String name, Object value) {
		String format = JsonHandler.instance().format(value);
		request.addParameter(name, format);
	}

	public <T> T invoke(Class<T> clazz) {
		WebResponse response = request.invoke();
		try {
			Integer code = response.getCode();
			if (code < 200 || code > 299) {
				throw new RuntimeException("http error: " + code);
			}
			T ret = (T) response.getJsonProtocol().parse(clazz);
			return ret;
		} finally {
			response.close();
		}
	}

	public Object invoke() {
		return invoke(null);
	}

}
