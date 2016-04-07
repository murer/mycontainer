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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.mycontainer.kernel.ShutdownHook;

public abstract class NamingDeployer extends Deployer implements ShutdownHook, SimpleDeployer {

	private static final long serialVersionUID = -7089079079467512034L;

	private static final Logger LOG = LoggerFactory
			.getLogger(NamingDeployer.class);

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void deploy() {
		try {
			Context ctx = getContext();
			LOG.info("Deploying: " + name);
			ctx.bind(name, getResource());
			getKernel().addShutdownHook(this);
		} catch (NamingException e) {
			throw new DeployException(e);
		}
	}

	protected abstract Object getResource();

	public void shutdown() {
		try {
			Context ctx = getContext();
			LOG.info("Undeploying: " + name);
			ctx.unbind(name);
		} catch (NamingException e) {
			throw new DeployException(e);
		}

	}

}
