package com.googlecode.mycontainer.darkproxy;

import static org.junit.Assert.assertEquals;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.googlecode.mycontainer.util.Util;

public class DarkProxyFilterTest extends AbstractTestCase {

	@Test
	public void testFilter() throws Exception {
		URL url = new URL("http://localhost:8380/test?a=1&b=2");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		try {
			conn.setDoOutput(true);
			conn.setRequestProperty("x-test", "my-value");
			conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
			OutputStream out = conn.getOutputStream();
			out.write("my body".getBytes());
			out.flush();
			assertEquals(200, conn.getResponseCode());
			assertEquals("text/plain; charset=UTF-8", conn.getHeaderField("Content-Type"));
			assertEquals("test", Util.readAll(conn.getInputStream(), "UTF-8"));
		} finally {
			Util.close(conn);
		}

		assertEquals("test", Util.readAll(url, "UTF-8"));
	}

}
