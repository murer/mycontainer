package com.googlecode.mycontainer.darkproxy;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.reflect.TypeToken;
import com.googlecode.mycontainer.util.Util;

public class DarkProxyFilterTest extends AbstractTestCase {

	private static final Logger LOG = LoggerFactory.getLogger(DarkProxyFilterTest.class);

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

		HttpPost req = new HttpPost("http://localhost:8380/any?n=1&n=2");
		req.addHeader("x-sum", "8");
		req.setEntity(new StringEntity("4"));
		CloseableHttpResponse resp = DarkProxyHttp.me().execute(req);
		try {
			assertEquals(200, resp.getStatusLine().getStatusCode());
			assertEquals("text/plain; charset=UTF-8", resp.getFirstHeader("Content-Type").getValue());
			assertEquals("15", resp.getFirstHeader("x-sum-resp").getValue());
			assertEquals("15", EntityUtils.toString(resp.getEntity()));
		} finally {
			Util.close(resp);
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
		DarkProxyResponse resp = waitForResponse();
		assertEquals("\"OK\"",
				Util.readURL("http://localhost:8380/_darkproxy/s/response/proceed?id=" + resp.getId(), "UTF-8"));
	}

	private DarkProxyResponse waitForResponse() {
		long before = System.currentTimeMillis();
		while (true) {
			SortedMap<Long, DarkProxyConn> conns = getConns();
			if (!conns.isEmpty()) {
				DarkProxyResponse ret = conns.get(conns.firstKey()).getResponse();
				if (ret != null) {
					return ret;
				}
			}
			Util.sleep(100L);
			if (System.currentTimeMillis() > before + 2000L) {
				throw new RuntimeException("timeout");
			}
		}
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
		DarkProxyRequest req = waitForRequest();

		String str = Util.readURL("http://localhost:8380/_darkproxy/s/request.json?id=" + req.getId(), "UTF-8");
		req = JSON.parse(str, DarkProxyRequest.class);
		req.setUri("/test/sum");

		assertEquals(200, Util.put("http://localhost:8380/_darkproxy/s/request.json?id=" + req.getId(),
				"application/json", JSON.stringify(req)));

		assertEquals("\"OK\"",
				Util.readURL("http://localhost:8380/_darkproxy/s/request/proceed?id=" + req.getId(), "UTF-8"));
	}

	private DarkProxyRequest waitForRequest() {
		long before = System.currentTimeMillis();
		while (true) {
			SortedMap<Long, DarkProxyConn> conns = getConns();
			if (!conns.isEmpty()) {
				return conns.get(conns.firstKey()).getRequest();
			}
			Util.sleep(100L);
			if (System.currentTimeMillis() > before + 2000L) {
				throw new RuntimeException("timeout");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private SortedMap<Long, DarkProxyConn> getConns() {
		try {
			String str = Util.readAll(new URL("http://localhost:8380/_darkproxy/s/conns"), "UTF-8");
			Type type = new TypeToken<Map<Long, DarkProxyConn>>() {
			}.getType();
			Map<Long, DarkProxyConn> ids = (Map<Long, DarkProxyConn>) JSON.parse(str, type);
			return new TreeMap<Long, DarkProxyConn>(ids);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

}
