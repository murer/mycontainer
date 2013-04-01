package com.googlecode.mycontainer.commons.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;

import com.googlecode.mycontainer.commons.httpclient.RequestMethod;
import com.googlecode.mycontainer.commons.httpclient.WebClient;
import com.googlecode.mycontainer.commons.httpclient.WebRequest;
import com.googlecode.mycontainer.commons.httpclient.WebResponse;

public class EchoServletTest extends AbstractTestCase {

	@Test
	public void testEchoGet() {
		WebClient client = createClient();
		WebRequest request = client.createRequest(RequestMethod.GET);
		request.setUri("echo/test.txt");
		request.addParameter("m", "my message");
		WebResponse response = request.invoke();
		try {
			assertEquals(200, response.getCode());
			assertEquals("my message", response.getContentAsString());
		} finally {
			response.close();
		}
	}

	@Test
	public void testEchoPost() {
		WebClient client = createClient();
		WebRequest request = client.createRequest(RequestMethod.POST);
		request.setUri("echo/test.txt");
		request.addParameter("m", "my message");
		WebResponse response = request.invoke();
		try {
			assertEquals(200, response.getCode());
			assertEquals("my message", response.getContentAsString());
		} finally {
			response.close();
		}
	}

	// @Test
	public void testEchoAsync() {
		WebClient client = createClient();
		WebRequest request = client.createRequest(RequestMethod.GET);
		request.setUri("echo/test.txt");
		request.addParameter("m", "my message");
		Future<WebResponse> f1 = request.invokeAsync();
		Future<WebResponse> f2 = request.invokeAsync();
		try {
			assertFalse(f1.isDone());
			assertFalse(f1.isCancelled());
			assertFalse(f2.isDone());
			assertFalse(f2.isCancelled());

			assertEquals(200, f1.get().getCode());
			assertEquals("my message", f1.get().getContentAsString());
			assertEquals(200, f2.get().getCode());
			assertEquals("my message", f2.get().getContentAsString());

			assertTrue(f1.isDone());
			assertFalse(f1.isCancelled());
			assertTrue(f2.isDone());
			assertFalse(f2.isCancelled());

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		} finally {
			f1.cancel(true);
			f2.cancel(true);
		}
	}

}
