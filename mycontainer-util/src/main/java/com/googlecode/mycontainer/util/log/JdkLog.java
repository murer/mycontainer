package com.googlecode.mycontainer.util.log;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JdkLog extends Log {

	private final Logger logger;

	public JdkLog(String name) {
		this.logger = Logger.getLogger(name);
	}

	@Override
	public void error(String msg) {
		logger.log(Level.SEVERE, msg);
	}

	@Override
	public void error(String msg, Throwable e) {
		logger.log(Level.SEVERE, msg, e);
	}

	@Override
	public void info(String msg) {
		logger.log(Level.INFO, msg);
	}

	@Override
	public void info(String msg, Throwable e) {
		logger.log(Level.INFO, msg, e);
	}

	@Override
	public void debug(String msg) {
		logger.log(Level.FINE, msg);
	}

	@Override
	public void debug(String msg, Throwable e) {
		logger.log(Level.FINE, msg, e);
	}

}
