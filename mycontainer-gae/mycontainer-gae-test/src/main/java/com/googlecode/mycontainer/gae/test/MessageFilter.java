package com.googlecode.mycontainer.gae.test;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.mycontainer.commons.json.JsonHandler;

public class MessageFilter implements Filter {

	private static final long serialVersionUID = -2051343931124948865L;

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory
			.getLogger(MessageFilter.class);

	private MessageService getMessageService(String type) {
		type = MessageServiceType.valueOf(type).name();
		try {
			String className = MessageService.class.getName() + type;
			Class<?> clazz = Class.forName(className);
			MessageService ret = (MessageService) clazz.newInstance();
			return ret;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {

	}

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		MessageService service = getMessageService(MessageServiceType.DaS
				.name());
		if (request.getRequestURI().endsWith("create")) {
			String json = request.getParameter("x");
			Message msg = JsonHandler.instance().parse(json, Message.class);
			service.create(msg);
			return;
		}
		if (request.getRequestURI().endsWith("list")) {
			List<Message> list = service.getAll();
			String json = JsonHandler.instance().format(list);
			resp.getWriter().print(json);
			return;
		}

		throw new RuntimeException("not supported");
	}

	public void destroy() {

	}

}
