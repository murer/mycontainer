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

import java.lang.reflect.Method;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.Context;
import javax.transaction.TransactionManager;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;
import com.googlecode.mycontainer.kernel.reflect.proxy.ContextInterceptor;
import com.googlecode.mycontainer.kernel.reflect.proxy.Request;


public abstract class AbstractTransactionHandler extends ContextInterceptor {

	private TransactionManager transactionManager;

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	private void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	private static TransactionAttributeType getTransactionAttributeType(
			Request request) {
		Method method = request.getImplMethod();
		TransactionAttribute annotation = method
				.getAnnotation(TransactionAttribute.class);
		if (annotation == null) {
			Object impl = request.getImpl();
			annotation = impl.getClass().getAnnotation(
					TransactionAttribute.class);
		}
		return (annotation == null ? TransactionAttributeType.REQUIRED
				: annotation.value());
	}

	private static AbstractTransactionHandler createTransactionHandler(
			TransactionAttributeType type) {
		switch (type) {
		case MANDATORY:
			return new MandatoryTransactionHandler();
		case NEVER:
			return new NeverTransactionHandler();
		case NOT_SUPPORTED:
			return new NotSupportedTransactionHandler();
		case REQUIRED:
			return new RequiredTransactionHandler();
		case REQUIRES_NEW:
			return new RequiresNewTransactionHandler();
		case SUPPORTS:
			return new SupportsTransactionHandler();
		}

		throw new KernelRuntimeException(
				"TransactionAttributeType not supported: " + type);
	}

	public static AbstractTransactionHandler getTransactionHandler(
			Request request, Context ctx, TransactionManager tm) {
		TransactionAttributeType type = getTransactionAttributeType(request);
		AbstractTransactionHandler ret = createTransactionHandler(type);
		ret.setContext(ctx);
		ret.setTransactionManager(tm);
		return ret;

	}

}
