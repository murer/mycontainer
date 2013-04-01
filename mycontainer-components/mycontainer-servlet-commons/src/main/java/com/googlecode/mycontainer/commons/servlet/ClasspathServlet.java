package com.googlecode.mycontainer.commons.servlet;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.mycontainer.commons.io.IOUtil;

public class ClasspathServlet extends HttpServlet {

	private static final long serialVersionUID = -3296497246890978842L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String path = ServletUtil.getUserPath(req);
		path = path.substring(1);
		URL url = getClass().getClassLoader().getResource(path);
		if (url == null) {
			throw new RuntimeException("path not found in classpath: " + path);
		}
		IOUtil.copyAll(url, resp.getOutputStream());
	}

}
