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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.googlecode.mycontainer.cpscanner.ScannerPlugin;

public class ZipClasspathPlugin implements ScannerPlugin {

	public List<URL> listDirectories(URL directory) {
		if (!check(directory)) {
			return null;
		}

		return new ArrayList<URL>();
	}

	public List<URL> listFiles(URL directory) {
		if (!check(directory)) {
			return null;
		}
		if (!check(directory)) {
			return null;
		}

		try {
			File file = new File(directory.toURI());
			ZipFile zip = new ZipFile(file);
			List<URL> ret = new ArrayList<URL>();
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (!entry.isDirectory()) {
					String name = entry.getName();
					URL resource = new URL("jar:" + directory + "!/" + name);
					ret.add(resource);
				}
			}

			return ret;
		} catch (ZipException e) {
			throw new RuntimeException(e);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean check(URL directory) {
		try {
			if (!directory.getProtocol().equals("file")) {
				return false;
			}
			File file = new File(directory.toURI());
			return file.isFile();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

}
