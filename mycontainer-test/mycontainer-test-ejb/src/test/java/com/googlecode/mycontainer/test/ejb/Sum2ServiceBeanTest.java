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

import static org.junit.Assert.assertTrue;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mycontainer.ejb.SessionInterceptorDeployer;
import com.googlecode.mycontainer.ejb.StatelessDeployer;
import com.googlecode.mycontainer.jta.MyTransactionManagerDeployer;
import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.test.Sum2Service;
import com.googlecode.mycontainer.test.ejb.Sum2ServiceBean;

public class Sum2ServiceBeanTest {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(Sum2ServiceBeanTest.class);

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
		deployer.deploy(Sum2ServiceBean.class);
	}

	@Test
	public void testLookup() throws Exception {
		InitialContext ic = new InitialContext();
		Object obj = ic.lookup("Sum2ServiceBean/local");
		assertTrue(obj instanceof Sum2Service);
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
