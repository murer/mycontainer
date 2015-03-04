package com.googlecode.mycontainer.test;

import javax.ejb.Local;

@Local
public interface ExceptionHandlerService {

	int doItButHandleException();

	int doItButHandleExceptionAndRollBack();

	int doItButHandleRuntimeException();

	int doItButHandleExceptionWithNewTransaction();

	int doItButHandleExceptionAndSetRollBackOnly();

}
