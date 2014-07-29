package com.googlecode.mycontainer.util.tunnel;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

import com.googlecode.mycontainer.util.Util;
import com.googlecode.mycontainer.util.log.Log;

public class TunnelConnection implements Closeable {

	private static Log LOG = Log.get(Tunnel.class);

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
		LOG.info("Closing " + this);
		Util.close(local);
		Util.close(remote);
	}

	@Override
	public String toString() {
		return "[TunnelConnection " + local + " " + remote + "]";
	}

	public void readData() {
		localData.readData();
		remoteData.readData();
	}

	public void handler() {
		String l = new String(localData.getBuffer());
		if (l.length() > 0) {
			System.out.println("> " + l);
		}
		String r = new String(remoteData.getBuffer());
		if (r.length() > 0) {
			System.out.println("< " + r);
		}
	}

}
