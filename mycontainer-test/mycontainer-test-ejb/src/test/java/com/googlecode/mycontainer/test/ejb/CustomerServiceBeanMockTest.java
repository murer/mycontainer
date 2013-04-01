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
import static org.junit.Assert.assertTrue;

import javax.naming.InitialContext;

import org.easymock.EasyMock;
import org.junit.Test;

import com.googlecode.mycontainer.test.CustomerService;
import com.googlecode.mycontainer.test.EntityManagerWrapper;
import com.googlecode.mycontainer.test.ejb.CustomerBean;


public class CustomerServiceBeanMockTest extends AbstractTestCase {

	private static class EntityManagerWrapperBeanMock implements
			EntityManagerWrapper {

		private boolean find = false;

		private boolean persist = false;

		@SuppressWarnings("unchecked")
		public <T> T find(Class<T> type, Object id) {
			if (!persist) {
				return null;
			}
			find = true;
			CustomerBean ret = new CustomerBean();
			ret.setId((Long) id);
			ret.setName("name10");
			return (T) ret;
		}

		public <T> T merge(T t) {
			return null;
		}

		public <T> T persist(T t) {
			persist = true;
			CustomerBean bean = (CustomerBean) t;
			bean.setId(10l);
			return t;
		}

		public void remove(Object t) {

		}

	}

	@Test
	public void testCreateBasicMock() throws Exception {
		InitialContext ic = new InitialContext();
		EntityManagerWrapperBeanMock mock = new EntityManagerWrapperBeanMock();
		ic.rebind("EntityManagerWrapperBean/local", mock);
		makeTestCreate();
		assertTrue(mock.find);
		assertTrue(mock.persist);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateEasyMock() throws Exception {
		InitialContext ic = new InitialContext();
		CustomerBean bean = new CustomerBean();
		bean.setId(10l);
		bean.setName("name10");

		EntityManagerWrapper mock = EasyMock
				.createMock(EntityManagerWrapper.class);
		EasyMock.expect(
				mock.find(EasyMock.isA(Class.class), EasyMock.anyObject()))
				.andReturn(null).once();
		EasyMock.expect(
				mock.find(EasyMock.isA(Class.class), EasyMock.anyObject()))
				.andReturn(bean).anyTimes();
		EasyMock.expect(mock.persist(EasyMock.anyObject())).andReturn(bean)
				.anyTimes();
		EasyMock.replay(mock);
		
		ic.rebind("EntityManagerWrapperBean/local", mock);
		makeTestCreate();
	}

	private void makeTestCreate() throws Exception {
		InitialContext ic = new InitialContext();
		CustomerService service = (CustomerService) ic
				.lookup("CustomerServiceBean/local");

		assertNull(service.findCustomer(10l));

		CustomerBean customer = new CustomerBean();
		customer.setName("name10");
		customer = service.createCustomer(customer);
		assertNotNull(customer.getId());

		customer = service.findCustomer(customer.getId());
		assertEquals("name10", customer.getName());
	}

}
