package com.googlecode.mycontainer.jpa;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.engine.jndi.JndiException;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;

public class MycontainerJtaPlatform extends AbstractJtaPlatform {

	private static final long serialVersionUID = 1L;

	@Override
	protected TransactionManager locateTransactionManager() {
		try {
			TransactionManager ret = (TransactionManager) jndiService().locate("TransactionManager");
			return ret;
		} catch (JndiException jndiException) {
			throw new JndiException("unable to find transaction manager", jndiException);
		}
	}

	@Override
	protected UserTransaction locateUserTransaction() {
		try {
			UserTransaction ret = (UserTransaction) jndiService().locate("UserTransaction");
			return ret;
		} catch (JndiException jndiException) {
			throw new JndiException("unable to find transaction manager", jndiException);
		}
	}

}
