package com.googlecode.mycontainer.test.ejb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.naming.InitialContext;

import org.junit.Test;

import com.googlecode.mycontainer.test.PropertyService;

public class PropertyServiceBeanTest extends AbstractTestCase {

	@Test
	public void testProperty() throws Exception {
		InitialContext ic = new InitialContext();
		PropertyService service = (PropertyService) ic
				.lookup("PropertyServiceBean/local");

		service.setProperty("test.key", "myvalue");

		assertNull(service.getProperty("notexist"));
		assertEquals("myvalue", service.getProperty("test.key"));
	}

}
