package com.googlecode.mycontainer.commons.servlet.json;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.googlecode.mycontainer.commons.httpclient.RequestMethod;
import com.googlecode.mycontainer.commons.httpclient.WebClient;
import com.googlecode.mycontainer.commons.httpclient.WebRequest;
import com.googlecode.mycontainer.commons.httpclient.WebResponse;
import com.googlecode.mycontainer.commons.servlet.AbstractTestCase;

public class PojoInvokerFilterTest extends AbstractTestCase {

	@Test
	public void testPojoInvoker() throws Exception {
		WebClient client = createClient();
		WebRequest request = client.createRequest(RequestMethod.GET);
		request.setUri("invoker/pojo/servlet.json.TestService/sum/test.js");
		request.addParameter("args", "5");
		request.addParameter("args", "7");
		WebResponse response = request.invoke();
		try {
			assertEquals(200, response.getCode());
			assertEquals(12, response.getJsonProtocol().parse());
		} finally {
			response.close();
		}
	}

	@Test
	public void testPojoInvokerRequest() throws Exception {
		WebClient client = createClient();
		WebRequest request = client.createRequest(RequestMethod.GET);
		request.setUri("invoker/pojo/servlet.json.TestService/multiply/test.js");
		request.addParameter("a", "5");
		request.addParameter("b", "7");
		WebResponse response = request.invoke();
		try {
			assertEquals(200, response.getCode());
			assertEquals("35", response.getHeader("result"));
			assertEquals("nothing", response.getJsonProtocol().parse());
		} finally {
			response.close();
		}
	}
}
