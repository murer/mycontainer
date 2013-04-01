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

package com.googlecode.mycontainer.kernel.reflect.proxy;

import java.lang.reflect.Method;

import com.googlecode.mycontainer.kernel.interceptors.ProxyInterceptor;


public class ReflectInterceptor implements ProxyInterceptor {

	private Request request;

	private ProxyChain chain;

	public Object intercept(Request request, ProxyChain chain) throws Throwable {
		this.request = request;
		this.chain = chain;

		Method method = request.getMethod();

		Method m = null;
		try {
			String name = method.getName();
			Class<?>[] parameterTypes = method.getParameterTypes();
			m = getClass().getDeclaredMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			return chain.proceed(request);
		}
		Object[] values = request.getValues();
		return m.invoke(this, values);
	}

	public Request getRequest() {
		return request;
	}

	public ProxyChain getChain() {
		return chain;
	}

}
