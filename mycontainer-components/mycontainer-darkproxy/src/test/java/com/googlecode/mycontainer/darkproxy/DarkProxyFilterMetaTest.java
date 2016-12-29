package com.googlecode.mycontainer.darkproxy;

import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.googlecode.mycontainer.util.Util;

public class DarkProxyFilterMetaTest extends AbstractTestCase {

	@Test
	public void testFilter() throws Exception {
		assertEquals("\"OK\"", Util.readURL("http://localhost:8380/_darkproxy/s/ping", "UTF-8"));

		assertSite("http://localhost:8380/_darkproxy/ping.txt", "text/plain; charset=UTF-8", "OK");
	}

	private void assertSite(String url, String contentType, String body) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		try {
			assertEquals(contentType, conn.getHeaderField("Content-Type"));
			assertEquals(body, Util.readAll(conn.getInputStream(), "UTF-8").trim());
		} finally {
			Util.close(conn);
		}
	}

}
