package com.googlecode.mycontainer.commons.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.mycontainer.commons.reflect.ObjectReflect;

public class IOUtil {

	private static final Logger LOG = LoggerFactory.getLogger(IOUtil.class);

	public static int copy(Reader reader, StringBuilder dest) {
		return copy(reader, dest, new char[512 * 1024]);
	}

	public static int copyAvailable(InputStream in, OutputStream out)
			throws IOException {
		return copy(in, out, new byte[Math.min(512 * 1024, in.available())]);
	}

	public static int copy(InputStream in, OutputStream out, byte[] buffer) {
		try {
			int read = in.read(buffer);
			if (read > 0) {
				out.write(buffer, 0, read);
			}
			return read;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static int copy(Reader reader, StringBuilder dest, char[] buffer) {
		try {
			int read = reader.read(buffer);
			if (read > 0) {
				dest.append(buffer, 0, read);
			}
			return read;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void copyAll(Reader in, Writer out) {
		try {
			IOUtils.copy(in, out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void copyAll(InputStream in, OutputStream out) {
		try {
			IOUtils.copy(in, out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] readBinary(URL url) {
		InputStream in = null;
		try {
			in = url.openStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			copyAll(in, out);
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			close(in);
		}
	}

	public static byte[] readAll(InputStream in) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			copyAll(in, out);
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void close(Object obj) {
		if (obj == null) {
			return;
		}
		try {

			ObjectReflect reflect = new ObjectReflect(obj);
			reflect.invoke("close");
		} catch (Exception e) {
			LOG.error("Error closing", e);
		}
	}

	public static void close(Closeable closeable) {
		if (closeable == null) {
			return;
		}
		try {
			closeable.close();
		} catch (Exception e) {
			LOG.error("Error closing", e);
		}
	}

	public static void copyAll(URL url, OutputStream out) {
		InputStream in = null;
		try {
			in = url.openStream();
			copyAll(in, out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			close(in);
		}
	}

	public static void copyAll(URL url, Writer out) {
		Reader in = null;
		try {
			in = new InputStreamReader(
					new BufferedInputStream(url.openStream()));
			copyAll(in, out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			close(in);
		}
	}

	public static Object serializeCopy(Object obj) {
		if (obj == null) {
			return null;
		}
		byte[] serialized = serialize(obj);
		return deserialize(serialized);
	}

	public static Object deserialize(byte[] serialized) {
		try {
			ObjectInputStream o = new ObjectInputStream(
					new ByteArrayInputStream(serialized));
			Object ret = o.readObject();
			o.close();
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] serialize(Object obj) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream o = new ObjectOutputStream(
					new BufferedOutputStream(out));
			o.writeObject(obj);
			o.close();
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void flush(Flushable o) {
		if (o != null) {
			try {
				o.flush();
			} catch (IOException e) {
				LOG.error("Error flushing", e);
			}
		}
	}

	public static char[] readChars(URL url) {
		CharArrayWriter out = new CharArrayWriter();
		copyAll(url, out);
		out.close();
		return out.toCharArray();

	}

	public static char[] readAll(Reader in) {
		CharArrayWriter out = new CharArrayWriter();
		copyAll(in, out);
		out.close();
		return out.toCharArray();
	}

	public static void copyAll(URL url, StringBuilder ret) {
		Reader in = null;
		try {
			in = new InputStreamReader(
					new BufferedInputStream(url.openStream()));
			copyAll(in, ret);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			close(in);
		}
	}

	private static void copyAll(Reader in, StringBuilder ret) {
		try {
			char[] buffer = new char[100 * 1024];
			while (true) {
				int read = in.read(buffer);
				if (read < 0) {
					return;
				}
				if (read > 0) {
					ret.append(buffer, 0, read);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static int compare(InputStream in1, InputStream in2) {
		try {
			BufferedInputStream bin1 = new BufferedInputStream(in1);
			BufferedInputStream bin2 = new BufferedInputStream(in2);
			while (true) {
				int b1 = bin1.read();
				int b2 = bin2.read();
				if (b1 < 0 || b2 < 0) {
					if (b1 < 0 && b2 < 0) {
						return 0;
					}
					return b1 < 0 ? -1 : 1;
				}
				if (b1 != b2) {
					return b1 - b2;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String read(File file, String charset) {
		try {
			return FileUtils.readFileToString(file, charset);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void copyAll(InputStream in, OutputStream out, byte[] buffer) {
		int read = copy(in, out, buffer);
		while (read >= 0) {
			read = copy(in, out, buffer);
		}
	}
}
