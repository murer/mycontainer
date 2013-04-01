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

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.mycontainer.kernel.interceptors.ProxyInterceptor;

public class ProxyEngine<T> implements InvocationHandler, Serializable {

	private static final long serialVersionUID = 5953476982797780990L;

	private final List<ProxyInterceptor> interceptors = new ArrayList<ProxyInterceptor>();

	private final Class<T> api;

	private final T impl;

	private T proxy;

	private Object info;

	private final List<Class<?>> interfaces = new ArrayList<Class<?>>();

	public ProxyEngine(Class<T> api, T impl) {
		this(api, impl, null);
	}

	public ProxyEngine(Class<T> api, T impl, Object info) {
		this.api = api;
		this.impl = impl;
		this.info = info;
	}

	public void addInterceptor(ProxyInterceptor interceptor) {
		this.interceptors.add(interceptor);
	}

	public T getProxy() {
		return proxy;
	}

	public void setProxy(T proxy) {
		this.proxy = proxy;
	}

	@SuppressWarnings("unchecked")
	public T create() {
		Class[] apis = new Class[interfaces.size() + 1];
		apis = interfaces.toArray(apis);
		apis[interfaces.size()] = api;
		proxy = (T) Proxy.newProxyInstance(api.getClassLoader(), apis, this);
		return proxy;
	}

	@SuppressWarnings("unchecked")
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		ProxyChainImpl chain = new ProxyChainImpl(interceptors.iterator());
		Request request = new Request();
		request.setApi((Class<Object>) api);
		request.setImpl(impl);
		request.setMethod(method);
		request.setValues(args);
		request.setInfo(info);
		request.setProxy(proxy);

		Object ret = chain.proceed(request);
		return ret;
	}

	public T getImpl() {
		return impl;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getImpl(T proxy) {
		ProxyEngine<T> engine = (ProxyEngine<T>) Proxy
				.getInvocationHandler(proxy);
		return engine.getImpl();
	}

	public void addInterface(Class<?> clazz) {
		this.interfaces.add(clazz);
	}

}
