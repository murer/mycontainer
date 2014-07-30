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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.mycontainer.kernel.KernelRuntimeException;
import com.googlecode.mycontainer.util.cpscanner.ClasspathScanner;
import com.googlecode.mycontainer.util.cpscanner.ListScannerListener;


public class ScannerDeployer extends Deployer {

	private static final long serialVersionUID = 3224579186395148293L;

	private final ClasspathScanner scanner;

	private final ListScannerListener listener;

	private final List<ScannableDeployer> deployers = new ArrayList<ScannableDeployer>();

	public ScannerDeployer() {
		this.scanner = new ClasspathScanner();
		this.listener = new ListScannerListener();
		this.scanner.addListener(listener);
	}

	public void add(ScannableDeployer deployer) {
		deployer.setContext(getContext());
		this.deployers.add(deployer);
	}

	public void shutdown() {

	}

	public void scan(String className) {
		try {
			scanner.scan(Class.forName(className));
		} catch (ClassNotFoundException e) {
			throw new KernelRuntimeException(e);
		}
	}

	public void scan(Class<?> clazz) {
		scanner.scan(clazz);
	}

	public void scan(URL url) {
		scanner.scan(url);
	}

	public void deploy() {
		for (ScannableDeployer deployer : deployers) {
			List<Class<?>> classes = this.listener.getClasses();
			List<URL> resources = this.listener.getResources();
			deployer.deploy(classes, resources);
		}
	}

}
