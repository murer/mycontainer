package com.googlecode.mycontainer.commons.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.googlecode.mycontainer.commons.file.ContentTypeUtil;


public class ContentTypeUtilTest {

	@Test
	public void testContentType() {
		assertEquals("text/html", ContentTypeUtil.getContentType("html"));
		assertEquals("text/html", ContentTypeUtil.getContentType("htm"));
		assertEquals("text/plain", ContentTypeUtil.getContentType("txt"));

		assertNull(ContentTypeUtil.getContentType("notexists"));
	}

	@Test
	public void testContentTypeByPath() {
		assertEquals("text/plain",
				ContentTypeUtil.getContentTypeByPath("/a/bb.txt"));
		assertEquals("text/html",
				ContentTypeUtil.getContentTypeByPath("/a/bb.html"));

		assertNull(ContentTypeUtil.getContentTypeByPath("/a/bb.notexists"));
		assertNull(ContentTypeUtil.getContentTypeByPath("/a/bb"));
	}
}
