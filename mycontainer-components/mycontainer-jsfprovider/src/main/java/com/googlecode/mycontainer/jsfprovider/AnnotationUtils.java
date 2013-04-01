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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class AnnotationUtils {
	public static Method findPostConstruct(Object instance) {
		return findUniqueAnnotedMethod(instance, PostConstruct.class);
	}

	public static Method findPreDestroy(Object instance) {
		return findUniqueAnnotedMethod(instance, PreDestroy.class);
	}

	private static Method findUniqueAnnotedMethod(Object instance,
			Class<? extends Annotation> annotationClass) {
		Method methods[] = instance.getClass().getDeclaredMethods();
		Method postConstruct = null;
		for (int i = 0; i < methods.length; i++) {
			if (!methods[i].isAnnotationPresent(annotationClass)) {
				continue;
			}
			if (postConstruct != null
					|| methods[i].getParameterTypes().length != 0
					|| Modifier.isStatic(methods[i].getModifiers())
					|| methods[i].getExceptionTypes().length > 0
					|| !methods[i].getReturnType().getName().equals("void"))
				throw new IllegalArgumentException(
						"Invalid method annotation");
			postConstruct = methods[i];
		}

		return postConstruct;
	}

}
