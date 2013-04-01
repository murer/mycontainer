package com.googlecode.mycontainer.commons.file;

import java.net.FileNameMap;
import java.net.URLConnection;

public class ContentTypeUtil {

	private ContentTypeUtil() {
	}

	public static String getContentTypeByPath(String ext) {
		ext = PathUtil.getExtention(ext);
		return getContentType(ext);
	}

	public static String getContentType(String ext) {
		FileNameMap map = URLConnection.getFileNameMap();
		String ret = map.getContentTypeFor("x." + ext);
		return ret;
	}

}
