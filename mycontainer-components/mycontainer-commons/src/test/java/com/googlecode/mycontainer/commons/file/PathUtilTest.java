package com.googlecode.mycontainer.commons.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.googlecode.mycontainer.commons.file.PathUtil;

public class PathUtilTest {

	@Test
	public void testFix() {
		assertNull(PathUtil.fix(null));
		assertEquals("xyz.txt", PathUtil.fix("xyz.txt"));
		assertEquals("/xyz.txt/", PathUtil.fix("///xyz.txt///"));
		assertEquals("/abc/fff/xyz.txt",
				PathUtil.fix("///abc////fff///xyz.txt"));
		assertEquals("/abc/fff/xyz.txt/",
				PathUtil.fix("///abc////fff///xyz.txt///"));
		assertEquals("/abc/fff/xyz.txt/",
				PathUtil.fix("   ///abc////fff///xyz.txt///   "));
		assertEquals("/abc/fff/xyz.txt/",
				PathUtil.fix("   // /abc/// / fff / / / xyz.txt//   /   "));
		assertEquals("/", PathUtil.fix("/"));
	}

	@Test
	public void testSplit() {
		assertNull(PathUtil.split(null));
		assertSplit("xyz.txt", "xyz.txt");
		assertSplit("//xyz.txt///", "xyz.txt");
		assertSplit(" / / abc/fff/ /xyz.txt/ // ", "abc", "fff", "xyz.txt");
	}

	private void assertSplit(String path, String... exp) {
		List<String> split = PathUtil.split(path);
		assertNotNull(split);
		int i = 0;
		while (i < exp.length && i < split.size()) {
			assertEquals(exp[i], split.get(i));
			i++;
		}
		assertEquals(exp.length, split.size());
	}

	@Test
	public void testGetName() {
		assertEquals("xyz.txt", PathUtil.getName("xyz.txt"));
		assertEquals("xyz.txt", PathUtil.getName("/xyz.txt"));
		assertEquals("xyz.txt", PathUtil.getName("/abc/fff/xyz.txt"));

		assertEquals("xyz.txt", PathUtil.getName("xyz.txt/"));
		assertEquals("xyz.txt", PathUtil.getName("/xyz.txt/"));
		assertEquals("xyz.txt", PathUtil.getName("/abc/fff/xyz.txt/"));

		assertEquals("/", PathUtil.getName("/"));

		assertEquals("xyz.txt", PathUtil.getName("///abc////fff///xyz.txt///"));
	}

	@Test
	public void testGetParent() {
		assertEquals("/", PathUtil.parentPath("xyz.txt"));
		assertEquals("/", PathUtil.parentPath("/xyz.txt"));
		assertEquals("/abc/fff", PathUtil.parentPath("/abc/fff/xyz.txt"));
		assertEquals("/", PathUtil.parentPath("xyz.txt/"));
		assertEquals("/", PathUtil.parentPath("/xyz.txt/"));
		assertEquals("/abc/fff", PathUtil.parentPath("/abc/fff/xyz.txt/"));

		assertNull(PathUtil.parentPath("/"));

		assertEquals("/abc/fff",
				PathUtil.parentPath("///abc////fff///xyz.txt///"));
	}
}
