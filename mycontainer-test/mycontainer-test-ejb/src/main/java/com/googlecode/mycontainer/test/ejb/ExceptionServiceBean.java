package com.googlecode.mycontainer.test.ejb;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import com.googlecode.mycontainer.test.CheckedException;
import com.googlecode.mycontainer.test.ExceptionService;
import com.googlecode.mycontainer.test.RollbackCheckedException;

@Stateless
public class ExceptionServiceBean implements ExceptionService {

	@Resource
	private SessionContext ctx;
	
	
	public void doItThrowRuntimeException() {
		throw new RuntimeException();
	}

	public void doIt() throws CheckedException {
		throw new CheckedException();
	}

	public void doItSetRollbackOnly() {
		ctx.setRollbackOnly();
	}

	public void doItButRollbackOnError() throws RollbackCheckedException {
		throw new RollbackCheckedException();
	}

}
