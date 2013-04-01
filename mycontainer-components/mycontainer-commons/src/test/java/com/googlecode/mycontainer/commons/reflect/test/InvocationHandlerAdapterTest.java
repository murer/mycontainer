package com.googlecode.mycontainer.commons.reflect.test;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.mycontainer.commons.reflect.BlockingInvocationHandlerAdapter;
import com.googlecode.mycontainer.commons.reflect.InvocationHandlerAdapter;
import com.googlecode.mycontainer.commons.reflect.TestService;
import com.googlecode.mycontainer.commons.reflect.TestServiceImpl;

public class InvocationHandlerAdapterTest {

	@Test
	public void testAdapter() {
		BlockingInvocationHandlerAdapter handler = new BlockingInvocationHandlerAdapter(
				new TestServiceImpl(), "web");
		TestService test = handler.createProxy(TestService.class);
		assertEquals(14, test.sumAllowed(5, 9));
	}

	@Test(expected = RuntimeException.class)
	public void testBlockedByDomainAdapter() {
		BlockingInvocationHandlerAdapter handler = new BlockingInvocationHandlerAdapter(
				new TestServiceImpl(), "nowhere");
		TestService test = handler.createProxy(TestService.class);
		test.sumAllowed(5, 9);
	}

	@Test(expected = RuntimeException.class)
	public void testBlockedAdapter() {
		BlockingInvocationHandlerAdapter handler = new BlockingInvocationHandlerAdapter(
				new TestServiceImpl(), "web");
		TestService test = handler.createProxy(TestService.class);
		test.sum(5, 9);
	}

	@Test
	public void testMethodWithAnonimousImplementation() {
		Interface impl = new Interface() {

			public String doIt(String a) {
				return a;
			}

		};

		InvocationHandlerAdapter handler = new InvocationHandlerAdapter(impl);
		Interface test = handler.createProxy(Interface.class);
		test.doIt("bla");
	}

	@Test
	public void testMethodWithoutImplementation() {
		Object impl = new NotInterface();

		InvocationHandlerAdapter handler = new InvocationHandlerAdapter(impl);
		Interface test = handler.createProxy(Interface.class);
		Assert.assertEquals("bla", test.doIt("bla"));
	}

	public static class NotInterface {
		public String doIt(String a) {
			return a;
		}
	}
}
