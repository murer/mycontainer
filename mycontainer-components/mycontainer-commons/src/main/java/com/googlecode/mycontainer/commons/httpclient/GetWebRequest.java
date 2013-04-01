package com.googlecode.mycontainer.commons.httpclient;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;

public class GetWebRequest extends WebRequest {

	private HttpGet method;

	private URI uri;

	public GetWebRequest(WebClient client) {
		super(client);
	}

	@Override
	protected HttpRequestBase createRequest(String url) {
		try {
			uri = new URI(url);
			method = new HttpGet(uri);
			return method;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public WebResponse invoke() {
		try {
			String format = URLEncodedUtils.format(getParameters(), "UTF-8");
			method.setURI(new URI(uri.toString() + "?" + format));
			return super.invoke();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

}
