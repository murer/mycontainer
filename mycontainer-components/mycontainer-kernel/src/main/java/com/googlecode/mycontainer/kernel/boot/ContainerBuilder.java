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

package com.googlecode.mycontainer.kernel.boot;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.googlecode.mycontainer.kernel.Kernel;
import com.googlecode.mycontainer.kernel.KernelRuntimeException;
import com.googlecode.mycontainer.kernel.deploy.Deployer;
import com.googlecode.mycontainer.kernel.deploy.VMShutdownHookDeployer;

public class ContainerBuilder {

	private final InitialContext ctx;

	public ContainerBuilder() throws NamingException {
		this(new InitialContext());
	}

	public ContainerBuilder(Properties props) throws NamingException {
		this(new InitialContext(props));
	}

	public ContainerBuilder(InitialContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * This is deprecated! You should do
	 * 'containerBuilder.createDeployer(MyTransactionManagerDeployer.class).setName("TransactionManager").deploy();
	 * ' Add a dependency to 'mycontainer-jta'
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void deployJTA() {
		throw new RuntimeException(
				"This is deprecated! You should do 'containerBuilder.createDeployer(MyTransactionManagerDeployer.class).setName(\"TransactionManager\").deploy();'. Add a dependency to 'mycontainer-jta'");
	}

	public void deployVMShutdownHook() {
		VMShutdownHookDeployer deployer = createDeployer(VMShutdownHookDeployer.class);
		deployer.deploy();
	}

	@SuppressWarnings("unchecked")
	public <T extends Deployer> T createDeployer(String deployer) {
		try {
			Class<T> clazz = (Class<T>) Class.forName(deployer);
			return createDeployer(clazz);
		} catch (ClassNotFoundException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public <T extends Deployer> T createDeployer(Class<T> deployer) {
		try {
			T ret = deployer.newInstance();
			ret.setContext(ctx);
			return ret;
		} catch (InstantiationException e) {
			throw new KernelRuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public InitialContext getContext() {
		return ctx;
	}

	public void waitFor() {
		try {
			Kernel kernel = (Kernel) ctx.lookup("Kernel");
			kernel.waitFor();
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}
}
