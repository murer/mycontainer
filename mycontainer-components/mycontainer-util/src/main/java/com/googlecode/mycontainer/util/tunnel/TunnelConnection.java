package com.googlecode.mycontainer.util.tunnel;

import java.io.Closeable;
import java.net.Socket;

import com.googlecode.mycontainer.util.Util;
import com.googlecode.mycontainer.util.log.Log;

public class TunnelConnection implements Closeable {

	private static Log LOG = Log.get(Tunnel.class);

	private Socket local;

	private Socket remote;

	public Socket getLocal() {
		return local;
	}

	public TunnelConnection setLocal(Socket local) {
		this.local = local;
		return this;
	}

	public Socket getRemote() {
		return remote;
	}

	public TunnelConnection setRemote(Socket remote) {
		this.remote = remote;
		return this;
	}

	public void close() {
		LOG.info("Closing " + this);
		Util.close(local);
		Util.close(remote);
	}

	@Override
	public String toString() {
		return "[TunnelConnection " + local + " " + remote + "]";
	}

}
