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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.mycontainer.kernel.reflect.ReflectUtil;
import com.googlecode.mycontainer.kernel.reflect.proxy.ProxyChain;
import com.googlecode.mycontainer.kernel.reflect.proxy.Request;

@SuppressWarnings("serial")
public abstract class ScannerInjectInterceptor extends
		AbstractEJBCallbackInterceptor {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(ScannerInjectInterceptor.class);

	private void clean(Request request, List<Field> updates,
			List<Method> invokeds) {
		Object impl = request.getImpl();
		for (Field field : updates) {
			ReflectUtil.setField(field, impl, null);
		}
		for (Method method : invokeds) {
			ReflectUtil.invokeMethod(method, impl, new Object[] { null });
		}
	}

	public abstract Object getInjectName(Request request, Field field)
			throws Throwable;

	public abstract Object getInjectName(Request request, Method method)
			throws Throwable;

	public Object ejbPreConstruct(Request request, ProxyChain chain)
			throws Throwable {
		Object impl = request.getImpl();
		ReflectUtil util = new ReflectUtil(impl.getClass());
		List<Field> fields = util.getFields();
		List<Field> updates = new ArrayList<Field>(fields.size());

		for (Field field : fields) {
			Object value = getInjectName(request, field);
			if (value != null) {
				ReflectUtil.setField(field, impl, value);
				updates.add(field);
			}
		}

		List<Method> methods = util.getMethods();
		List<Method> invokeds = new ArrayList<Method>(methods.size());

		for (Method method : methods) {
			Object value = getInjectName(request, method);
			if (value != null) {
				ReflectUtil.invokeMethod(method, impl, new Object[] { value });
				invokeds.add(method);
			}
		}

		return chain.proceed(request);
	}

	@Override
	public Object interceptBusiness(Request request, ProxyChain chain)
			throws Throwable {
		return chain.proceed(request);
	}

}
