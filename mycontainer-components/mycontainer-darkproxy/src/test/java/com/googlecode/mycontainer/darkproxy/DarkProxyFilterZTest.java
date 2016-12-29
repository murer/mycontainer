package com.googlecode.mycontainer.darkproxy;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.reflect.TypeToken;
import com.googlecode.mycontainer.util.Util;

public class DarkProxyFilterZTest extends AbstractTestCase {

	private static final Logger LOG = LoggerFactory.getLogger(DarkProxyFilterZTest.class);

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

		URL url = new URL("http://localhost:8380/any?n=1&n=2");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		OutputStream out = null;
		InputStream in = null;
		try {
			String msg = "4";
			conn.setDoOutput(true);
			conn.setRequestProperty("x-sum", "8");
			conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
			conn.setRequestProperty("Content-Length", Integer.toString(msg.length()));
			out = conn.getOutputStream();
			out.write(msg.getBytes());
			out.flush();
			out.close();
			out = null;
			in = conn.getInputStream();

			assertEquals(200, conn.getResponseCode());
			assertEquals("text/plain; charset=UTF-8", conn.getHeaderField("Content-Type"));
			assertEquals("15", conn.getHeaderField("x-sum-resp"));
			assertEquals("15", Util.readAll(conn.getInputStream(), "UTF-8"));
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
					LOG.error("error", e);
				}
			}

		};
		respThread.start();
	}

	private void changeResponse() {
		Util.sleep(2000L);
		List<Long> conns = getConns();
		assertEquals(1, conns.size());

		assertEquals("\"OK\"",
				Util.readURL("http://localhost:8380/_darkproxy/s/response/proceed?id=" + conns.get(0), "UTF-8"));
	}

	private void forwardRequest() {
		reqThread = new Thread() {
			public void run() {
				try {
					changeRequest();
				} catch (Throwable e) {
					exp = e;
					LOG.error("error", e);
				}
			}

		};
		reqThread.start();
	}

	private void changeRequest() {
		Util.sleep(500L);
		List<Long> conns = getConns();
		assertEquals(1, conns.size());

		String str = Util.readURL("http://localhost:8380/_darkproxy/s/request.json?id=" + conns.get(0), "UTF-8");
		DarkProxyRequest req = JSON.parse(str, DarkProxyRequest.class);
		req.setUri("/test/sum");

		assertEquals(200, Util.put("http://localhost:8380/_darkproxy/s/request.json?id=" + conns.get(0),
				"application/json", JSON.stringify(req)));

		assertEquals("\"OK\"",
				Util.readURL("http://localhost:8380/_darkproxy/s/request/proceed?id=" + conns.get(0), "UTF-8"));
	}

	@SuppressWarnings("unchecked")
	private List<Long> getConns() {
		try {
			String str = Util.readAll(new URL("http://localhost:8380/_darkproxy/s/conns"), "UTF-8");
			Type type = new TypeToken<Map<Long, DarkProxyConn>>() {
			}.getType();
			Map<Long, DarkProxyConn> ids = (Map<Long, DarkProxyConn>) JSON.parse(str, type);
			return new ArrayList<Long>(ids.keySet());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

}
