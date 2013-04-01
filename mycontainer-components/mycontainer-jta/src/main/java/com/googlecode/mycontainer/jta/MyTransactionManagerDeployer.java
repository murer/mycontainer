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

package com.googlecode.mycontainer.jta;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;
import com.googlecode.mycontainer.kernel.deploy.DeployException;
import com.googlecode.mycontainer.kernel.deploy.Deployer;
import com.googlecode.mycontainer.kernel.deploy.NamingAliasDeployer;
import com.googlecode.mycontainer.kernel.naming.MyNameParser;

public class MyTransactionManagerDeployer extends Deployer {

	private static final long serialVersionUID = 1L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MyTransactionManagerDeployer.class);

	private String name;

	private String userTransactionName = MyNameParser.parseClassName("resource", UserTransaction.class);

	public String getUserTransactionName() {
		return userTransactionName;
	}

	public void setUserTransactionName(String userTransactionName) {
		this.userTransactionName = userTransactionName;
	}

	public String getName() {
		return name;
	}

	public MyTransactionManagerDeployer setName(String name) {
		this.name = name;
		return this;
	}

	public void deploy() {
		try {
			Context ctx = getContext();
			LOG.info("Deploying: " + name);
			TransactionManager tm = getTransactionManager();
			UserTransaction ut = new MyUserTransaction(tm);
			ctx.bind(name, tm);
			ctx.bind(userTransactionName, ut);
			getKernel().addShutdownHook(this);

			String alias = MyNameParser.parseClassName("resource", UserTransaction.class);
			if (!userTransactionName.equals(alias)) {
				new NamingAliasDeployer(ctx, alias, userTransactionName).deploy();
			}
		} catch (NamingException e) {
			throw new DeployException(e);
		}
	}

	public void shutdown() {
		try {
			Context ctx = getContext();
			LOG.info("Undeploying: " + userTransactionName);
			ctx.unbind(userTransactionName);
			LOG.info("Undeploying: " + name);
			ctx.unbind(name);
		} catch (NamingException e) {
			throw new DeployException(e);
		}

	}

	private TransactionManager getTransactionManager() {
		try {
			Context ctx = getContext();
			MyTransactionManager ret = new MyTransactionManager(ctx.getEnvironment());
			return ret;
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

}
