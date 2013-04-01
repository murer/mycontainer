package com.googlecode.mycontainer.commons.httpclient;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

public class PostWebRequest extends WebRequest {

	private HttpPost method;

	// private final List<Part> parts = new ArrayList<Part>();

	public PostWebRequest(WebClient client) {
		super(client);
	}

	@Override
	protected HttpRequestBase createRequest(String url) {
		method = new HttpPost(url);
		return method;
	}

	@Override
	public WebResponse invoke() {
		// if (parts.size() > 0) {
		//
		// NameValuePair[] parameters = method.getParameters();
		// for (int i = 0; i < parameters.length; i++) {
		// NameValuePair pair = parameters[i];
		// parts.add(i, new StringPart(pair.getName(), pair.getValue()));
		// }
		//
		// Part[] array = parts.toArray(new Part[parts.size()]);
		// MultipartRequestEntity entity = new MultipartRequestEntity(array,
		// method.getParams());
		// method.setRequestEntity(entity);
		// }

		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
					getParameters(), "UTF-8");
			method.setEntity(entity);

			return super.invoke();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	//
	// public void upload(String name, URL url) {
	// try {
	// parts.add(new FilePart(name, new File(url.getPath())));
	// } catch (FileNotFoundException e) {
	// throw new RuntimeException(e);
	// }
	// }

}
