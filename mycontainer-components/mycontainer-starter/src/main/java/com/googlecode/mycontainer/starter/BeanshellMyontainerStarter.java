package com.googlecode.mycontainer.starter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bsh.EvalError;
import bsh.Interpreter;

public class BeanshellMyontainerStarter implements MycontainerStarter {

	private final Logger LOG = LoggerFactory
			.getLogger(BeanshellMyontainerStarter.class);

	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void execute() {
		LOG.info("Creating interpreter");
		Reader reader = null;
		try {
			URL u = new URL(url);
			Interpreter interpreter = new Interpreter();
			reader = new InputStreamReader(u.openStream());
			interpreter.eval(reader);
		} catch (IOException e) {
			throw new RuntimeException("error", e);
		} catch (SecurityException e) {
			throw new RuntimeException("error", e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("error", e);
		} catch (EvalError e) {
			throw new RuntimeException("error", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					LOG.error("error closing", e);
				}
			}
		}
	}

}
