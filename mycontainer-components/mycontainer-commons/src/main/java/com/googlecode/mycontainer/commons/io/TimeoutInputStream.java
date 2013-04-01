package com.googlecode.mycontainer.commons.io;

import java.io.InputStream;

import com.googlecode.mycontainer.commons.lang.ThreadUtil;


public class TimeoutInputStream extends FilterInputStream {

	private long timeout = 1000l;

	private long sleep = 10l;

	public TimeoutInputStream(InputStream in, long timeout) {
		super(in);
		setTimeout(timeout);
	}

	public TimeoutInputStream(InputStream in) {
		super(in);
	}

	public long getSleep() {
		return sleep;
	}

	public void setSleep(long sleep) {
		this.sleep = sleep;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public int read() {
		waitFor(1);
		return super.read();
	}

	@Override
	public int read(byte[] b) {
		waitFor(1);
		return super.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) {
		waitFor(1);
		return super.read(b, off, len);
	}

	@Override
	public long skip(long n) {
		waitFor(1);
		return super.skip(n);
	}

	@Override
	public int available() {
		waitFor(1);
		return super.available();
	}

	public boolean readyFor(int bytes) {
		return super.available() >= bytes;
	}

	public void waitFor(int bytes) {
		long before = System.currentTimeMillis();
		while (!readyFor(bytes)) {
			ThreadUtil.sleep(sleep);
			long now = System.currentTimeMillis();
			if (before + timeout < now) {
				throw new IllegalStateException("timeout");
			}
		}
	}

}
