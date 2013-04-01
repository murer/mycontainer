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
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;

import org.junit.Test;

import com.googlecode.mycontainer.test.SQLService;


public class SQLServiceBeanTest extends AbstractTestCase {

	@Test
	public void testSQLService() throws Exception {
		InitialContext ic = new InitialContext();
		SQLService service = (SQLService) ic.lookup("SQLServiceBean/local");

		List<Map<String, Object>> result = service
				.executeQuery("select id, name from customerbean");
		assertTrue(result.isEmpty());

		int update = service.executeUpdate(
				"insert into customerbean(name) values (?);", "testname");
		assertEquals(1, update);

		result = service.executeQuery("select id, name from customerbean");
		assertEquals(1, result.size());
		assertEquals("testname", result.get(0).get("name"));
	}

}
