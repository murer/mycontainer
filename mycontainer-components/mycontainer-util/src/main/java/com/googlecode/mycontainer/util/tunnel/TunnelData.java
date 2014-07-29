package com.googlecode.mycontainer.util.tunnel;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

public class TunnelData {

	private final InputStream in;

	private byte[] data = new byte[10 * 1024];

	private int offset = 0;

	public TunnelData(InputStream in) {
		this.in = in;
	}

	public void readData() {
		try {
			if (offset >= data.length) {
				return;
			}
			int read = in.read(data, offset, data.length - offset);
			offset += read;
		} catch (SocketTimeoutException e) {
			return;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] getBuffer() {
		byte[] ret = new byte[offset];
		System.arraycopy(data, 0, ret, 0, offset);
		return ret;
	}

}
