package com.googlecode.mycontainer.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SpecServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp);

	public void doPost(HttpServletRequest req, HttpServletResponse resp);

	public void doDelete(HttpServletRequest req, HttpServletResponse resp);

	public void doPut(HttpServletRequest req, HttpServletResponse resp);

}
