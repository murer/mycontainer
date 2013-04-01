package com.googlecode.mycontainer.gae.web;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import com.google.appengine.tools.development.testing.LocalServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class LocalServiceTestHelperFilter extends LocalEnvironmentFilter {

	private LocalServiceTestHelper helper;

	public LocalServiceTestHelperFilter() {

	}

	public LocalServiceTestHelperFilter(LocalServiceTestHelper helper) {
		this.helper = helper;
	}

	public LocalServiceTestHelperFilter(LocalServiceTestConfig... helpers) {
		this(new LocalServiceTestHelper(helpers));
	}

	public LocalServiceTestHelper getHelper() {
		return helper;
	}

	public void setHelper(LocalServiceTestHelper helper) {
		this.helper = helper;
	}

	public void init(FilterConfig config) throws ServletException {
		helper.setUp();
		super.init(config);
	}

	public void destroy() {
		super.destroy();
		helper.tearDown();
	}

}
