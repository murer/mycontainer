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

package com.googlecode.mycontainer.ejb.transaction;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import com.googlecode.mycontainer.kernel.reflect.proxy.ProxyChain;
import com.googlecode.mycontainer.kernel.reflect.proxy.Request;

public class NotSupportedTransactionHandler extends AbstractTransactionHandler {

	private static final long serialVersionUID = 157787137427634531L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(NotSupportedTransactionHandler.class);

	public Object intercept(Request request, ProxyChain chain) throws Throwable {
		TransactionManager tm = getTransactionManager();
		Transaction tx = tm.suspend();
		try {
			return chain.proceed(request);
		} finally {
			if (tx != null) {
				try {
					tm.resume(tx);
				} catch (Exception e1) {
					LOG.error("Error on finally. It will not raise", e1);
				}
			}
		}
	}
}
