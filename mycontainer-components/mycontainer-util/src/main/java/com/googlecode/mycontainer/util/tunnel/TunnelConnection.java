package com.googlecode.mycontainer.util.tunnel;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import com.googlecode.mycontainer.util.Util;

public class TunnelConnection implements Closeable {

	private Socket local;

	private Socket remote;

	private TunnelData localData;

	private TunnelData remoteData;

	public Socket getLocal() {
		return local;
	}

	public TunnelConnection setLocal(Socket local) {
		try {
			this.local = local;
			this.localData = new TunnelData(local.getInputStream());
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Socket getRemote() {
		return remote;
	}

	public TunnelConnection setRemote(Socket remote) {
		try {
			this.remote = remote;
			this.remoteData = new TunnelData(remote.getInputStream());
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		Util.close(local);
		Util.close(remote);
	}

	@Override
	public String toString() {
		return "[TunnelConnection " + toString(local) + " " + toString(remote) + "]";
	}

	private String toString(Socket sck) {
		try {
			if (!sck.isConnected()) {
				return "[Socket disconnected]";
			}
			InetAddress addr = sck.getInetAddress();
			String host = addr == null ? null : addr.getHostAddress();
			int port = sck.getPort();
			int localPort = sck.getLocalPort();
			return new StringBuilder().append(localPort).append(":").append(host).append(":").append(port).toString();
		} catch (Exception e) {
			return sck.toString();
		}
	}

	public void readData() {
		boolean localStopped = localData.isStopped();
		if (!localStopped) {
			localData.readData();
		}
		boolean remoteStopped = remoteData.isStopped();
		if (!remoteStopped) {
			remoteData.readData();
		}
	}

	public TunnelData getLocalData() {
		return localData;
	}

	public void setLocalData(TunnelData localData) {
		this.localData = localData;
	}

	public TunnelData getRemoteData() {
		return remoteData;
	}

	public void setRemoteData(TunnelData remoteData) {
		this.remoteData = remoteData;
	}

	public void writeRemote(byte[] data) {
		try {
			OutputStream out = remote.getOutputStream();
			out.write(data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeLocal(byte[] data) {
		try {
			OutputStream out = local.getOutputStream();
			out.write(data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isStopped() {
		return local.isClosed() || remote.isClosed();
	}

	public boolean hasBuffer() {
		return localData.hasBuffer() || remoteData.hasBuffer();
	}

}
