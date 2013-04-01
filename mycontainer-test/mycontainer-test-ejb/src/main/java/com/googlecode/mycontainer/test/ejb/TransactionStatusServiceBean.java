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

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.googlecode.mycontainer.test.TransactionStatusService;


@Stateless
public class TransactionStatusServiceBean implements TransactionStatusService {

	@Resource
	private SessionContext ctx;

	private Integer getStatus() {
		try {
			UserTransaction userTransaction = ctx.getUserTransaction();
			return userTransaction.getStatus();
		} catch (SystemException e) {
			throw new RuntimeException(e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public Integer getMandatoryStatus() {
		return getStatus();
	}

	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Integer getNeverStatus() {
		return getStatus();
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Integer getNotSupportedStatus() {
		return getStatus();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer getRequiredStatus() {
		return getStatus();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Integer getRequiresNewStatus() {
		return getStatus();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Integer getSupportsStatus() {
		return getStatus();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer setRollbackOnly() {
		ctx.setRollbackOnly();
		return getStatus();
	}

}
