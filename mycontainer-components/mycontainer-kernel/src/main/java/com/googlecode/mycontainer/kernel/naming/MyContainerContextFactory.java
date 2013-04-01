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

package com.googlecode.mycontainer.kernel.naming;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import com.googlecode.mycontainer.kernel.Kernel;


public class MyContainerContextFactory implements InitialContextFactory {

	public static final String CONTAINER_PARTITION = "com.mycontainer.kernel.naming.partition";

	private static final Map<String, Kernel> containers = new HashMap<String, Kernel>();

	private static final Object MUTEX = new Object();

	@SuppressWarnings("unchecked")
	public Context getInitialContext(Hashtable<?, ?> env)
			throws NamingException {
		String partition = (String) env.get(CONTAINER_PARTITION);
		if (partition == null) {
			partition = "default";
		}

		synchronized (MUTEX) {
			Kernel container = containers.get(partition);
			if (container == null) {
				container = new Kernel(partition);
				container.boot((Map<String, Object>) env);
				containers.put(partition, container);
			}
			MyContainerContext context = container.getContext();
			return context;
		}
	}

	public static void removeContainer(String name) {
		synchronized (MUTEX) {
			containers.remove(name);
		}
	}

}
