package com.googlecode.mycontainer.commons.servlet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.googlecode.mycontainer.commons.httpclient.RequestMethod;
import com.googlecode.mycontainer.commons.httpclient.WebClient;
import com.googlecode.mycontainer.commons.httpclient.WebRequest;
import com.googlecode.mycontainer.commons.httpclient.WebResponse;

public class ContentTypeByExtentionFilterTest extends AbstractTestCase {

	@Test
	public void testParameter() {
		WebClient client = createClient();
		WebRequest request = client.createRequest(RequestMethod.GET);
		request.setUri("myfile.css");
		request.addParameter("contentType", "text/plain");
		WebResponse response = request.invoke();
		try {
			assertEquals(200, response.getCode());
			assertEquals("TEST {}", response.getContentAsString().trim());
			assertEquals("text/plain", response.getContentType());
		} finally {
			response.close();
		}
	}

	@Test
	public void testNative() {
		WebClient client = createClient();
		WebRequest request = client.createRequest(RequestMethod.GET);
		request.setUri("myfile.css");
		WebResponse response = request.invoke();
		try {
			assertEquals(200, response.getCode());
			assertEquals("TEST {}", response.getContentAsString().trim());
			assertEquals("text/css", response.getContentType());
		} finally {
			response.close();
		}
	}

	@Test
	public void testExtention() {
		WebClient client = createClient();
		WebRequest request = client.createRequest(RequestMethod.GET);
		request.setUri("echo/test.txt");
		request.addParameter("m", "'my message'");
		WebResponse response = request.invoke();
		try {
			assertEquals(200, response.getCode());
			assertEquals("'my message'", response.getContentAsString().trim());
			assertEquals("text/plain; charset=ISO-8859-1", response.getContentType());
		} finally {
			response.close();
		}
	}

}
