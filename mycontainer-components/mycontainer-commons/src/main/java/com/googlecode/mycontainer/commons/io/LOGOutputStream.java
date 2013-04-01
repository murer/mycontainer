package com.googlecode.mycontainer.commons.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LOGOutputStream extends OutputStream {

	private static final Logger LOG = LoggerFactory
			.getLogger(LOGOutputStream.class);

	private final Charset charset;

	public LOGOutputStream() {
		this(Charset.defaultCharset());
	}

	public LOGOutputStream(Charset charset) {
		this.charset = charset;
	}

	@Override
	public void write(int b) throws IOException {
		LOG.info("write (incomplete): " + ((char) b));
	}

	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		String str = new String(b, off, len, charset);
		StringBuilder sb = new StringBuilder(str.length());
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '\r') {
				continue;
			}
			if (c == '\n') {
				LOG.info(sb.insert(0, "write: ").toString());
				sb.setLength(0);
				continue;
			}
			sb.append(c);
		}
		if (sb.length() > 0) {
			LOG.info(sb.insert(0, "write (incomplete): ").toString());
		}
	}

}
