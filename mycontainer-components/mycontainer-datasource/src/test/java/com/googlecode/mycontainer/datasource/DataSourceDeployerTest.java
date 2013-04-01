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

package com.googlecode.mycontainer.datasource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mycontainer.datasource.DataSourceDeployer;
import com.googlecode.mycontainer.jta.MyTransactionManagerDeployer;
import com.googlecode.mycontainer.kernel.ShutdownCommand;

public class DataSourceDeployerTest {

	@Before
	public void boot() throws Exception {
		InitialContext ic = new InitialContext();

		MyTransactionManagerDeployer jta = new MyTransactionManagerDeployer();
		jta.setContext(ic);
		jta.setName("TransactionManager");
		jta.deploy();

		DataSourceDeployer ds1 = new DataSourceDeployer();
		ds1.setContext(ic);
		ds1.setName("TestDS");
		ds1.setDriver("org.hsqldb.jdbcDriver");
		ds1.setUrl("jdbc:hsqldb:mem:.");
		ds1.setUser("sa");
		ds1.deploy();

		DataSourceDeployer ds = new DataSourceDeployer();
		ds.setContext(ic);
		ds.setName("OtherTestDS");
		ds.setDriver("org.hsqldb.jdbcDriver");
		ds.setUrl("jdbc:hsqldb:mem:.");
		ds.setUser("sa");
		ds.deploy();

		TransactionManager tm = (TransactionManager) ic
				.lookup("TransactionManager");
		DataSource datas = (DataSource) ic.lookup("TestDS");

		Connection conn = null;
		java.sql.Statement st = null;
		tm.begin();
		try {
			conn = (Connection) datas.getConnection();
			st = conn.createStatement();
			st.executeUpdate("create table testTable (id integer)");
		} finally {
			if (st != null) {
				st.close();
			}

			if (conn != null) {
				conn.close();
			}
		}
		tm.commit();

		ds1.setNewConnectionSql("insert into testTable values (1)");
	}

	@After
	public void shutdown() throws Exception {
		InitialContext ic = new InitialContext();
		DataSource datas = (DataSource) ic.lookup("TestDS");

		Connection conn = null;
		java.sql.Statement st = null;
		try {
			conn = (Connection) datas.getConnection();
			st = conn.createStatement();
			st.executeUpdate("drop table testTable");
		} finally {
			if (st != null) {
				st.close();
			}

			if (conn != null) {
				conn.close();
			}
		}

		ShutdownCommand shutdown = new ShutdownCommand();
		shutdown.setContext(ic);
		shutdown.shutdown();
	}

	@Test
	public void testConnection() throws Exception {
		InitialContext ic = new InitialContext();

		TransactionManager tm = (TransactionManager) ic
				.lookup("TransactionManager");
		DataSource ds = (DataSource) ic.lookup("TestDS");
		assertNotNull(ds);

		Connection conn = null;
		tm.begin();
		try {
			conn = (Connection) ds.getConnection();
			assertNotNull(conn);

			Connection conn1 = ds.getConnection();
			ds = (DataSource) ic.lookup("TestDS");
			Connection conn2 = ds.getConnection();
			assertEquals(conn, conn1);
			assertEquals(conn, conn2);

			Transaction tx = tm.suspend();
			tm.begin();
			conn2 = ds.getConnection();
			assertFalse(conn.equals(conn2));
			tm.commit();
			tm.resume(tx);

			ds = (DataSource) ic.lookup("TestDS");
			conn1 = ds.getConnection();
			ds = (DataSource) ic.lookup("OtherTestDS");
			conn2 = ds.getConnection();
			assertEquals(conn, conn1);
			assertFalse(conn.equals(conn2));
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		tm.commit();
	}

	@Test
	public void testNewConnectionSql() throws Exception {
		InitialContext ic = new InitialContext();

		TransactionManager tm = (TransactionManager) ic
				.lookup("TransactionManager");
		DataSource ds = (DataSource) ic.lookup("TestDS");
		assertNotNull(ds);

		Connection conn = null;
		java.sql.Statement st = null;
		java.sql.ResultSet rs = null;
		tm.begin();
		try {
			conn = (Connection) ds.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery("select count(0) from testTable");
			assertTrue(rs.next());
			assertTrue(rs.getInt(1) > 0);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}

			if (conn != null) {
				conn.close();
			}
		}
		tm.commit();
	}

}
