package com.googlecode.mycontainer.commons.http;

import com.googlecode.mycontainer.commons.util.JsonUtil;

public class RequestAdapter implements RequestService {

	private final RequestService service;

	public RequestAdapter(RequestService service) {
		this.service = service;
	}

	public RequestService getService() {
		return service;
	}

	public Response execute(Request req) {
		RequestService service = this.service;
		return service.execute(req);
	}

	public Integer code(String type, String path) {
		return execute(new Request(Type.valueOf(type), path)).code();
	}

	public Response createIfNotExists(String path, String contentType,
			String content) {
		if (code("GET", path) == 404) {
			return execute(new Request(Type.PUT, path).contentType(contentType)
					.content(content));
		}
		return null;
	}

	public String getJson(String path) {
		Response resp = execute(new Request(Type.GET, path));
		if (resp.code() == 404) {
			return null;
		}
		if (resp.code() != 200) {
			throw new RuntimeException("error: " + resp);
		}
		Content content = resp.content();
		return JsonUtil.parse(content.chars()).getAsString();
	}

	public boolean exists(String path) {
		return execute(new Request(Type.GET, path)).code() == 200;
	}

	public Response execute(String type, String path, String contentType,
			String content) {
		Request req = new Request(Type.valueOf(type), path);
		if (contentType != null) {
			req.contentType(contentType).content(content);
		}
		return execute(req);
	}

	public Response success(String type, String path, String contentType,
			String content) {
		Response resp = execute(type, path, contentType, content);
		if (resp.code() != 200) {
			throw new RuntimeException("error: " + resp);
		}
		return resp;
	}

	public Integer code(String type, String path, String ct, String c) {
		return execute(
				new Request(Type.valueOf(type), path).contentType(ct)
						.content(c)).code();
	}

	public void setUser(String user, String pass) {
		((HttpClientRequestService) service).setUser(user, pass);
	}

	public Content content(String type, String path) {
		Response resp = execute(type, path, null, null);
		if (resp.code() != 200) {
			throw new RuntimeException("error: " + resp);
		}
		return resp.content();
	}

	public Content content(Request request) {
		Response resp = execute(request);
		if (resp.code() != 200) {
			throw new RuntimeException("error: " + resp);
		}
		return resp.content();
	}

	public Response success(Request request) {
		Response resp = execute(request);
		if (resp.code() != 200) {
			throw new RuntimeException("error: " + resp);
		}
		return resp;
	}

}
