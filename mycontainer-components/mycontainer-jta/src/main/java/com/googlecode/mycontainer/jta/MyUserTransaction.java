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

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

public class MyUserTransaction implements UserTransaction {

	private final TransactionManager tm;

	public MyUserTransaction(TransactionManager tm) {
		this.tm = tm;
	}

	public void begin() throws NotSupportedException, SystemException {
		tm.begin();
	}

	public void commit() throws RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SecurityException,
			IllegalStateException, SystemException {
		tm.commit();
	}

	public int getStatus() throws SystemException {
		return tm.getStatus();
	}

	public void rollback() throws IllegalStateException, SecurityException,
			SystemException {
		tm.rollback();
	}

	public void setRollbackOnly() throws IllegalStateException, SystemException {
		tm.setRollbackOnly();
	}

	public void setTransactionTimeout(int timeout) throws SystemException {
		tm.setTransactionTimeout(timeout);
	}

}
