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
import java.lang.reflect.Method;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;

public class Request implements Serializable {

	private static final long serialVersionUID = -8758743331209456272L;

	private Object impl;

	private Class<Object> api;

	private Method method;

	private Object[] values;

	private Object info;

	private Object proxy;

	public Object getProxy() {
		return proxy;
	}

	public void setProxy(Object proxy) {
		this.proxy = proxy;
	}

	public Object getInfo() {
		return info;
	}

	public void setInfo(Object info) {
		this.info = info;
	}

	public Object getImpl() {
		return impl;
	}

	public void setImpl(Object impl) {
		this.impl = impl;
	}

	public Class<Object> getApi() {
		return api;
	}

	public void setApi(Class<Object> api) {
		this.api = api;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

	public Method getImplMethod() {
		return getMethod(impl);
	}

	public Method getMethod(Object impl) {
		try {
			String name = method.getName();
			Class<?>[] parameters = method.getParameterTypes();
			Method ret = impl.getClass().getMethod(name, parameters);
			return ret;
		} catch (SecurityException e) {
			throw new KernelRuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public boolean isMethodExists(Object impl) {
		try {
			String name = method.getName();
			Class<?>[] parameters = method.getParameterTypes();
			impl.getClass().getMethod(name, parameters);
			return true;
		} catch (SecurityException e) {
			throw new KernelRuntimeException(e);
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	@Override
	public String toString() {
		return "Request [api=" + api + ", method=" + method + "]";
	}

	public Request copy() {
		Request ret = new Request();
		ret.api = this.api;
		ret.impl = this.impl;
		ret.info = this.info;
		ret.method = this.method;
		ret.proxy = this.proxy;
		ret.values = this.values;
		return ret;
	}

}
