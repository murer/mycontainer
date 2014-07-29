package com.googlecode.mycontainer.util.tunnel;

public class RedirectTunnelHandler implements TunnelHandler {

	public void data(TunnelConnection conn) {
		byte[] local = conn.getLocalData().consume();
		if (local.length > 0) {
			conn.writeRemote(local);
		}

		byte[] remote = conn.getRemoteData().consume();
		if (remote.length > 0) {
			conn.writeLocal(remote);
		}
	}

	public void connected(TunnelConnection socketTunnel) {

	}

	public void disconnected(TunnelConnection conn) {

	}

}
