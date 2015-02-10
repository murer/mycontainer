package com.googlecode.mycontainer.jpa;

import org.hibernate.engine.transaction.internal.jta.CMTTransaction;
import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
import org.hibernate.engine.transaction.spi.TransactionCoordinator;

public class MycontainerTransactionFactory extends CMTTransactionFactory {

	private static final long serialVersionUID = 1L;

	@Override
	public CMTTransaction createTransaction(TransactionCoordinator transactionCoordinator) {
		CMTTransaction ret = super.createTransaction(transactionCoordinator);
		return ret;
	}

	@Override
	public boolean canBeDriver() {
		return super.canBeDriver();
	}

	@Override
	public boolean compatibleWithJtaSynchronization() {
		boolean ret = super.compatibleWithJtaSynchronization();
		return ret;
	}

	@Override
	public boolean isJoinableJtaTransaction(TransactionCoordinator transactionCoordinator, CMTTransaction transaction) {
		boolean ret = super.isJoinableJtaTransaction(transactionCoordinator, transaction);
		return ret;
	}

}
