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

package com.googlecode.mycontainer.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextWebServer {

	private String context;

	private String resources;

	private final List<ServletDesc> servlets = new ArrayList<ServletDesc>();

	private final List<FilterDesc> filters = new ArrayList<FilterDesc>();

	private final List<Object> listeners = new ArrayList<Object>();

	private final Map<String, String> initParameters = new HashMap<String, String>();

	private final Map<String, Object> attributes = new HashMap<String, Object>();

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getResources() {
		return resources;
	}

	public void setResources(String resources) {
		this.resources = resources;
	}

	public List<ServletDesc> getServlets() {
		return servlets;
	}

	public List<FilterDesc> getFilters() {
		return filters;
	}

	public List<Object> getListeners() {
		return listeners;
	}

	public Map<String, String> getInitParameters() {
		return initParameters;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

}
