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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;

public class JPAInfoBuilder implements PersistenceUnitInfo {

	private ClassLoader classLoader;

	private String jtaDataSourceName;

	private final List<URL> jarFileUrls = new ArrayList<URL>();

	private String persistenceUnitName;

	private final Properties props = new Properties();

	private URL persistenceUnitRootUrl;

	private Context ctx;

	public JPAInfoBuilder() {
		classLoader = this.getClass().getClassLoader();
	}

	public Context getContext() {
		return ctx;
	}

	public void setContext(Context ctx) {
		this.ctx = ctx;
	}

	public void addTransformer(ClassTransformer transformer) {
		throw new KernelRuntimeException("not support");
	}

	public boolean excludeUnlistedClasses() {
		return false;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void addJarFileUrl(String className) {
		try {
			Class<?> clazz = (Class<?>) Class.forName(className);
			addJarFileUrl(clazz);
		} catch (ClassNotFoundException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public void addJarFileUrl(Class<?> clazz) {
		URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
		addJarFileUrl(url);
	}

	public void addJarFileUrl(URL url) {
		if (!url.equals(persistenceUnitRootUrl)) {
			this.jarFileUrls.add(url);
		}
	}

	public List<URL> getJarFileUrls() {
		return jarFileUrls;
	}

	public String getJtaDataSourceName() {
		return jtaDataSourceName;
	}

	public void setJtaDataSourceName(String jtaDataSourceName) {
		this.jtaDataSourceName = jtaDataSourceName;
	}

	public DataSource getJtaDataSource() {
		try {
			DataSource ret = (DataSource) ctx.lookup(jtaDataSourceName);
			return ret;
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public List<String> getManagedClassNames() {
		return Collections.emptyList();
	}

	public List<String> getMappingFileNames() {
		return Collections.emptyList();
	}

	public ClassLoader getNewTempClassLoader() {
		return classLoader;
	}

	public DataSource getNonJtaDataSource() {
		return null;
	}

	public String getPersistenceProviderClassName() {
		return null;
	}

	public void setPersistenceUnitName(String persistenceUnitName) {
		this.persistenceUnitName = persistenceUnitName;
	}

	public String getPersistenceUnitName() {
		return persistenceUnitName;
	}

	public URL getPersistenceUnitRootUrl() {
		return persistenceUnitRootUrl;
	}

	public void setPersistenceUnitRootUrl(URL persistenceUnitRootUrl) {
		this.persistenceUnitRootUrl = persistenceUnitRootUrl;
	}

	public void setPersistenceUnitRootUrl(String className) {
		try {
			Class<?> clazz = Class.forName(className);
			setPersistenceUnitRootUrl(clazz);
		} catch (ClassNotFoundException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public void setPersistenceUnitRootUrl(Class<?> clazz) {
		this.persistenceUnitRootUrl = clazz.getProtectionDomain()
				.getCodeSource().getLocation();
		jarFileUrls.remove(this.persistenceUnitRootUrl);
		setClassLoader(clazz.getClassLoader());
	}

	public Properties getProperties() {
		return props;
	}

	public PersistenceUnitTransactionType getTransactionType() {
		return PersistenceUnitTransactionType.JTA;
	}

	public String getPersistenceXMLSchemaVersion() {
		return "2.0";
	}

	public SharedCacheMode getSharedCacheMode() {
		return null;
	}

	public ValidationMode getValidationMode() {
		return ValidationMode.AUTO;
	}

}
