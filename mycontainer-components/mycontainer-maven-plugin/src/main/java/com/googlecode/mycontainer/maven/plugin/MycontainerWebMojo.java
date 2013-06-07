package com.googlecode.mycontainer.maven.plugin;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.web.ContextWebServer;
import com.googlecode.mycontainer.web.jetty.JettyServerDeployer;

/**
 * @goal web
 * @aggregator
 * @requiresProject false
 */
public class MycontainerWebMojo extends AbstractMojo {

	/**
	 * @parameter expression="${mycontainer.web.port}" default-value="0"
	 * @required
	 */
	private Integer port;

	/**
	 * @parameter expression="${mycontainer.web.context}" default-value="/"
	 * @required
	 */
	private String context;

	/**
	 * @parameter expression="${mycontainer.web.resources}" default-value="."
	 * @required
	 */
	private String resources;

	public void execute() throws MojoExecutionException, MojoFailureException {
		PluginUtil.configureLogger(getLog());
		ContainerBuilder builder = new ContainerBuilder(getInitialContext());
		JettyServerDeployer web = builder.createDeployer(JettyServerDeployer.class);
		web.setName("WebServer");
		web.bindPort(port);
		ContextWebServer ctx = web.createContextWebServer();
		ctx.setContext(context);
		ctx.setResources(resources);
		web.deploy();
		builder.waitFor();
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
