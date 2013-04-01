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

package com.googlecode.mycontainer.kernel.deploy;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;


public class NamingAliasDeployer extends ObjectProviderDeployer {

	private static final long serialVersionUID = -4349844345245417645L;

	private String destination;

	public NamingAliasDeployer() {

	}

	public NamingAliasDeployer(Context ctx, String name, String destination) {
		setContext(ctx);
		setName(name);
		setDestination(destination);
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Object provide(Name name) {
		try {
			Context ctx = getContext();
			return ctx.lookup(destination);
		} catch (NamingException e) {
			throw new KernelRuntimeException(e);
		}
	}

}
