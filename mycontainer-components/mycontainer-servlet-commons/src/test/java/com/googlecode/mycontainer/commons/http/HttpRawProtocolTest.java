package com.googlecode.mycontainer.commons.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import com.googlecode.mycontainer.commons.http.HttpRawProtocol;
import com.googlecode.mycontainer.commons.http.HttpRawProtocol.State;
import com.googlecode.mycontainer.commons.io.IOUtil;
import com.googlecode.mycontainer.commons.servlet.AbstractTestCase;

public class HttpRawProtocolTest extends AbstractTestCase {

	@Test
	public void testGet() throws Exception {
		HttpRawProtocol conn = createConnection();
		try {
			conn.getState().check(State.CREATED);
			conn.connect("localhost", 8380);
			conn.setSoTimeout(1);
			conn.getState().check(State.READY);
			conn.sendRequest("GET", "/jseng/echo/test.txt?m=xyz&sleep=500",
					"HTTP/1.1");
			conn.getState().check(State.SENDING_HEADERS);
			conn.sendHeader("Host", "localhost:8380");
			conn.getState().check(State.SENDING_HEADERS);
			conn.sendHeaderFinished();
			conn.getState().check(State.UPLOADING);
			assertResponse(conn);
		} finally {
			IOUtil.close(conn);
		}
		conn.getState().check(State.CLOSED);
	}

	private HttpRawProtocol createConnection() {
		HttpRawProtocol ret = new HttpRawProtocol();
		return ret;
	}

	@Test
	public void testPost() throws Exception {
		HttpRawProtocol conn = createConnection();
		try {
			conn.getState().check(State.CREATED);
			conn.connect("localhost", 8380);
			conn.setSoTimeout(1);
			conn.getState().check(State.READY);
			conn.sendRequest("POST", "/jseng/echo/test.txt", "HTTP/1.1");
			conn.getState().check(State.SENDING_HEADERS);
			conn.sendHeader("Host", "localhost:8380");
			conn.getState().check(State.SENDING_HEADERS);
			conn.sendHeader("Content-Type", "application/x-www-form-urlencoded");
			conn.getState().check(State.SENDING_HEADERS);
			String params = "m=xyz&sleep=500";
			conn.sendHeader("Content-Length", Integer.toString(params.length()));
			conn.getState().check(State.SENDING_HEADERS);
			conn.sendHeaderFinished();
			conn.getState().check(State.UPLOADING);
			conn.sendBytes(params.getBytes());
			conn.getState().check(State.UPLOADING);
			assertResponse(conn);
		} finally {
			IOUtil.close(conn);
		}
		conn.getState().check(State.CLOSED);
	}

	private void assertResponse(HttpRawProtocol conn) throws Exception {
		long time = System.currentTimeMillis();
		assertNull(conn.readProtocolVersion());
		assertTrue(System.currentTimeMillis() - time < 500);
		int tries = 1;
		while (true) {
			tries++;
			String version = conn.readProtocolVersion();
			if (version != null) {
				assertEquals("HTTP/1.1", version);
				break;
			}
			Thread.sleep(100);
		}
		assertTrue(tries > 2);
		conn.getState().check(State.READING_HEADERS);
		conn.setSoTimeout(1);
		assertEquals(200, conn.readCode());
		conn.getState().check(State.READING_HEADERS);
		assertEquals("OK", conn.readCodeMessage());
		conn.getState().check(State.READING_HEADERS);
		String contentType = null;
		while (true) {
			String header = conn.readHeader();
			if (conn.getState().equals(State.DOWNLOADING)) {
				break;
			}
			if (header == null) {
				continue;
			}
			String[] values = conn.readHeaderValues();
			assertTrue(values.length >= 1);
			if ("Content-Type".equals(header)) {
				contentType = values[0];
			}
		}
		conn.getState().check(State.DOWNLOADING);
		assertEquals("text/plain;charset=ISO-8859-1", contentType);

		InputStream in = conn.getInputStream();
		StringBuilder content = new StringBuilder();
		IOUtil.copy(new InputStreamReader(in), content);
		assertEquals("xyz", content.toString());
		conn.getState().check(State.DOWNLOADING);
	}
}
