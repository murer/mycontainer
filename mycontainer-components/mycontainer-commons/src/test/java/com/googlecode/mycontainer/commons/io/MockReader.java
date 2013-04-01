package com.googlecode.mycontainer.commons.io;

import java.io.CharArrayReader;
import java.io.Reader;

import com.googlecode.mycontainer.commons.io.FilterReader;


public class MockReader extends FilterReader {

	public MockReader() {
		this(new CharArrayReader(new char[0]));
	}

	public MockReader(Reader in) {
		super(in);
	}

	@Override
	public int read() {
		try {
			Thread.sleep(1000);
			return super.read();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int read(char[] b) {
		try {
			Thread.sleep(1000);
			return super.read(b);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int read(char[] b, int off, int len) {
		try {
			Thread.sleep(1000);
			return super.read(b, off, len);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
