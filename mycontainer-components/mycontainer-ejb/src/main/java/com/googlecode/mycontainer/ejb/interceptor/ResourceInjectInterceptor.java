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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.SessionContext;
import javax.naming.Context;


import com.googlecode.mycontainer.ejb.MySessionContext;
import com.googlecode.mycontainer.kernel.naming.MyNameParser;
import com.googlecode.mycontainer.kernel.reflect.proxy.ProxyChain;
import com.googlecode.mycontainer.kernel.reflect.proxy.Request;

public class ResourceInjectInterceptor extends ScannerInjectInterceptor {

	private static final long serialVersionUID = -5770731218040446831L;

	private EJBContext context;

	private EJBContext createEJBContext(Request request, String info) {
		Context ctx = getContext();
		MySessionContext ret = new MySessionContext(ctx, info);
		return ret;
	}

	@Override
	public Object ejbPreConstruct(Request request, ProxyChain chain)
			throws Throwable {
		this.context = createEJBContext(request, (String) request.getInfo());
		return super.ejbPreConstruct(request, chain);
	}

	private String getAnnotationName(AccessibleObject member) {
		String name = null;
		Resource annotation = member.getAnnotation(Resource.class);
		if (annotation != null) {
			name = annotation.mappedName();
			if (name != null && name.length() == 0) {
				name = annotation.name();
			}
		}
		return name;
	}

	@Override
	public Object getInjectName(Request request, Field field) throws Throwable {
		String name = getAnnotationName(field);
		if (name != null && name.length() == 0) {
			Class<?> type = field.getType();
			Object ret = checkBuiltin(type);
			if (ret != null) {
				return ret;
			}
			name = MyNameParser.parseClassName("resource", type);
		}
		if (name == null) {
			return null;
		}
		return getContext().lookup(name);
	}

	@Override
	public Object getInjectName(Request request, Method method)
			throws Throwable {
		String name = getAnnotationName(method);
		if (name != null && name.length() == 0) {
			Class<?> paramsType[] = method.getParameterTypes();
			if (paramsType.length == 1) {
				Class<?> type = paramsType[0];
				Object ret = checkBuiltin(type);
				if (ret != null) {
					return ret;
				}
				name = MyNameParser.parseClassName("resource", type);
			}
		}
		if (name == null) {
			return null;
		}
		return getContext().lookup(name);
	}

	private Object checkBuiltin(Class<?> type) {
		if (type.isAssignableFrom(SessionContext.class)) {
			return context;
		}
		return null;
	}
}
