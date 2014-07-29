package com.googlecode.mycontainer.util.tunnel;

import java.awt.BorderLayout;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;

public class TunnelMonitor implements TunnelHandler {

	public TunnelMonitor() {
		init();
	}

	private void init() {
		JDesktopPane desktop = new JDesktopPane();
		desktop.setOpaque(false);
		JFrame main = new JFrame("Tunnel Monitor");
		main.getContentPane().add(desktop, BorderLayout.CENTER);
		main.setSize(300, 300);
		main.setVisible(true);
		main.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public void connected(TunnelConnection socketTunnel) {

	}

	public void disconnected(TunnelConnection conn) {

	}

	public void data(TunnelConnection conn) {

	}

	public static void main(String[] args) throws Exception {
		new TunnelMonitor();
	}

}
