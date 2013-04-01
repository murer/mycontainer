package com.googlecode.mycontainer.commons.reflect;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Test;

import com.googlecode.mycontainer.commons.reflect.InvocationHandlerAdapter;

public class BlockingInvocationHandlerAdapterTest {

	@Test
	public void testAdapter() {
		InvocationHandlerAdapter handler = new InvocationHandlerAdapter(
				new TestServiceImpl());
		TestService test = handler.createProxy(TestService.class);
		assertEquals(14, test.sum(5, 9));
	}

	@Test
	public void testHookedAdapter() {
		InvocationHandlerAdapter handler = new InvocationHandlerAdapter(
				new TestServiceImpl()) {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				Integer ret = (Integer) super.invoke(proxy, method, args);
				ret = ret * 2;
				return ret;
			}
		};
		TestService test = handler.createProxy(TestService.class);
		assertEquals(28, test.sum(5, 9));
	}

}
