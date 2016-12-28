package com.googlecode.mycontainer.darkproxy;

import javax.naming.InitialContext;

import org.junit.After;
import org.junit.Before;

import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.util.Util;
import com.googlecode.mycontainer.web.ContextWebServer;
import com.googlecode.mycontainer.web.FilterDesc;
import com.googlecode.mycontainer.web.jetty.JettyServerDeployer;

public class AbstractTestCase {

	protected ContainerBuilder builder;

	protected InitialContext ctx;

	protected DarkProxy proxy;

	@Before
	public void boot() throws Exception {
		builder = new ContainerBuilder();
		ctx = builder.getContext();
		builder.deployVMShutdownHook();

		JettyServerDeployer webServer = builder.createDeployer(JettyServerDeployer.class);
		webServer.bindPort(8380);
		webServer.setName("WebServer");
		
		proxy = new DarkProxy();
		proxy.setDest("target/requests");
		proxy.cleanDest();

		ContextWebServer webContext = webServer.createContextWebServer();
		webContext.setContext("/");
		DarkProxyFilter filter = new DarkProxyFilter();
		filter.setProxy(proxy);
		webContext.getFilters().add(new FilterDesc(filter, "/*"));

		webServer.deploy();
	}

	@After
	public void shutdown() throws Exception {
		Util.close(proxy);
		
		ShutdownCommand shutdown = new ShutdownCommand();
		shutdown.setContext(new InitialContext());
		shutdown.shutdown();
	}

}
