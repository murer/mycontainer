package com.googlecode.mycontainer.commons.http;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.googlecode.mycontainer.commons.file.PathUtil;
import com.googlecode.mycontainer.commons.util.JsonUtil;

import flexjson.JSON;

public class Request {

	private Type type;

	private String path;

	private final NamePairs params = new NamePairs();

	private final NamePairs headers = new NamePairs();

	private Content content;

	public Request() {

	}

	public Request(String type, String path) {
		type(type);
		this.path = path;
	}

	public Request(Type type, String path) {
		this.type = type;
		this.path = path;
	}

	public Type type() {
		return type;
	}

	public Request type(String type) {
		return type(type == null ? null : Type.valueOf(type));
	}

	public Request type(Type type) {
		this.type = type;
		return this;
	}

	public String path() {
		return path;
	}

	public Request path(String path) {
		this.path = path;
		return this;
	}

	public Content content() {
		return content;
	}

	public String contenType() {
		return headers.contentType();
	}

	public String mediaType() {
		return headers.mediaType();
	}

	public String charset() {
		return headers.charset();
	}

	public Request content(String text) {
		String mediaType = mediaType();
		String charset = charset();
		return content(Content.createFromString(mediaType, charset, text));
	}

	public Request content(char... charArray) {
		String mediaType = mediaType();
		String charset = charset();
		return content(Content.createFromString(mediaType, charset, charArray));
	}

	public Request contentJson(String json) {
		return content(JsonUtil.parse(json));
	}

	public Request content(JsonElement element) {
		contentType("application/json;charset=utf8");
		return content(element == null ? "null" : element.toString());
	}

	public Request content(Content content) {
		if (content == null) {
			this.content = null;
			return this;
		}
		String mediaType = mediaType();
		String charset = charset();

		if (mediaType == null) {
			throw new RuntimeException("mediaType is required");
		}
		if (!mediaType.equals(content.mediaType())) {
			throw new RuntimeException("mediaType wrong: " + mediaType + ", " + content.mediaType());
		}
		if (charset == null && content.charset() != null) {
			throw new RuntimeException("charset wrong: " + charset + ", " + content.charset());
		}
		if (charset != null && !charset.equals(content.charset())) {
			throw new RuntimeException("charset wrong: " + charset + ", " + content.charset());
		}

		this.content = content;
		return this;
	}

	public NamePairs params() {
		return params;
	}

	public NamePairs headers() {
		return headers;
	}

	@Override
	public String toString() {
		String ret = "" + type + " " + path;
		String p = params.toString();
		if (p.length() > 0) {
			ret += "?" + params;
		}
		if (content != null) {
			ret += " " + content;
		}
		return ret;
	}

	@JSON(include = false)
	public String contentType() {
		return headers.contentType();
	}

	public Request copy() {
		Request ret = new Request().path(path).type(type);
		ret.headers().pairs(headers().pairs());
		ret.params().pairs(params().pairs());
		ret.content(content);
		return ret;
	}

	public Request setHeader(String key, String value) {
		headers.set(key, value);
		return this;
	}

	public Request accept(String value) {
		headers.set("Accept", value);
		return this;
	}

	public Request contentType(String value) {
		headers.set("Content-Type", value);
		return this;
	}

	public Request content(byte... bytes) {
		return content(Content.create(mediaType(), charset(), bytes));
	}

	public URI toURI(String repository) {
		try {
			String uriPath = encodeRequestLine();
			if (uriPath.startsWith("http://") || uriPath.startsWith("https://")) {
				return new URI(uriPath);
			}
			return new URI(repository + uriPath);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private String encodeRequestLine() {
		String p = params.encodeParams();
		if (p.length() > 0) {
			return "" + path + "?" + p;
		} else {
			return "" + path;
		}
	}

	public Request param(String key, String value) {
		params.add(key, value);
		return this;
	}

	public Request header(String key, String value) {
		headers.add(key, value);
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + ((params == null) ? 0 : params.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Request other = (Request) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (headers == null) {
			if (other.headers != null)
				return false;
		} else if (!headers.equals(other.headers))
			return false;
		if (params == null) {
			if (other.params != null)
				return false;
		} else if (!params.equals(other.params))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public URL toURL() {
		try {
			return new URL(encodeRequestLine());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public Request appendPath(String path) {
		return path(PathUtil.concatPath(this.path, path));
	}

	public boolean isBinary() {
		return headers.isBinary();
	}

	public static Request create(String type, String url) {
		return new Request(type, url);
	}

	public Response exec(RequestService s) {
		return s.execute(this);
	}

	public Response success(RequestService s) {
		Response ret = exec(s);
		Integer code = ret.code();
		if (code < 200 || code >= 300) {
			throw new RuntimeException("error: " + ret);
		}
		return ret;
	}

	public Content content(RequestService s) {
		Response resp = success(s);
		return resp.content();
	}

	public JsonElement json(RequestService s) {
		Content ret = content(s);
		return ret.json();
	}

	public JsonObject jsonObject(RequestService s) {
		JsonElement ret = json(s);
		return (JsonObject) ret;
	}

	public JsonArray jsonArray(RequestService s) {
		JsonElement ret = json(s);
		return (JsonArray) ret;
	}

	public JsonPrimitive jsonPrimitive(RequestService s) {
		JsonElement ret = json(s);
		return (JsonPrimitive) ret;
	}

	public String jsonString(RequestService s) {
		JsonElement ret = json(s);
		return ret == null || ret.isJsonNull() ? null : ret.getAsString();
	}

	public Long jsonLong(RequestService s) {
		JsonElement ret = json(s);
		return ret == null || ret.isJsonNull() ? null : ret.getAsLong();
	}

	public Boolean jsonBoolean(RequestService s) {
		JsonElement ret = json(s);
		return ret == null || ret.isJsonNull() ? null : ret.getAsBoolean();
	}

	public Double jsonDouble(RequestService s) {
		JsonElement ret = json(s);
		return ret == null || ret.isJsonNull() ? null : ret.getAsDouble();
	}
}
