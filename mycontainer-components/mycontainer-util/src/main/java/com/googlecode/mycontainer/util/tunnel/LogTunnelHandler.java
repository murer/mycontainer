package com.googlecode.mycontainer.util.tunnel;

import com.googlecode.mycontainer.util.log.Log;

public class LogTunnelHandler extends ConsoleTunnelHandler {

	private static Log LOG = Log.get(LogTunnelHandler.class);

	@Override
	protected void log(String msg) {
		LOG.info(msg);
	}

}
