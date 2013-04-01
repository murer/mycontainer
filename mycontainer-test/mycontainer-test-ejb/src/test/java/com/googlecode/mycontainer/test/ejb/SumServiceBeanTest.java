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

package com.googlecode.mycontainer.test.ejb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;

import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mycontainer.ejb.SessionInterceptorDeployer;
import com.googlecode.mycontainer.ejb.StatelessDeployer;
import com.googlecode.mycontainer.jta.MyTransactionManagerDeployer;
import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.test.SumService;
import com.googlecode.mycontainer.test.ejb.SumServiceBean;

public class SumServiceBeanTest {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SumServiceBeanTest.class);

	private static final String SUM_SERVICE_BEAN_NAME = "service/SumServiceBean/local";

	@Before
	public void setUp() throws Exception {
		InitialContext ic = new InitialContext();

		SessionInterceptorDeployer sessionInterceptorDeployer = new SessionInterceptorDeployer();
		sessionInterceptorDeployer.setContext(ic);
		sessionInterceptorDeployer.deploy();

		MyTransactionManagerDeployer jta = new MyTransactionManagerDeployer();
		jta.setContext(ic);
		jta.setName("TransactionManager");
		jta.deploy();

		StatelessDeployer deployer = new StatelessDeployer();
		deployer.setContext(ic);
		deployer.deploy(SumServiceBean.class);
	}

	@Test
	public void testSum() throws Exception {
		InitialContext ic = new InitialContext();
		SumService sumService = (SumService) ic.lookup(SUM_SERVICE_BEAN_NAME);
		assertEquals(3, sumService.sum(1, 2));

		sumService = (SumService) ic.lookup(SUM_SERVICE_BEAN_NAME);
		assertEquals(3, sumService.sum(1, 2));
	}

	@Test(expected = ParseException.class)
	public void testException() throws Exception {
		InitialContext ic = new InitialContext();
		SumService sumService = (SumService) ic.lookup(SUM_SERVICE_BEAN_NAME);
		sumService.sum("a", "b");
	}

	@Test
	public void testRuntimeException() throws Exception {
		InitialContext ic = new InitialContext();
		SumService sumService = (SumService) ic.lookup(SUM_SERVICE_BEAN_NAME);
		try {
			sumService.divide(10, 0);
			fail("EJBException expected");
		} catch (EJBException e) {
			assertTrue(e.getCausedByException() instanceof ArithmeticException);
		}
	}

	@After
	public void tearDown() {
		try {
			ShutdownCommand shutdown = new ShutdownCommand();
			shutdown.setContext(new InitialContext());
			shutdown.shutdown();
		} catch (NamingException e) {
			LOG.error("Error shutdown", e);
		}
	}

}
