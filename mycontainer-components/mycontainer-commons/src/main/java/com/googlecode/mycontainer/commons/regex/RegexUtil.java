package com.googlecode.mycontainer.commons.regex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	public static List<String> groups(String regex, CharSequence str) {
		Pattern pattern = Pattern.compile(regex);
		return groups(pattern, str);
	}

	public static List<String> groups(Pattern pattern, CharSequence str) {
		Matcher matcher = pattern.matcher(str);
		if (!matcher.matches()) {
			return Collections.emptyList();
		}
		int count = matcher.groupCount();
		ArrayList<String> ret = new ArrayList<String>(count);
		for (int i = 0; i < count; i++) {
			ret.add(matcher.group(i));
		}
		return ret;
	}

	public static List<String> match(String regex, String str) {
		List<String> ret = groups(regex, str);
		if (ret.isEmpty()) {
			throw new RuntimeException("not match: '" + regex + "', " + str);
		}
		return ret;
	}

	public static boolean matches(Pattern pattern, String path) {
		Matcher matcher = pattern.matcher(path);
		return matcher.matches();
	}

}
