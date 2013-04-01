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

package com.googlecode.mycontainer.ejb;

import java.io.Serializable;

import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.xml.rpc.handler.MessageContext;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;


public class MySessionContext extends MyEJBContext implements SessionContext,
		Serializable {

	private static final long serialVersionUID = 4325436547632435L;

	public MySessionContext(Context ctx, String info) {
		super(ctx, info);
	}

	public <T> T getBusinessObject(Class<T> arg0) throws IllegalStateException {
		throw new KernelRuntimeException("not supported");
	}

	public EJBLocalObject getEJBLocalObject() throws IllegalStateException {
		throw new KernelRuntimeException("not supported");
	}

	public EJBObject getEJBObject() throws IllegalStateException {
		throw new KernelRuntimeException("not supported");
	}

	public Class<?> getInvokedBusinessInterface() throws IllegalStateException {
		throw new KernelRuntimeException("not supported");
	}

	public MessageContext getMessageContext() throws IllegalStateException {
		throw new KernelRuntimeException("not supported");
	}

}
