package com.googlecode.mycontainer.commons.httpclient;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

public abstract class WebRequest implements Closeable {

	protected final WebClient client;

	private RequestMethod method = RequestMethod.GET;

	private HttpRequestBase request;

	private final List<NameValuePair> parameters = new ArrayList<NameValuePair>();

	public WebRequest(WebClient client) {
		this.client = client;
	}

	public WebClient getClient() {
		return client;
	}

	public List<NameValuePair> getParameters() {
		return parameters;
	}

	public HttpRequestBase getRequest() {
		return request;
	}

	protected abstract HttpRequestBase createRequest(String url);

	public RequestMethod getMethod() {
		return method;
	}

	public void setMethod(RequestMethod method) {
		this.method = method;
	}

	public void setUrl(String url) {
		request = createRequest(url);
		Map<String, List<String>> headers = getClient().getHeaders();
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			for (String value : entry.getValue()) {
				request.addHeader(entry.getKey(), value);
			}
		}
	}

	public WebResponse invoke() {
		try {
			HttpResponse response = client.getClient().execute(request);
			WebResponse ret = new WebResponse(this, response);
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void setUri(String uri) {
		setUrl(client.getUrl() + uri);
	}

	public void close() throws IOException {
		request.abort();
	}

	public void addParameter(String name, String value) {
		parameters.add(new BasicNameValuePair(name, value));
	}

	public Future<WebResponse> invokeAsync() {
		throw new RuntimeException("not implemented yet");
	}

}
