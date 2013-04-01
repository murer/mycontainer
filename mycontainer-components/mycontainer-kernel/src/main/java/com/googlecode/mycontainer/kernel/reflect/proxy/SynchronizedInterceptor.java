package com.googlecode.mycontainer.kernel.reflect.proxy;

public class SynchronizedInterceptor extends ContextInterceptor {

	private static final long serialVersionUID = 5446569992627860866L;

	public Object intercept(Request request, ProxyChain chain) throws Throwable {
		Object impl = request.getImpl();
		synchronized (impl) {
			return chain.proceed(request);
		}
	}

}
