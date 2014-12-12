package com.googlecode.mycontainer.maven.plugin.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileCrawler {

	private static final Logger LOG = LoggerFactory.getLogger(FileCrawler.class);

	public void crawl(File... file) {
		if (file == null) {
			return;
		}
		for (File f : file) {
			if (!f.exists()) {
				continue;
			}
			String name = f.getName();
			if (name.equals("..")) {
				continue;
			}
			found(f);

			if (f.isDirectory()) {
				File[] list = f.listFiles();
				if (list == null) {
					LOG.error("Error reading directory: " + f);
				}
				crawl(list);
			}
		}
	}

	protected abstract void found(File file);

}
