package com.googlecode.mycontainer.commons.servlet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PrintServlet extends HttpServlet {

	private static final long serialVersionUID = -3296497246890978842L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("uri", req.getRequestURI());
		ret.put("parameters", req.getParameterMap());
		ret.put("requestAttributes", ServletUtil.getAttributes(req));
		ObjectOutputStream out = new ObjectOutputStream(resp.getOutputStream());
		out.writeObject(ret);
		out.flush();
	}

}
