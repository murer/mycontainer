package com.googlecode.mycontainer.darkproxy;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.googlecode.mycontainer.util.Util;

public class DarkProxyFiles {

	public static File getFile(String dest, Long id, String ext) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		String dir = format.format(new Date(id));
		String strid = strid(id);
		String filename = String.format("%s/%s.%s", dir, strid, ext);
		File ret = new File(dest, filename);
		return ret;
	}

	private static String strid(Long id) {
		char[] array = Long.toHexString(id).toCharArray();
		char[] padding = "0000000000000000".toCharArray();
		int o = padding.length - array.length;
		System.arraycopy(array, 0, padding, o, array.length);
		return new String(padding);
	}

	public static void write(File file, String json) {
		file.getParentFile().mkdirs();
		Util.write(file, json, "UTF-8");
	}

	public static void write(File file, InputStream in) {
		file.getParentFile().mkdirs();
		Util.write(file, in);
	}

}
