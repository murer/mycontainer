package com.googlecode.mycontainer.commons.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.googlecode.mycontainer.commons.io.IOUtil;

public class ZipUtil {

	public static byte[] gzip(String str, String charset) {
		try {
			byte[] data = str.getBytes(charset);
			return gzip(data);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] gzip(byte[] data) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			GZIPOutputStream out = new GZIPOutputStream(bout);
			out.write(data);
			out.close();
			return bout.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String gunzipString(byte[] gz, String string) {
		try {
			byte[] data = gunzip(gz);
			return new String(data, string);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private static byte[] gunzip(byte[] gz) {
		try {
			GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(
					gz));
			byte[] ret = IOUtil.readAll(in);
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends ZipProcessor> T process(ZipInputStream in,
			T processor) {
		try {
			ZipEntry entry = in.getNextEntry();
			while (entry != null) {
				if (entry.getName() != null && entry.getName().length() > 0) {
					processor.process(entry, in);
				}
				in.closeEntry();

				entry = in.getNextEntry();
			}
			return processor;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends ZipProcessor> T process(URL url, T processor) {
		ZipInputStream in = null;
		try {
			in = new ZipInputStream(new BufferedInputStream(url.openStream()));
			return process(in, processor);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtil.close(in);
		}
	}

}
