package com.googlecode.mycontainer.gae.test;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexServlet extends HttpServlet {

	private static final long serialVersionUID = -2051343931124948865L;

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory
			.getLogger(IndexServlet.class);

	private MessageService getMessageService(HttpServletRequest request) {
		String type = request.getParameter("type");
		if (type == null) {
			type = "DaS";
		}
		return getMessageService(type);
	}

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

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (log.isDebugEnabled()) {
			log.debug("doGet");
		}

		// delete
		if (request.getParameter("id") != null) {
			deleteMessage(request);

			response.sendRedirect("index");
			return;
		}

		// get
		Collection<Message> messages = getMessageService(request).getAll();
		request.setAttribute("messages", messages);

		if (log.isDebugEnabled()) {
			log.debug("messages: " + messages);
		}

		forward(request, response, "index.jsp");
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (log.isDebugEnabled()) {
			log.debug("doPost");
		}

		// create
		createMessage(request);
		response.sendRedirect("index");
	}

	protected void createMessage(HttpServletRequest request) {
		String text = request.getParameter("text");
		if (log.isDebugEnabled()) {
			log.debug("creating message with text: " + text);
		}

		Message message = new Message();
		message.setText(text);
		getMessageService(request).create(message);
	}

	protected void deleteMessage(HttpServletRequest request) throws IOException {
		Long id = Long.valueOf(request.getParameter("id"));
		if (log.isDebugEnabled()) {
			log.debug("deleting message with id: " + id);
		}
		getMessageService(request).deleteById(id);
	}

	/**
	 * Forwards request and response to given path. Handles any exceptions
	 * caused by forward target by printing them to logger.
	 * 
	 * @param request
	 * @param response
	 * @param path
	 */
	protected void forward(HttpServletRequest request,
			HttpServletResponse response, String path) {
		try {
			RequestDispatcher rd = request.getRequestDispatcher(path);
			rd.forward(request, response);
		} catch (Throwable tr) {
			if (log.isErrorEnabled()) {
				log.error("Cought Exception: " + tr.getMessage());
				log.debug("StackTrace:", tr);
			}
		}
	}

}
