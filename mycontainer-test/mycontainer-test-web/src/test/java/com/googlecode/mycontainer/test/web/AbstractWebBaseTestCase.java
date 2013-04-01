package com.googlecode.mycontainer.test.web;

import org.junit.After;
import org.junit.Before;

public abstract class AbstractWebBaseTestCase {

	protected MycontainerTestHelper helper;

	@Before
	public void boot() throws Exception {
		helper = new MycontainerTestHelper();
		helper.bootBackend();
		helper.bootFrontend();
	}

	@After
	public void shutdown() {
		helper.shutdown();
	}

}
