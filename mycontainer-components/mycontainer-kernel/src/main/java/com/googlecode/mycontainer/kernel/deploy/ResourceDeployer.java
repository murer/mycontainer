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

import javax.naming.Context;
import javax.naming.NamingException;

public class ResourceDeployer extends NamingDeployer {

	private static final long serialVersionUID = 2591585000768151201L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ResourceDeployer.class);

	private Object resource;

	public Object getResource() {
		return resource;
	}

	public void setResource(Object resource) {
		this.resource = resource;
	}

	public void deploy() {
		try {
			Context ctx = getContext();
			String name = getName();
			LOG.info("Deploying: " + name);
			ctx.bind(name, resource);
		} catch (NamingException e) {
			throw new DeployException(e);
		}
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
