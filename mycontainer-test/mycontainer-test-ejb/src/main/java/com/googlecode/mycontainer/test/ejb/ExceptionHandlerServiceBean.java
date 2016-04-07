package com.googlecode.mycontainer.test.ejb;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.SystemException;

import com.googlecode.mycontainer.test.CheckedException;
import com.googlecode.mycontainer.test.ExceptionHandlerService;
import com.googlecode.mycontainer.test.ExceptionService;
import com.googlecode.mycontainer.test.RollbackCheckedException;

@Stateless
public class ExceptionHandlerServiceBean implements ExceptionHandlerService {

	private static final Logger LOGGER = Logger
			.getLogger(ExceptionHandlerServiceBean.class.getSimpleName());

	@EJB
	private ExceptionService exceptionService;

	@Resource
	private SessionContext sessionContext;

	public int doItButHandleRuntimeException() {
		exceptionService.doItThrowRuntimeException();
		try {
			int status = sessionContext.getUserTransaction().getStatus();
			return status;
		} catch (IllegalStateException e) {
			throw new RuntimeException(e);
		} catch (SystemException e) {
			throw new RuntimeException(e);
		}
	}

	public int doItButHandleException() {
		try {
			exceptionService.doIt();
		} catch (CheckedException e) {
			LOGGER.log(Level.INFO, "Checked exception handled", e);
		}
		try {
			int status = sessionContext.getUserTransaction().getStatus();
			return status;
		} catch (IllegalStateException e) {
			throw new RuntimeException(e);
		} catch (SystemException e) {
			throw new RuntimeException(e);
		}
	}

	public int doItButHandleExceptionAndRollBack() {
		try {
			exceptionService.doItButRollbackOnError();
		} catch (RollbackCheckedException e) {
			LOGGER.log(Level.INFO, "Checked exception handled", e);
		}
		try {
			int status = sessionContext.getUserTransaction().getStatus();
			return status;
		} catch (IllegalStateException e) {
			throw new RuntimeException(e);
		} catch (SystemException e) {
			throw new RuntimeException(e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public int doItButHandleExceptionWithNewTransaction() {
		return doItButHandleException();
	}

	public int doItButHandleExceptionAndSetRollBackOnly() {
		exceptionService.doItSetRollbackOnly();
		try {
			int status = sessionContext.getUserTransaction().getStatus();
			return status;
		} catch (IllegalStateException e) {
			throw new RuntimeException(e);
		} catch (SystemException e) {
			throw new RuntimeException(e);
		}
	}

}
