package com.googlecode.mycontainer.maven.plugin.util;

import java.io.File;

public abstract class FileCrawler {

	public void crawl(File... file) {
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
				crawl(f.listFiles());
			}
		}
	}

	protected abstract void found(File file);

}
