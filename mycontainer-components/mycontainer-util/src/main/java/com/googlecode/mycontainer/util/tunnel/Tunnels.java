package com.googlecode.mycontainer.util.tunnel;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.googlecode.mycontainer.util.ReflectionUtil;
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
		// args = new String[] { "Console", "0.0.0.0:5001:google.com:80",
		// "localhost:5002:chat.freenode.net:6667", "5003:localhost:5000" };
		if (args.length == 0 || "-h".equals(args[0]) || "--help".equals(args[0])) {
			System.out.println("Tunnels <Redirect|Log|Console> <local-host:local-port:remote-host:remote-port> <local-host:local-port:remote-host:remote-port> ...");
			return;
		}

		String handlerName = args[0];
		TunnelHandler handler = (TunnelHandler) ReflectionUtil.newInstance(Tunnels.class.getPackage().getName() + "." + handlerName + "TunnelHandler");
		List<Tunnel> list = new ArrayList<Tunnel>();
		for (int i = 1; i < args.length; i++) {
			String[] array = args[i].split(":");
			String localhost = "127.0.0.1";
			int j = 0;
			if (array.length >= 4) {
				localhost = array[j++];
			}
			int localport = Integer.parseInt(array[j++]);
			String remotehost = array[j++];
			int remoteport = Integer.parseInt(array[j++]);
			Tunnel tunnel = new Tunnel(localhost, localport, remotehost, remoteport);
			list.add(tunnel);
		}

		Tunnels tunnels = new Tunnels();
		tunnels.setHandler(handler);
		try {
			tunnels.bindAll(list);
			tunnels.run();
		} finally {
			tunnels.close();
		}
	}

	public void bindAll(Iterable<Tunnel> list) {
		for (Tunnel tunnel : list) {
			bind(tunnel);
		}
	}

	private void setHandler(TunnelHandler handler) {
		this.handler = handler;
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
			tunnel.accepts(handler);
			tunnel.read(handler);
		}
	}

}
