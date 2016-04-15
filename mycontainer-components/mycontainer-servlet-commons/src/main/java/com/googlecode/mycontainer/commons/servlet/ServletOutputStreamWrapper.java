package com.googlecode.mycontainer.commons.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class ServletOutputStreamWrapper extends ServletOutputStream {

	private OutputStream out;

	public ServletOutputStreamWrapper(OutputStream out) {
		this.out = out;
	}

	@Override
	public void write(int b) throws IOException {
		this.out.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		this.out.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		this.out.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		super.flush();
		out.flush();
	}

	@Override
	public void close() throws IOException {
		super.close();
		out.close();
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setWriteListener(WriteListener writeListener) {
		throw new RuntimeException("not supported");
	}

}
