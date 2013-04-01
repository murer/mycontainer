package com.googlecode.mycontainer.commons.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.CharArrayReader;

import org.junit.Test;

import com.googlecode.mycontainer.commons.io.TimeoutReader;


public class TimeoutReaderTest {

	@Test
	public void testOk() throws Exception {
		CharArrayReader bin = new CharArrayReader(
				"this is my test\n".toCharArray());
		TimeoutReader in = new TimeoutReader(bin, 500l);
		BufferedReader data = new BufferedReader(in);
		assertEquals("this is my test", data.readLine());
		try {
			data.readLine();
			fail("IllegalStateException expected");
		} catch (IllegalStateException e) {
			assertEquals("timeout", e.getMessage());
		}
		data.close();
	}

	@Test
	public void testTimeout() throws Exception {
		try {
			MockReader mock = new MockReader();
			TimeoutReader in = new TimeoutReader(mock, 500l);
			BufferedReader data = new BufferedReader(in);
			data.readLine();
			fail("RuntimeException expected");
		} catch (IllegalStateException e) {
			assertEquals("timeout", e.getMessage());
		}
	}
}
