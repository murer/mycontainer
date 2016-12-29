package com.googlecode.mycontainer.darkproxy;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;

import com.googlecode.mycontainer.util.Util;

public class DarkProxyFilterMetaTest extends AbstractTestCase {

	@Test
	public void testFilter() throws Exception {
		assertEquals("\"OK\"", DarkProxyHttp.me().readURL("http://localhost:8380/_darkproxy/s/ping", "UTF-8"));

		assertSite("http://localhost:8380/_darkproxy/ping.txt", "text/plain; charset=UTF-8", "OK");
	}

	private void assertSite(String url, String contentType, String body) throws Exception {
		CloseableHttpResponse resp = null;
		InputStream in = null;
		try {
			resp = DarkProxyHttp.me().execute(new HttpGet(url));
			HttpEntity entity = resp.getEntity();
			in = entity.getContent();
			assertEquals(contentType, entity.getContentType().getValue());
			assertEquals(body, Util.readAll(in, "UTF-8").trim());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Util.close(in);
			Util.close(resp);
		}
	}

}
