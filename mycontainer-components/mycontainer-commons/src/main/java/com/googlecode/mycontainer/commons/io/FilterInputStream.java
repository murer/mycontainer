package com.googlecode.mycontainer.commons.io;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FilterInputStream extends java.io.FilterInputStream {

	private static final Logger LOG = LoggerFactory
			.getLogger(FilterInputStream.class);

	public FilterInputStream(InputStream in) {
		super(in);
	}

	@Override
	public int read() {
		try {
			return super.read();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int read(byte[] b) {
		try {
			return super.read(b);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int read(byte[] b, int off, int len) {
		try {
			return super.read(b, off, len);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public synchronized void mark(int readlimit) {
		super.mark(readlimit);
	}

	@Override
	public int available() {
		try {
			return in.available();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public synchronized void reset() {
		try {
			in.reset();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long skip(long n) {
		try {
			return in.skip(n);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			super.close();
		} catch (IOException e) {
			LOG.error("Error closing");
		}
	}

}
