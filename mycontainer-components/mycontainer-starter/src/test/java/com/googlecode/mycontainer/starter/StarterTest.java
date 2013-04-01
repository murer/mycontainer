package com.googlecode.mycontainer.starter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.starter.Starter;

public class StarterTest {

	@Before
	public void setUp() throws Exception {
		ShutdownCommand shutdown = new ShutdownCommand();
		shutdown.setContext(new InitialContext());
		shutdown.shutdown();
	}

	@After
	public void teardown() throws Exception {
		ShutdownCommand shutdown = new ShutdownCommand();
		shutdown.setContext(new InitialContext());
		shutdown.shutdown();
	}

	@Test
	public void testURL() throws Exception {
		assertNotFound();

		String url = getClass().getResource("mycontainer-main.bsh").toString();
		Starter.main(new String[] { "-url", url });

		assertFound();
	}

	@Test
	public void testFile() throws Exception {
		assertNotFound();

		Starter.main(new String[] { "-file",
				"src/test/resources/com/googlecode/mycontainer/starter/mycontainer-main.bsh" });

		assertFound();
	}

	@Test
	public void testResource() throws Exception {
		assertNotFound();

		Starter.main(new String[] { "-resource",
				"com/googlecode/mycontainer/starter/mycontainer-main.bsh" });

		assertFound();
	}

	private void assertFound() throws Exception {
		DataSource ds = (DataSource) new InitialContext().lookup("TestDS");
		assertNotNull(ds);
	}

	private void assertNotFound() throws Exception {
		try {
			new InitialContext().lookup("TestDS");
			fail("expected NameNotFoundException");
		} catch (NameNotFoundException e) {
		}
	}

}
