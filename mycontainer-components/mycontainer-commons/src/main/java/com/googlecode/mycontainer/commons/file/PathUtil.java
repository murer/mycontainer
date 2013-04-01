package com.googlecode.mycontainer.commons.file;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.googlecode.mycontainer.commons.regex.RegexUtil;

public class PathUtil {

	private static final Pattern PATTERN_EXT = Pattern
			.compile("^.*\\.(([^/\\.]*))?");

	public static String parentPath(String path) {
		path = fix(path);
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (path.equals("/")) {
			return null;
		}
		if (path.matches("^/[^/]{1,}/{0,1}$")) {
			path = "/";
		} else {
			List<String> parentPath = RegexUtil.groups(
					"((/{1}[^/]{1}.*))/{1}[^/]{1,}/{0,1}?", path);
			if (parentPath.size() <= 1) {
				throw new RuntimeException("error: " + parentPath + " " + path);
			}
			path = parentPath.get(1);
		}
		return path;
	}

	public static String fix(String path) {
		if (path == null) {
			return null;
		}
		path = path.trim();
		path = path.replaceAll("[/\\s]+", "/");
		return path;
	}

	public static String getExtention(String path) {
		List<String> groups = RegexUtil.groups(PATTERN_EXT, path);
		if (groups.isEmpty()) {
			return null;
		}
		return groups.get(1);
	}

	public static String concatPath(String path, String child) {
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		if (child.startsWith("/")) {
			child = child.substring(1);
		}
		StringBuilder sb = new StringBuilder(path.length() + child.length() + 1);
		sb.append(path);
		sb.append('/');
		sb.append(child);
		return sb.toString();
	}

	public static String removeExtention(String path) {
		int slash = path.lastIndexOf("/");
		int idx = path.lastIndexOf(".");
		if (idx < 0 || slash > idx) {
			return path;
		}
		return path.substring(0, idx);
	}

	public static String getName(String path) {
		if (path == "/") {
			return "/";
		}
		List<String> groups = RegexUtil.groups("^/*(([^/]+))/*$", path);
		if (groups.size() > 1) {
			return groups.get(1);
		}

		groups = RegexUtil.groups("^.*/+(([^/]+))/*$", path);
		return groups.get(1);
	}

	public static String toUrl(URL resource) {
		if (!resource.getProtocol().equals("file")) {
			return resource.toString();
		}
		return "file://" + resource.getPath();
	}

	public static String toUrl(String url) {
		try {
			return toUrl(new URL(url));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<String> split(String path) {
		if (path == null) {
			return null;
		}
		path = fix(path);
		if (path.length() == 0) {
			return Collections.emptyList();
		}
		path = path.replaceAll("^/", "");
		path = path.replaceAll("/$", "");
		String[] ret = path.split("/");
		return Arrays.asList(ret);
	}

}
