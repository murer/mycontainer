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

package com.googlecode.mycontainer.jpa;

import static org.junit.Assert.assertEquals;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mycontainer.datasource.DataSourceDeployer;
import com.googlecode.mycontainer.jta.MyTransactionManagerDeployer;
import com.googlecode.mycontainer.kernel.ShutdownCommand;


public class MyTransactionManagerTest {

	@Before
	public void boot() throws NamingException {
		InitialContext ic = new InitialContext();

		MyTransactionManagerDeployer jta = new MyTransactionManagerDeployer();
		jta.setContext(ic);
		jta.setName("TransactionManager");
		jta.deploy();

		DataSourceDeployer ds = new DataSourceDeployer();
		ds.setContext(ic);
		ds.setName("TestDS");
		ds.setDriver("org.hsqldb.jdbcDriver");
		ds.setUrl("jdbc:hsqldb:mem:.");
		ds.setUser("sa");
		ds.deploy();
	}

	@After
	public void shutdown() throws Exception {
		ShutdownCommand shutdown = new ShutdownCommand();
		shutdown.setContext(new InitialContext());
		shutdown.shutdown();
	}

	@Test
	public void testTransactionCommitRollback() throws Exception {
		InitialContext ic = new InitialContext();
		TransactionManager tm = (TransactionManager) ic
				.lookup("TransactionManager");

		assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
		tm.begin();
		assertEquals(Status.STATUS_ACTIVE, tm.getStatus());

		tm.commit();
		assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());

		assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
		tm.begin();
		assertEquals(Status.STATUS_ACTIVE, tm.getStatus());

		tm.rollback();
		assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
	}

	@Test
	public void testTransactionSuspendResume() throws Exception {
		InitialContext ic = new InitialContext();
		TransactionManager tm = (TransactionManager) ic
				.lookup("TransactionManager");

		assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
		tm.begin();
		assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
		Transaction tx = tm.suspend();
		assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
		tm.resume(tx);
		assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
		tm.commit();
		assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
	}

}
