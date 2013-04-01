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

import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;

public class MyTransactionManager implements TransactionManager {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(MyTransactionManager.class);

	private final Hashtable<Object, Object> env;

	@SuppressWarnings("unchecked")
	public MyTransactionManager(Hashtable<?, ?> env) {
		this.env = (Hashtable<Object, Object>) env;
	}

	private void bindTransaction(Transaction tx) {
		try {
			InitialContext ic = new InitialContext(env);
			ic.bind("tl/tx", tx);
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	private void unbindTransaction() {
		try {
			InitialContext ic = new InitialContext(env);
			ic.unbind("tl/tx");
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	private Transaction lookupTransaction() {
		try {
			InitialContext ic = new InitialContext(env);
			Transaction ret = (Transaction) ic.lookup("tl/tx");
			return ret;
		} catch (NameNotFoundException e) {
			return null;
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public int getStatus() throws SystemException {
		Transaction tx = lookupTransaction();
		if (tx == null) {
			return Status.STATUS_NO_TRANSACTION;
		}
		return tx.getStatus();
	}

	public void begin() throws NotSupportedException, SystemException {
		Transaction tx = lookupTransaction();
		if (tx != null) {
			throw new NotSupportedException("nested transcation");
		}

		tx = new MyTransaction();
		bindTransaction(tx);
	}

	public void commit() throws RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SecurityException,
			IllegalStateException, SystemException {
		Transaction tx = lookupTransaction();
		if (tx == null) {
			throw new IllegalStateException("no tx");
		}
		if (getStatus() == Status.STATUS_MARKED_ROLLBACK) {
			LOG.warn("Transaction STATUS_MARKED_ROLLBACK... Rolling back...");
			rollback();
			return;
		}
		getTransaction().commit();
		unbindTransaction();
	}

	public void rollback() throws IllegalStateException, SecurityException,
			SystemException {
		Transaction tx = lookupTransaction();
		if (tx != null) {
			getTransaction().rollback();
			unbindTransaction();
		}
	}

	public Transaction getTransaction() throws SystemException {
		return lookupTransaction();
	}

	public Transaction suspend() throws SystemException {
		Transaction tx = lookupTransaction();
		if (tx != null) {
			unbindTransaction();
		}
		return tx;
	}

	public void resume(Transaction transcation)
			throws InvalidTransactionException, IllegalStateException,
			SystemException {
		Transaction tx = lookupTransaction();
		if (tx != null) {
			throw new IllegalStateException("this thread has a tx");
		}
		bindTransaction(transcation);
	}

	public void setRollbackOnly() throws IllegalStateException, SystemException {
		Transaction tx = getTransaction();
		if (tx != null) {
			tx.setRollbackOnly();
		}
	}

	public void setTransactionTimeout(int timeout) throws SystemException {

	}

}
