package com.googlecode.mycontainer.commons.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegexResult {

	private static final Logger LOG = LoggerFactory
			.getLogger(RegexResult.class);

	private Pattern pattern;

	private final List<RegexResultRow> rows = new ArrayList<RegexResultRow>();

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public void process(String line) {
		List<String> groups = RegexUtil.groups(pattern, line);
		LogUtil.debug(LOG, "Parsing '%1s' [%2s]: [%3s]", line, pattern, groups);
		if (!groups.isEmpty()) {
			RegexResultRow row = new RegexResultRow(line);
			row.getGroups().addAll(groups);
			rows.add(row);
		}
	}

	public List<RegexResultRow> getRows() {
		return rows;
	}

	@Override
	public String toString() {
		return "[" + pattern + ", " + rows + "]";
	}

	public RegexResultRow getRow() {
		if (rows.isEmpty()) {
			return null;
		}
		return rows.get(0);
	}

}
