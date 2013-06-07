package com.googlecode.mycontainer.maven.plugin.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class ListFileCrawlerTest {

	@Test
	public void testCrawl() {
		ListFileCrawler crawler = new ListFileCrawler();
		crawler.crawl(new File("./src/test/java"));
		List<File> files = crawler.getFiles();
		assertFalse(files.contains(new File("./src/test/java/not-found.txt")));
		assertTrue(files.contains(new File("./src/test/java/com/googlecode/mycontainer/maven/plugin/util/ListFileCrawlerTest.java")));
	}

}
