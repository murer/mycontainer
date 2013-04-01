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

package com.googlecode.mycontainer.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import com.googlecode.mycontainer.kernel.naming.MyContainerContext;
import com.googlecode.mycontainer.kernel.naming.MyContainerContextFactory;
import com.googlecode.mycontainer.kernel.naming.ThreadLocalObjectProvider;

public class Kernel implements Serializable {

	private static final long serialVersionUID = -9088221853026134701L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Kernel.class);

	private MyContainerContext context;

	private Map<Object, Object> env;

	private final String name;

	private final List<ShutdownHook> hooks = new ArrayList<ShutdownHook>();

	private final Object mutex = new Object();

	public Kernel(String name) {
		this.name = name;
	}

	public void boot(Map<String, Object> env) {
		try {
			LOG.info("Booting up Container " + name);
			LOG.debug("Booting up Container on " + getClass().getClassLoader());
			this.env = new Hashtable<Object, Object>(env);
			LOG.debug("Booting up JNDI");
			context = new MyContainerContext(env);
			context.bind("Kernel", this);
			context.bind("tl", new ThreadLocalObjectProvider());
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public void shutdown() {
		LOG.info("Shutting down contaier " + name);
		while (!hooks.isEmpty()) {
			ShutdownHook hook = hooks.remove(hooks.size() - 1);
			hook.shutdown();
		}
		MyContainerContextFactory.removeContainer(name);
		LOG.info("Done");
		synchronized (mutex) {
			mutex.notifyAll();
		}
	}

	public MyContainerContext getContext() {
		return context;
	}

	public void setContext(MyContainerContext context) {
		this.context = context;
	}

	public Map<Object, Object> getEnv() {
		return env;
	}

	public void addShutdownHook(ShutdownHook hook) {
		hooks.add(hook);
	}

	public void waitFor() {
		try {
			LOG.info("Waiting for kernel shutdown");
			synchronized (mutex) {
				mutex.wait();
			}
		} catch (InterruptedException e) {
			throw new KernelRuntimeException(e);
		}
	}

}
