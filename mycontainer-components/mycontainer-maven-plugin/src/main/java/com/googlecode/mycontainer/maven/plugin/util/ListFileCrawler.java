package com.googlecode.mycontainer.maven.plugin.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListFileCrawler extends FileCrawler {

	private final List<File> files = new ArrayList<File>();

	@Override
	protected void found(File file) {
		files.add(file);
	}

	public List<File> getFiles() {
		return files;
	}

}
