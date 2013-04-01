package com.googlecode.mycontainer.commons.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.mycontainer.commons.io.IOUtil;

public class ZipUtilTestManual {

	private File target;

	@Before
	public void setUp() throws IOException {
		target = new File("./target/lay-baretest");
		target.delete();
		FileUtils.deleteDirectory(target);
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testListZip() {
		URL url = getClass().getClassLoader().getResource("test.zip");

		assertFalse(target.exists());

		ZipUtilList entries = ZipUtil.process(url, new ZipUtilList());
		List<String> list = entries.getFileNames();

		assertTrue(list
				.contains("com/googlecode/mycontainer/commons/laybare/test1.txt"));
		assertTrue(list
				.contains("com/googlecode/mycontainer/commons/laybare/test2.txt"));
		assertFalse(list
				.contains("com/googlecode/mycontainer/commons/laybare/test-notfound.txt"));
		assertFalse(list
				.contains("com/googlecode/mycontainer/commons/laybare/"));

		list = entries.getDirectoryNames();
		assertFalse(list
				.contains("com/googlecode/mycontainer/commons/laybare/test1.txt"));
		assertTrue(list.contains("com/googlecode/mycontainer/commons/laybare/"));
		assertFalse(list
				.contains("com/googlecode/mycontainer/commons/notfound/"));

		list = entries.getNames();
		assertTrue(list
				.contains("com/googlecode/mycontainer/commons/laybare/test1.txt"));
		assertTrue(list
				.contains("com/googlecode/mycontainer/commons/laybare/test2.txt"));
		assertFalse(list
				.contains("com/googlecode/mycontainer/commons/laybare/test-notfound.txt"));
		assertTrue(list.contains("com/googlecode/mycontainer/commons/laybare/"));
		assertFalse(list
				.contains("com/googlecode/mycontainer/commons/notfound/"));

	}

	@Test
	public void testGzip() {
		byte[] gz = ZipUtil.gzip("my test", "utf-8");
		assertEquals(27, gz.length);
		assertEquals("my test", ZipUtil.gunzipString(gz, "utf-8"));
	}

	@Test
	public void testUnzip() {
		URL url = getClass().getClassLoader().getResource("test.zip");

		assertFalse(target.exists());
		ZipUtilExtract.withTarget(target).unzip(url);

		assertEquals(
				"test1 text",
				IOUtil.read(
						new File(target,
								"com/googlecode/mycontainer/commons/laybare/test1.txt"),
						"utf-8").trim());
		assertEquals(
				"test2 text",
				IOUtil.read(
						new File(target,
								"com/googlecode/mycontainer/commons/laybare/test2.txt"),
						"utf-8").trim());

		assertFalse(new File(target,
				"com/googlecode/mycontainer/commons/testbare").exists());
	}

	@Test
	public void testReunzip() {
		URL url = getClass().getClassLoader().getResource("test.zip");

		assertFalse(target.exists());
		ZipUtilExtract.withTarget(target).reunzip(1).unzip(url);

		assertEquals(
				"test1 text",
				IOUtil.read(
						new File(target,
								"com/googlecode/mycontainer/commons/laybare/test1.txt"),
						"utf-8").trim());
		assertEquals(
				"test2 text",
				IOUtil.read(
						new File(target,
								"com/googlecode/mycontainer/commons/laybare/test2.txt"),
						"utf-8").trim());

		assertEquals(
				"test2 text",
				IOUtil.read(
						new File(target,
								"com/googlecode/mycontainer/commons/testbare.zip/laybare/test2.txt"),
						"utf-8").trim());
	}
}
