package com.googlecode.mycontainer.test;

import javax.ejb.Local;

@Local
public interface ExceptionService {

	void doIt() throws CheckedException;
	
	public void doItThrowRuntimeException();

	public void doItSetRollbackOnly();

	void doItButRollbackOnError() throws RollbackCheckedException;

}
