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
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;


import com.googlecode.mycontainer.annotation.MycontainerLocalBinding;
import com.googlecode.mycontainer.kernel.deploy.DefaultIntercetorDeployer;
import com.googlecode.mycontainer.kernel.deploy.DeployException;
import com.googlecode.mycontainer.kernel.deploy.NamingAliasDeployer;
import com.googlecode.mycontainer.kernel.naming.MyNameParser;
import com.googlecode.mycontainer.kernel.naming.ObjectProvider;
import com.googlecode.mycontainer.kernel.reflect.proxy.ContextInterceptor;
import com.googlecode.mycontainer.kernel.reflect.proxy.ProxyEngine;

public class StatelessDeployer extends SessionBeanDeployer implements
		ObjectProvider {

	private static final long serialVersionUID = 6140595848352213485L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(StatelessDeployer.class);

	private Class<Object> resource;

	private Class<Object> api;

	private String intercetorDeployerName = SessionInterceptorDeployer.DEFAULT_NAME;

	public String getIntercetorDeployerName() {
		return intercetorDeployerName;
	}

	public void setIntercetorDeployerName(String intercetorDeployerName) {
		this.intercetorDeployerName = intercetorDeployerName;
	}

	public void config(Class<Object> resource) {
		Stateless stateless = resource.getAnnotation(Stateless.class);
		if (stateless == null) {
			throw new DeployException("@Stateless not found");
		}
		this.resource = resource;
		resolveInterface();

	}

	@SuppressWarnings("unchecked")
	private void resolveInterface() {
		this.api = null;
		resolveBeanLocalInterface();
		if (this.api != null) {
			return;
		}
		List<Class<?>> interfaces = getInterfaces();
		if (interfaces.isEmpty()) {
			throw new DeployException(
					"The bean must need some interface or @Local: "
							+ resource.getName());
		}
		if (interfaces.size() == 1) {
			this.api = (Class<Object>) interfaces.get(0);
			return;
		}
		int i = 0;
		while (i < interfaces.size()) {
			Class<?> clazz = interfaces.get(i);
			if (clazz.getAnnotation(Local.class) == null) {
				interfaces.remove(i);
			} else {
				i++;
			}
		}
		if (interfaces.isEmpty()) {
			throw new DeployException(
					"Multiples interfaces, all without @Local: "
							+ resource.getName());
		}
		if (interfaces.size() > 1) {
			throw new DeployException("Multiples interfaces with @Local: "
					+ resource.getName());
		}
		this.api = (Class<Object>) interfaces.get(0);
	}

	private List<Class<?>> getInterfaces() {
		Class<?>[] interfaces = resource.getInterfaces();
		List<Class<?>> ret = new ArrayList<Class<?>>();
		for (Class<?> clazz : interfaces) {
			if (isValidInterface(clazz)) {
				ret.add(clazz);
			}
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private void resolveBeanLocalInterface() {
		Local local = resource.getAnnotation(Local.class);
		if (local != null) {
			if (local.value() == null || local.value().length == 0) {
				throw new RuntimeException(
						"@Local used in the bean need one argument");
			}
			this.api = local.value()[0];
		}
	}

	private boolean isValidInterface(Class<?> clazz) {
		boolean ret = !Serializable.class.isAssignableFrom(clazz)
				&& !Cloneable.class.isAssignableFrom(clazz);
		if (!ret) {
			return ret;
		}
		ret = !clazz.getName().startsWith("javax.ejb.");
		return ret;
	}

	public Object provide(Name name) {
		try {
			Context ctx = getContext();
			Object impl = resource.newInstance();
			ProxyEngine<Object> engine = new ProxyEngine<Object>(api, impl,
					getSessionContextName());
			engine.addInterface(StatelessCallback.class);

			DefaultIntercetorDeployer interceptors = (DefaultIntercetorDeployer) ctx
					.lookup(intercetorDeployerName);
			List<ContextInterceptor> list = interceptors.createInterceptors();
			for (ContextInterceptor contextInterceptor : list) {
				engine.addInterceptor(contextInterceptor);
			}

			StatelessCallback ret = (StatelessCallback) engine.create();
			ret.ejbPreConstruct();
			ret.ejbPostConstruct();

			return ret;
		} catch (InstantiationException e) {
			throw new DeployException(e);
		} catch (IllegalAccessException e) {
			throw new DeployException(e);
		} catch (NamingException e) {
			throw new DeployException(e);
		}
	}

	public String getName() {
		String name = resource.getSimpleName() + "/local";
		MycontainerLocalBinding localBinding = resource
				.getAnnotation(MycontainerLocalBinding.class);
		if (localBinding != null) {
			name = localBinding.value();
		}
		return name;
	}

	public static boolean isStateless(Class<?> clazz) {
		return (clazz.getAnnotation(Stateless.class) != null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deploy(Class<?> resource) {
		try {
			Context ctx = getContext();

			config((Class<Object>) resource);
			String name = getName();
			LOG.info("Deploying: " + name + " " + resource.getSimpleName());
			String sessionContextName = getSessionContextName();
			ctx.createSubcontext(sessionContextName);
			ctx.bind(name, this);
			getKernel().addShutdownHook(this);

			String alias = MyNameParser.parseClassName("ejb", api);
			if (!name.equals(alias)) {
				new NamingAliasDeployer(ctx, alias, name).deploy();
			}

		} catch (NamingException e) {
			throw new DeployException(e);
		}
	}

	private String getSessionContextName() {
		String name = getName();
		return "sessionContext/" + name;
	}

	public void shutdown() {
		try {
			Context ctx = getContext();
			String name = getName();
			LOG.info("Undeploying: " + name);
			ctx.unbind(name);
		} catch (NamingException e) {
			throw new DeployException(e);
		}
	}

}
