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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mycontainer.datasource.DataSourceDeployer;
import com.googlecode.mycontainer.ejb.SessionInterceptorDeployer;
import com.googlecode.mycontainer.ejb.StatelessDeployer;
import com.googlecode.mycontainer.jpa.HibernateJPADeployer;
import com.googlecode.mycontainer.jpa.JPADeployer;
import com.googlecode.mycontainer.jpa.JPAInfoBuilder;
import com.googlecode.mycontainer.jta.MyTransactionManagerDeployer;
import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.test.EntityManagerWrapper;

public class CustomerBeanTest {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(CustomerBeanTest.class);

	protected TransactionManager tm;

	private InitialContext ic;

	@Before
	public void boot() throws Exception {
		Properties prop = new Properties();
		prop.setProperty("java.naming.factory.initial",
				"com.googlecode.mycontainer.kernel.naming.MyContainerContextFactory");

		ContainerBuilder builder = new ContainerBuilder(prop);

		ic = builder.getContext();
		SessionInterceptorDeployer sessionInterceptorDeployer = new SessionInterceptorDeployer();
		sessionInterceptorDeployer.setContext(ic);
		sessionInterceptorDeployer.deploy();

		MyTransactionManagerDeployer jta = new MyTransactionManagerDeployer();
		jta.setContext(ic);
		jta.setName("TransactionManager");
		jta.deploy();
		tm = (TransactionManager) ic.lookup("TransactionManager");

		DataSourceDeployer ds = new DataSourceDeployer();
		ds.setContext(ic);
		ds.setName("TestDS");
		ds.setDriver("org.hsqldb.jdbcDriver");
		ds.setUrl("jdbc:hsqldb:mem:.");
		ds.setUser("sa");
		ds.deploy();

		JPADeployer jpa = builder.createDeployer(HibernateJPADeployer.class);
		JPAInfoBuilder infoBuilder = (JPAInfoBuilder) jpa.getInfo();
		infoBuilder.setPersistenceUnitName("test-pu");
		infoBuilder.setJtaDataSourceName("TestDS");
		infoBuilder.addJarFileUrl(CustomerBean.class);
		infoBuilder.setPersistenceUnitRootUrl(CustomerBean.class);
		Properties props = infoBuilder.getProperties();
		props.setProperty("hibernate.dialect",
				"org.hibernate.dialect.HSQLDialect");
		props.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		props.setProperty("hibernate.show_sql", "true");
		jpa.setSessionInterceptorDeployer(sessionInterceptorDeployer.getName());
		jpa.deploy();

		StatelessDeployer deployer = new StatelessDeployer();
		deployer.setContext(ic);
		deployer.deploy(EntityManagerWrapperBean.class);

	}

	@After
	public void shutdown() throws Exception {
		try {
			ShutdownCommand shutdown = new ShutdownCommand();
			shutdown.setContext(ic);
			shutdown.shutdown();
		} catch (Exception e) {
			LOG.error("Error shutdown", e);
		}
	}

	@Test
	public void testRemove() throws Exception {
		tm.begin();
		try {
			EntityManager em = (EntityManager) ic.lookup("test-pu");

			assertNull(em.find(CustomerBean.class, 10l));

			CustomerBean customer = new CustomerBean();
			customer.setName("name10");
			em.persist(customer);
			assertNotNull(customer.getId());

			customer = em.find(CustomerBean.class, customer.getId());
			assertEquals("name10", customer.getName());

			customer.setName("nameTest");
			em.merge(customer);
			customer = em.find(CustomerBean.class, customer.getId());
			assertEquals("nameTest", customer.getName());

			em.remove(customer);
			assertNull(em.find(CustomerBean.class, 10l));

		} finally {
			tm.commit();
		}
	}

	@Test
	public void testCreate() throws Exception {
		EntityManagerWrapper em = (EntityManagerWrapper) ic
				.lookup("EntityManagerWrapperBean/local");

		assertNull(em.find(CustomerBean.class, 10l));

		CustomerBean customer = new CustomerBean();
		customer.setName("name10");
		customer = em.persist(customer);
		assertNotNull(customer.getId());

		customer = em.find(CustomerBean.class, customer.getId());
		assertEquals("name10", customer.getName());

		customer.setName("nameTest");
		customer = em.merge(customer);
		assertEquals("nameTest", customer.getName());
		customer = em.find(CustomerBean.class, customer.getId());
		assertEquals("nameTest", customer.getName());

		em.remove(customer);
		assertNull(em.find(CustomerBean.class, 10l));
	}

}
