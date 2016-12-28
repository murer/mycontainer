package com.googlecode.mycontainer.darkproxy;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.mycontainer.util.Util;

public class DarkProxyMeta {

	public static void filter(DarkProxy proxy, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String uri = uri(req);
		if (uri.startsWith("/_darkproxy/ping")) {
			writeJson(resp, "OK");
		} else if (uri.startsWith("/_darkproxy/conns")) {
			writeJson(resp, proxy.getConns().keySet());
		} else if (uri.startsWith("/_darkproxy/download")) {
			download(proxy, req, resp);
		} else if (uri.startsWith("/_darkproxy/request/proceed")) {
			requestProceed(proxy, req, resp);
		} else if (uri.startsWith("/_darkproxy/response/proceed")) {
			responseProceed(proxy, req, resp);
		} else {
			resp.sendError(404);
		}
	}

	private static void download(DarkProxy proxy, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Long id = paramLong(req, "id");
		String ext = req.getParameter("ext");
		File file = DarkProxyFiles.getFile(proxy.getDest(), id, ext);
		if (ext.endsWith(".json")) {
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
		} else {
			resp.setContentType("application/octet-stream");
		}
		Util.read(file, resp.getOutputStream());
	}

	private static void responseProceed(DarkProxy proxy, HttpServletRequest req, HttpServletResponse resp) {
		Long id = paramLong(req, "id");
		proxy.getResponse(id).proceed();
		writeJson(resp, "OK");
	}

	private static void requestProceed(DarkProxy proxy, HttpServletRequest req, HttpServletResponse resp) {
		Long id = paramLong(req, "id");
		proxy.getRequest(id).proceed();
		writeJson(resp, "OK");
	}

	private static Long paramLong(HttpServletRequest req, String name) {
		String value = req.getParameter(name);
		if (value == null || value.length() == 0) {
			return null;
		}
		return new Long(value);
	}

	public static String uri(HttpServletRequest req) {
		String uri = req.getRequestURI();
		String contextPath = req.getContextPath();

		if (contextPath != null && !contextPath.isEmpty()) {
			uri = uri.replace(contextPath, "");
		}

		return uri;
	}

	public static void writeJson(HttpServletResponse resp, Object obj) {
		try {
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			String json = JSON.stringify(obj);
			resp.getWriter().write(json);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}
