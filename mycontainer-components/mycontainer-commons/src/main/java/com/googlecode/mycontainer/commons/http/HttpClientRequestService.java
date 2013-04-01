package com.googlecode.mycontainer.commons.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.googlecode.mycontainer.commons.util.CryptoUtil;

public class HttpClientRequestService implements RequestService {

	private String repository;
	private String pass;
	private String user;

	public HttpClientRequestService() {

	}

	public HttpClientRequestService(String repository) {
		this.repository = repository;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public HttpClient getClient() {
		DefaultHttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager());
		return client;
	}

	public Response execute(Request req) {
		try {
			HttpClient client = getClient();
			HttpRequestBase request = createRequest(req);
			if (user != null) {
				String auth = user + ':' + pass;
				auth = CryptoUtil.encodeBase64(auth.getBytes());
				request.addHeader(new BasicHeader("Authorization", "Basic " + auth));
			}
			HttpResponse response = client.execute(request);
			Response resp = createResponse(response, req);
			return resp;
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Response createResponse(HttpResponse response, Request req) {
		try {
			Response ret = new Response(req);
			Header[] headers = response.getAllHeaders();
			for (Header header : headers) {
				String name = header.getName();
				HeaderElement[] elements = header.getElements();
				for (HeaderElement headerElement : elements) {
					ret.headers().add(name, headerElement.toString());
				}
			}
			ret.code(response.getStatusLine().getStatusCode());
			ret.message(response.getStatusLine().getReasonPhrase());
			HttpEntity entity = response.getEntity();
			if (entity != null && entity.getContentType() != null) {
				ret.content(EntityUtils.toByteArray(entity));
			}
			return ret;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private HttpRequestBase createRequest(Request req) {
		try {
			Type type = req.type();
			URI uri = req.toURI(repository);
			HttpRequestBase ret;
			switch (type) {
			case GET:
				ret = new HttpGet(uri);
				break;
			case DELETE:
				ret = new HttpDelete(uri);
				break;
			case PUT:
				ret = new HttpPut(uri);
				break;
			case POST:
				ret = new HttpPost(uri);
				break;
			default:
				throw new RuntimeException("not supported: " + type);
			}

			List<NamePair> pairs = req.headers().pairs();
			for (NamePair header : pairs) {
				ret.addHeader(header.name(), header.value());
			}

			if (ret instanceof HttpEntityEnclosingRequestBase) {
				HttpEntityEnclosingRequestBase base = (HttpEntityEnclosingRequestBase) ret;
				Content content = req.content();
				AbstractHttpEntity entity;
				if (!content.isBinary()) {
					entity = new StringEntity(content.text(), content.charset());
				} else {
					entity = new ByteArrayEntity(content.data());
				}
				entity.setContentType(content.mediaType());
				entity.setContentEncoding(content.charset());
				base.setEntity(entity);
			}
			return ret;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void setUser(String user, String pass) {
		this.user = user;
		this.pass = pass;
	}

}
