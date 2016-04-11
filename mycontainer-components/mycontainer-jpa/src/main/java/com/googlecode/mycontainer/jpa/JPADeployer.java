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

package com.googlecode.mycontainer.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import com.googlecode.mycontainer.jta.TxEntry;
import com.googlecode.mycontainer.kernel.KernelRuntimeException;
import com.googlecode.mycontainer.kernel.ShutdownHook;
import com.googlecode.mycontainer.kernel.deploy.DeployException;
import com.googlecode.mycontainer.kernel.deploy.Deployer;
import com.googlecode.mycontainer.kernel.deploy.SimpleDeployer;
import com.googlecode.mycontainer.kernel.interceptors.AbstractIntercetorDeployer;
import com.googlecode.mycontainer.kernel.naming.ObjectProvider;

public abstract class JPADeployer extends Deployer implements ShutdownHook,
		ObjectProvider, SimpleDeployer {

	private static final long serialVersionUID = -5739685369847277589L;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(JPADeployer.class);

	private String transcationManagerName = "TransactionManager";

	private PersistenceUnitInfo info = new JPAInfoBuilder();

	private EntityManagerFactory emf;

	private String sessionInterceptorDeployer = "StatelessDeployer/intercetorDeployerName";

	public String getSessionInterceptorDeployer() {
		return sessionInterceptorDeployer;
	}

	public void setSessionInterceptorDeployer(String sessionInterceptorDeployer) {
		this.sessionInterceptorDeployer = sessionInterceptorDeployer;
	}

	public String getTranscationManagerName() {
		return transcationManagerName;
	}

	public void setTranscationManagerName(String transcationManagerName) {
		this.transcationManagerName = transcationManagerName;
	}

	public String getEntityManagerName() {
		String name = info.getPersistenceUnitName();
		return name;
	}

	public PersistenceUnitInfo getInfo() {
		((JPAInfoBuilder) this.info).setContext(getContext());
		return info;
	}

	public void setInfo(PersistenceUnitInfo info) {
		this.info = info;
	}

	public void deploy() {
		overrrideConfig(info);
		TransactionManager tm = null;
		try {
			Context ctx = getContext();

			AbstractIntercetorDeployer interceptorDeployer = (AbstractIntercetorDeployer) ctx
					.lookup(sessionInterceptorDeployer);
			interceptorDeployer
					.addContextInterceptor(PersistenceContextInterceptor.class);

			String name = getEntityManagerName();
			LOG.info("Deploying: " + name);
			PersistenceProvider conf = getPersistenceProvider();

			tm = getTransactionManager(info);
			tm.begin();
			emf = conf.createContainerEntityManagerFactory(info, null);
			ctx.bind(name, this);
			getKernel().addShutdownHook(this);
			tm.commit();
		} catch (RuntimeException e) {
			if (tm != null) {
				try {
					tm.rollback();
				} catch (Exception e1) {
					LOG.error("Error rollback", e1);
				}
			}
			throw e;
		} catch (Exception e) {
			if (tm != null) {
				try {
					tm.rollback();
				} catch (Exception e1) {
					LOG.error("Error rollback", e1);
				}
			}
			throw new DeployException(e);
		}
	}

	protected abstract PersistenceProvider getPersistenceProvider();

	protected abstract void overrrideConfig(PersistenceUnitInfo info);

	protected abstract TransactionManager getTransactionManager(
			PersistenceUnitInfo info);

	public void shutdown() {
		TransactionManager tm = null;
		try {
			tm = getTransactionManager(info);
			tm.begin();
			Context ctx = getContext();
			String name = getEntityManagerName();
			LOG.info("Undeploying: " + name);
			emf.close();
			ctx.unbind(name);
			tm.commit();
		} catch (RuntimeException e) {
			try {
				tm.rollback();
			} catch (Exception e1) {
				LOG.error("Error rollback", e1);
			}
			throw e;
		} catch (Exception e) {
			try {
				tm.rollback();
			} catch (Exception e1) {
				LOG.error("Error rollback", e1);
			}
			throw new DeployException(e);
		}
	}

	public Object provide(Name name) {
		try {
			TransactionManager tm = getTransactionManager(info);
			if (tm.getStatus() == Status.STATUS_NO_TRANSACTION) {
				return emf.createEntityManager();
			}

			Transaction tx = tm.getTransaction();
			TxEntry entry = new TxEntry(tx, getEntityManagerName());
			Map<TxEntry, EntityManager> ems = lookupEntityManagers();
			EntityManager ret = ems.get(entry);
			if (ret != null) {
				return ret;
			}

			ret = emf.createEntityManager();
			ems.put(entry, ret);

			return ret;
		} catch (SystemException e) {
			throw new KernelRuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<TxEntry, EntityManager> lookupEntityManagers() {
		Context ctx = getContext();
		try {
			try {
				Map<TxEntry, EntityManager> ret = (Map<TxEntry, EntityManager>) ctx
						.lookup("tl/jpa/persistences");
				return ret;
			} catch (NameNotFoundException e) {
				Map<TxEntry, EntityManager> ret = new HashMap<TxEntry, EntityManager>();
				ctx.bind("tl/jpa/persistences", ret);
				return ret;
			}
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	@Override
	public void setContext(Context context) {
		super.setContext(context);
		((JPAInfoBuilder) this.info).setContext(context);
	}

}
