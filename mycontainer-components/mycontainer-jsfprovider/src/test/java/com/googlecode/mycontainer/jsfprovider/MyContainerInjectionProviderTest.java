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
package com.googlecode.mycontainer.jsfprovider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.CompositeName;
import javax.naming.InitialContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mycontainer.ejb.SessionInterceptorDeployer;
import com.googlecode.mycontainer.ejb.StatelessScannableDeployer;
import com.googlecode.mycontainer.jta.MyTransactionManagerDeployer;
import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.kernel.deploy.ScannerDeployer;
import com.googlecode.mycontainer.kernel.naming.MyNameParser;

public class MyContainerInjectionProviderTest {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MyContainerInjectionProviderTest.class);

	private static final String IDO_IT = "IDoIt";

	private ContainerBuilder builder;

	private boolean postConstruct;

	private boolean preDestroy;

	private MyContainerInjectionProvider provider = new MyContainerInjectionProvider();

	@Before
	public void boot() throws Exception {
		this.postConstruct = false;
		this.preDestroy = false;

		builder = new ContainerBuilder();

		SessionInterceptorDeployer sessionInterceptorDeployer = builder.createDeployer(SessionInterceptorDeployer.class);
		sessionInterceptorDeployer.deploy();

		builder.createDeployer(MyTransactionManagerDeployer.class).setName("TransactionManager").deploy();

		ScannerDeployer scanner = builder.createDeployer(ScannerDeployer.class);
		scanner.add(new StatelessScannableDeployer());
		scanner.scan(MockServiceBean.class);
		scanner.deploy();
	}

	@After
	public void shutdown() {
		try {
			ShutdownCommand shutdown = new ShutdownCommand();
			shutdown.setContext(new InitialContext());
			shutdown.shutdown();
		} catch (Exception e) {
			LOG.error("Error shutdown", e);
		}
	}

	@Test
	public void testInject() throws Exception {
		MockServiceBean bean = new MockServiceBean();
		InitialContext ctx = new InitialContext();
		ctx.bind(new CompositeName(MyNameParser.parseClassName("resource", MockService.class)), bean);

		ManagedBean mb = new ManagedBean();
		provider.inject(mb);
		assertNotNull(mb.mockService);
		assertEquals(IDO_IT, mb.callDoIt());
		assertEquals(IDO_IT, mb.callDoItMapped());
		assertEquals(IDO_IT, mb.callDoItResoucedAndMapped());
		assertEquals(IDO_IT, mb.callDoItResourced());
		assertEquals(IDO_IT, mb.callDoItInterface());
		assertEquals(IDO_IT, mb.callDoItResourcedByType());
	}

	// @Test
	public void testInheritance() throws Exception {
		MockServiceBean bean = new MockServiceBean();
		InitialContext ctx = new InitialContext();
		ctx.bind(new CompositeName("resource/org/mycontainer/jsfprovider/MockService"), bean);

		SubManagedBean mb = new SubManagedBean();
		provider.inject(mb);
		assertNotNull(mb.getMockService());
		assertEquals(IDO_IT, mb.callDoIt());
		assertEquals(IDO_IT, mb.callDoItMapped());
		assertEquals(IDO_IT, mb.callDoItResoucedAndMapped());
		assertEquals(IDO_IT, mb.callDoItResourced());
		assertEquals(IDO_IT, mb.callDoItInterface());
		assertEquals(IDO_IT, mb.callDoItResourcedByType());
		assertEquals(IDO_IT, mb.callDoItOtherMockService());
	}

	@Test
	public void testInvokePostConstruct() throws Exception {
		ManagedBean mb = new ManagedBean();
		provider.invokePostConstruct(mb);
		assertTrue(postConstruct);
	}

	@Test
	public void testInvokePreDestroy() throws Exception {
		ManagedBean mb = new ManagedBean();
		provider.invokePreDestroy(mb);
		assertTrue(preDestroy);
	}

	public class ManagedBean {
		@EJB
		private MockService mockService;

		@EJB(mappedName = "MockServiceBean/local")
		private MockService mockServiceMapped;

		@EJB(beanInterface = MockService.class)
		private Object mockServiceInterface;

		@Resource(mappedName = "ejb/com/googlecode/mycontainer/jsfprovider/MockService")
		private MockService mockServiceResoucedAndMapped;

		@Resource
		private MockService mockServiceResourced;

		@Resource(type = MockService.class)
		private Object mockServiceResourcedByType;

		@PostConstruct
		public void postConstruct() {
			postConstruct = true;
		}

		@PreDestroy
		public void preDestroy() {
			preDestroy = true;
		}

		public String callDoIt() {
			return mockService.doIt();
		}

		public String callDoItMapped() {
			return mockServiceMapped.doIt();
		}

		public String callDoItResoucedAndMapped() {
			return mockServiceResoucedAndMapped.doIt();
		}

		public String callDoItResourced() {
			return mockServiceResourced.doIt();
		}

		public String callDoItResourcedByType() {
			return ((MockService) mockServiceResourcedByType).doIt();
		}

		public String callDoItInterface() {
			return ((MockService) mockServiceInterface).doIt();
		}

		public MockService getMockService() {
			return mockService;
		}

	}

	public class SubManagedBean extends MyContainerInjectionProviderTest.ManagedBean {
		@EJB
		private MockService otherMockService;

		public String callDoItOtherMockService() {
			return otherMockService.doIt();
		}
	}

	@Stateless
	public static class MockServiceBean implements MockService {
		public String doIt() {
			return IDO_IT;
		}

	}

}
