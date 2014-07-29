package com.googlecode.mycontainer.util.tunnel;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.net.ServerSocketFactory;

import com.googlecode.mycontainer.util.Util;
import com.googlecode.mycontainer.util.log.Log;

public class Tunnel implements Closeable {

	private static Log LOG = Log.get(Tunnel.class);

	private String localHost;
	private int localPort;
	private String remoteHost;
	private int remotePort;
	private ServerSocket serverSocket;

	public Tunnel() {

	}

	public Tunnel(String localHost, int localPort, String remoteHost, int remotePort) {
		this.localHost = localHost;
		this.localPort = localPort;
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
	}

	public String getLocalHost() {
		return localHost;
	}

	public Tunnel setLocalHost(String localHost) {
		this.localHost = localHost;
		return this;
	}

	public int getLocalPort() {
		return localPort;
	}

	public Tunnel setLocalPort(int localPort) {
		this.localPort = localPort;
		return this;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public Tunnel setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
		return this;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public Tunnel setRemotePort(int remotePort) {
		this.remotePort = remotePort;
		return this;
	}

	public void bind() {
		try {
			LOG.info("Binding " + this);
			InetAddress address = InetAddress.getByName(localHost);
			this.serverSocket = ServerSocketFactory.getDefault().createServerSocket(localPort, 50, address);
			this.serverSocket.setSoTimeout(1);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		} catch (SocketException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "[Tunnel " + localHost + ":" + localPort + " " + remoteHost + ":" + remotePort + "]";
	}

	public void close() {
		Util.close(serverSocket);
	}

}
