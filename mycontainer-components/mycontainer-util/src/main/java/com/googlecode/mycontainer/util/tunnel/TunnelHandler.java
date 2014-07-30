package com.googlecode.mycontainer.util.tunnel;

public interface TunnelHandler {

	void connected(TunnelConnection conn);

	void disconnected(TunnelConnection conn);

	void data(TunnelConnection conn);

}
