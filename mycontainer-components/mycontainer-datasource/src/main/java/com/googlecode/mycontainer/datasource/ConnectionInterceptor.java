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

package com.googlecode.mycontainer.datasource;

import java.lang.reflect.Proxy;

import com.googlecode.mycontainer.kernel.reflect.proxy.ProxyEngine;
import com.googlecode.mycontainer.kernel.reflect.proxy.ReflectInterceptor;

public class ConnectionInterceptor extends ReflectInterceptor {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(ConnectionInterceptor.class);

	public void close() {
		LOG.trace("Ignoring close call");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !Proxy.isProxyClass(obj.getClass())) {
			return false;
		}

		Object impl = ProxyEngine.getImpl(obj);
		Object my = getRequest().getImpl();
		return my.equals(impl);
	}

}
