package com.googlecode.mycontainer.commons.servlet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.googlecode.mycontainer.commons.httpclient.RequestMethod;
import com.googlecode.mycontainer.commons.httpclient.WebClient;
import com.googlecode.mycontainer.commons.httpclient.WebRequest;
import com.googlecode.mycontainer.commons.httpclient.WebResponse;

public class ClasspathServletTest extends AbstractTestCase {

	@Test
	public void testEcho() {
		WebClient client = createClient();
		WebRequest request = client.createRequest(RequestMethod.GET);
		request.setUri("cp/com/googlecode/mycontainer/commons/servlet/myfile.txt");
		WebResponse response = request.invoke();
		try {
			assertEquals(200, response.getCode());
			assertEquals("test file", response.getContentAsString());
		} finally {
			response.close();
		}
	}

}
