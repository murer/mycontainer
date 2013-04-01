package com.googlecode.mycontainer.commons.servlet.json;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.googlecode.mycontainer.commons.httpclient.RequestMethod;
import com.googlecode.mycontainer.commons.httpclient.WebClient;
import com.googlecode.mycontainer.commons.httpclient.WebRequest;
import com.googlecode.mycontainer.commons.httpclient.WebResponse;
import com.googlecode.mycontainer.commons.servlet.AbstractTestCase;

public class NamingInvokerFilterTest extends AbstractTestCase {

	@Test
	public void testNamingServlet() throws Exception {
		TestCustomerInterface customer = new TestCustomer();
		ctx.bind("mymock/test", customer);

		WebClient client = createClient();
		WebRequest request = client.createRequest(RequestMethod.GET);
		request.setUri("invoker/naming/mymock.test/setAll/test.js");
		request.addParameter("args", "'c1'");
		request.addParameter("args", "15");
		request.addParameter("args", "{name:'f1',age:35}");
		WebResponse response = request.invoke();
		try {
			assertEquals(200, response.getCode());
			assertEquals("c1", customer.getName());
			assertEquals(15, customer.getAge());
			assertEquals("f1", customer.getFather().getName());
			assertEquals(35, customer.getFather().getAge());
		} finally {
			response.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNamingServletMapOfMap() throws Exception {
		TestCustomerInterface customer = new TestCustomer();
		ctx.bind("mymock/test", customer);

		WebClient client = createClient();
		WebRequest request = client.createRequest(RequestMethod.GET);
		request.setUri("invoker/naming/mymock.test/setAll/test.js");
		request.addParameter("args", "'c1'");
		request.addParameter("args", "15");
		request.addParameter(
				"args",
				"{name:'f1',age:35, content: { map: { data: 'abc', submap: {subdata: 'def', subdataNum: 1}}}}");
		WebResponse response = request.invoke();
		try {
			TestCustomerInterface father = customer.getFather();
			Map<String, Object> content = father.getContent();
			Map<String, Object> map = (Map<String, Object>) content.get("map");
			Map<String, Object> submap = (Map<String, Object>) map
					.get("submap");
			assertEquals("abc", map.get("data"));
			assertEquals("def", submap.get("subdata"));
			assertEquals(1, submap.get("subdataNum"));
		} finally {
			response.close();
		}
	}

	@Test
	public void testNamingInvoker() throws Exception {
		TestService service = new TestService();
		ctx.bind("servlet/json/TestService", service);

		WebClient client = createClient();
		WebRequest request = client.createRequest(RequestMethod.GET);
		request.setUri("invoker/naming/servlet.json.TestService/sum/test.js");
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
	public void testNamingInvokerRequest() throws Exception {
		TestService service = new TestService();
		ctx.bind("servlet/json/TestService", service);

		WebClient client = createClient();
		WebRequest request = client.createRequest(RequestMethod.GET);
		request.setUri("invoker/naming/servlet.json.TestService/multiply/test.js");
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
