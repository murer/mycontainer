package com.googlecode.mycontainer.gae.web;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.taskqueue.dev.LocalTaskQueueCallback;
import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;
import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest.Header;
import com.google.appengine.repackaged.com.google.protobuf.ByteString;
import com.googlecode.mycontainer.commons.http.HttpClientRequestService;
import com.googlecode.mycontainer.commons.http.Request;
import com.googlecode.mycontainer.commons.http.Response;
import com.googlecode.mycontainer.commons.http.Type;

public class TaskCallback implements LocalTaskQueueCallback {

	private static final long serialVersionUID = -1014385841687576970L;

	public int execute(URLFetchRequest req) {
		try {

			HttpClientRequestService service = new HttpClientRequestService("http://localhost:8380");

			String path = new URI(req.getUrl()).getPath();
			Type type = Type.valueOf(req.getMethod().name());
			Request request = new Request(type, path);

			if (req.hasPayload()) {
				String text = getHeader(req, "X-Payload-Text");
				String contentType = getHeader(req, "Content-Type");
				request.contentType(contentType);
				ByteString payload = req.getPayload();
				if (text == null) {
					request.content(payload.toByteArray());
				} else {
					request.content(payload.toString(text));
				}
			}
			Response response = service.execute(request);
			return response.code();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private String getHeader(URLFetchRequest req, String name) {
		List<Header> list = req.getHeaderList();
		for (Header header : list) {
			if (header.getKey().equals(name)) {
				return header.getValue();
			}
		}
		return null;
	}

	public void initialize(Map<String, String> map) {
	}
}
