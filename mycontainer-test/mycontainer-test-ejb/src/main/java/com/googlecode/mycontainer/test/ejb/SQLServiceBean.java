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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import com.googlecode.mycontainer.test.SQLService;


@Stateless
public class SQLServiceBean implements SQLService {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SQLServiceBean.class);

	@Resource(mappedName = "TestDS")
	private DataSource ds;

	public List<Map<String, Object>> executeQuery(String sql, Object... values) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = ds.getConnection();
			ps = conn.prepareStatement(sql);
			int i = 1;
			for (Object param : values) {
				ps.setObject(i++, param);
			}
			rs = ps.executeQuery();
			List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
			read(rs, ret);
			return ret;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(rs);
			close(ps);
			close(conn);
		}
	}

	private void read(ResultSet rs, List<Map<String, Object>> ret) {
		try {
			ResultSetMetaData meta = rs.getMetaData();
			int columnCount = meta.getColumnCount();
			String[] columns = new String[columnCount];
			for (int i = 0; i < columnCount; i++) {
				columns[i] = meta.getColumnName(i + 1);
			}

			while (rs.next()) {
				Map<String, Object> row = new HashMap<String, Object>();
				for (String column : columns) {
					Object value = rs.getObject(column);
					row.put(column.toLowerCase(), value);
				}
				ret.add(row);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void close(Connection c) {
		if (c != null) {
			try {
				c.close();
			} catch (SQLException e) {
				LOG.error("Error closing", e);
			}
		}
	}

	private void close(Statement c) {
		if (c != null) {
			try {
				c.close();
			} catch (SQLException e) {
				LOG.error("Error closing", e);
			}
		}
	}

	private void close(ResultSet c) {
		if (c != null) {
			try {
				c.close();
			} catch (SQLException e) {
				LOG.error("Error closing", e);
			}
		}
	}

	public int executeUpdate(String sql, Object... values) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = ds.getConnection();
			ps = conn.prepareStatement(sql);
			int i = 1;
			for (Object param : values) {
				ps.setObject(i++, param);
			}
			int ret = ps.executeUpdate();
			return ret;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(ps);
			close(conn);
		}
	}

}
