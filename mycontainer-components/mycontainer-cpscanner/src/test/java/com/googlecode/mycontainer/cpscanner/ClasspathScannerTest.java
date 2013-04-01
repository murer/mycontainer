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

package com.googlecode.mycontainer.cpscanner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.List;

import org.junit.Test;

import com.googlecode.mycontainer.cpscanner.ClasspathScanner;
import com.googlecode.mycontainer.cpscanner.ListScannerListener;

public class ClasspathScannerTest {

	@Test
	public void testScanDirectory() {
		ClasspathScanner scanner = new ClasspathScanner();
		ListScannerListener listener = new ListScannerListener();
		scanner.addListener(listener);

		URL url = ClasspathScannerTest.class.getProtectionDomain()
				.getCodeSource().getLocation();
		scanner.scan(url);

		List<Class<?>> classes = listener.getClasses();
		assertFalse(classes.isEmpty());
		assertTrue(classes.contains(ClasspathScannerTest.class));

		List<URL> resources = listener.getResources();
		for (URL resource : resources) {
			assertTrue(resource.toString().contains(".svn"));
		}
	}

	@Test
	public void testScanJar() throws Exception {
		ClasspathScanner scanner = new ClasspathScanner();
		ListScannerListener listener = new ListScannerListener();
		scanner.addListener(listener);

		URL url = Test.class.getProtectionDomain().getCodeSource()
				.getLocation();
		scanner.scan(url);

		List<Class<?>> classes = listener.getClasses();
		assertFalse(classes.isEmpty());
		assertTrue(classes.contains(Test.class));

		List<URL> resources = listener.getResources();
		for (URL resource : resources) {
			if (!resource.toString().contains(".svn")) {
				return;
			}
		}

		fail("no resources");
	}

}
