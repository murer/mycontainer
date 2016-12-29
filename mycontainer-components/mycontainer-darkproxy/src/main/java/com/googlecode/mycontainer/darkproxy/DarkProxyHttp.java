package com.googlecode.mycontainer.darkproxy;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.googlecode.mycontainer.util.Util;

public class DarkProxyHttp {

	private static Object MUTEX = new Object();
	private static DarkProxyHttp me = null;

	private CloseableHttpClient client;

	public static DarkProxyHttp me() {
		if (me == null) {
			synchronized (MUTEX) {
				if (me == null) {
					DarkProxyHttp http = new DarkProxyHttp();
					http.init();
					me = http;
				}
			}
		}
		return me;
	}

	public void init() {
		HttpClientBuilder builder = HttpClientBuilder.create();
		builder.disableAuthCaching();
		builder.disableAutomaticRetries();
		builder.disableConnectionState();
		builder.disableContentCompression();
		builder.disableCookieManagement();
		builder.disableRedirectHandling();
		builder.setUserAgent("DarkProxy");

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(2000);
		cm.setDefaultMaxPerRoute(1000);
		builder.setConnectionManager(cm);

		client = builder.build();
	}

	public String readURL(String url, String charset) {
		CloseableHttpResponse resp = null;
		InputStream in = null;
		try {
			resp = client.execute(new HttpGet(url));
			HttpEntity entity = resp.getEntity();
			in = entity.getContent();
			return Util.readAll(in, charset);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Util.close(in);
			Util.close(resp);
		}
	}

	public CloseableHttpResponse execute(HttpUriRequest req) {
		try {
			return client.execute(req);
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
