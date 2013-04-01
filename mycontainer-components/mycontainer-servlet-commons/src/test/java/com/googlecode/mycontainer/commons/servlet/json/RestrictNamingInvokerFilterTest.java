package com.googlecode.mycontainer.commons.servlet.json;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.googlecode.mycontainer.commons.httpclient.RequestMethod;
import com.googlecode.mycontainer.commons.httpclient.WebClient;
import com.googlecode.mycontainer.commons.httpclient.WebRequest;
import com.googlecode.mycontainer.commons.httpclient.WebResponse;
import com.googlecode.mycontainer.commons.servlet.AbstractTestCase;

public class RestrictNamingInvokerFilterTest extends AbstractTestCase {

	@Test
	public void testNamingServlet() throws Exception {
		TestCustomerInterface customer = new TestCustomer();
		ctx.bind("mymock/test", customer);

		WebClient client = createClient();
		client.setTimeout(0);
		WebRequest request = client.createRequest(RequestMethod.GET);
		request.setUri("invoker/restrictnaming/mymock.test/setAll/test.js");
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

	@Test
	public void testNamingServletBlocked() throws Exception {
		TestCustomerInterface customer = new TestCustomer();
		ctx.bind("mymock/test", customer);

		WebClient client = createClient();
		client.setTimeout(0);
		WebRequest request = client.createRequest(RequestMethod.GET);
		request.setUri("invoker/restrictnaming/mymock.test/setAllBlocked/test.js");
		request.addParameter("args", "'c1'");
		request.addParameter("args", "15");
		request.addParameter("args", "{name:'f1',age:35}");
		WebResponse response = request.invoke();
		try {
			assertEquals(500, response.getCode());
		} finally {
			response.close();
		}
	}

}
