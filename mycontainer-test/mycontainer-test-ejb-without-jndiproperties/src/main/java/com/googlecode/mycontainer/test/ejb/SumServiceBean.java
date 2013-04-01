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

import java.text.DecimalFormat;
import java.text.ParseException;

import javax.ejb.Stateless;


import com.googlecode.mycontainer.annotation.MycontainerLocalBinding;
import com.googlecode.mycontainer.test.SumService;

@Stateless
@MycontainerLocalBinding("service/SumServiceBean/local")
public class SumServiceBean implements SumService {

	public Integer sum(Integer a, Integer b) {
		return a + b;
	}

	public Integer divide(Integer a, Integer b) {
		return a / b;
	}

	public Integer sum(String a, String b) throws ParseException {
		DecimalFormat formatter = new DecimalFormat("0");
		Number n1 = formatter.parse(a);
		Number n2 = formatter.parse(b);
		return n1.intValue() + n2.intValue();
	}

}
