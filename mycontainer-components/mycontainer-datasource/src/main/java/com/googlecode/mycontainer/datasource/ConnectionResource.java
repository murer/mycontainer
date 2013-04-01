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

import java.sql.Connection;
import java.sql.SQLException;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;
import com.googlecode.mycontainer.kernel.reflect.proxy.ProxyEngine;


public class ConnectionResource implements XAResource {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConnectionResource.class);

	private final Connection conn;

	private final Connection proxy;

	private String name;

	public ConnectionResource(String name, Connection conn) {
		this.name = name;
		this.conn = conn;
		ProxyEngine<Connection> engine = new ProxyEngine<Connection>(
				Connection.class, conn);
		engine.addInterceptor(new ConnectionInterceptor());
		this.proxy = engine.create();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Connection getProxy() {
		return proxy;
	}

	public void commit(Xid xid, boolean onePhase) throws XAException {
		try {
			conn.commit();
		} catch (SQLException e) {
			throw new KernelRuntimeException(e);
		} finally {
			close();
		}
	}

	public void end(Xid xid, int flags) throws XAException {
		throw new KernelRuntimeException("not supported operation");
	}

	public void forget(Xid xid) throws XAException {
		throw new KernelRuntimeException("not supported operation");
	}

	public int getTransactionTimeout() throws XAException {
		throw new KernelRuntimeException("not supported operation");
	}

	public int prepare(Xid xid) throws XAException {
		throw new KernelRuntimeException("not supported operation");
	}

	public Xid[] recover(int flag) throws XAException {
		throw new KernelRuntimeException("not supported operation");
	}

	public void rollback(Xid xid) throws XAException {
		try {
			conn.rollback();
		} catch (SQLException e) {
			throw new KernelRuntimeException(e);
		} finally {
			close();
		}
	}

	public boolean setTransactionTimeout(int seconds) throws XAException {
		throw new KernelRuntimeException("not supported operation");
	}

	public void start(Xid xid, int flags) throws XAException {
		throw new KernelRuntimeException("not supported operation");
	}

	public boolean isSameRM(XAResource xares) throws XAException {
		throw new KernelRuntimeException("not supported operation");
	}

	private void close() {
		try {
			conn.close();
		} catch (Exception e) {
			LOG.error("Error closing", e);
		}
	}

}
