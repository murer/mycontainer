package com.googlecode.mycontainer.commons.servlet.json;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestService {

	public int sum(int a, int b) {
		return a + b;
	}

	public void multiply(HttpServletRequest req, HttpServletResponse resp,
			FilterChain chain) {
		try {
			int a = Integer.parseInt(req.getParameter("a"));
			int b = Integer.parseInt(req.getParameter("b"));
			int ret = a * b;
			resp.addHeader("result", Integer.toString(ret));
			chain.doFilter(req, resp);
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
	}

}
