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

package com.googlecode.mycontainer.ejb;


import com.googlecode.mycontainer.ejb.interceptor.DefaultEJBCallbackInterceptor;
import com.googlecode.mycontainer.ejb.interceptor.EJBExceptionInterceptor;
import com.googlecode.mycontainer.ejb.interceptor.EJBInjectInterceptor;
import com.googlecode.mycontainer.ejb.interceptor.ResourceInjectInterceptor;
import com.googlecode.mycontainer.ejb.interceptor.TransactionInterceptor;
import com.googlecode.mycontainer.kernel.deploy.DefaultIntercetorDeployer;
import com.googlecode.mycontainer.kernel.reflect.proxy.SynchronizedInterceptor;

public class SessionInterceptorDeployer extends DefaultIntercetorDeployer {

	private static final long serialVersionUID = 7955903289949951072L;

	public static final String DEFAULT_NAME = "StatelessDeployer/intercetorDeployerName";

	public SessionInterceptorDeployer() {
		setName(DEFAULT_NAME);
		addContextInterceptor(SynchronizedInterceptor.class);
		addContextInterceptor(EJBExceptionInterceptor.class);
		addContextInterceptor(ResourceInjectInterceptor.class);
		addContextInterceptor(EJBInjectInterceptor.class);
		addContextInterceptor(DefaultEJBCallbackInterceptor.class);
		addContextInterceptor(TransactionInterceptor.class);
	}

}
