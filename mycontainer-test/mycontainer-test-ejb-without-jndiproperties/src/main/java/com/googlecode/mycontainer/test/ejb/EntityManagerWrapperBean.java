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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.googlecode.mycontainer.test.EntityManagerWrapper;


@Stateless
public class EntityManagerWrapperBean implements EntityManagerWrapper,
		Serializable {

	private static final long serialVersionUID = -7571124415669864753L;

	@PersistenceContext(unitName = "test-pu")
	private EntityManager em;

	public <T> T find(Class<T> type, Object id) {
		return em.find(type, id);
	}

	public <T> T merge(T t) {
		return em.merge(t);
	}

	public <T> T persist(T t) {
		em.persist(t);
		return t;
	}

	public void remove(Object obj) {
		obj = em.merge(obj);
		em.remove(obj);
	}

}
