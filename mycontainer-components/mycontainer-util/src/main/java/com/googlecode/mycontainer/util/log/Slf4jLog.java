package com.googlecode.mycontainer.util.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jLog extends Log {

	private final Logger logger;

	public Slf4jLog(String name) {
		this.logger = LoggerFactory.getLogger(name);
	}

	@Override
	public void error(String msg) {
		logger.error(msg);
	}

	@Override
	public void error(String msg, Throwable e) {
		logger.error(msg, e);
	}

	@Override
	public void info(String msg) {
		logger.info(msg);
	}

	@Override
	public void info(String msg, Throwable e) {
		logger.info(msg, e);
	}

	@Override
	public void debug(String msg) {
		logger.debug(msg);
	}

	@Override
	public void debug(String msg, Throwable e) {
		logger.debug(msg, e);
	}

}
