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

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;

import org.junit.After;
import org.junit.Before;

import com.googlecode.mycontainer.datasource.DataSourceDeployer;
import com.googlecode.mycontainer.ejb.SessionInterceptorDeployer;
import com.googlecode.mycontainer.ejb.StatelessScannableDeployer;
import com.googlecode.mycontainer.jpa.HibernateJPADeployer;
import com.googlecode.mycontainer.jpa.JPADeployer;
import com.googlecode.mycontainer.jpa.JPAInfoBuilder;
import com.googlecode.mycontainer.jta.MyTransactionManagerDeployer;
import com.googlecode.mycontainer.kernel.ShutdownCommand;
import com.googlecode.mycontainer.kernel.boot.ContainerBuilder;
import com.googlecode.mycontainer.kernel.deploy.ScannerDeployer;
import com.googlecode.mycontainer.kernel.naming.MyNameParser;
import com.googlecode.mycontainer.mail.MailDeployer;

public abstract class AbstractTestCase {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractTestCase.class);

	protected ContainerBuilder builder;

	protected TransactionManager tm;

	private InitialContext ctx;

	@Before
	public void boot() throws Exception {
		builder = new ContainerBuilder();

		SessionInterceptorDeployer sessionInterceptorDeployer = builder.createDeployer(SessionInterceptorDeployer.class);
		sessionInterceptorDeployer.deploy();

		builder.createDeployer(MyTransactionManagerDeployer.class).setName("TransactionManager").deploy();

		DataSourceDeployer ds = builder.createDeployer(DataSourceDeployer.class);
		ds.setName("TestDS");
		ds.setDriver("org.hsqldb.jdbcDriver");
		ds.setUrl("jdbc:hsqldb:mem:.");
		ds.setUser("sa");
		ds.deploy();

		JPADeployer jpa = builder.createDeployer(HibernateJPADeployer.class);
		JPAInfoBuilder info = (JPAInfoBuilder) jpa.getInfo();
		info.setPersistenceUnitName("test-pu");
		info.setJtaDataSourceName("TestDS");
		info.addJarFileUrl(CustomerBean.class);
		info.setPersistenceUnitRootUrl(CustomerBean.class);
		Properties props = info.getProperties();
		props.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		props.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		props.setProperty("hibernate.show_sql", "true");
		jpa.deploy();

		ScannerDeployer scanner = builder.createDeployer(ScannerDeployer.class);
		scanner.add(new StatelessScannableDeployer());
		scanner.scan(EntityManagerWrapperBean.class);
		scanner.deploy();

		ctx = builder.getContext();
		tm = (TransactionManager) ctx.lookup("TransactionManager");

		MailDeployer mail = builder.createDeployer(MailDeployer.class);
		mail.setName("java:/Mail");
		mail.deploy();
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
	
	@SuppressWarnings("unchecked")
	protected <V> V lookupEJB(Class<V> clazz) throws NamingException {
		Name name = new MyNameParser().parse("ejb", clazz);
		return (V) ctx.lookup(name);
	}
}
