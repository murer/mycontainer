package com.googlecode.mycontainer.darkproxy;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.mycontainer.util.Util;

public class DarkProxyMeta {

	public static void filter(DarkProxy proxy, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String uri = uri(req);
		if (uri.startsWith("/_darkproxy/s/ping")) {
			writeJson(resp, "OK");
		} else if (uri.startsWith("/_darkproxy/s/conns")) {
			writeJson(resp, proxy.getConns().keySet());
		} else if (method(req, "GET") && uri.startsWith("/_darkproxy/s/request.json")) {
			download("req.json", proxy, req, resp);
		} else if (method(req, "GET") && uri.startsWith("/_darkproxy/s/response.body")) {
			download("resp.body", proxy, req, resp);
		} else if (method(req, "POST", "PUT") && uri.startsWith("/_darkproxy/s/request.json")) {
			upload("req.json", proxy, req, resp);
		} else if (method(req, "POST", "PUT") && uri.startsWith("/_darkproxy/s/response.body")) {
			upload("resp.body", proxy, req, resp);
		} else if (uri.startsWith("/_darkproxy/s/request/proceed")) {
			requestProceed(proxy, req, resp);
		} else if (uri.startsWith("/_darkproxy/s/response/proceed")) {
			responseProceed(proxy, req, resp);
		} else {
			resp.sendError(404);
		}
	}

	private static boolean method(HttpServletRequest req, String... methods) {
		String m = req.getMethod();
		for (String method : methods) {
			if (method.equals(m)) {
				return true;
			}
		}
		return false;
	}

	private static void upload(String ext, DarkProxy proxy, HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Long id = paramLong(req, "id");
		File file = DarkProxyFiles.getFile(proxy.getDest(), id, ext);
		Util.write(file, req.getInputStream());
		if ("req.json".equals(ext)) {
			proxy.getRequest(id).reload(proxy.getDest());
		} else if ("resp.json".equals(ext)) {
			proxy.getResponse(id).reload(proxy.getDest());
		}
	}

	private static void download(String ext, DarkProxy proxy, HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Long id = paramLong(req, "id");
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
		DarkProxyResponse response = proxy.getResponse(id);
		response.proceed();
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
