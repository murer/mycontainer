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

package com.googlecode.mycontainer.web.jetty;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class JettyRequestLogImpl extends AbstractLifeCycle implements
		RequestLog {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(JettyRequestLogImpl.class);

	public void log(Request request, Response response) {
		if (LOG.isInfoEnabled()) {
			String remoteAddr = request.getRemoteAddr();
			String method = request.getMethod();
			String protocol = request.getProtocol();
			StringBuffer url = request.getRequestURL();

			LOG.info("Request: " + protocol + " " + method + " " + remoteAddr
					+ " " + url);
		}
	}

}
