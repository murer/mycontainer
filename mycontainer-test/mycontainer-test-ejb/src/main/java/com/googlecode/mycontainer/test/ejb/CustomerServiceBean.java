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

import java.io.Serializable;
import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.googlecode.mycontainer.test.CustomerService;
import com.googlecode.mycontainer.test.EntityManagerWrapper;
import com.googlecode.mycontainer.test.HelloService;

@Stateless
public class CustomerServiceBean implements Serializable, CustomerService {

	private static final long serialVersionUID = 8968616573033367996L;

	@PersistenceContext(unitName = "test-pu")
	private EntityManager em;

	@EJB
	private EntityManagerWrapper wrapper;
	
	private HelloService helloService;
	
	@EJB
	public void setHelloService(HelloService helloService) {
		this.helloService = helloService;
	}

	public CustomerBean createCustomer(CustomerBean customer) {
		return wrapper.persist(customer);
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public CustomerBean findCustomer(Long id) {
		return wrapper.find(CustomerBean.class, id);
	}

	@SuppressWarnings("unchecked")
	public Collection<CustomerBean> findAll() {
		Query query = em.createQuery("FROM CustomerBean");
		return query.getResultList();
	}
	
	public String getHello() {
		return helloService.sayHello();
	}
	

}
