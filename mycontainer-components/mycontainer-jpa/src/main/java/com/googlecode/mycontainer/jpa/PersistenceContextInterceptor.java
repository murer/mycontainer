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

package com.googlecode.mycontainer.jpa;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.naming.Context;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.googlecode.mycontainer.kernel.reflect.ReflectUtil;
import com.googlecode.mycontainer.kernel.reflect.proxy.ContextInterceptor;
import com.googlecode.mycontainer.kernel.reflect.proxy.ProxyChain;
import com.googlecode.mycontainer.kernel.reflect.proxy.Request;

public class PersistenceContextInterceptor extends ContextInterceptor {

	private static final long serialVersionUID = -1982454746385905074L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PersistenceContextInterceptor.class);

	public Object intercept(Request request, ProxyChain chain) throws Throwable {

		Context ctx = getContext();
		Object impl = request.getImpl();
		ReflectUtil util = new ReflectUtil(impl.getClass());
		List<Field> fields = util.getFields(PersistenceContext.class);
		List<Method> methods = util.getMethods(PersistenceContext.class);

		for (Field field : fields) {
			PersistenceContext annotation = field.getAnnotation(PersistenceContext.class);
			String unitName = annotation.unitName();
			if (LOG.isTraceEnabled()) {
				LOG.trace("Injecting EntityManager: " + unitName + " on " + request.getImpl().getClass().getName());
			}
			EntityManager em = (EntityManager) ctx.lookup(unitName);
			ReflectUtil.setField(field, impl, em);
		}
		
		for (Method method : methods) {
			PersistenceContext annotation = method.getAnnotation(PersistenceContext.class);
			String unitName = annotation.unitName();
			if (LOG.isTraceEnabled()) {
				LOG.trace("Injecting EntityManager: " + unitName + " on " + request.getImpl().getClass().getName());
			}
			EntityManager em = (EntityManager) ctx.lookup(unitName);
			ReflectUtil.invokeMethod(method, impl, em);
		}
		return chain.proceed(request);
	}
}
