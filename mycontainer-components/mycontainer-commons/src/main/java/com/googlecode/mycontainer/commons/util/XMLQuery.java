package com.googlecode.mycontainer.commons.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLQuery {

	private final List<Node> nodes;

	public XMLQuery() {
		nodes = new ArrayList<Node>();
	}

	public XMLQuery(List<Node> nodes) {
		this();
		if (nodes == null) {
			return;
		}
		this.nodes.addAll(nodes);
	}

	public XMLQuery(String xml) {
		XMLQuery query = append(xml);
		this.nodes = query.nodes;
	}

	private DocumentBuilder getDocumentBuilder() {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			documentBuilderFactory.setCoalescing(true);
			return documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	private XPath getXPath() {
		return XPathFactory.newInstance().newXPath();
	}

	private Transformer getTransformer() throws TransformerConfigurationException, TransformerFactoryConfigurationError {
		return TransformerFactory.newInstance().newTransformer();
	}

	public XMLQuery append(String xml) {
		try {
			DocumentBuilder builder = getDocumentBuilder();
			Document root = builder.parse(new ByteArrayInputStream(xml.getBytes("utf-8")));
			return append(root);
		} catch (FactoryConfigurationError e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private XMLQuery append(Node node) {
		XMLQuery ret = new XMLQuery();
		ret.nodes.add(node);
		return ret;
	}

	@Override
	public String toString() {
		return "" + nodes;
	}

	public String toXML() {
		try {
			Transformer t = getTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			for (Node node : this.nodes) {
				t.transform(new DOMSource(node), result);
			}
			return sw.toString();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new RuntimeException(e);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}

	public XMLQuery clone() {
		return new XMLQuery(this.nodes);
	}

	public XMLQuery find(String query) {
		try {
			XPath xpath = getXPath();
			XPathExpression expr = xpath.compile(query);
			XMLQuery ret = new XMLQuery();
			for (Node node : this.nodes) {
				NodeList result = (NodeList) expr.evaluate(node, XPathConstants.NODESET);
				convertNodeList(result, ret.nodes);
			}
			return ret;
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	private List<Node> convertNodeList(NodeList result, List<Node> nodes) {
		if (nodes == null) {
			nodes = new ArrayList<Node>();
		}
		for (int i = 0; i < result.getLength(); i++) {
			Node node = result.item(i);
			nodes.add(node);
		}
		return nodes;
	}

	public String text() {
		StringBuilder sb = new StringBuilder();
		for (Node node : this.nodes) {
			if (node.getNodeType() == Node.ATTRIBUTE_NODE || node.getNodeType() == Node.TEXT_NODE) {
				sb.append(' ').append(node.getNodeValue());
			}
		}
		if (sb.length() == 0) {
			return null;
		}
		sb.deleteCharAt(0);
		return sb.toString();
	}

	public int size() {
		return nodes.size();
	}
	
	// REFACT!
	public XMLQuery item(int i) {
		return get(i);
	}

	public XMLQuery get(int i) {
		XMLQuery ret = new XMLQuery();
		ret.nodes.add(nodes.get(i));
		return ret;
	}

	public String getContent() {
		if (nodes.isEmpty()) {
			return null;
		}
		Node node = nodes.get(0);
		String ret = node.getNodeValue();
		return (ret == null ? null : ret.trim());
	}

	public String getContent(String parent) {
		if (nodes.isEmpty()) {
			return null;
		}

		for (Node node : nodes) {
			if (node.getParentNode().getNodeName().equals(parent)) {
				return node.getNodeValue();
			}
		}
		return null;
	}

	public static XMLQuery create(List<Node> nodes) {
		XMLQuery ret = new XMLQuery();
		ret.nodes.addAll(nodes);
		return ret;
	}

	public static XMLQuery parse(byte[] xml, boolean ns) {
		try {
			XMLQuery ret = new XMLQuery();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(ns);
			factory.setCoalescing(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document root = builder.parse(new ByteArrayInputStream(xml));
			ret.nodes.add(root);
			return ret;
		} catch (FactoryConfigurationError e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void sort(Comparator<XMLQuery> comparator) {
		List<XMLQuery> list = new ArrayList<XMLQuery>(nodes.size());
		for (Node node : nodes) {
			list.add(create(node));
		}
		Collections.sort(list, comparator);
		nodes.clear();
		for (XMLQuery parser : list) {
			nodes.addAll(parser.nodes);
		}
	}

	public static XMLQuery create(Node... list) {
		return create(Arrays.asList(list));
	}

}
