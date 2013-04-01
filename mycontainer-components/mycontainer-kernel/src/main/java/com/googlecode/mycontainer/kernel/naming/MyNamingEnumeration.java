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

import java.util.Iterator;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;


public class MyNamingEnumeration<T> implements NamingEnumeration<T> {

	private final Iterator<T> iterator;

	public MyNamingEnumeration(Iterator<T> iterator) {
		this.iterator = iterator;
	}

	public void close() throws NamingException {
	}

	public boolean hasMore() throws NamingException {
		return iterator.hasNext();
	}

	public T next() throws NamingException {
		return iterator.next();
	}

	public boolean hasMoreElements() {
		try {
			return hasMore();
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public T nextElement() {
		try {
			return next();
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

}
