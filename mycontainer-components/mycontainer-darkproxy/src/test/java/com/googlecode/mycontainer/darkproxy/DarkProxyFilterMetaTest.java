package com.googlecode.mycontainer.darkproxy;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.junit.Test;

import com.googlecode.mycontainer.util.Util;

public class DarkProxyFilterMetaTest extends AbstractTestCase {

	@Test
	public void testFilter() throws Exception {
		URL url = new URL("http://localhost:8380/_darkproxy/ping");
		assertEquals("\"OK\"", Util.readAll(url, "UTF-8"));
	}

}
