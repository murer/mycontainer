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

package com.googlecode.mycontainer.jpa.reflect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;

import org.junit.Test;

import com.googlecode.mycontainer.kernel.reflect.ReflectUtil;

public class ReflectUtilTest {

	@Test
	public void testGetFields() {
		ReflectUtil util = new ReflectUtil(ReflectUtilTestObject1.class);
		List<Field> list = util.getFields();
		assertEquals(2, list.size());
		assertEquals("name", list.get(0).getName());
		assertEquals("height", list.get(1).getName());

		util = new ReflectUtil(ReflectUtilTestObject2.class);
		list = util.getFields();
		assertEquals(4, list.size());
		assertEquals("id", list.get(0).getName());
		assertEquals("lastName", list.get(1).getName());
		assertEquals("name", list.get(2).getName());
		assertEquals("height", list.get(3).getName());
	}

	@Test
	public void testGetAnnotationFields() {
		ReflectUtil util = new ReflectUtil(ReflectUtilTestObject1.class);
		assertTrue(util.getFields(Id.class).isEmpty());

		List<Field> list = util.getFields(Column.class);
		assertEquals(1, list.size());
		assertEquals("name", list.get(0).getName());

		util = new ReflectUtil(ReflectUtilTestObject2.class);
		assertTrue(util.getFields(Id.class).isEmpty());
		list = util.getFields(Column.class);
		assertEquals(2, list.size());
		assertEquals("lastName", list.get(0).getName());
		assertEquals("name", list.get(1).getName());
	}
	
	private boolean hasAllMethods(List<Method> list, Method[] pojoMethods, Method[] objectMethods) {
		for (Method method : pojoMethods) {
			if (!list.contains(method)) {
				return false;
			}
		}
		for (Method method : objectMethods) {
			if (!list.contains(method)) {
				return false;
			}
		}
		return true;
	}
	
	@Test
	public void testGetMethods() {
		ReflectUtil util = new ReflectUtil(ReflectUtilTestObject1.class);
		List<Method> list = util.getMethods();
		
		Method pojoMethods[] = ReflectUtilTestObject1.class.getDeclaredMethods();
		Method objectMethods[] = ReflectUtilTestObject1.class.getSuperclass().getDeclaredMethods();
		
		assertEquals(list.size(), pojoMethods.length + objectMethods.length);
		assertTrue(hasAllMethods(list, pojoMethods, objectMethods));
	}

	@Test
	public void testGetAnnotationMethods() {
		ReflectUtil util = new ReflectUtil(ReflectUtilTestObject1.class);
		assertEquals(util.getMethods(Id.class).size(), 0);
		
		util = new ReflectUtil(ReflectUtilTestObject2.class);
		List<Method> list = util.getMethods(Id.class);
		assertEquals(list.size(), 1);
		assertEquals("getId", list.get(0).getName());
	}
	
	
}
