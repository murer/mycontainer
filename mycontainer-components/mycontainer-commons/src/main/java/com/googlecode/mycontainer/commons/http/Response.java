package com.googlecode.mycontainer.commons.http;

public class Response {

	private Integer code;

	private Request request;

	private final NamePairs headers = new NamePairs();

	private Content content;

	private String message;

	public Response() {

	}

	public Response(Request req) {
		request(req);
	}

	public Request request() {
		return request;
	}

	public Response request(Request request) {
		this.request = request;
		return this;
	}

	public Content content() {
		return content;
	}

	public Response content(Content content) {
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
			throw new RuntimeException("mediaType wrong: " + mediaType + ", "
					+ content.mediaType());
		}
		if (charset == null && content.charset() != null) {
			throw new RuntimeException("charset wrong: " + charset + ", "
					+ content.charset());
		}
		if (charset != null && !charset.equals(content.charset())) {
			throw new RuntimeException("charset wrong: " + charset + ", "
					+ content.charset());
		}

		this.content = content;
		return this;
	}

	public NamePairs headers() {
		return headers;
	}

	public Integer code() {
		return code;
	}

	public Response code(Integer code) {
		this.code = code;
		return this;
	}

	public Response message(String message) {
		this.message = message;
		return this;
	}

	public String messages() {
		return message;
	}

	public Response content(String text) {
		String mediaType = mediaType();
		String charset = charset();
		return content(Content.createFromString(mediaType, charset, text));
	}

	public Response content(char[] charArray) {
		String mediaType = mediaType();
		String charset = charset();
		return content(Content.createFromString(mediaType, charset, charArray));
	}

	public Response content(byte[] bytes) {
		return content(Content.create(mediaType(), charset(), bytes));
	}

	public String contentType() {
		return headers.contentType();
	}

	public String mediaType() {
		return headers.mediaType();
	}

	public String charset() {
		return headers.charset();
	}

	public Response contentType(String value) {
		headers.set("Content-Type", value);
		return this;
	}

	public Response copy() {
		Response ret = new Response(request).code(code).message(message);
		ret.headers().pairs(headers().pairs());
		ret.content(content);
		return ret;
	}

	@Override
	public String toString() {
		return "" + code + " " + message + " " + request + " " + content;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((request == null) ? 0 : request.hashCode());
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
		Response other = (Response) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
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
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (request == null) {
			if (other.request != null)
				return false;
		} else if (!request.equals(other.request))
			return false;
		return true;
	}

	public Response addHeader(String name, String value) {
		headers.add(name, value);
		return this;
	}

	public Response contentType(String mediaType, String charset) {
		headers.contentType(mediaType, charset);
		return this;
	}

}
