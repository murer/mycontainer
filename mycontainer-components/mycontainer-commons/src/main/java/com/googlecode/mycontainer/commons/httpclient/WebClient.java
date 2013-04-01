package com.googlecode.mycontainer.commons.httpclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class WebClient {

	private DefaultHttpClient client;

	private String url = "";

	private final Map<String, List<String>> headers = new HashMap<String, List<String>>();

	public WebClient() {
		client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUserAgent(params, "MycontainerWebClient/1.1");
		HttpProtocolParams.setUseExpectContinue(params, true);
		params.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);

		List<String> authpref = new ArrayList<String>();
		authpref.add(AuthPolicy.BASIC);
		authpref.add(AuthPolicy.DIGEST);
		client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authpref);

		client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
				30000);
		// client.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if (url == null) {
			url = "";
		}
		url = url.trim();
		if (url.length() > 0 && !url.endsWith("/")) {
			url += "/";
		}
		this.url = url;
	}

	public HttpClient getClient() {
		return client;
	}

	public void setClient(DefaultHttpClient client) {
		this.client = client;
	}

	public WebRequest createRequest(RequestMethod method) {
		if (RequestMethod.GET.equals(method)) {
			return new GetWebRequest(this);
		} else if (RequestMethod.POST.equals(method)) {
			return new PostWebRequest(this);
		}
		throw new RuntimeException("Method not supported: " + method);
	}

	public void setUser(String user, String pass) {
		client.getCredentialsProvider().setCredentials(
				new AuthScope("localhost", AuthScope.ANY_PORT,
						AuthScope.ANY_REALM),
				new UsernamePasswordCredentials(user, pass));

		// client.getParams().setAuthenticationPreemptive(true);
	}

	public WebResponse invoke(RequestMethod type, String uri, String... params) {
		WebRequest m = createRequest(type);
		m.setUri(uri);
		for (String param : params) {
			int idx = param.indexOf("=");
			String key = param.substring(0, idx);
			String value = param.substring(idx + 1);
			m.addParameter(key, value);
		}
		return m.invoke();
	}

	public void addHeader(String name, String header) {
		List<String> list = headers.get(name);
		if (list == null) {
			list = new ArrayList<String>();
			headers.put(name, list);
		}
		list.add(header);
	}

	public void setTimeout(long timeout) {
		client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
				(int) timeout);
	}

}
