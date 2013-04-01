package com.googlecode.mycontainer.commons.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import com.googlecode.mycontainer.commons.regex.RegexUtil;

public class ContentUtil {

	// private static final String PATTERN_CONTENT_TYPE =
	// "(.*)\\;\\s*charset=([^;]*).*";
	private static final String PATTERN_CONTENT_TYPE = "([\\+\\w\\/-]*);{0,1}\\s*(charset=)*(([\\w-]*))";

	public static byte[] changeCharset(byte[] data, String from, String to) {
		if (from == null || to == null) {
			throw new RuntimeException("both charsets are requried");
		}
		try {
			Reader in = new InputStreamReader(new ByteArrayInputStream(data),
					from);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Writer out = new OutputStreamWriter(buffer, to);
			copy(in, out);
			out.close();
			in.close();
			return buffer.toByteArray();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void copy(Reader in, Writer out) {
		try {
			char[] buffer = new char[1024 * 512];
			int read = 0;
			do {
				read = in.read(buffer);
				if (read > 0) {
					out.write(buffer, 0, read);
				}
			} while (read >= 0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] writeCharset(char[] array, String charset) {
		if (charset == null) {
			throw new RuntimeException("charset are requried");
		}
		try {
			Reader in = new CharArrayReader(array);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Writer out = new OutputStreamWriter(buffer, charset);
			copy(in, out);
			out.close();
			in.close();
			return buffer.toByteArray();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getMediaType(String contentType) {
		if (contentType == null) {
			return null;
		}
		List<String> groups = RegexUtil.groups(PATTERN_CONTENT_TYPE,
				contentType);
		if (groups.isEmpty()) {
			return contentType;
		}
		String r = groups.get(1);
		if (r == null || r.length() == 0) {
			return contentType;
		}
		return r;
	}

	public static String getCharset(String contentType) {
		if (contentType == null) {
			return null;
		}
		List<String> groups = RegexUtil.groups(PATTERN_CONTENT_TYPE,
				contentType);
		if (groups.isEmpty()) {
			return null;
		}
		String r = groups.get(3);
		if (r == null || r.length() == 0) {
			return null;
		}
		return r;
	}

	public static char[] getChars(byte[] data, String charset) {
		if (charset == null) {
			throw new RuntimeException("charset are requried");
		}
		try {
			Reader in = new InputStreamReader(new ByteArrayInputStream(data),
					charset);
			CharArrayWriter out = new CharArrayWriter();
			copy(in, out);
			out.close();
			in.close();
			return out.toCharArray();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getString(byte[] bytes, String charset) {
		try {
			return new String(bytes, charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
