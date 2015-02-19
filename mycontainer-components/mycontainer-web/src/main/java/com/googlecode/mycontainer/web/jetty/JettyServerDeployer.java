/*
 * Copyright 2008 Whohoo Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.googlecode.mycontainer.web.jetty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler.Decorator;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Password;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;
import com.googlecode.mycontainer.kernel.deploy.SimpleDeployer;
import com.googlecode.mycontainer.kernel.reflect.ReflectUtil;
import com.googlecode.mycontainer.web.ContextWebServer;
import com.googlecode.mycontainer.web.FilterDesc;
import com.googlecode.mycontainer.web.Realm;
import com.googlecode.mycontainer.web.Realm.UserRole;
import com.googlecode.mycontainer.web.ServletDesc;
import com.googlecode.mycontainer.web.WebServerDeployer;

public class JettyServerDeployer extends WebServerDeployer implements SimpleDeployer {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JettyServerDeployer.class);

	private static final long serialVersionUID = 3380253274628567229L;

	public static final String JETTY_USE_FILE_MAPPTED_BUFFER = "org.eclipse.jetty.servlet.Default.useFileMappedBuffer";

	private Server server;

	private List<Decorator> decorators = new ArrayList<ServletContextHandler.Decorator>();

	public JettyServerDeployer() {
		server = new Server();

		System.setProperty("com.sun.faces.InjectionProvider", "com.googlecode.mycontainer.jsfprovider.MyContainerInjectionProvider");

		RequestLogHandler requestLogHandler = new RequestLogHandler();
		requestLogHandler.setRequestLog(new JettyRequestLogImpl());
		requestLogHandler.setServer(server);
		// server.addBean(requestLogHandler);
	}

	public Server getServer() {
		return server;
	}

	@Override
	protected Object getResource() {
		return server;
	}

	@Override
	public void shutdown() {

		LOG.info("Shutting down Jetty web server...");

		try {
			server.stop();
			server.join();
		} catch (Exception e) {
			throw new KernelRuntimeException(e);
		}

		super.shutdown();
	}

	@Override
	public void bindPort(int port) {
		Connector connector = createConnector(port);
		server.addConnector(connector);
	}

	private SelectChannelConnector createConnector(int port) {
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(port);
		connector.setMaxIdleTime(30000);
		return connector;
	}

	@Override
	public void bindPort(int port, int confidentialPort) {
		SelectChannelConnector connector = createConnector(port);
		connector.setConfidentialPort(confidentialPort);
		server.addConnector(connector);
	}

	@Deprecated
	@Override
	public void bindSSLPort(int port, String keystore, String password) {
		SslConnectorInfo info = new SslConnectorInfo(port, keystore);
		info.setKeyManagerPassword(password);
		info.setKeyStorePassword(password);

		bindSSLPort(info);
	}

	@Override
	public void bindSSLPort(SslConnectorInfo info) {
		Connector connector = info.createConnector();
		server.addConnector(connector);
	}

	@Override
	public void deploy() {
		try {
			deployWebContexts();
			server.start();
		} catch (Exception e) {
			throw new KernelRuntimeException(e);
		}

		super.deploy();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void deployWebContexts() {
		List<ContextWebServer> webContexts = getWebContexts();

		Handler[] handlers = new Handler[webContexts.size()];
		int i = 0;
		for (ContextWebServer contextWebServer : webContexts) {
			ServletContextHandler handler;
			if (contextWebServer.getResources() != null) {
				handler = new WebAppContext(contextWebServer.getResources(), contextWebServer.getContext());
			} else {
				handler = new ServletContextHandler(null, contextWebServer.getContext(), ServletContextHandler.SESSIONS | ServletContextHandler.SECURITY);
				handler.addServlet(DefaultServlet.class, "/*");
			}
			handler.setAttribute(
					WebInfConfiguration.CONTAINER_JAR_PATTERN,
					".*/.*jsp-api-[^/]*\\.jar$|.*/.*jsp-[^/]*\\.jar$|.*/.*taglibs[^/]*\\.jar$|.*/.*jstl[^/]*\\.jar$|.*/.*jsf-impl-[^/]*\\.jar$|.*/.*javax.faces-[^/]*\\.jar$|.*/.*myfaces-impl-[^/]*\\.jar$");
			// performRemoveTagLibConfiguration((WebAppContext) handler);
			Set<Entry<String, Object>> attrs = contextWebServer.getAttributes().entrySet();
			for (Entry<String, Object> entry : attrs) {
				handler.setAttribute(entry.getKey(), entry.getValue());
			}
			Set<Entry<String, String>> params = contextWebServer.getInitParameters().entrySet();
			for (Entry<String, String> entry : params) {
				handler.setInitParameter(entry.getKey(), entry.getValue());
			}
			configWindows(handler);

			List<Object> listeners = contextWebServer.getListeners();
			for (Object listener : listeners) {
				if (listener instanceof String) {
					listener = ReflectUtil.classForName((String) listener);
				}
				if (listener instanceof Class) {
					listener = ReflectUtil.newInstance((Class) listener);
				}
				handler.addEventListener((EventListener) listener);
			}

			List<FilterDesc> filters = contextWebServer.getFilters();
			for (FilterDesc desc : filters) {
				Object filter = desc.getFilter();
				if (filter instanceof Filter) {
					FilterHolder holder = new FilterHolder((Filter) filter);
					handler.addFilter(holder, desc.getPath(), FilterMapping.ALL);
				} else if (filter instanceof FilterHolder) {
					handler.addFilter((FilterHolder) filter, desc.getPath(), FilterMapping.ALL);
				} else if (filter instanceof Class) {
					handler.addFilter((Class) filter, desc.getPath(), FilterMapping.ALL);
				} else {
					handler.addFilter((String) filter, desc.getPath(), FilterMapping.ALL);
				}
			}

			List<ServletDesc> servlets = contextWebServer.getServlets();
			for (ServletDesc desc : servlets) {
				Object servlet = desc.getServlet();
				if (servlet instanceof HttpServlet) {
					ServletHolder holder = new ServletHolder((HttpServlet) servlet);
					handler.addServlet(holder, desc.getPath());
				} else if (servlet instanceof ServletHolder) {
					handler.addServlet((ServletHolder) servlet, desc.getPath());
				} else if (servlet instanceof Class) {
					handler.addServlet((Class) servlet, desc.getPath());
				} else {
					handler.addServlet((String) servlet, desc.getPath());
				}
			}

			for (Decorator decorator : decorators) {
				handler.addDecorator(decorator);
			}

			handlers[i++] = handler;
		}
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(handlers);

		server.setHandler(contexts);
	}

	private void configWindows(ServletContextHandler handler) {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("win") >= 0 && handler.getInitParameter(JETTY_USE_FILE_MAPPTED_BUFFER) == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Windows detected (" + os + "). Turning " + JETTY_USE_FILE_MAPPTED_BUFFER + " off to " + handler);
			}
			handler.setInitParameter(JETTY_USE_FILE_MAPPTED_BUFFER, "false");
		}
	}

	// private void performRemoveTagLibConfiguration(WebAppContext
	// contextHandler) {
	// ArrayList<String> configs = new ArrayList<String>(
	// Arrays.asList(contextHandler.getConfigurationClasses()));
	// boolean remove = configs
	// .remove("org.mortbay.jetty.webapp.TagLibConfiguration");
	// if (remove) {
	// contextHandler.setConfigurationClasses(configs
	// .toArray(new String[configs.size()]));
	// }
	// LOG.info("org.mortbay.jetty.webapp.TagLibConfiguration removed: "
	// + remove);
	// }

	public JettyServerDeployer addDecorator(Decorator decorator) {
		this.decorators.add(decorator);
		return this;
	}

	@Override
	public void addRealm(Realm realm) {
		HashLoginService loginService = new HashLoginService();
		loginService.setName(realm.getName());
		Collection<UserRole> users = realm.getUsers().values();
		for (UserRole user : users) {
			String[] roles = user.getRoles().toArray(new String[0]);
			Password password = new Password(user.getPassword());
			loginService.putUser(user.getUser(), password, roles);
		}
		server.addBean(loginService);
	}

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		WebAppContext webapp1 = new WebAppContext();
		webapp1.setContextPath("/ctx1");
		webapp1.setResourceBase("target");

		WebAppContext webapp2 = new WebAppContext();
		webapp2.setContextPath("/ctx2");
		webapp2.setResourceBase("src");

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[] { webapp1, webapp2 });

		server.setHandler(contexts);

		server.start();
		server.join();
	}

}
