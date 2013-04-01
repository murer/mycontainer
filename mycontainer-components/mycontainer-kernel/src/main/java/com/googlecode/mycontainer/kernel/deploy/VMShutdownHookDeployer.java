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

import com.googlecode.mycontainer.kernel.Kernel;
import com.googlecode.mycontainer.kernel.KernelRuntimeException;

public class VMShutdownHookDeployer extends Deployer implements Runnable {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(VMShutdownHookDeployer.class);

	private static final long serialVersionUID = -2754930748172142004L;

	private String kernelName = "Kernel";

	public void setKernelName(String kernelName) {
		this.kernelName = kernelName;
	}

	public void deploy() {
		LOG.info("Deploy Virtual Machine Mycontainer shutdown hook: "
				+ kernelName);
		Thread thread = new Thread(this, "MycontainerShutdownHookThread");
		Runtime.getRuntime().addShutdownHook(thread);
	}

	public void run() {
		try {
			LOG.info("Executing Virtual Machine Mycontainer shutdown hook: "
					+ kernelName);
			Context ctx = getContext();
			Kernel kernel = (Kernel) ctx.lookup(kernelName);
			kernel.shutdown();
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public void shutdown() {

	}

}
