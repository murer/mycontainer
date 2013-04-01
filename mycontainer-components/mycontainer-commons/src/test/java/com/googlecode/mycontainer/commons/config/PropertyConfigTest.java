package com.googlecode.mycontainer.commons.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

import com.googlecode.mycontainer.commons.config.PropertyConfig;


public class PropertyConfigTest {

	@Test
	public void testConfigRegion() {
		System.setProperty("test4", "value4");

		Properties config = PropertyConfig.instance().getConfig(
				PropertyConfigTest.class);
		assertEquals("value1", config.getProperty("test1"));
		assertEquals("value2", config.getProperty("test2"));
		assertEquals("value4", config.getProperty("test4"));
	}

	@Test
	public void testEnvs() {
		Properties config = PropertyConfig.instance().getConfig(
				PropertyConfigTest.class);
		Set<Object> keys = config.keySet();
		for (Object object : keys) {
			String key = (String) object;
			if (key.startsWith("env.")) {
				return;
			}
		}

		fail("expected some env variable");
	}

	@Test(expected = RuntimeException.class)
	public void testConfigNoRegion() {
		PropertyConfig.instance().getConfig("xxxx");
	}

	public void testErrorConfig() {
		Properties props = PropertyConfig.instance().getConfig(ArrayList.class);
		assertNotNull(props);
	}

}
