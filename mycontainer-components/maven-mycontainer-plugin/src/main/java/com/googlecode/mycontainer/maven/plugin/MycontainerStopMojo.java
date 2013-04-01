package com.googlecode.mycontainer.maven.plugin;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.googlecode.mycontainer.kernel.ShutdownCommand;

/**
 * @goal stop
 * @aggregator
 */
public class MycontainerStopMojo extends AbstractMojo {

	public MycontainerStopMojo() {
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		Properties props = new Properties();
		props.setProperty("java.naming.factory.initial",
				"com.googlecode.mycontainer.kernel.naming.MyContainerContextFactory");

		ShutdownCommand shutdown = new ShutdownCommand();
		InitialContext context;
		try {
			context = new InitialContext(props);
		} catch (NamingException e) {
			throw new MojoFailureException(
					"Error creating JNDI InitialContext", e);
		}
		shutdown.setContext(context);
		shutdown.shutdown();
	}
}
