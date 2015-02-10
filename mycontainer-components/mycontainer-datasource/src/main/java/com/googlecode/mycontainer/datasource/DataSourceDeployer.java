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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import com.googlecode.mycontainer.jta.TxEntry;
import com.googlecode.mycontainer.kernel.KernelRuntimeException;
import com.googlecode.mycontainer.kernel.ShutdownHook;
import com.googlecode.mycontainer.kernel.deploy.DeployException;
import com.googlecode.mycontainer.kernel.deploy.Deployer;

public class DataSourceDeployer extends Deployer implements ShutdownHook, DataSource {

	private static final long serialVersionUID = -2510091316151254781L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DataSourceDeployer.class);

	private String transcationManagerName = "TransactionManager";

	private String driver;

	private String url;

	private String user;

	private String pass;

	private String name;

	private String newConnectionSql;

	private PrintWriter logWriter;

	private int loginTimeout;

	public void setTranscationManagerName(String transcationManagerName) {
		this.transcationManagerName = transcationManagerName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public void setNewConnectionSql(String newConnectionSql) {
		this.newConnectionSql = newConnectionSql;
	}

	public void deploy() {
		try {
			Context ctx = getContext();
			LOG.info("Deploying: " + name);
			ctx.bind(name, this);
			getKernel().addShutdownHook(this);
		} catch (NamingException e) {
			throw new DeployException(e);
		}
	}

	public void shutdown() {
		try {
			Context ctx = getContext();
			LOG.info("Undeploying: " + name);
			ctx.unbind(name);
		} catch (NamingException e) {
			throw new DeployException(e);
		}
	}

	public Connection getConnection() throws SQLException {
		return getConnection(user, pass);
	}

	public Connection getConnection(String username, String password) {
		Connection ret = null;
		try {
			Context ctx = getContext();
			TransactionManager tm = (TransactionManager) ctx.lookup(transcationManagerName);

			if (tm.getStatus() == Status.STATUS_NO_TRANSACTION) {
				ret = createConnection();
				return ret;
			}

			Transaction tx = tm.getTransaction();
			TxEntry entry = new TxEntry(tx, name);

			Map<TxEntry, Connection> connections = lookupConnections();
			ret = connections.get(entry);
			if (ret != null) {
				return ret;
			}

			ret = createConnection();

			ret = enlist(ret, tx);
			connections.put(entry, ret);
			ConnectionResourceSynchronization sync = new ConnectionResourceSynchronization(connections, entry);
			tx.registerSynchronization(sync);

			return ret;
		} catch (ClassNotFoundException e) {
			rollbackAndClose(ret);
			throw new DeployException(e);
		} catch (SQLException e) {
			rollbackAndClose(ret);
			throw new DeployException(e);
		} catch (NamingException e) {
			rollbackAndClose(ret);
			throw new DeployException(e);
		} catch (SystemException e) {
			rollbackAndClose(ret);
			throw new DeployException(e);
		} catch (RollbackException e) {
			rollbackAndClose(ret);
			throw new DeployException(e);
		} catch (RuntimeException e) {
			rollbackAndClose(ret);
			throw e;
		}
	}

	private Connection createConnection() throws ClassNotFoundException, SQLException {
		Connection ret;
		Class.forName(driver);
		ret = DriverManager.getConnection(url, user, pass);
		ret.setAutoCommit(false);

		handleNewConnection(ret);

		return ret;
	}

	private void handleNewConnection(Connection con) throws SQLException {
		if (newConnectionSql != null && newConnectionSql.trim().length() > 0) {
			Statement st = con.createStatement();
			try {
				st.execute(newConnectionSql);
			} finally {
				try {
					st.close();
				} catch (SQLException ex) {
					LOG.error("Error closing st.", ex);
				}
			}
			try {
				con.commit();
			} catch (Exception e) {
				throw new RuntimeException("error commiting newConnectionSql", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Map<TxEntry, Connection> lookupConnections() {
		Context ctx = getContext();
		try {
			try {
				Map<TxEntry, Connection> ret = (Map<TxEntry, Connection>) ctx.lookup("tl/ds/connections");
				return ret;
			} catch (NameNotFoundException e) {
				Map<TxEntry, Connection> ret = new HashMap<TxEntry, Connection>();
				ctx.bind("tl/ds/connections", ret);
				return ret;
			}
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	private void rollbackAndClose(Connection ret) {
		if (ret != null) {
			try {
				ret.rollback();
				ret.close();
			} catch (Exception e) {
				LOG.error("Error closing", e);
			}
		}
	}

	private Connection enlist(Connection ret, Transaction tx) {
		try {
			ConnectionResource resource = new ConnectionResource(name, ret);
			tx.enlistResource(resource);
			return resource.getProxy();
		} catch (SystemException e) {
			throw new KernelRuntimeException(e);
		} catch (IllegalStateException e) {
			throw new KernelRuntimeException(e);
		} catch (RollbackException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public PrintWriter getLogWriter() throws SQLException {
		return this.logWriter;
	}

	public int getLoginTimeout() throws SQLException {
		return this.loginTimeout;
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		this.logWriter = out;
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		this.loginTimeout = seconds;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new KernelRuntimeException("unsupported opration");
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new KernelRuntimeException("unsupported opration");
	}

	public Logger getParentLogger() {
		throw new KernelRuntimeException("unsupported opration");
	}

}
