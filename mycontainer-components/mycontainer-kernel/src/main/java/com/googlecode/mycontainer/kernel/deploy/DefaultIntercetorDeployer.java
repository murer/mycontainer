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

package com.googlecode.mycontainer.kernel.deploy;

import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;
import com.googlecode.mycontainer.kernel.interceptors.AbstractIntercetorDeployer;
import com.googlecode.mycontainer.kernel.reflect.proxy.ContextInterceptor;


public class DefaultIntercetorDeployer extends AbstractIntercetorDeployer {

	private static final long serialVersionUID = -6799619112352155878L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultIntercetorDeployer.class);

	private String name;

	private final List<Class<ContextInterceptor>> interceptors = new ArrayList<Class<ContextInterceptor>>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ContextInterceptor> void addContextInterceptor(
			Class<T> interceptor) {
		interceptors.add((Class<ContextInterceptor>) interceptor);
	}

	@Override
	public <T extends ContextInterceptor> void removeContextInterceptor(
			Class<T> interceptor) {
		interceptors.remove(interceptor);
	}

	public List<ContextInterceptor> createInterceptors() {
		try {
			Context ctx = getContext();
			List<ContextInterceptor> ret = new ArrayList<ContextInterceptor>();
			for (Class<ContextInterceptor> clazz : interceptors) {
				ContextInterceptor interceptor = clazz.newInstance();
				interceptor.setContext(ctx);
				ret.add(interceptor);
			}
			return ret;
		} catch (InstantiationException e) {
			throw new KernelRuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public void shutdown() {
		try {
			Context ctx = getContext();
			LOG.info("Undeploying: " + name);
			ctx.unbind(name);
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public void deploy() {
		try {
			Context ctx = getContext();
			LOG.info("Deploying: " + name);
			ctx.bind(name, this);
			getKernel().addShutdownHook(this);
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

}
