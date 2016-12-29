package com.googlecode.mycontainer.maven.plugin;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.googlecode.mycontainer.darkproxy.DarkProxy;
import com.googlecode.mycontainer.darkproxy.DarkProxyFilter;
import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.util.Util;
import com.googlecode.mycontainer.web.ContextWebServer;
import com.googlecode.mycontainer.web.FilterDesc;
import com.googlecode.mycontainer.web.jetty.JettyServerDeployer;

/**
 * @goal dark-proxy
 * @aggregator
 * @requiresProject false
 */
public class MycontainerDarkProxyMojo extends AbstractMojo {

	/**
	 * @parameter expression="${mycontainer.darkproxy.timeout}"
	 */
	private long timeout = -1L;

	/**
	 * @parameter expression="${mycontainer.darkproxy.dest}"
	 */
	private String dest = "target/requests";

	/**
	 * @parameter expression="${mycontainer.darkproxy.port}"
	 */
	private int port = 8000;

	public void execute() throws MojoExecutionException, MojoFailureException {
		DarkProxy proxy = new DarkProxy();
		proxy.setDest(dest);
		proxy.setTimeout(timeout);

		ContainerBuilder builder = new ContainerBuilder(getInitialContext());
		try {
			JettyServerDeployer web = builder.createDeployer(JettyServerDeployer.class);
			web.setName("WebServer");
			web.bindPort(port);
			ContextWebServer ctx = web.createContextWebServer();
			ctx.setContext("/");
			DarkProxyFilter filter = new DarkProxyFilter();
			filter.setProxy(proxy);
			ctx.getFilters().add(new FilterDesc(filter, "/*"));
			web.deploy();
			builder.waitFor();
		} finally {
			shutdow(proxy);
		}
	}

	private void shutdow(DarkProxy proxy) {
		try {
			ShutdownCommand shutdown = new ShutdownCommand();
			shutdown.setContext(getInitialContext());
			shutdown.shutdown();
			Util.close(proxy);
		} catch (Exception e) {
			getLog().error("Error closing", e);
		}
	}

	private static InitialContext getInitialContext() {
		try {
			return new InitialContext(getInitialContextProperties());
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

	private static Properties getInitialContextProperties() {
		Properties ret = new Properties();
		ret.put("java.naming.factory.initial", "com.googlecode.mycontainer.kernel.naming.MyContainerContextFactory");
		return ret;
	}
}
