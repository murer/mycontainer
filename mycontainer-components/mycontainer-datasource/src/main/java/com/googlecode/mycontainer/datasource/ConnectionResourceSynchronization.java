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

package com.googlecode.mycontainer.datasource;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;

import javax.transaction.Synchronization;

import com.googlecode.mycontainer.jta.TxEntry;


public class ConnectionResourceSynchronization implements Synchronization,
		Serializable {

	private static final long serialVersionUID = 7042065057427399288L;

	private final Map<TxEntry, Connection> connections;

	private final TxEntry entry;

	public ConnectionResourceSynchronization(
			Map<TxEntry, Connection> connections, TxEntry entry) {
		this.connections = connections;
		this.entry = entry;
	}

	public void afterCompletion(int arg0) {
		this.connections.remove(entry);
	}

	public void beforeCompletion() {

	}

}
