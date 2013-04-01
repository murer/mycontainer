package com.googlecode.mycontainer.commons.servlet.json;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.googlecode.mycontainer.commons.httpclient.RequestMethod;
import com.googlecode.mycontainer.commons.httpclient.WebClient;
import com.googlecode.mycontainer.commons.httpclient.WebRequest;
import com.googlecode.mycontainer.commons.httpclient.WebResponse;
import com.googlecode.mycontainer.commons.servlet.AbstractTestCase;


public class JsonProtocolFilterTest extends AbstractTestCase {

	@Test
	public void testWithoutCallback() throws Exception {
		WebClient client = createClient();
		WebRequest request = client.createRequest(RequestMethod.GET);
		request.setUri("echo/test.js");
		request.addParameter("m", "{}");
		WebResponse response = request.invoke();
		try {
			assertEquals(200, response.getCode());
			assertEquals("{}", response.getContentAsString());
		} finally {
			response.close();
		}
	}

	@Test
	public void testWithCallback() throws Exception {
		WebClient client = createClient();
		WebRequest request = client.createRequest(RequestMethod.GET);
		request.setUri("echo/test.js");
		request.addParameter("m", "{}");
		request.addParameter("callback", "mycb");
		WebResponse response = request.invoke();
		try {
			assertEquals(200, response.getCode());
			assertEquals("mycb({});", response.getContentAsString());
		} finally {
			response.close();
		}
	}
	
	@Test
	public void testError() throws Exception {
		WebClient client = createClient();
		WebRequest request = client.createRequest(RequestMethod.GET);
		request.setUri("echo1/test.js");
		request.addParameter("m", "{}");
		request.addParameter("callback", "mycb");
		WebResponse response = request.invoke();
		try {
			assertEquals(404, response.getCode());
		} finally {
			response.close();
		}
	}
	
	
}
