package com.googlecode.mycontainer.darkproxy;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.googlecode.mycontainer.util.Util;

public class DarkProxyFilterTest extends AbstractTestCase {

	@Test
	public void testFilter() throws Exception {
		forwardRequest();
		forwardResponse();

		URL url = new URL("http://localhost:8380/repoz/docs.html?a=1&b=2");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		OutputStream out = null;
		InputStream in = null;
		try {
			String msg = "my body";
			conn.setDoOutput(true);
			conn.setRequestProperty("x-test", "my-value");
			conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
			conn.setRequestProperty("Content-Length", Integer.toString(msg.length()));
			out = conn.getOutputStream();
			out.write(msg.getBytes());
			out.flush();
			out.close();
			out = null;
			in = conn.getInputStream();

			assertEquals(200, conn.getResponseCode());
			assertEquals("text/html", conn.getHeaderField("Content-Type"));
			// assertEquals("test", Util.readAll(conn.getInputStream(),
			// "UTF-8"));
		} finally {
			Util.close(in);
			Util.close(out);
			Util.close(conn);
		}
	}

	private void forwardResponse() {
		Thread t = new Thread() {
			public void run() {
				Util.sleep(2000L);
				DarkProxyConn conn = proxy.getFirst();
				conn.getResponse().proceed();
			}
		};
		t.start();
	}

	private void forwardRequest() {
		Thread t = new Thread() {
			public void run() {
				Util.sleep(500L);
				DarkProxyConn conn = proxy.getFirst();
				conn.getRequest().proceed();
			}
		};
		t.start();
	}

}
