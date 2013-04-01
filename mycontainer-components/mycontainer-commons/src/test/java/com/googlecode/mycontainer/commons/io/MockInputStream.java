package com.googlecode.mycontainer.commons.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.googlecode.mycontainer.commons.io.FilterInputStream;


public class MockInputStream extends FilterInputStream {

	public MockInputStream() {
		this(new ByteArrayInputStream(new byte[0]));
	}

	public MockInputStream(InputStream in) {
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
	public int read(byte[] b) {
		try {
			Thread.sleep(1000);
			return super.read(b);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int read(byte[] b, int off, int len) {
		try {
			Thread.sleep(1000);
			return super.read(b, off, len);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
