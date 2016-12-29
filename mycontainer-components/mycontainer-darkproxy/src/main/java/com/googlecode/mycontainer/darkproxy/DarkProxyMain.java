package com.googlecode.mycontainer.darkproxy;

import java.util.Properties;

import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.util.Util;
import com.googlecode.mycontainer.web.ContextWebServer;
import com.googlecode.mycontainer.web.FilterDesc;
import com.googlecode.mycontainer.web.jetty.JettyServerDeployer;

public class DarkProxyMain {
	
	public static void main(String[] args) throws Exception {
		DarkProxy proxy = new DarkProxy();
		proxy.setDest("target/requests");
		proxy.setTimeout(-1);

		Properties prop = new Properties();
		prop.put("java.naming.factory.initial", "com.googlecode.mycontainer.kernel.naming.MyContainerContextFactory");
		ContainerBuilder builder = new ContainerBuilder(prop);
		try {
			JettyServerDeployer web = builder.createDeployer(JettyServerDeployer.class);
			web.setName("WebServer");
			web.bindPort(8000);
			ContextWebServer ctx = web.createContextWebServer();
			ctx.setContext("/");
			DarkProxyFilter filter = new DarkProxyFilter();
			filter.setProxy(proxy);
			ctx.getFilters().add(new FilterDesc(filter, "/*"));
			web.deploy();
			builder.waitFor();
		} finally {
			try {
				ShutdownCommand shutdown = new ShutdownCommand();
				shutdown.setContext(builder.getContext());
				shutdown.shutdown();
				Util.close(proxy);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
