package com.googlecode.mycontainer.test.web;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;

import com.googlecode.mycontainer.datasource.DataSourceDeployer;
import com.googlecode.mycontainer.ejb.SessionInterceptorDeployer;
import com.googlecode.mycontainer.ejb.StatelessScannableDeployer;
import com.googlecode.mycontainer.jpa.HibernateJPADeployer;
import com.googlecode.mycontainer.jpa.JPADeployer;
import com.googlecode.mycontainer.jpa.JPAInfoBuilder;
import com.googlecode.mycontainer.jta.MyTransactionManagerDeployer;
import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.kernel.deploy.ScannerDeployer;
import com.googlecode.mycontainer.test.ejb.CustomerBean;
import com.googlecode.mycontainer.test.ejb.EntityManagerWrapperBean;
import com.googlecode.mycontainer.web.ContextWebServer;
import com.googlecode.mycontainer.web.FilterDesc;
import com.googlecode.mycontainer.web.LogFilter;
import com.googlecode.mycontainer.web.Realm;
import com.googlecode.mycontainer.web.ServletDesc;
import com.googlecode.mycontainer.web.jetty.JettyServerDeployer;
import com.sun.faces.config.ConfigureListener;

public class MycontainerTestHelper {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MycontainerWebTest.class);

	private ContainerBuilder builder;
	private InitialContext ctx;
	private TransactionManager tm;

	public void bootBackend() throws Exception {
		builder = new ContainerBuilder();

		SessionInterceptorDeployer sessionInterceptorDeployer = builder.createDeployer(SessionInterceptorDeployer.class);
		sessionInterceptorDeployer.deploy();

		builder.createDeployer(MyTransactionManagerDeployer.class).setName("TransactionManager").deploy();

		DataSourceDeployer ds = builder.createDeployer(DataSourceDeployer.class);
		ds.setName("TestDS");
		ds.setDriver("org.hsqldb.jdbcDriver");
		ds.setUrl("jdbc:hsqldb:mem:.");
		ds.setUser("sa");
		ds.deploy();

		JPADeployer jpa = builder.createDeployer(HibernateJPADeployer.class);
		JPAInfoBuilder info = (JPAInfoBuilder) jpa.getInfo();
		info.setPersistenceUnitName("test-pu");
		info.setJtaDataSourceName("TestDS");
		info.addJarFileUrl(CustomerBean.class);
		info.setPersistenceUnitRootUrl(CustomerBean.class);
		Properties props = info.getProperties();
		props.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		props.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		props.setProperty("hibernate.show_sql", "true");
		jpa.deploy();

		ScannerDeployer scanner = builder.createDeployer(ScannerDeployer.class);
		scanner.add(new StatelessScannableDeployer());
		scanner.scan(EntityManagerWrapperBean.class);
		scanner.deploy();

		ctx = builder.getContext();
		tm = (TransactionManager) ctx.lookup("TransactionManager");
	}

	public void bootFrontend() {
		JettyServerDeployer webServer = builder.createDeployer(JettyServerDeployer.class);

		webServer.bindPort(8380);
		webServer.setName("WebServer");

		Realm realm = new Realm("testRealm");
		realm.config("teste", "pass", "admin", "user");
		webServer.addRealm(realm);

		ContextWebServer webContext = webServer.createContextWebServer();
		webContext.setContext("/test");
		webContext.setResources("src/main/webapp/");
		webContext.getListeners().add(ConfigureListener.class);
		webContext.getFilters().add(new FilterDesc(LogFilter.class, "/*"));
		webContext.getServlets().add(new ServletDesc(WebTestServlet.class, "/test.txt"));
		webContext.getFilters().add(new FilterDesc(WebTestFilter.class, "/*"));

		webContext = webServer.createContextWebServer();
		webContext.setContext("/test-other");
		webContext.setResources("src/main/webapp/");
		webContext.getListeners().add(ConfigureListener.class);
		webContext.getFilters().add(new FilterDesc(LogFilter.class, "/*"));
		webContext.getServlets().add(new ServletDesc(WebTestServlet.class, "/test.txt"));
		webContext.getFilters().add(new FilterDesc(WebTestFilter.class, "/*"));

		webServer.deploy();
	}

	public void shutdown() {
		try {
			ShutdownCommand shutdown = new ShutdownCommand();
			shutdown.setContext(new InitialContext());
			shutdown.shutdown();
		} catch (Exception e) {
			LOG.error("Error shutdown", e);
		}
	}

	public ContainerBuilder getBuilder() {
		return builder;
	}

	public InitialContext getCtx() {
		return ctx;
	}

	public TransactionManager getTm() {
		return tm;
	}

}
