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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;


public class NamingHelper {

	private final Context ctx;

	public NamingHelper(Context ctx) {
		this.ctx = ctx;
	}

	public NamingHelper(Hashtable<?, ?> env) {
		try {
			this.ctx = new InitialContext(env);
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public Long generateId(String name) {
		try {
			long id = 0l;
			try {
				id = (Long) ctx.lookup(name);
			} catch (NameNotFoundException e) {
				ctx.bind(name, 0l);
			}

			id++;
			ctx.rebind(name, id);

			return id;
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public Long bind(String prefix, Object obj) {
		try {
			Long id = generateId(prefix + "/namingHelperId");
			Context sub = (Context) ctx.lookup(prefix + "/namingHelper");
			sub.bind("id" + id, obj);
			return id;
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public Object lookup(String prefix, Long id) {
		try {
			Object ret = ctx.lookup(prefix + "/namingHelper/" + id);
			return ret;
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public Context getCtx() {
		return ctx;
	}

}
