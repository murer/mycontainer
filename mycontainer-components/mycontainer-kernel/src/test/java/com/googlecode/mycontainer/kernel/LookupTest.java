/*
 * Copyright 2008 Whohoo Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.googlecode.mycontainer.kernel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.junit.Test;

import com.googlecode.mycontainer.kernel.naming.MyContainerContextFactory;


public class LookupTest {

	private static class MyThread extends Thread {

		private boolean ok = false;

		@Override
		public void run() {
			try {
				InitialContext ic = new InitialContext();
				ic.bind("tl/test1", "test1");
				assertEquals("bla1", ic.lookup("xyz/test"));
				assertEquals("test1", ic.lookup("tl/test1"));
				ok = true;
			} catch (NamingException e) {
				throw new RuntimeException(e);
			}
		}

		public boolean isOk() {
			return ok;
		}

	}

	@Test
	public void testLookup() throws NamingException {
		InitialContext ic = new InitialContext();
		ic.bind("test", "bla");

		ic = new InitialContext();
		assertEquals("bla", ic.lookup("test"));
	}


	@Test
	public void testEmptyLookup() throws NamingException {
		InitialContext ic = new InitialContext();
		Object result = ic.lookup("");
		assertTrue(result instanceof Context);
	}


	@Test
	public void testLookupInstances() throws NamingException {
		Properties props1 = new Properties();
		props1.setProperty("java.naming.factory.initial",
				"com.googlecode.mycontainer.kernel.naming.MyContainerContextFactory");
		props1.setProperty(MyContainerContextFactory.CONTAINER_PARTITION, "p1");

		Properties props2 = new Properties();
		props2.setProperty("java.naming.factory.initial",
				"com.googlecode.mycontainer.kernel.naming.MyContainerContextFactory");
		props2.setProperty(MyContainerContextFactory.CONTAINER_PARTITION, "p2");

		InitialContext ic1 = new InitialContext(props1);
		InitialContext ic2 = new InitialContext(props2);

		ic1.bind("/xyz/test", "bla1");
		ic1 = new InitialContext(props1);
		assertEquals("bla1", ic1.lookup("/xyz/test"));

		try {
			ic2.lookup("/xyz/test");
			fail("NameNotFoundException  expected");
		} catch (NameNotFoundException e) {
			// Ok...
		}
		ic2.bind("test", "bla2");
		ic2 = new InitialContext(props2);
		assertEquals("bla2", ic2.lookup("test"));

		ic1 = new InitialContext(props1);
		ic1.bind("/xyz/test2", "bla2");
		assertEquals("bla1", ic1.lookup("/xyz/test"));
		assertEquals("bla2", ic1.lookup("/xyz/test2"));
	}

	@Test
	public void testLookupThread() throws Exception {
		final InitialContext ic = new InitialContext();

		ic.bind("xyz/test", "bla1");
		ic.bind("tl/test1", "mytest");
		MyThread thread = new MyThread();
		thread.start();
		thread.join();
		assertTrue("thread fail", thread.isOk());
		assertEquals("bla1", ic.lookup("xyz/test"));
		assertEquals("mytest", ic.lookup("tl/test1"));
	}
}
