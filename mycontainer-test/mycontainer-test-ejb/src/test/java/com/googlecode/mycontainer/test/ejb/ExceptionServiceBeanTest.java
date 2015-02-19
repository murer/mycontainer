package com.googlecode.mycontainer.test.ejb;

import org.junit.Test;

import com.googlecode.mycontainer.test.CheckedException;
import com.googlecode.mycontainer.test.ExceptionService;

public class ExceptionServiceBeanTest extends AbstractTestCase {

	@Test(expected = CheckedException.class)
	public void testCheckedException() throws Exception {
		ExceptionService exceptionServiceBean = lookupEJB(ExceptionService.class);
		exceptionServiceBean.doIt();
	}

}
