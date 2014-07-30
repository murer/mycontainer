package com.googlecode.mycontainer.util.tunnel;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

public class TunnelData {

	private TunnelState state = TunnelState.RUN;

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
			if (TunnelState.STOP.equals(state)) {
				if (offset > 0) {
					return;
				}
				throw new RuntimeException("wrong");
			}
			int read = in.read(data, offset, data.length - offset);
			if (read >= 0) {
				offset += read;
			} else {
				state = TunnelState.STOP;
			}
		} catch (SocketTimeoutException e) {
			return;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] consume() {
		byte[] ret = getBuffer();
		offset = 0;
		return ret;
	}

	public byte[] getBuffer() {
		byte[] ret = new byte[offset];
		System.arraycopy(data, 0, ret, 0, offset);
		return ret;
	}

	public boolean isStopped() {
		return TunnelState.STOP.equals(state);
	}

	public boolean hasBuffer() {
		return offset > 0;
	}

}
