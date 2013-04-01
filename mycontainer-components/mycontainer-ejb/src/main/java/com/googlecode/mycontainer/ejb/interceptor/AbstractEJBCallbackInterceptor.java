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

import java.lang.reflect.Method;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;
import com.googlecode.mycontainer.kernel.reflect.proxy.ContextInterceptor;
import com.googlecode.mycontainer.kernel.reflect.proxy.ProxyChain;
import com.googlecode.mycontainer.kernel.reflect.proxy.Request;

public abstract class AbstractEJBCallbackInterceptor extends ContextInterceptor {

	private static final long serialVersionUID = -8847984975527874386L;

	public Object intercept(Request request, ProxyChain chain) throws Throwable {
		if (!request.getMethod().getName().startsWith("ejb")) {
			return interceptBusiness(request, chain);
		}
		Method method = getMethod(request);
		if (method == null) {
			return interceptBusiness(request, chain);
		}
		Object ret = method.invoke(this, request, chain);
		return ret;
	}

	public abstract Object interceptBusiness(Request request, ProxyChain chain)
			throws Throwable;

	private Method getMethod(Request request) {
		try {
			Method method = request.getMethod();
			String name = method.getName();
			Method ret = this.getClass().getMethod(name, Request.class,
					ProxyChain.class);
			return ret;
		} catch (SecurityException e) {
			throw new KernelRuntimeException(e);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

}
