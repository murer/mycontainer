package com.googlecode.mycontainer.util.tunnel;

import java.text.MessageFormat;

public class ConsoleTunnelHandler extends RedirectTunnelHandler {

	@Override
	public void data(TunnelConnection conn) {
		byte[] sent = conn.getLocalData().getBuffer();
		if (sent.length > 0) {
			println("sent {0} {1}", conn, bufferToString(sent));
		}
		byte[] received = conn.getRemoteData().getBuffer();
		if (received.length > 0) {
			println("received {0} {1}", conn, bufferToString(received));
		}
		super.data(conn);
	}

	protected String bufferToString(byte[] buffer) {
		StringBuilder sb = new StringBuilder();
		StringBuilder hex = new StringBuilder();
		for (int i = 0; i < buffer.length; i++) {
			byte b = buffer[i];
			int num = 0xFFFFFFFF & b;
			if (num <= 0xF) {
				hex.append('0');
			}
			hex.append(Integer.toHexString(num));
			if (i % 4 == 0 && i > 0) {
				hex.append(' ');
			}
			if (b < 20 || b > 127) {
				b = (char) '.';
			}
			char c = (char) (0xFFFF & b);
			sb.append(c);
		}
		return sb.append(' ').append(hex).toString();
	}

	@Override
	public void connected(TunnelConnection conn) {
		println("connected {0}", conn);
		super.connected(conn);
	}

	private void println(String msg, Object... values) {
		log(MessageFormat.format(msg, values));
	}

	protected void log(String msg) {
		System.out.println(msg);
	}

	@Override
	public void disconnected(TunnelConnection conn) {
		println("disconnected {0}", conn);
		super.disconnected(conn);
	}

}
