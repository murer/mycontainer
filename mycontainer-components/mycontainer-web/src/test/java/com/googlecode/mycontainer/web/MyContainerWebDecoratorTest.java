package com.googlecode.mycontainer.web;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mycontainer.ejb.SessionInterceptorDeployer;
import com.googlecode.mycontainer.ejb.StatelessScannableDeployer;
import com.googlecode.mycontainer.jta.MyTransactionManagerDeployer;
import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.kernel.deploy.ScannerDeployer;
import com.googlecode.mycontainer.web.jetty.JettyServerDeployer;
import com.googlecode.mycontainer.web.jetty.MyContainerWebDecorator;

public class MyContainerWebDecoratorTest {

	private ContainerBuilder builder;

	@Before
	public void boot() throws NamingException {

		builder = new ContainerBuilder();

		SessionInterceptorDeployer sessionInterceptorDeployer = builder.createDeployer(SessionInterceptorDeployer.class);
		sessionInterceptorDeployer.deploy();

		builder.createDeployer(MyTransactionManagerDeployer.class).setName("TransactionManager").deploy();

		ScannerDeployer scanner = builder.createDeployer(ScannerDeployer.class);
		scanner.add(new StatelessScannableDeployer());
		scanner.scan(EJBMockServiceBean.class);
		scanner.deploy();

		InitialContext ic = new InitialContext();

		JettyServerDeployer server = new JettyServerDeployer();
		server.setContext(ic);
		server.setName("WebServer");
		server.bindPort(8380);
		server.addDecorator(new MyContainerWebDecorator());

		ContextWebServer web = server.createContextWebServer();
		web.setContext("/");
		web.getServlets().add(new ServletDesc(EJBTestServlet.class, "/testServletEJB"));
		web.getFilters().add(new FilterDesc(EJBTestFilter.class, "/testFilterEJB"));

		server.deploy();
	}

	@After
	public void shutdown() throws Exception {
		ShutdownCommand shutdown = new ShutdownCommand();
		shutdown.setContext(new InitialContext());
		shutdown.shutdown();
	}

	private void testURL(String urlStr) throws MalformedURLException, IOException {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			int code = conn.getResponseCode();

			assertEquals(418, code);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	@Test
	public void testInjectServlet() throws Exception {
		testURL("http://localhost:8380/testServletEJB");
	}

	@Test
	public void testInjectFilter() throws Exception {
		testURL("http://localhost:8380/testFilterEJB");
	}

	public static class EJBTestFilter implements Filter {

		@EJB
		private EJBMockService service;

		public void init(FilterConfig filterConfig) throws ServletException {

		}

		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
			HttpServletResponse resp = (HttpServletResponse) response;
			resp.setContentType("text/plain");
			resp.setStatus(service.doWork());
		}

		public void destroy() {

		}

	}

	public static class EJBTestServlet extends HttpServlet {

		private static final long serialVersionUID = 6705117406932550699L;

		@EJB
		private EJBMockService service;

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setContentType("text/plain");
			resp.setStatus(service.doWork());
		}

	}

	@Local
	public static interface EJBMockService {

		int doWork();

	}

	@Stateless
	public static class EJBMockServiceBean implements EJBMockService {

		public int doWork() {
			return 418;
		}

	}

}
