package com.googlecode.mycontainer.darkproxy;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class DarkProxyHttp {

	public void create() {
		HttpClientBuilder builder = HttpClientBuilder.create();
		builder.disableAuthCaching();
		builder.disableAutomaticRetries();
		builder.disableConnectionState();
		builder.disableContentCompression();
		builder.disableCookieManagement();
		builder.disableRedirectHandling();
		builder.setUserAgent("");

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(2000);
		cm.setDefaultMaxPerRoute(1000);
		builder.setConnectionManager(cm);
	}

}
