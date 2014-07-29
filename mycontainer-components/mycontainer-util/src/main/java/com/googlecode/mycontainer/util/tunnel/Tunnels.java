package com.googlecode.mycontainer.util.tunnel;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.googlecode.mycontainer.util.Util;

public class Tunnels implements Closeable {

	private final List<Tunnel> tunnels = new ArrayList<Tunnel>();

	private TunnelHandler handler = new RedirectTunnelHandler();

	public Tunnels() {
	}

	public Tunnels bind(Tunnel tunnel) {
		this.tunnels.add(tunnel);
		tunnel.bind();
		return this;
	}

	public void close() {
		for (Tunnel tunnel : tunnels) {
			Util.close(tunnel);
		}
	}

	public void run() {
		while (true) {
			Thread.yield();
			step();
		}
	}

	public static void main(String[] args) {
		Tunnels tunnels = new Tunnels();
		try {
			// tunnels.bind(new Tunnel("0.0.0.0", 5001, "google.com", 80));
			// tunnels.bind(new Tunnel("0.0.0.0", 5002, "chat.freenode.net",
			// 6667));
			tunnels.bind(new Tunnel("0.0.0.0", 5003, "localhost", 5000));
			tunnels.run();
		} finally {
			tunnels.close();
		}
	}

	public void step() {
		Iterator<Tunnel> it = tunnels.iterator();
		while (it.hasNext()) {
			Tunnel tunnel = it.next();
			if (tunnel.isClosed()) {
				it.remove();
				continue;
			}
			tunnel.closeFinisheds();
			tunnel.accepts();
			tunnel.read();

			List<TunnelConnection> connections = tunnel.getConnections();
			for (TunnelConnection conn : connections) {
				handler.handle(conn);
			}
		}
	}

}
