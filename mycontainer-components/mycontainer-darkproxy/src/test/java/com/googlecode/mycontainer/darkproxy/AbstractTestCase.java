package com.googlecode.mycontainer.darkproxy;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.util.Util;
import com.googlecode.mycontainer.web.ContextWebServer;
import com.googlecode.mycontainer.web.FilterDesc;
import com.googlecode.mycontainer.web.jetty.JettyServerDeployer;

public class AbstractTestCase {

	private static final Logger LOG = LoggerFactory.getLogger(DarkProxyFilter.class);

	protected ContainerBuilder builder;

	protected InitialContext ctx;

	protected DarkProxy proxy;

	@Before
	public void boot() throws Exception {
		proxy = new DarkProxy();
		proxy.setDest("target/requests");
		proxy.setTimeout(5000L);
		proxy.cleanDest();
		
		builder = new ContainerBuilder();
		ctx = builder.getContext();
		builder.deployVMShutdownHook();
		
		JettyServerDeployer webServer = builder.createDeployer(JettyServerDeployer.class);
		webServer.bindPort(8380);
		webServer.setName("WebServer");

		ContextWebServer webContext = webServer.createContextWebServer();
		webContext.setContext("/");
		DarkProxyFilter filter = new DarkProxyFilter();
		filter.setProxy(proxy);
		webContext.getFilters().add(new FilterDesc(sum(), "/test/sum"));
		webContext.getFilters().add(new FilterDesc(filter, "/*"));

		webServer.deploy();
	}

	private Filter sum() {
		return new Filter() {
			public void init(FilterConfig filterConfig) throws ServletException {
			}

			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
					throws IOException, ServletException {
				LOG.info("Sum: {}", request);
				String body = Util.readAll(request.getInputStream(), "UTF-8");
				int num = 0;
				if (body != null && body.length() > 0) {
					num += Integer.parseInt(body);
				}
				String[] array = request.getParameterValues("n");
				for (String n : array) {
					num += Integer.parseInt(n);
				}
				String header = ((HttpServletRequest) request).getHeader("x-sum");
				if (header != null) {
					num += Integer.parseInt(header);
				}
				((HttpServletResponse) response).setIntHeader("x-sum-resp", num);
				response.setContentType("text/plain");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(Integer.toString(num));
			}

			public void destroy() {

			}
		};
	}

	@After
	public void shutdown() throws Exception {
		ShutdownCommand shutdown = new ShutdownCommand();
		shutdown.setContext(new InitialContext());
		shutdown.shutdown();
		
		Util.close(proxy);
	}

}
