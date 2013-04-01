package com.googlecode.mycontainer.commons.httpclient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.googlecode.mycontainer.commons.file.PathUtil;

public class JsonInvocationHandler implements InvocationHandler {

	private WebClient client;
	private String uri;

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		JsonWebRequest request = new JsonWebRequest(
				client.createRequest(RequestMethod.POST));
		String uri = PathUtil.concatPath(this.uri, method.getName());
		request.getRequest().setUri(uri);
		if (args != null) {
			for (Object arg : args) {
				request.addParameter("args", arg);
			}
		}
		Object ret = request.invoke();
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static <T> T proxy(WebClient client, Class<T> clazz, String uri) {
		JsonInvocationHandler handler = new JsonInvocationHandler();
		handler.client = client;
		handler.uri = uri;
		Class<T>[] array = new Class[] { clazz };
		T ret = (T) Proxy.newProxyInstance(
				JsonInvocationHandler.class.getClassLoader(), array, handler);
		return ret;
	}

}
