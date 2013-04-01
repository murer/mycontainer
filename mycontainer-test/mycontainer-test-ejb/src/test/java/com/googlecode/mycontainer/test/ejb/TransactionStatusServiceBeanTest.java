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

package com.googlecode.mycontainer.test.ejb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRequiredException;
import javax.transaction.Status;

import org.junit.Test;

import com.googlecode.mycontainer.test.TransactionStatusService;

public class TransactionStatusServiceBeanTest extends AbstractTestCase {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TransactionStatusServiceBeanTest.class);

	@Test
	public void testNoTransactionResults() throws Exception {
		TransactionStatusService service = (TransactionStatusService) builder
				.getContext().lookup("TransactionStatusServiceBean/local");

		assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
		try {
			service.getMandatoryStatus();
			fail("EJBTransactionRequiredException expected...");
		} catch (EJBTransactionRequiredException e) {
		}
		assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
		assertEquals(Status.STATUS_NO_TRANSACTION, service.getNeverStatus());
		assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());

		assertEquals(Status.STATUS_NO_TRANSACTION, service
				.getNotSupportedStatus());
		assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());

		assertEquals(Status.STATUS_ACTIVE, service.getRequiredStatus());
		assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());

		assertEquals(Status.STATUS_ACTIVE, service.getRequiresNewStatus());
		assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());

		assertEquals(Status.STATUS_NO_TRANSACTION, service.getSupportsStatus());
		assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());

	}

	@Test
	public void testTransactionResults() throws Exception {
		TransactionStatusService service = (TransactionStatusService) builder
				.getContext().lookup("TransactionStatusServiceBean/local");

		assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
		tm.begin();
		try {
			assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
			assertEquals(Status.STATUS_ACTIVE, service.getMandatoryStatus());
			assertEquals(Status.STATUS_ACTIVE, tm.getStatus());

			try {
				service.getNeverStatus();
				fail("EJBException expected");
			} catch (EJBException e) {
			}

			assertEquals(Status.STATUS_ACTIVE, tm.getStatus());

			assertEquals(Status.STATUS_NO_TRANSACTION, service
					.getNotSupportedStatus());
			assertEquals(Status.STATUS_ACTIVE, tm.getStatus());

			assertEquals(Status.STATUS_ACTIVE, service.getRequiredStatus());
			assertEquals(Status.STATUS_ACTIVE, tm.getStatus());

			assertEquals(Status.STATUS_ACTIVE, service.getRequiresNewStatus());
			assertEquals(Status.STATUS_ACTIVE, tm.getStatus());

			assertEquals(Status.STATUS_ACTIVE, service.getSupportsStatus());
			assertEquals(Status.STATUS_ACTIVE, tm.getStatus());

		} finally {
			try {
				tm.commit();
				assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
			} catch (Exception e) {
				LOG.error("Error commiting...", e);
			}
		}
	}

	@Test
	public void testSetRollbackOnly() throws Exception {
		TransactionStatusService service = (TransactionStatusService) builder
				.getContext().lookup("TransactionStatusServiceBean/local");

		assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
		tm.begin();
		Integer status = null;
		try {
			assertEquals(Status.STATUS_ACTIVE, tm.getStatus());
			status = service.setRollbackOnly();
		} finally {
			assertEquals(Status.STATUS_MARKED_ROLLBACK, status);
			assertEquals(Status.STATUS_MARKED_ROLLBACK, tm.getStatus());
			tm.commit();
			assertEquals(Status.STATUS_NO_TRANSACTION, tm.getStatus());
		}
	}
}
