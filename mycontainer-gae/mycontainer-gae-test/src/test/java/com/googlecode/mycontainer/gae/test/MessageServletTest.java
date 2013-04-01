package com.googlecode.mycontainer.gae.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.naming.InitialContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.googlecode.mycontainer.commons.httpclient.RequestMethod;
import com.googlecode.mycontainer.commons.httpclient.WebClient;
import com.googlecode.mycontainer.commons.httpclient.WebRequest;
import com.googlecode.mycontainer.commons.httpclient.WebResponse;
import com.googlecode.mycontainer.commons.json.JsonHandler;
import com.googlecode.mycontainer.gae.test.Message;
import com.googlecode.mycontainer.gae.test.MessageService;
import com.googlecode.mycontainer.gae.test.MessageServiceDaS;
import com.googlecode.mycontainer.gae.web.LocalServiceTestHelperFilter;
import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.web.ContextWebServer;
import com.googlecode.mycontainer.web.FilterDesc;
import com.googlecode.mycontainer.web.WebServerDeployer;
import com.googlecode.mycontainer.web.jetty.JettyServerDeployer;

public class MessageServletTest {

	@Before
	public void setUp() throws Exception {

		ContainerBuilder builder = new ContainerBuilder();

		WebServerDeployer server = builder
				.createDeployer(JettyServerDeployer.class);
		server.setName("WebServer");
		server.bindPort(8380);

		ContextWebServer web = server.createContextWebServer();
		web.setContext("/");
		web.setResources("src/main/webapp");

		LocalServiceTestHelperFilter gae = new LocalServiceTestHelperFilter(
				new LocalDatastoreServiceTestConfig());
		web.getFilters().add(new FilterDesc(gae, "/*"));

		server.deploy();
	}

	@After
	public void tearDown() throws Exception {
		ShutdownCommand shutdown = new ShutdownCommand();
		shutdown.setContext(new InitialContext());
		shutdown.shutdown();

	}

	public WebClient createClient() {
		WebClient ret = new WebClient();
		ret.setTimeout(2000l);
		ret.setUrl("http://localhost:8380/");
		return ret;
	}

	@Test
	public void testFirst() {
		testTwice();
	}

	@Test
	public void testSecond() {
		testTwice();
	}

	private void testTwice() {
		WebClient client = createClient();
		assertEquals(0, getSize(client));
		create(client);
		create(client);
		assertEquals(2, getSize(client));
	}

	@Test
	public void testCreate() {
		MessageService service = new MessageServiceDaS();

		Message msg = new Message();
		msg.setText("test1");
		service.create(msg);

		List<Message> all = service.getAll();
		assertEquals("test1", all.get(0).getText());
		assertNotNull(all.get(0).getId());
		assertEquals(1, all.size());

		msg = new Message();
		msg.setText("test2");
		service.create(msg);

		all = service.getAll();
		assertEquals("test2", all.get(1).getText());
		assertNotNull(all.get(1).getId());
		assertEquals(2, all.size());

	}

	@SuppressWarnings("unchecked")
	private Integer getSize(WebClient client) {
		WebRequest req = client.createRequest(RequestMethod.GET);
		req.setUri("message/list");
		WebResponse resp = req.invoke();
		try {
			assertEquals(200, resp.getCode());
			List<Message> l = (List<Message>) resp.getJsonProtocol().parse();
			return l.size();
		} finally {
			resp.close();
		}
	}

	private void create(WebClient client) {
		WebRequest req = client.createRequest(RequestMethod.GET);
		req.setUri("message/create");
		Message msg = new Message();
		msg.setText("test");
		req.addParameter("x", JsonHandler.instance().format(msg));
		WebResponse resp = req.invoke();
		try {
			assertEquals(200, resp.getCode());
		} finally {
			resp.close();
		}
	}

}
