package com.googlecode.mycontainer.darkproxy.site;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.mycontainer.darkproxy.DarkProxyMediaTypeResolver;
import com.googlecode.mycontainer.darkproxy.DarkProxyMediaTypeResolver.MediaType;
import com.googlecode.mycontainer.darkproxy.DarkProxyMeta;
import com.googlecode.mycontainer.util.Util;

public class DarkProxySite {

	public static void serveFile(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String uri = DarkProxyMeta.uri(req);
		uri = uri.replaceAll("^/_darkproxy/", "");
		if (uri.startsWith("/")) {
			throw new RuntimeException("wrong: " + uri);
		}
		if (uri.indexOf("..") >= 0) {
			throw new RuntimeException("wrong: " + uri);
		}
		URL fileURL = DarkProxySite.class.getResource(uri);
		if (fileURL == null) {
			resp.sendError(404);
			return;
		}
		MediaType mediaType = DarkProxyMediaTypeResolver.me().mediaType(fileURL.getPath());
		if (mediaType == null) {
			mediaType = new MediaType().setMediaType("application/octet-stream");
		}
		resp.setContentType(mediaType.getMediaType());
		if (mediaType.isRequiredCharset()) {
			resp.setCharacterEncoding("UTF-8");
		}
		Util.copy(fileURL, resp.getOutputStream());
	}

}
