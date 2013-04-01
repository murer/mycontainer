package com.googlecode.mycontainer.commons.file;

import java.io.File;

public class FileUtil {

	public static int compare(String f1, String f2) {
		return compare(new File(f1), new File(f2));
	}

	private static int compare(File f1, File f2) {
		FileComparator comparator = new FileComparator();
		return comparator.compare(f1, f2);
	}

	public static File[] list(String file, String regex) {
		return list(new File(file), regex);
	}

	private static File[] list(File file, String regex) {
		RegexFileFilter filter = new RegexFileFilter(regex);
		File[] ret = file.listFiles(filter);
		return ret;
	}

}
