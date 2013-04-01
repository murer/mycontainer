package com.googlecode.mycontainer.commons.io;

import java.io.IOException;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FilterReader extends java.io.FilterReader {

	private static final Logger LOG = LoggerFactory
			.getLogger(FilterReader.class);

	public FilterReader(Reader in) {
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
	public int read(char[] b) {
		try {
			return super.read(b);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int read(char[] b, int off, int len) {
		try {
			return super.read(b, off, len);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public synchronized void mark(int readlimit) {
		try {
			super.mark(readlimit);
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
	public boolean ready() {
		try {
			return super.ready();
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
