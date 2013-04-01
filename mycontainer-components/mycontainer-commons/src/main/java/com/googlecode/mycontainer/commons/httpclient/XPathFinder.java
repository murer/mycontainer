package com.googlecode.mycontainer.commons.httpclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XPathFinder {

	private Document doc;

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public void config(WebResponse response) {
		try {
			byte[] array = response.getContentByteArray();
			ByteArrayInputStream in = new ByteArrayInputStream(array);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(in);
			in.close();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public NodeList query(String query) {
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(query);
			NodeList result = (NodeList) expr.evaluate(doc,
					XPathConstants.NODESET);
			return result;
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> queryTexts(String query) {
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(query);
			NodeList result = (NodeList) expr.evaluate(doc,
					XPathConstants.NODESET);
			int length = result.getLength();
			List<String> ret = new ArrayList<String>(length);
			for (int i = 0; i < length; i++) {
				Node item = result.item(i);
				String text = item.getNodeValue();
				if (text == null && item.getChildNodes().getLength() > 0) {
					text = item.getFirstChild().getNodeValue();
				}
				ret.add(text);
			}
			return ret;
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Long> queryLongs(String query) {
		List<Long> ret = new ArrayList<Long>();
		List<String> strs = queryTexts(query);
		for (String str : strs) {
			ret.add(new Long(str));
		}
		return ret;
	}

}
