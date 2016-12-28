package com.googlecode.mycontainer.darkproxy;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;
import com.googlecode.mycontainer.util.Util;

public class DarkProxyFilterTest extends AbstractTestCase {

	private Thread respThread;
	private Thread reqThread;
	private Throwable exp;

	@Override
	public void boot() throws Exception {
		super.boot();

		exp = null;
	}

	@Override
	public void shutdown() throws Exception {
		Util.join(respThread);
		Util.join(reqThread);
		super.shutdown();
	}

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

		Util.join(reqThread);
		Util.join(respThread);
		if (exp != null) {
			throw new RuntimeException("error on test thread: " + exp);
		}
	}

	private void forwardResponse() {
		respThread = new Thread() {
			public void run() {
				try {
					changeResponse();
				} catch (Throwable e) {
					exp = e;
				}
			}

		};
		respThread.start();
	}

	private void changeResponse() {
		Util.sleep(2000L);
		List<String> conns = getConns();
		assertEquals(1, conns.size());

		assertEquals("\"OK\"",
				Util.readURL("http://localhost:8380/_darkproxy/response/proceed?id=" + conns.get(0), "UTF-8"));
	}

	private void forwardRequest() {
		reqThread = new Thread() {
			public void run() {
				try {
					changeRequest();
				} catch (Throwable e) {
					exp = e;
				}
			}

		};
		reqThread.start();
	}

	private void changeRequest() {
		Util.sleep(500L);
		List<String> conns = getConns();
		assertEquals(1, conns.size());

		assertEquals("\"OK\"",
				Util.readURL("http://localhost:8380/_darkproxy/request/proceed?id=" + conns.get(0), "UTF-8"));
	}

	@SuppressWarnings("unchecked")
	private List<String> getConns() {
		try {
			String str = Util.readAll(new URL("http://localhost:8380/_darkproxy/conns"), "UTF-8");
			Type type = new TypeToken<List<String>>() {
			}.getType();
			List<String> ids = (List<String>) JSON.parse(str, type);
			return ids;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

}
