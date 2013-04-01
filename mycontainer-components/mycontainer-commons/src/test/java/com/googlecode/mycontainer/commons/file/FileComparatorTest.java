package com.googlecode.mycontainer.commons.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.googlecode.mycontainer.commons.file.FileUtil;

public class FileComparatorTest {

	@Test
	public void testCompareDirectory() {
		assertEquals(0, FileUtil.compare("src", "src"));
		assertTrue(FileUtil.compare("src", "target") != 0);

		assertTrue(FileUtil.compare("src", "src-notfound") < 0);
		assertTrue(FileUtil.compare("src-notfound", "src") > 0);
		assertTrue(FileUtil.compare("src-notfound", "src-notfound-2") == 0);
	}

	@Test
	public void testCompareFile() {
		assertEquals(0, FileUtil.compare("pom.xml", "pom.xml"));
		assertTrue(FileUtil.compare("pom.xml",
				"src/test/resources/log4j.properties") != 0);

		assertTrue(FileUtil.compare("pom.xml", "pom-notfound.xml") < 0);
		assertTrue(FileUtil.compare("pom-notfound.xml", "pom.xml") > 0);
		assertTrue(FileUtil.compare("pom-notfound.xml", "pom-notfound-2.xml") == 0);
	}

	@Test
	public void testCompareCrazy() {
		assertTrue(FileUtil.compare("src", "pom.xml") < 0);
		assertTrue(FileUtil.compare("pom.xml", "src") > 0);
	}
}
