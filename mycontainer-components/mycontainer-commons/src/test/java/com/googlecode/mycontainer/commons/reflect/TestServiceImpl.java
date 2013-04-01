package com.googlecode.mycontainer.commons.reflect;

public class TestServiceImpl {

	public Integer sum(Integer a, Integer b) {
		return a + b;
	}

	public Integer sumAllowed(Integer a, Integer b) {
		return sum(a, b);
	}
}
