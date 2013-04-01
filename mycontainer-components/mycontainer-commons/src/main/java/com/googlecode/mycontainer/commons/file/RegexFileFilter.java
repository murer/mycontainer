package com.googlecode.mycontainer.commons.file;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexFileFilter implements FileFilter {

	private final Pattern pattern;

	public RegexFileFilter(String regex) {
		pattern = Pattern.compile(regex);
	}

	public boolean accept(File file) {
		String name = file.getName();
		Matcher matcher = pattern.matcher(name);
		boolean ret = matcher.matches();
		return ret;
	}

}
