package com.googlecode.mycontainer.commons.regex;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import com.googlecode.mycontainer.commons.lang.StringUtil;


public class RegexParser {

	private final Map<String, RegexResult> patterns = new HashMap<String, RegexResult>();

	public RegexResult getResult(String pattern) {
		return patterns.get(pattern);
	}

	public void addPattern(String pattern) {
		RegexResult result = new RegexResult();
		result.setPattern(Pattern.compile(pattern));
		patterns.put(pattern, result);
	}

	public Map<String, RegexResult> getPatterns() {
		return patterns;
	}

	public void parse(CharSequence chars) {
		char[] array = StringUtil.toCharArray(chars);
		parse(array);
	}

	public void parse(char[] array) {
		BufferedReader bf = new BufferedReader(new CharArrayReader(array));
		parse(bf);
	}

	public void parse(BufferedReader reader) {
		try {
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				parseLine(line);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void parseLine(String line) {
		Set<Entry<String, RegexResult>> set = patterns.entrySet();
		for (Entry<String, RegexResult> entry : set) {
			RegexResult result = entry.getValue();
			result.process(line);
		}
	}

	public String getResult(String pattern, int row, int group) {
		List<RegexResultRow> rows = getResult(pattern).getRows();
		List<String> groups = rows.get(row).getGroups();
		String ret = groups.get(group);
		return ret;
	}

	@Override
	public String toString() {
		return "RegexParser [patterns=" + patterns + "]";
	}

}
