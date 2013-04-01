package com.googlecode.mycontainer.gae.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.mycontainer.gae.test.Message;
import com.googlecode.mycontainer.gae.test.MessageService;
import com.googlecode.mycontainer.gae.test.MessageServiceDaS;

public class MessageServiceDaSTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
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

}
