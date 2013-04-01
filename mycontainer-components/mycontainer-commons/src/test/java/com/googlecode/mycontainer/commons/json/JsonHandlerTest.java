package com.googlecode.mycontainer.commons.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.mycontainer.commons.json.JsonHandler;

public class JsonHandlerTest {

	private static final Logger LOG = LoggerFactory
			.getLogger(JsonHandlerTest.class);

	private JsonHandler handler;

	@Before
	public void setUp() {
		handler = JsonHandler.instance();
	}

	@Test
	public void testFormatBasics() {
		assertEquals("null", handler.format(null));

		assertEquals("true", handler.format(true));
		assertEquals("false", handler.format(false));

		assertEquals("\"my string\"", handler.format("my string"));
		assertEquals("\"null\"", handler.format("null"));
		assertEquals("\"true\"", handler.format("true"));
		assertEquals("2", handler.format(2));
		assertEquals("2.4", handler.format(2.4));

		assertEquals("\"\"", handler.format(""));
	}

	@Test
	public void testParseBasics() {
		assertEquals(null, handler.parse("null"));

		assertEquals(true, handler.parse("true"));
		assertEquals(false, handler.parse("false"));

		assertEquals("my string", handler.parse("'my string'"));
		assertEquals("null", handler.parse("'null'"));
		assertEquals("true", handler.parse("'true'"));
		assertEquals(2, handler.parse("2"));
		assertEquals(2.4, handler.parse("2.4"));

		assertEquals("", handler.parse("''"));
	}

	@Test
	public void testPhone() {
		testFormatParse("{\"class\":\"" + TestJsonPhone.class.getName()
				+ "\",\"id\":null,\"mainPhone\":null,\"number\":null}",
				TestJsonPhone.class);
		testFormatParse(
				"{\"class\":\""
						+ TestJsonPhone.class.getName()
						+ "\",\"id\":null,\"mainPhone\":null,\"number\":\"my number\"}",
				TestJsonPhone.class);
		testFormatParse("{\"class\":\"" + TestJsonPhone.class.getName()
				+ "\",\"id\":5,\"mainPhone\":null,\"number\":\"my number\"}",
				TestJsonPhone.class);
		testFormatParse(
				"{\"class\":\""
						+ TestJsonPhone.class.getName()
						+ "\",\"id\":5,\"mainPhone\":{\"class\":\""
						+ TestJsonPhone.class.getName()
						+ "\",\"id\":6,\"mainPhone\":null,\"number\":\"my number6\"},\"number\":\"my number\"}",
				TestJsonPhone.class);

	}

	@Test
	public void testClient() {
		testFormatParse(
				"{\"age\":0,\"class\":\"" + TestJsonCustomer.class.getName()
						+ "\",\"id\":null,\"name\":null,\"phones\":[]}",
				TestJsonCustomer.class);
		testFormatParse(
				"{\"age\":0,\"class\":\""
						+ TestJsonCustomer.class.getName()
						+ "\",\"id\":null,\"name\":null,\"phones\":[{\"class\":\""
						+ TestJsonPhone.class.getName()
						+ "\",\"id\":null,\"mainPhone\":null,\"number\":\"test1\"},{\"class\":\""
						+ TestJsonPhone.class.getName()
						+ "\",\"id\":null,\"mainPhone\":null,\"number\":\"test1\"}]}",
				TestJsonCustomer.class);
	}

	protected void testFormatParse(String str, Class<?> clazz) {
		LOG.info("Parsing: " + clazz + ": " + str);
		assertNotNull(str);
		assertNotNull(clazz);
		Object obj = handler.parse(str, clazz);
		assertNotNull(obj);
		assertEquals(clazz, obj.getClass());
		String format = handler.format(obj);
		LOG.info("Format : " + clazz + ": " + format);
		assertEquals(str, format);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void parseMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", "FILE");
		map.put("test", "value");
		String json = handler.format(map);
		assertEquals("{\"test\":\"value\",\"type\":\"FILE\"}", json);
		Map<String, Object> parsed = handler.parse(json, map.getClass());
		assertEquals(map, parsed);
	}
}
