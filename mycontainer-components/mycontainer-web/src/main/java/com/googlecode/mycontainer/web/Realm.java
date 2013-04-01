/*
 * Copyright 2009 Whohoo Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.googlecode.mycontainer.web;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Realm implements Serializable {

	private static final long serialVersionUID = -1858492564401032084L;

	public static class UserRole implements Serializable {
		private static final long serialVersionUID = -8567344310671232832L;
		private final String user;
		private final String password;
		private final Set<String> roles = new HashSet<String>();

		public UserRole(String user, String password) {
			super();
			this.user = user;
			this.password = password;
		}

		public String getUser() {
			return user;
		}

		public String getPassword() {
			return password;
		}

		public Set<String> getRoles() {
			return roles;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((user == null) ? 0 : user.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			UserRole other = (UserRole) obj;
			if (user == null) {
				if (other.user != null)
					return false;
			} else if (!user.equals(other.user))
				return false;
			return true;
		}

	}

	private final Map<String, UserRole> users = new HashMap<String, UserRole>();

	private final String name;

	public Realm(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Map<String, UserRole> getUsers() {
		return users;
	}

	public void addUser(String user, String password) {
		users.put(user, new UserRole(user, password));
	}

	public void addRole(String user, String role) {
		users.get(user).getRoles().add(role);
	}

	public void config(String user, String password, String... roles) {
		addUser(user, password);
		for (String role : roles) {
			addRole(user, role);
		}
	}

}
