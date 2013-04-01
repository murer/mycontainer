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

package com.googlecode.mycontainer.ejb;

import java.io.Serializable;
import java.security.Identity;
import java.security.Principal;
import java.util.Properties;

import javax.ejb.EJBContext;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.TimerService;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;
import com.googlecode.mycontainer.kernel.naming.MyNameParser;

public abstract class MyEJBContext implements EJBContext, Serializable {

	private static final long serialVersionUID = -4056037757355740170L;

	private final Context ctx;

	private final String info;

	private final Context subContext;

	public MyEJBContext(Context ctx, String info) {
		try {
			this.ctx = ctx;
			this.info = info;
			this.subContext = (Context) ctx.lookup(info);
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public Context getCtx() {
		return ctx;
	}

	public String getInfo() {
		return info;
	}

	public Context getSubContext() {
		return subContext;
	}

	@Deprecated
	public Identity getCallerIdentity() {
		throw new KernelRuntimeException("not supported");
	}

	public Principal getCallerPrincipal() {
		throw new KernelRuntimeException("not supported");
	}

	public EJBHome getEJBHome() {
		throw new KernelRuntimeException("not supported");
	}

	public EJBLocalHome getEJBLocalHome() {
		throw new KernelRuntimeException("not supported");
	}

	public Properties getEnvironment() {
		return new Properties();
	}

	public boolean getRollbackOnly() throws IllegalStateException {
		try {
			UserTransaction userTransaction = getUserTransaction();
			return (userTransaction.getStatus() == Status.STATUS_MARKED_ROLLBACK);
		} catch (SystemException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public TimerService getTimerService() throws IllegalStateException {
		throw new KernelRuntimeException("not supported");
	}

	public UserTransaction getUserTransaction() throws IllegalStateException {
		try {
			String name = MyNameParser.parseClassName("resource",
					UserTransaction.class);
			return (UserTransaction) ctx.lookup(name);
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	@Deprecated
	public boolean isCallerInRole(Identity arg0) {
		throw new KernelRuntimeException("not supported");
	}

	public boolean isCallerInRole(String arg0) {
		throw new KernelRuntimeException("not supported");
	}

	public Object lookup(String name) {
		try {
			return subContext.lookup(name);
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public void setRollbackOnly() throws IllegalStateException {
		try {
			UserTransaction userTransaction = getUserTransaction();
			userTransaction.setRollbackOnly();
		} catch (SystemException e) {
			throw new KernelRuntimeException(e);
		}
	}

}
