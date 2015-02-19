package com.googlecode.mycontainer.test.ejb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.ejb.EJBException;
import javax.transaction.Status;

import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.mycontainer.test.ExceptionHandlerService;

public class ExceptionHandlerServiceBeanTest extends AbstractTestCase {

	@Test
	public void testExceptionHandling() throws Exception {
		ExceptionHandlerService service = lookupEJB(ExceptionHandlerService.class);
		int transactionStatus = service.doItButHandleException();
		assertEquals(Status.STATUS_ACTIVE, transactionStatus);
	}

	@Test
	public void testRuntimeExceptionHandling() throws Exception {
		ExceptionHandlerService service = lookupEJB(ExceptionHandlerService.class);
		try {
			int transactionStatus = service.doItButHandleRuntimeException();
			fail("tx: " + transactionStatus);
		} catch (EJBException e) {
			assertEquals(RuntimeException.class.getName(), e.getCause().getClass().getName());
		}
	}

	@Test
	@Ignore
	public void testExceptionHandlingAndRollback() throws Exception {
		ExceptionHandlerService service = lookupEJB(ExceptionHandlerService.class);
		int transactionStatus = service.doItButHandleExceptionAndRollBack();
		assertEquals(Status.STATUS_MARKED_ROLLBACK, transactionStatus);
	}

	@Test
	public void testExceptionHandlingAndSetRollbackOnly() throws Exception {
		ExceptionHandlerService service = lookupEJB(ExceptionHandlerService.class);
		int transactionStatus = service
				.doItButHandleExceptionAndSetRollBackOnly();
		assertEquals(Status.STATUS_MARKED_ROLLBACK, transactionStatus);
	}

	@Test
	public void testExceptionHandlingRequiresNewTransaction() throws Exception {
		ExceptionHandlerService service = lookupEJB(ExceptionHandlerService.class);
		int transactionStatus = service
				.doItButHandleExceptionWithNewTransaction();
		assertEquals(Status.STATUS_ACTIVE, transactionStatus);
	}

}
