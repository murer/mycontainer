package com.googlecode.mycontainer.ejb.interceptor;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.PostConstruct;

import com.googlecode.mycontainer.kernel.reflect.ReflectUtil;
import com.googlecode.mycontainer.kernel.reflect.proxy.ProxyChain;
import com.googlecode.mycontainer.kernel.reflect.proxy.Request;

public class DefaultEJBCallbackInterceptor extends
		AbstractEJBCallbackInterceptor {

	private static final long serialVersionUID = 6506426121914781363L;

	@Override
	public Object interceptBusiness(Request request, ProxyChain chain)
			throws Throwable {
		return chain.proceed(request);
	}

	public void ejbPreConstruct(Request request, ProxyChain chain) {

	}

	public Object ejbPostConstruct(Request request, ProxyChain chain)
			throws Throwable {
		ReflectUtil util = new ReflectUtil(request.getImpl().getClass());
		List<Method> methods = util.getMethods(PostConstruct.class);
		if (methods.size() > 1) {
			throw new RuntimeException(
					"You can define just one callback by the spec (PostConstruct): "
							+ request);
		}
		if (methods.isEmpty()) {
			return null;
		}
		Method callback = methods.get(0);
		Request req = request.copy();
		req.setMethod(callback);

		return chain.proceed(req);
	}

}
