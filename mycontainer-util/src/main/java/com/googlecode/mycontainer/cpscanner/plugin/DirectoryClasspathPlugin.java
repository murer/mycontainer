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

package com.googlecode.mycontainer.cpscanner.plugin;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.mycontainer.cpscanner.ScannerPlugin;


public class DirectoryClasspathPlugin implements ScannerPlugin {

	private static final FilenameFilter FILTER_DIRECTORY = new FilenameFilter() {

		public boolean accept(File dir, String name) {
			File file = new File(dir, name);
			return file.isDirectory();
		}
	};

	private static final FilenameFilter FILTER_FILE = new FilenameFilter() {

		public boolean accept(File dir, String name) {
			File file = new File(dir, name);
			return !file.isDirectory();
		}
	};

	private List<URL> convert(File... files) {
		try {
			List<URL> ret = new ArrayList<URL>(files.length);
			for (File child : files) {
				ret.add(child.toURL());
			}
			return ret;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<URL> listDirectories(URL directory) {
		if (!check(directory)) {
			return null;
		}
		try {
			File file = new File(directory.toURI());
			File[] files = file.listFiles(FILTER_DIRECTORY);
			return convert(files);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean check(URL directory) {
		try {
			if (!directory.getProtocol().equals("file")) {
				return false;
			}
			File file = new File(directory.toURI());
			return file.isDirectory();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public List<URL> listFiles(URL directory) {
		if (!check(directory)) {
			return null;
		}

		try {
			File file = new File(directory.toURI());
			File[] files = file.listFiles(FILTER_FILE);
			return convert(files);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

}
