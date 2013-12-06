#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.container;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalSearchServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.googlecode.mycontainer.gae.web.LocalServiceTestHelperFilter;
import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.web.ContextWebServer;
import com.googlecode.mycontainer.web.FilterDesc;
import com.googlecode.mycontainer.web.WebServerDeployer;
import com.googlecode.mycontainer.web.jetty.JettyServerDeployer;

public class GAETestServer {

	private LocalServiceTestHelper gaeTestHelper;

	private List<LocalServiceTestConfig> gaeConfigurations = new ArrayList<LocalServiceTestConfig>();

	private WebServerDeployer server;
		
	private boolean authenticationEnabled = true;
	private boolean userLoggedIn = true;
	private boolean userIsAdmin = false;

	private boolean jettyIsEnabled = false;
	private int jettyPort = 8080;

	public void start() {
		gaeTestHelper = new LocalServiceTestHelper(gaeConfigurations.toArray(new LocalServiceTestConfig[gaeConfigurations.size()]));

		if (authenticationEnabled) {
			gaeTestHelper.setEnvIsLoggedIn(this.userLoggedIn);
			gaeTestHelper.setEnvIsAdmin(this.userIsAdmin);
		}
		
		gaeTestHelper.setEnvEmail("login.google@example.com");
		gaeTestHelper.setEnvAuthDomain("example.com");
		gaeTestHelper.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));

		gaeTestHelper.setUp();

		if (jettyIsEnabled) {
			this.startJetty();
		}
	}

	public void stop() throws IOException {
		gaeTestHelper.tearDown();
		FileUtils.deleteDirectory(new File("WEB-INF"));
	}

	public void enableDatastore(boolean noStorage) {
		LocalDatastoreServiceTestConfig localDatastoreServiceTestConfig = new LocalDatastoreServiceTestConfig();
		localDatastoreServiceTestConfig.setNoStorage(noStorage);
		localDatastoreServiceTestConfig.setNoIndexAutoGen(noStorage);

		gaeConfigurations.add(localDatastoreServiceTestConfig);
	}

	public void enableAuthentication(boolean userLoggedIn, boolean userIsAdmin) {
		gaeConfigurations.add(new LocalUserServiceTestConfig());

		authenticationEnabled = true;
		this.userIsAdmin = userIsAdmin;
		this.userLoggedIn = userLoggedIn;
	}

	public void enableSearch() {
		LocalSearchServiceTestConfig localSearchServiceTestConfig = new LocalSearchServiceTestConfig();
		gaeConfigurations.add(localSearchServiceTestConfig);
	}
	

	public void enableJetty(int port) {
		this.jettyIsEnabled = true;
		this.jettyPort = port;
	}

	private void startJetty() {
		if(server != null) {
			return;
		}
		
		ContainerBuilder builder;
		try {
			builder = new ContainerBuilder();
			builder.deployVMShutdownHook();
	
			server = builder.createDeployer(JettyServerDeployer.class);
			server.setName("WebServer");
			server.bindPort(this.jettyPort);
	
			ContextWebServer web = server.createContextWebServer();
			web.setContext("/");
			web.setResources("src/main/webapp");
	
			LocalServiceTestHelperFilter gae = new LocalServiceTestHelperFilter(gaeTestHelper);
			web.getFilters().add(new FilterDesc(gae, "/*"));
			
			server.deploy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
