package com.googlecode.mycontainer.commons.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import com.googlecode.mycontainer.commons.lang.ThreadUtil;


public class TimeoutReader extends FilterReader {

	private long timeout = 0l;

	private long sleep = 0l;

	public TimeoutReader(Reader in, long timeout) {
		super(in);
		setTimeout(timeout);
	}

	public TimeoutReader(Reader in) {
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
		waitFor();
		return super.read();
	}

	@Override
	public int read(char[] b) {
		waitFor();
		return super.read(b);
	}

	@Override
	public int read(char[] b, int off, int len) {
		waitFor();
		return super.read(b, off, len);
	}

	@Override
	public int read(CharBuffer target) throws IOException {
		waitFor();
		return super.read(target);
	}

	@Override
	public boolean ready() {
		return super.ready();
	}

	public void waitFor() {
		long before = System.currentTimeMillis();
		while (!ready()) {
			ThreadUtil.sleep(sleep);
			long now = System.currentTimeMillis();
			if (before + timeout < now) {
				throw new IllegalStateException("timeout");
			}
		}
	}

	@Override
	public long skip(long n) {
		waitFor();
		return super.skip(n);
	}

}
