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

package com.googlecode.mycontainer.ejb.interceptor;

import javax.naming.Context;
import javax.transaction.TransactionManager;


import com.googlecode.mycontainer.ejb.transaction.AbstractTransactionHandler;
import com.googlecode.mycontainer.kernel.reflect.proxy.ContextInterceptor;
import com.googlecode.mycontainer.kernel.reflect.proxy.ProxyChain;
import com.googlecode.mycontainer.kernel.reflect.proxy.Request;


public class TransactionInterceptor extends ContextInterceptor {

	private static final long serialVersionUID = -8609380155494213913L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TransactionInterceptor.class);

	private String transactionManagerName = "TransactionManager";

	public String getTransactionManagerName() {
		return transactionManagerName;
	}

	public void setTransactionManagerName(String transactionManagerName) {
		this.transactionManagerName = transactionManagerName;
	}

	public Object intercept(Request request, ProxyChain chain) throws Throwable {
		Context ctx = getContext();
		TransactionManager tm = (TransactionManager) ctx
				.lookup(transactionManagerName);
		AbstractTransactionHandler handler = AbstractTransactionHandler
				.getTransactionHandler(request, ctx, tm);
		if (LOG.isDebugEnabled()) {
			LOG.debug("TransactionHandler: "
					+ (handler == null ? "null" : handler.getClass()));
		}
		return handler.intercept(request, chain);
	}
}
