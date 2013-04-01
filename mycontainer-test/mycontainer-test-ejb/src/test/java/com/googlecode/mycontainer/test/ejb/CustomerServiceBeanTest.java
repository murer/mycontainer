/*
 * Copyright 2008 Whohoo Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.googlecode.mycontainer.test.ejb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.naming.InitialContext;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.mycontainer.test.CustomerService;
import com.googlecode.mycontainer.test.ejb.CustomerBean;


public class CustomerServiceBeanTest extends AbstractTestCase {
	
	
	private CustomerService service;
	
	
	@Before
	public void setupClass() throws Exception {
		InitialContext ic = new InitialContext();
		service = (CustomerService) ic.lookup("CustomerServiceBean/local");
	}
	

	@Test
	public void testCreate() throws Exception {
		assertNull(service.findCustomer(10l));

		CustomerBean customer = new CustomerBean();
		customer.setName("name10");
		customer = service.createCustomer(customer);
		assertNotNull(customer.getId());

		customer = service.findCustomer(customer.getId());
		assertEquals("name10", customer.getName());

	}
	
	@Test
	public void testGetHello() throws Exception {
		String hello = service.getHello();
		assertNotNull(hello);
		assertEquals(hello, "Hello World");
	}
	

}
