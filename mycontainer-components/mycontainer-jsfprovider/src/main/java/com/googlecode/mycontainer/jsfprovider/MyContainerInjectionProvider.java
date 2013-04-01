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
package com.googlecode.mycontainer.jsfprovider;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.googlecode.mycontainer.kernel.naming.MyNameParser;
import com.sun.faces.spi.DiscoverableInjectionProvider;
import com.sun.faces.spi.InjectionProviderException;

public class MyContainerInjectionProvider extends DiscoverableInjectionProvider {

	private Context context;

	public MyContainerInjectionProvider() {
		try {
			this.context = new InitialContext();
		} catch (NamingException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void inject(Object instance) throws InjectionProviderException {
		injectEjbs(instance);
		injectResource(instance);
	}

	private void injectEjbs(Object instance) throws InjectionProviderException {
		Field fields[] = instance.getClass().getDeclaredFields();

		for (Field field : fields) {
			if (field.isAnnotationPresent(EJB.class)) {
				EJB ejb = field.getAnnotation(EJB.class);

				String beanName;
				if (ejb.mappedName().trim().length() > 0) {
					beanName = ejb.mappedName().trim();
				} else if (ejb.beanInterface() == Object.class) {
					beanName = MyNameParser.parseClassName("ejb", field
							.getType());
				} else {
					beanName = MyNameParser.parseClassName("ejb", ejb
							.beanInterface());
				}

				try {
					setField(instance, field, context.lookup(beanName));
				} catch (NamingException e) {
					throw new InjectionProviderException(e);
				}
			}
		}
	}

	private void injectResource(Object instance)
			throws InjectionProviderException {
		Field fields[] = instance.getClass().getDeclaredFields();

		for (Field field : fields) {
			if (field.isAnnotationPresent(Resource.class)) {
				Resource resource = field.getAnnotation(Resource.class);

				String beanName;
				if (resource.mappedName().trim().length() > 0) {
					beanName = resource.mappedName().trim();
				} else if (resource.type() == Object.class) {
					beanName = MyNameParser.parseClassName("resource", field
							.getType());
				} else {
					beanName = MyNameParser.parseClassName("resource", resource
							.type());
				}

				try {
					setField(instance, field, context.lookup(beanName));
				} catch (NamingException e) {
					throw new InjectionProviderException(e);
				}
			}
		}
	}

	public void invokePostConstruct(Object instance)
			throws InjectionProviderException {
		Method postConstruct = AnnotationUtils.findPostConstruct(instance);

		if (postConstruct != null) {
			invokeMethod(instance, postConstruct);
		}
	}

	public void invokePreDestroy(Object instance)
			throws InjectionProviderException {
		Method preDestroy = AnnotationUtils.findPreDestroy(instance);

		if (preDestroy != null) {
			invokeMethod(instance, preDestroy);
		}
	}

	private void invokeMethod(Object instance, Method method)
			throws InjectionProviderException {
		boolean accessibility = method.isAccessible();
		method.setAccessible(true);

		try {
			method.invoke(instance);
		} catch (IllegalArgumentException e) {
			throw new InjectionProviderException(e);
		} catch (IllegalAccessException e) {
			throw new InjectionProviderException(e);
		} catch (InvocationTargetException e) {
			throw new InjectionProviderException(e);
		} finally {
			method.setAccessible(accessibility);
		}
	}

	private void setField(Object instance, Field field, Object value)
			throws InjectionProviderException {
		boolean accessibility = field.isAccessible();
		field.setAccessible(true);

		try {
			field.set(instance, value);
		} catch (IllegalArgumentException e) {
			throw new InjectionProviderException(e);
		} catch (IllegalAccessException e) {
			throw new InjectionProviderException(e);
		} finally {
			field.setAccessible(accessibility);
		}
	}
}
