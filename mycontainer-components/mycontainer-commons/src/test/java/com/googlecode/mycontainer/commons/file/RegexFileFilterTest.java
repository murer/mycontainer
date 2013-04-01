package com.googlecode.mycontainer.commons.file;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import com.googlecode.mycontainer.commons.file.FileUtil;

public class RegexFileFilterTest {

	@Test
	public void testAll() {
		File[] files = FileUtil.list("src", "^[^\\.].*");
		Arrays.sort(files);
		assertEquals("main", files[0].getName());
		assertEquals("test", files[1].getName());
		assertEquals(2, files.length);
	}

	@Test
	public void testJava() {
		File[] files = FileUtil.list("src", "^m.*");
		Arrays.sort(files);
		assertEquals("main", files[0].getName());
		assertEquals(1, files.length);
	}

}
