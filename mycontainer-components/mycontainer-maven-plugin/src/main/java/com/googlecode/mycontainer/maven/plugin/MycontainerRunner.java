package com.googlecode.mycontainer.maven.plugin;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.maven.plugin.logging.Log;

import com.googlecode.mycontainer.starter.BeanshellMyontainerStarter;

public class MycontainerRunner implements Runnable {

	private final Thread thread;

	private final Log log;

	private final File bsh;

	private ClassLoader classloader;

	public MycontainerRunner(Log log, ClassLoader classloader, File bsh) {
		this.log = log;
		this.bsh = bsh;
		this.classloader = classloader;
		thread = new Thread(this, "MycontainerRunner");
		thread.setContextClassLoader(classloader);
	}

	public void start() {
		thread.start();
	}

	public void join() throws InterruptedException {
		thread.join();
	}

	public void run() {
		try {
			log.info("Creating starter");
			PluginUtil.configureLogger(classloader, log);
			BeanshellMyontainerStarter starter = new BeanshellMyontainerStarter();
			starter.setUrl(bsh.toURI().toURL().toString());
			starter.execute();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
