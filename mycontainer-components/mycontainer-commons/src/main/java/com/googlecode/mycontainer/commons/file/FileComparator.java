package com.googlecode.mycontainer.commons.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

import com.googlecode.mycontainer.commons.io.IOUtil;

public class FileComparator implements Comparator<File>, FileFilter {

	private FileFilter filter = this;

	public FileFilter getFilter() {
		return filter;
	}

	public void setFilter(FileFilter filter) {
		this.filter = filter;
	}

	public int compare(File f1, File f2) {
		InputStream in1 = null;
		InputStream in2 = null;
		try {
			boolean f1Exists = f1.exists();
			boolean f2Exists = f2.exists();
			if (f1Exists != f2Exists) {
				return f1Exists ? -1 : 1;
			}
			if (!f1Exists && !f2Exists) {
				return 0;
			}

			boolean isDir1 = f1.isDirectory();
			boolean isDir2 = f2.isDirectory();
			if (isDir1 != isDir2) {
				return isDir1 ? -1 : 1;
			}

			if (!isDir1) {
				in1 = new FileInputStream(f1);
				in2 = new FileInputStream(f2);
				return IOUtil.compare(in1, in2);
			}

			File[] files1 = f1.listFiles(filter);
			File[] files2 = f2.listFiles(filter);

			int ret = files1.length - files2.length;
			if (ret != 0) {
				return ret;
			}

			Arrays.sort(files1);
			Arrays.sort(files2);
			for (int i = 0; i < files1.length; i++) {
				File child1 = files1[i];
				File child2 = files2[i];
				ret = compare(child1, child2);
				if (ret != 0) {
					return ret;
				}
			}

			return 0;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtil.close(in1);
			IOUtil.close(in2);
		}
	}

	public boolean accept(File file) {
		return true;
	}

}
