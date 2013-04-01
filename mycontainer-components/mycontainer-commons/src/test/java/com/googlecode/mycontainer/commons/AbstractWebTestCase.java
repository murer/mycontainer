package com.googlecode.mycontainer.commons;

import java.util.Random;

import javax.naming.InitialContext;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.junit.After;
import org.junit.Before;

import com.googlecode.mycontainer.commons.httpclient.WebClient;
import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.web.ContextWebServer;
import com.googlecode.mycontainer.web.FilterDesc;
import com.googlecode.mycontainer.web.jetty.JettyServerDeployer;

public class AbstractWebTestCase {

	protected ContainerBuilder builder;

	protected InitialContext ctx;

	@Before
	public void boot() throws Exception {
		builder = new ContainerBuilder();
		ctx = builder.getContext();
		builder.deployVMShutdownHook();

		JettyServerDeployer webServer = builder.createDeployer(JettyServerDeployer.class);
		webServer.bindPort(8380);
		webServer.getServer().setSessionIdManager(new HashSessionIdManager(new Random()));
		webServer.setName("WebServer");

		ContextWebServer webContext = webServer.createContextWebServer();
		webServer.getServer().setSessionIdManager(new HashSessionIdManager(new Random()));
		webContext.setContext("/");

		webContext.getFilters().add(new FilterDesc(ReplyFilter.class, "/reply"));

		webServer.deploy();
	}

	@After
	public void shutdown() throws Exception {
		ShutdownCommand shutdown = new ShutdownCommand();
		shutdown.setContext(new InitialContext());
		shutdown.shutdown();
	}

	public WebClient createWebClient() {
		WebClient ret = new WebClient();
		ret.setTimeout(2000l);
		ret.setUrl("http://localhost:8380/");
		return ret;
	}

	public HttpClient createHttpClient() {
		DefaultHttpClient ret = new DefaultHttpClient(new ThreadSafeClientConnManager());
		return ret;
	}

}
