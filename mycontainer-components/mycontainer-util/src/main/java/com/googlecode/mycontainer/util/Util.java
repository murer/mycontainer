package com.googlecode.mycontainer.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

	private static final Logger LOG = LoggerFactory.getLogger(Util.class);

	public static void close(AutoCloseable o) {
		if (o != null) {
			try {
				o.close();
			} catch (Exception e) {
				LOG.error("error closing", e);
			}
		}
	}

	public static Long exitCode(Process process) {
		try {
			int ret = process.exitValue();
			return new Long(ret);
		} catch (IllegalThreadStateException e) {
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static int copyAvailable(InputStream in, OutputStream out, int limit) {
		try {
			limit = Math.min(available(in), limit);
			if (limit <= 0) {
				return 0;
			}
			byte[] buffer = new byte[limit];
			int read = in.read(buffer);
			out.write(buffer, 0, read);
			return read;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static int available(InputStream in) {
		try {
			return in.available();
		} catch (IOException e) {
			return -1;
		}
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toString(ByteArrayOutputStream out, String charset) {
		return toString(out.toByteArray(), charset);
	}

	public static String toString(byte[] array, String charset) {
		try {
			return new String(array, charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] cut(byte[] buffer, int offset, int limit) {
		byte[] ret = new byte[limit];
		System.arraycopy(buffer, offset, ret, 0, limit);
		return ret;
	}

	public static void writeFlush(OutputStream out, String str, String charset) {
		byte[] buffer = toBytes(str, charset);
		writeFlush(out, buffer);
	}

	public static byte[] toBytes(String str, String charset) {
		try {
			return str.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] readAvailable(InputStream in, int max) {
		try {
			int available = available(in);
			if (available <= 0) {
				return new byte[0];
			}
			max = Math.min(available, max);
			byte[] ret = new byte[max];
			if (max > 0) {
				in.read(ret, 0, max);
			}
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String readAvailable(InputStream in, int limit, String charset) {
		byte[] buffer = readAvailable(in, limit);
		return toString(buffer, charset);
	}

	public static void writeFlush(OutputStream out, byte[] buffer) {
		try {
			if (buffer.length == 0) {
				return;
			}
			out.write(buffer);
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> map(K k, V v, Object... values) {
		HashMap<K, V> ret = new HashMap<K, V>();
		ret.put(k, v);
		for (int i = 0; i < values.length; i++) {
			K key = (K) values[i];
			V value = (V) values[i + 1];
			ret.put(key, value);
		}
		return ret;
	}

	public static void close(HttpURLConnection conn) {
		if (conn != null) {
			try {
				conn.disconnect();
			} catch (Exception e) {
				LOG.error("error closing", e);
			}
		}
	}

	public static String readAll(InputStream in, String charset) {
		try {
			return readAll(new InputStreamReader(in, charset));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private static String readAll(InputStreamReader in) {
		try {
			StringBuilder ret = new StringBuilder();
			char[] buffer = new char[8 * 1024];
			while (true) {
				int read = in.read(buffer);
				if (read < 0) {
					return ret.toString();
				}
				ret.append(buffer, 0, read);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String format(String str, Object... args) {
		return String.format(str, args);
	}

	@SuppressWarnings("unchecked")
	public static <T> T cause(Throwable exp, Class<T> clazz) {
		Throwable current = exp;
		while (current != null) {
			if (clazz.isInstance(current)) {
				return (T) current;
			}
			current = current.getCause();
		}
		return null;
	}

	public static String stack(Exception exp) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			PrintStream out = new PrintStream(bout);
			exp.printStackTrace(out);
			out.close();
			byte[] data = bout.toByteArray();
			return toString(data, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static long copy(InputStream in, OutputStream out) {
		try {
			return copyExp(in, out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static long copyExp(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[10 * 1024];
		long total = 0;
		while (true) {
			int read = in.read(buffer);
			if (read < 0) {
				return total;
			}
			if (read > 0) {
				total += read;
				out.write(buffer, 0, read);
				out.flush();
			}
		}
	}

	public static void join(Thread thread) {
		if (thread != null) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				LOG.error("error on thread.join", e);
			}
		}
	}

	public static synchronized void hold() {
		try {
			Util.class.wait();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static String str(String str) {
		if (str == null) {
			return null;
		}
		str = str.trim();
		return str.length() == 0 ? null : str;
	}

	public static String readAll(URL url, String charset) {
		InputStream in = null;
		try {
			in = new BufferedInputStream(url.openStream());
			return readAll(in, charset);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			close(in);
		}
	}

	public static String readAll(File file, String charset) {
		try {
			return readAll(file.toURI().toURL(), charset);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public static void sleep(long millis, int nanos) {
		try {
			Thread.sleep(millis, nanos);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static void write(File file, String data, String charset) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			write(out, data, charset);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			close(out);
		}
	}

	public static void write(OutputStream out, String data, String charset) {
		try {
			byte[] b = toBytes(data, charset);
			out.write(b);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void deleteAll(String dir) {
		deleteAll(new File(dir));
	}

	public static void deleteAll(File file) {
		if (!file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File child : files) {
				deleteAll(child);
			}
		}
		if (!file.delete()) {
			throw new RuntimeException("wrong: " + file);
		}
		return;
	}

	public static void write(File file, InputStream in) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			copy(in, out);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			close(out);
		}
	}

	public static void read(File file, OutputStream out) {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			copy(in, out);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			close(in);
		}
	}

	public static String readURL(String url, String charset) {
		try {
			return readAll(new URL(url), charset);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public static int put(String strurl, String contentType, String data) {
		HttpURLConnection conn = null;
		OutputStream out = null;
		try {
			URL url = new URL(strurl);
			byte[] array = toBytes(data, "UTF-8");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", contentType);
			conn.setRequestProperty("Content-Length", Integer.toString(array.length));
			conn.setDoOutput(true);
			out = conn.getOutputStream();
			out.write(array);
			out.close();
			out = null;

			return conn.getResponseCode();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Util.close(out);
			Util.close(conn);
		}
	}

	public static void copy(URL url, OutputStream out) {
		InputStream in = null;
		try {
			in = url.openStream();
			copy(in, out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			close(in);
		}
	}

}
