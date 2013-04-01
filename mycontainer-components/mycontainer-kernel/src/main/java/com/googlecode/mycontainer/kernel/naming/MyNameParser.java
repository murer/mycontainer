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

package com.googlecode.mycontainer.kernel.naming;

import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;


public class MyNameParser implements NameParser {

	public Name parse(String name) throws NamingException {
		return new CompositeName(name);
	}

	public Name parse(String preffix, Class<?> clazz) {
		try {
			StringBuilder builder = new StringBuilder(clazz.getName().replace(
					'.', '/'));
			if (preffix != null) {
				builder.insert(0, '/');
				builder.insert(0, preffix);
			}
			return parse(builder.toString());
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public static String parseClassName(String preffix, Class<?> clazz) {
		return new MyNameParser().parse(preffix, clazz).toString();
	}
}
