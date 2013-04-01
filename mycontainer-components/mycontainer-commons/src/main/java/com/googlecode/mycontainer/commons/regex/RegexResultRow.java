package com.googlecode.mycontainer.commons.regex;

import java.util.ArrayList;
import java.util.List;

public class RegexResultRow {

	private final String line;

	private final List<String> groups = new ArrayList<String>();

	public RegexResultRow(String line) {
		this.line = line;
	}

	public String getLine() {
		return line;
	}

	public List<String> getGroups() {
		return groups;
	}

	@Override
	public String toString() {
		return "[line=" + line + ", groups=" + groups + "]";
	}

}
