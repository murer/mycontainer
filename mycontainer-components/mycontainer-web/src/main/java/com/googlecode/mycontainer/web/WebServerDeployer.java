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

package com.googlecode.mycontainer.web;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.mycontainer.kernel.deploy.NamingDeployer;
import com.googlecode.mycontainer.web.jetty.SslConnectorInfo;

public abstract class WebServerDeployer extends NamingDeployer {

	private static final long serialVersionUID = -1823687216783L;

	private final List<ContextWebServer> webContexts = new ArrayList<ContextWebServer>();

	public abstract int bindPort(int port);

	public abstract void bindSSLPort(SslConnectorInfo info);

	public ContextWebServer createContextWebServer() {
		ContextWebServer ret = new ContextWebServer();
		webContexts.add(ret);
		return ret;
	}

	public List<ContextWebServer> getWebContexts() {
		return webContexts;
	}

	public abstract void addRealm(Realm realm);

}
