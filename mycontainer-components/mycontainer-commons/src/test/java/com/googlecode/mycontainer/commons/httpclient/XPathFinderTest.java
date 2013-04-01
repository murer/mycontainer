package com.googlecode.mycontainer.commons.httpclient;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.googlecode.mycontainer.commons.httpclient.XPathFinder;

public class XPathFinderTest {

	@Test
	public void testQueryLongs() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		ByteArrayInputStream in = new ByteArrayInputStream(
				"<a><b>2</b><b>4</b></a>".getBytes());
		Document doc = builder.parse(in);
		XPathFinder finder = new XPathFinder();
		finder.setDoc(doc);

		List<Long> longs = finder.queryLongs("//b");
		assertEquals(2l, longs.get(0));
		assertEquals(4l, longs.get(1));
		assertEquals(2, longs.size());
	}

	@Test
	public void testQueryTexts() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		ByteArrayInputStream in = new ByteArrayInputStream(
				"<a><b>2</b><b>4</b></a>".getBytes());
		Document doc = builder.parse(in);
		XPathFinder finder = new XPathFinder();
		finder.setDoc(doc);

		List<String> ret = finder.queryTexts("//b");
		assertEquals("2", ret.get(0));
		assertEquals("4", ret.get(1));
		assertEquals(2, ret.size());
	}

	@Test
	public void testQuery() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		ByteArrayInputStream in = new ByteArrayInputStream(
				"<a><b>2</b><b>4</b></a>".getBytes());
		Document doc = builder.parse(in);
		XPathFinder finder = new XPathFinder();
		finder.setDoc(doc);

		NodeList ret = finder.query("//b");
		assertEquals("2", ret.item(0).getFirstChild().getNodeValue());
		assertEquals("4", ret.item(1).getFirstChild().getNodeValue());
		assertEquals(2, ret.getLength());
	}
}
