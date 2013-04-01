package com.googlecode.mycontainer.commons.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtilList implements ZipProcessor {

	private final List<ZipEntry> entries = new ArrayList<ZipEntry>();

	public void process(ZipEntry entry, ZipInputStream in) {
		entries.add(entry);
	}

	public List<ZipEntry> getEntries() {
		return entries;
	}

	public static List<ZipEntry> entries(URL url) {
		return ZipUtil.process(url, new ZipUtilList()).getEntries();
	}

	public List<String> getNames() {
		List<String> ret = new ArrayList<String>();
		for (ZipEntry entry : entries) {
			ret.add(entry.getName());
		}
		return ret;
	}

	public List<String> getFileNames() {
		List<String> ret = new ArrayList<String>();
		for (ZipEntry entry : entries) {
			if (!entry.isDirectory()) {
				ret.add(entry.getName());
			}
		}
		return ret;
	}

	public List<String> getDirectoryNames() {
		List<String> ret = new ArrayList<String>();
		for (ZipEntry entry : entries) {
			if (entry.isDirectory()) {
				ret.add(entry.getName());
			}
		}
		return ret;
	}

}
