package com.googlecode.mycontainer.jpa;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.transaction.TransactionManager;

import org.hibernate.cfg.Environment;
import org.hibernate.ejb.HibernatePersistence;
import org.hibernate.transaction.TransactionManagerLookup;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;

public class HibernateJPADeployer extends JPADeployer {

	private static final long serialVersionUID = -6535646563439755792L;

	@Override
	protected PersistenceProvider getPersistenceProvider() {
		return new HibernatePersistence();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void overrrideConfig(PersistenceUnitInfo info) {
		try {
			Properties props = info.getProperties();

			Map<String, String> ctxEnv = (Map<String, String>) getContext()
					.getEnvironment();
			for (Entry<String, String> entry : ctxEnv.entrySet()) {
				if (entry.getKey().equals(Context.INITIAL_CONTEXT_FACTORY)) {
					props.setProperty(Environment.JNDI_CLASS, entry.getValue());
				} else if (entry.getKey().equals(Context.URL_PKG_PREFIXES)) {
					props.setProperty(Environment.JNDI_URL, entry.getValue());
				} else {
					props.setProperty(entry.getKey(), entry.getValue());
				}
			}

			props.setProperty(Environment.TRANSACTION_MANAGER_STRATEGY,
					MyContainerTransactionManagerLookup.class.getName());
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	@Override
	protected TransactionManager getTransactionManager(PersistenceUnitInfo info) {
		try {
			Properties props = info.getProperties();
			String className = props
					.getProperty(Environment.TRANSACTION_MANAGER_STRATEGY);

			Class<?> clazz = Class.forName(className);
			TransactionManagerLookup i = (TransactionManagerLookup) clazz
					.newInstance();
			TransactionManager tm = i.getTransactionManager(props);
			return tm;
		} catch (ClassNotFoundException e) {
			throw new KernelRuntimeException(e);
		} catch (InstantiationException e) {
			throw new KernelRuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new KernelRuntimeException(e);
		}
	}

}
