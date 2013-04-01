package com.googlecode.mycontainer.commons.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EchoServlet extends HttpServlet {

	private static final long serialVersionUID = -3296497246890978842L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String msg = req.getParameter("m");
			String sleep = req.getParameter("sleep");
			if (sleep != null) {
				long time = Long.parseLong(sleep);
				Thread.sleep(time);
			}
			resp.getWriter().write("" + msg);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
