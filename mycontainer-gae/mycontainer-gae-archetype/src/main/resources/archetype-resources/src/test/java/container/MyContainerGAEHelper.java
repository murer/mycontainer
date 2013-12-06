#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.search.dev.LocalSearchService;
import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalSearchServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.mycontainer.gae.web.LocalServiceTestHelperFilter;
import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.web.ContextWebServer;
import com.googlecode.mycontainer.web.FilterDesc;
import com.googlecode.mycontainer.web.WebServerDeployer;
import com.googlecode.mycontainer.web.jetty.JettyServerDeployer;

public class MyContainerGAEHelper {

	private static final Logger LOG = LoggerFactory.getLogger(MyContainerGAEHelper.class);

	protected LocalServiceTestHelper helper;

	private LocalDatastoreServiceTestConfig ds;
	private LocalSearchServiceTestConfig fts;

	private int port = 8080;

	public void setPort(int port) {
		this.port = port;
	}

	public void prepareLocalServiceTestHelper() throws Exception {
		ds = new LocalDatastoreServiceTestConfig();
		ds.setDefaultHighRepJobPolicyUnappliedJobPercentage(0);
		fts = new LocalSearchServiceTestConfig();

		List<LocalServiceTestConfig> list = new ArrayList<LocalServiceTestConfig>();
		list.add(ds);
		list.add(fts);
		LocalBlobstoreServiceTestConfig localBlobstoreServiceTestConfig = new LocalBlobstoreServiceTestConfig();
		localBlobstoreServiceTestConfig.setNoStorage(true);
		list.add(localBlobstoreServiceTestConfig);

		helper = new LocalServiceTestHelper(list.toArray(new LocalServiceTestConfig[0]));
		Map<String, Object> envs = new HashMap<String, Object>();
		envs.put("com.google.appengine.api.users.UserService.user_id_key", "10");
		helper.setEnvAttributes(envs);
		helper.setEnvIsLoggedIn(true);
		helper.setEnvIsAdmin(false);
		helper.setEnvEmail("login.google@example.com");
		helper.setEnvAuthDomain("example.com");
		helper.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
	}

	public void prepareSearchServiceTestHelper() throws Exception {
		new LocalSearchService();
	}

	public LocalServiceTestHelper getHelper() {
		return helper;
	}

	public LocalDatastoreServiceTestConfig getDs() {
		return ds;
	}

	public void bootLocalServiceTestHelper() throws Exception {
		helper.setUp();
	}

	public void shutdownLocalServiceTestHelper() {
		try {
			if (helper != null) {
				helper.tearDown();
			}
		} catch (Exception e) {
			LOG.info("error", e);
		}
	}

	public ContainerBuilder bootMycontainer() throws Exception {
		ContainerBuilder builder = new ContainerBuilder();
		builder.deployVMShutdownHook();

		WebServerDeployer server = builder.createDeployer(JettyServerDeployer.class);
		server.setName("WebServer");
		server.bindPort(port);

		ContextWebServer web = server.createContextWebServer();
		web.setContext("/");
		web.setResources("src/main/webapp");

		LocalServiceTestHelperFilter gae = new LocalServiceTestHelperFilter(helper);
		web.getFilters().add(new FilterDesc(gae, "/*"));

		server.deploy();
		return builder;
	}

	public void shutdownMycontainer() {
		try {
			ShutdownCommand shutdown = new ShutdownCommand();
			shutdown.setContext(new InitialContext());
			shutdown.shutdown();
		} catch (Exception e) {
			LOG.error("error", e);
		}
	}

	public void tearDown() {
		if (helper != null) {
			helper.tearDown();
		}
	}

	public void setUp() {
		if (helper != null) {
			helper.setUp();
		}
	}

	public LocalServiceTestHelper getGaeHelper() {
		return helper;
	}

}
