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
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mycontainer.datasource.DataSourceDeployer;
import com.googlecode.mycontainer.jta.MyTransactionManagerDeployer;
import com.googlecode.mycontainer.kernel.ShutdownCommand;

public class DataSourceSQLTest {

	@Before
	public void boot() throws Exception {
		InitialContext ic = new InitialContext();

		MyTransactionManagerDeployer jta = new MyTransactionManagerDeployer();
		jta.setContext(ic);
		jta.setName("TransactionManager");
		jta.deploy();

		DataSourceDeployer deployer = new DataSourceDeployer();
		deployer.setContext(ic);
		deployer.setName("TestDS");
		deployer.setDriver("org.hsqldb.jdbcDriver");
		deployer.setUrl("jdbc:hsqldb:mem:.");
		deployer.setUser("sa");
		deployer.deploy();

		TransactionManager tm = (TransactionManager) ic
				.lookup("TransactionManager");
		DataSource ds = (DataSource) ic.lookup("TestDS");
		Connection conn = null;
		tm.begin();
		try {
			conn = (Connection) ds.getConnection();
			executeUpdate(conn, "create table test(id integer)");
		} finally {
			close(conn);
		}
		tm.commit();
	}

	@After
	public void shutdown() throws Exception {
		InitialContext ic = new InitialContext();
		TransactionManager tm = (TransactionManager) ic
				.lookup("TransactionManager");
		DataSource ds = (DataSource) ic.lookup("TestDS");
		Connection conn = null;
		tm.begin();
		try {
			conn = (Connection) ds.getConnection();
			executeUpdate(conn, "drop table test");
		} finally {
			close(conn);
		}
		tm.commit();
		ShutdownCommand shutdown = new ShutdownCommand();
		shutdown.setContext(ic);
		shutdown.shutdown();
	}

	@Test
	public void testCommit() throws Exception {
		InitialContext ic = new InitialContext();

		TransactionManager tm = (TransactionManager) ic
				.lookup("TransactionManager");
		DataSource ds = (DataSource) ic.lookup("TestDS");
		assertNotNull(ds);

		Connection conn = null;
		Statement s = null;
		ResultSet rs = null;
		tm.begin();
		try {
			conn = (Connection) ds.getConnection();
			executeUpdate(conn, "insert into test(id) values (1)");
		} finally {
			close(rs);
			close(s);
			close(conn);
			tm.commit();
		}

		tm.begin();
		try {
			conn = (Connection) ds.getConnection();
			s = conn.createStatement();
			rs = s.executeQuery("select id from test");
			assertTrue(rs.next());
			assertEquals(1, rs.getInt("id"));
			assertFalse(rs.next());
		} finally {
			close(rs);
			close(s);
			close(conn);
			tm.rollback();
		}

	}

	@Test
	public void testNoTransactiopn() throws Exception {
		InitialContext ic = new InitialContext();

		TransactionManager tm = (TransactionManager) ic
				.lookup("TransactionManager");
		DataSource ds = (DataSource) ic.lookup("TestDS");
		assertNotNull(ds);

		Connection conn = null;
		Statement s = null;
		ResultSet rs = null;
		tm.begin();
		try {
			conn = (Connection) ds.getConnection();
			executeUpdate(conn, "insert into test(id) values (1)");
		} finally {
			close(rs);
			close(s);
			close(conn);
			tm.commit();
		}

		try {
			conn = (Connection) ds.getConnection();
			s = conn.createStatement();
			rs = s.executeQuery("select id from test");
			assertTrue(rs.next());
			assertEquals(1, rs.getInt("id"));
			assertFalse(rs.next());
		} finally {
			close(rs);
			close(s);
			close(conn);
		}

	}

	private void executeUpdate(Connection conn, String sql) throws Exception {
		Statement s = conn.createStatement();
		try {
			s.executeUpdate(sql);
		} finally {
			if (s != null) {
				s.close();
			}
		}
	}

	private void close(Connection c) throws Exception {
		if (c != null) {
			c.close();
		}
	}

	private void close(Statement c) throws Exception {
		if (c != null) {
			c.close();
		}
	}

	private void close(ResultSet c) throws Exception {
		if (c != null) {
			c.close();
		}
	}

	@Test
	public void testRollback() throws Exception {
		InitialContext ic = new InitialContext();

		TransactionManager tm = (TransactionManager) ic
				.lookup("TransactionManager");
		DataSource ds = (DataSource) ic.lookup("TestDS");
		assertNotNull(ds);

		Connection conn = null;
		Statement s = null;
		ResultSet rs = null;
		tm.begin();
		try {
			conn = (Connection) ds.getConnection();
			executeUpdate(conn, "insert into test(id) values (1)");
		} finally {
			close(rs);
			close(s);
			close(conn);
			tm.rollback();
		}

		tm.begin();
		try {
			conn = (Connection) ds.getConnection();
			s = conn.createStatement();
			rs = s.executeQuery("select id from test");
			assertFalse(rs.next());
		} finally {
			close(rs);
			close(s);
			close(conn);
			tm.rollback();
		}

	}
}
