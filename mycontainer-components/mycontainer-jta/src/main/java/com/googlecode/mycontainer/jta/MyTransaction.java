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

package com.googlecode.mycontainer.jta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;

/**
 * It is not thread safe! Use on the thread local context!
 */
public class MyTransaction implements Transaction, Serializable {

	private static final long serialVersionUID = -5531513227138116991L;

	private Collection<XAResource> xas = new HashSet<XAResource>();

	private List<Synchronization> syncs = new ArrayList<Synchronization>();

	private int status = Status.STATUS_ACTIVE;

	private Collection<XAResource> getXas() {
		return this.xas;
	}

	private boolean addXa(XAResource xa) {
		return xas.add(xa);
	}

	public void commit() throws RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SecurityException,
			IllegalStateException, SystemException {
		if (status != Status.STATUS_ACTIVE) {
			throw new IllegalStateException("tx is not active: " + status);
		}
		try {
			beforeCompletion();
			status = Status.STATUS_COMMITTING;
			for (XAResource xa : getXas()) {
				xa.commit(null, true);
			}
			status = Status.STATUS_COMMITTED;
		} catch (XAException e) {
			throw new KernelRuntimeException(e);
		}
		afterCompletion(status);
	}

	public void rollback() throws IllegalStateException, SystemException {
		if (status != Status.STATUS_ACTIVE
				&& status != Status.STATUS_MARKED_ROLLBACK) {
			throw new IllegalStateException(
					"tx is not active and marked to rollback: " + status);
		}
		try {
			status = Status.STATUS_ROLLING_BACK;
			for (XAResource xa : getXas()) {
				xa.rollback(null);
			}
			status = Status.STATUS_ROLLEDBACK;
			afterCompletion(status);
		} catch (XAException e) {
			throw new KernelRuntimeException(e);
		}
	}

	private void beforeCompletion() {
		int i = 0;
		while (i < syncs.size()) {
			Synchronization sync = syncs.get(i);
			sync.beforeCompletion();
			i++;
		}
	}

	private void afterCompletion(int status) {
		int i = 0;
		while (i < syncs.size()) {
			Synchronization sync = syncs.get(i);
			sync.afterCompletion(status);
			i++;
		}
	}

	public boolean delistResource(XAResource xa, int flag)
			throws IllegalStateException, SystemException {
		if (status != Status.STATUS_ACTIVE) {
			throw new IllegalStateException(Integer.toString(status));
		}
		boolean ret = getXas().remove(xa);
		return ret;
	}

	public boolean enlistResource(XAResource xa) throws RollbackException,
			IllegalStateException, SystemException {
		if (status != Status.STATUS_ACTIVE) {
			throw new IllegalStateException(Integer.toString(status));
		}
		boolean ret = addXa(xa);
		return ret;
	}

	public int getStatus() throws SystemException {
		return status;
	}

	public void registerSynchronization(Synchronization sync)
			throws RollbackException, IllegalStateException, SystemException {
		if (syncs.contains(sync)) {
			throw new SystemException("Sync already registered");
		}
		syncs.add(sync);
	}

	public void setRollbackOnly() throws IllegalStateException, SystemException {
		if (status != Status.STATUS_ACTIVE) {
			throw new IllegalStateException(Integer.toString(status));
		}
		status = Status.STATUS_MARKED_ROLLBACK;
	}

}
