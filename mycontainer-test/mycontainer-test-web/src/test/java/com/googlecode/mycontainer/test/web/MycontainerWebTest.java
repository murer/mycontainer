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

package com.googlecode.mycontainer.test.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.mycontainer.commons.io.IOUtil;

public class MycontainerWebTest extends AbstractWebBaseTestCase {

	private static final Logger LOG = LoggerFactory.getLogger(MycontainerWebTest.class);

	private String testURL(int exp, String urlStr) throws MalformedURLException, IOException {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			int code = conn.getResponseCode();
			assertEquals(exp, code);
			if (exp != 200) {
				return null;
			}
			InputStream in = conn.getInputStream();
			byte[] data = IOUtil.readAll(in);
			return new String(data, "UTF-8");
		} finally {
			if (conn != null) {
				try {
					conn.disconnect();
				} catch (Exception e) {
					LOG.error("error closing", e);
				}
			}
		}
	}

	@Test
	public void testBasic() throws Exception {
		testURL(200, "http://localhost:8380/test/index.html");
		testURL(200, "http://localhost:8380/test/test.txt");
		testURL(200, "http://localhost:8380/test/filter.txt");
		testURL(404, "http://localhost:8380/test/notfound.txt");

		testURL(200, "http://localhost:8380/test-other/index.html");
		testURL(200, "http://localhost:8380/test-other/test.txt");
		testURL(200, "http://localhost:8380/test-other/filter.txt");
		testURL(404, "http://localhost:8380/test-other/notfound.txt");
	}

	@Test
	public void testJsf() throws Exception {
		String data = testURL(200, "http://localhost:8380/test/version.jsf");
		assertTrue(data.contains("<h3>v-e-r-s-i-o-n</h3>"));

		data = testURL(200, "http://localhost:8380/test/abc.jsf");
		assertTrue(data.contains("<h3>abc-def</h3>"));

		data = testURL(200, "http://localhost:8380/test/index.jsf");
		assertTrue(data.contains("<h3>[]</h3>"));
	}

	@Test
	public void testLockedFiles() throws Exception {
		deleteFile("src/main/webapp/lockedfile-test/lockedfile.txt");
		testURL(404, "http://localhost:8380/test/lockedfile-test/lockedfile.txt");

		writeFile("src/main/webapp/lockedfile-test/lockedfile.txt", "first");
		String data = testURL(200, "http://localhost:8380/test/lockedfile-test/lockedfile.txt");
		assertEquals("first", data);

		writeFile("src/main/webapp/lockedfile-test/lockedfile.txt", "fffff");
	}

	private void deleteFile(String name) {
		new File(name).delete();
	}

	private void writeFile(String name, String content) throws IOException {
		File file = new File(name);
		file.getParentFile().mkdir();
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(content.getBytes());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LOG.error("error closing", e);
				}
			}
		}
	}
}
