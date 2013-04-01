package com.googlecode.mycontainer.commons.http;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.JsonElement;
import com.googlecode.mycontainer.commons.util.ContentUtil;
import com.googlecode.mycontainer.commons.util.JsonUtil;

public class Content {

	private String charset;

	private final String mediaType;

	private byte[] data = new byte[0];

	private Content(String mediaType, String charset, byte[] data) {
		charset(charset);
		this.mediaType = mediaType;
		this.data = data;
	}

	public static Content create(String mediaType, String charset, byte[] data) {
		return new Content(mediaType, charset, data);
	}

	public byte[] data() {
		return data;
	}

	public String charset() {
		return charset;
	}

	public String mediaType() {
		return mediaType;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(mediaType).append("; charset=")
				.append(charset).append(" ")
				.append(data == null ? "null" : data.length).toString();
	}

	public Content changeCharset(String charset) {
		if (charset == null) {
			throw new RuntimeException("charset requried");
		}
		data = ContentUtil.changeCharset(data, this.charset, charset);
		this.charset = charset;
		return this;
	}

	public String text() {
		if (charset == null) {
			throw new RuntimeException("charset requried");
		}
		try {
			return new String(data, charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void charset(String charset) {
		this.charset = charset;
	}

	public void data(byte[] data) {
		this.data = data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((charset == null) ? 0 : charset.hashCode());
		result = prime * result + Arrays.hashCode(data);
		result = prime * result
				+ ((mediaType == null) ? 0 : mediaType.hashCode());
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
		Content other = (Content) obj;
		if (charset == null) {
			if (other.charset != null)
				return false;
		} else if (!charset.equals(other.charset))
			return false;
		if (!Arrays.equals(data, other.data))
			return false;
		if (mediaType == null) {
			if (other.mediaType != null)
				return false;
		} else if (!mediaType.equals(other.mediaType))
			return false;
		return true;
	}

	public static Content createFromString(String mediaType, String text) {
		try {
			return create(mediaType, "utf-8", text.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static Content createFromString(String mediaType, char[] array) {
		return create(mediaType, "utf-8",
				ContentUtil.writeCharset(array, "utf-8"));
	}

	public static Content createFromString(String mediaType, String charset,
			char[] array) {
		return create(mediaType, charset,
				ContentUtil.writeCharset(array, charset));
	}

	public static Content createFromString(String mediaType, String charset,
			String text) {
		try {
			return create(mediaType, charset, text.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isBinary() {
		return charset() == null;
	}

	public String encoded() {
		return Base64.encodeBase64String(data);
	}

	public static Content createFromEncoded(String mediaType, String str) {
		byte[] data = Base64.decodeBase64(str);
		return create(mediaType, null, data);
	}

	public char[] chars() {
		if (charset == null) {
			throw new RuntimeException("charset requried");
		}
		return ContentUtil.getChars(data, charset);
	}

	public JsonElement json() {
		String text = text();
		return JsonUtil.parse(text);
	}

	public static Content create(JsonElement element) {
		return createFromString("application/json", element.toString());
	}

	public boolean isJson() {
		return "application/json".equals(mediaType);
	}
}
